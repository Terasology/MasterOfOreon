/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.spawning;

import org.joml.Vector3fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.nameTags.NameTagComponent;
import org.terasology.engine.logic.players.PlayerUtil;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.MersenneRandom;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.namegenerator.creature.CreatureNameComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The authority system which handles the spawning of Oreons into the world.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class SpawningAuthoritySystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SpawningAuthoritySystem.class);

    @In
    private BlockManager blockManager;

    @In
    private EntityManager entityManager;

    @In
    private InventoryManager inventoryManager;



    /**
     * Spawns the desired Oreon at the location of Portal which sends the event
     * @param event The {@link OreonSpawnEvent} which is sent by the {@link org.terasology.spawning.nui.SpawnScreenLayer} when a player selects the Oreon to be spawned
     * @param player The player entity which is spawning the Oreon.
     */
    @ReceiveEvent
    public void oreonSpawn(OreonSpawnEvent event, EntityRef player) {
        Prefab prefabToSpawn = event.getOreonPrefab();
        Vector3fc spawnPosition = event.getSpawnPosition();

        boolean shouldSpawn = consumeItem(player, prefabToSpawn);
        if (shouldSpawn) {
            // spawn the new oreon into the world
            EntityRef newOreon = entityManager.create(prefabToSpawn, spawnPosition);
            NetworkComponent networkComponent = new NetworkComponent();
            networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;
            newOreon.addComponent(networkComponent);
            newOreon.getComponent(OreonSpawnComponent.class).parent = player;

            CreatureNameComponent oreonNameComponent = newOreon.getComponent(CreatureNameComponent.class);
            NameTagComponent nameTagComponent = new NameTagComponent();
            nameTagComponent.text = oreonNameComponent.firstName;
            nameTagComponent.yOffset = 1.0f;
            newOreon.addComponent(nameTagComponent);

            assignRandomAttributes(prefabToSpawn, newOreon);

            logger.info("Player " + PlayerUtil.getColoredPlayerName(player) + " spawned a new Oreon of Type : " + prefabToSpawn.getName());
        }
    }

    /**
     * Checks if the Oreon to be spawned has an item requirement and calls {@code removeNeededItem} item method
     * @param player The player entity spawning the Oreon
     * @param prefab The prefab of the Oreon to be spawned
     * @return A boolean value which signifies if the required items were found in inventory and successfully deducted
     */
    private boolean consumeItem(EntityRef player, Prefab prefab) {
        OreonSpawnComponent oreonSpawnComponent = prefab.getComponent(OreonSpawnComponent.class);

        if (oreonSpawnComponent == null) {
            logger.info(prefab.getName() + " is not spawnable.");
            return false;
        }
        Map<String, Integer> itemsHashMap = oreonSpawnComponent.itemsToConsume;

        int numberOfItems = itemsHashMap.size();

        if (numberOfItems != 0) {
            if (player.hasComponent(InventoryComponent.class)) {
                List<Integer> requiredSlots = getSlotsForRequiredItems(itemsHashMap, player);

                //all required items not in inventory
                if (requiredSlots.size() != numberOfItems) {
                    logger.info("Could not find all required items in inventory");
                    return false;
                }

                for (int slotNumber = 0; slotNumber < numberOfItems; slotNumber++) {
                    if (!removeNeededItem(itemsHashMap, requiredSlots.get(slotNumber), player)) {
                        //could not decrement the required number from inventory
                        return false;
                    }
                }

                logger.info("Found all items required for spawning, creating your Oreon");
                return true;
            }

            logger.info("No inventory to source material from, cannot spawn");
            return false;
        }

        logger.info("No item required to spawn, spawning your Oreon");
        return true;
    }

    /**
     * Makes a pass through all items in a player's inventory and adds the slot number of all required items to a list.
     * @param items The map which consists of all items and their quantity required for spawning
     * @param player The player entity spawning the Oreon
     * @return A list of slot number of all items required for spawning. If the size of this is list is not equal to the
     * number of items required then a required item was not found in the inventory
     */
    private List<Integer> getSlotsForRequiredItems(Map<String, Integer> items, EntityRef player) {
        List<Integer> requiredSlots = new ArrayList<>();

        int inventorySize = inventoryManager.getNumSlots(player);

        for (int slotNumber = 0; slotNumber <= inventorySize; slotNumber++) {
            EntityRef inventorySlot = inventoryManager.getItemInSlot(player, slotNumber);

            DisplayNameComponent displayNameComponent = inventorySlot.getComponent(DisplayNameComponent.class);
            if (displayNameComponent != null) {
                String blockName = displayNameComponent.name;
                //if this item is required
                if (items.containsKey(blockName)) {
                    //check if required number is present in the inventory
                    int requiredNumber = items.get(blockName);
                    if (requiredNumber <= inventoryManager.getStackSize(inventorySlot)) {
                        requiredSlots.add(slotNumber);
                    } else {
                        logger.info("You don't have enough({} required) {} blocks to spawn the Oreon", requiredNumber, blockName);
                        break;
                    }
                }
            }
        }

        return requiredSlots;
    }

    /**
     * Removes an item required for spawning from the player's inventory
     * @param items The map which consists of all items and their quantity required for spawning
     * @param slotNumber The slot number of the item to be removed
     * @param player The player entity spawning the Oreon
     * @return True - if the item removal from the inventory was successful.<br>
     *     False - if the required amount was not found in the inventory
     */
    private boolean removeNeededItem(Map<String, Integer> items, int slotNumber, EntityRef player) {
        EntityRef inventorySlot = inventoryManager.getItemInSlot(player, slotNumber);
        DisplayNameComponent displayNameComponent = inventorySlot.getComponent(DisplayNameComponent.class);
        String blockName = displayNameComponent.name;

        logger.info("This Oreon has an item demand for spawning: " + blockName);
        logger.info("Found the item needed to spawn stuff! Decrementing by {}, then spawning", items.get(blockName));

        EntityRef result = inventoryManager.removeItem(player, player, inventorySlot, false, items.get(blockName));
        if (result == null) {
            logger.info("Could not decrement the required amount from inventory, not spawning");
            return false;
        }

        //successfully removed the required number of needed item
        return true;
    }

    private void assignRandomAttributes(Prefab prefabToSpawn,EntityRef oreon) {
        OreonAttributeComponent oreonAttributes = new OreonAttributeComponent();

        MersenneRandom random = new MersenneRandom();

        if (prefabToSpawn.getName().equals(MooConstants.OREON_BUILDER_PREFAB)) {
            oreonAttributes.intelligence = random.nextInt(oreonAttributes.maxIntelligence);
        } else {
            oreonAttributes.intelligence = 0;
        }
        oreonAttributes.strength = random.nextInt(oreonAttributes.maxStrength);

        oreon.addComponent(oreonAttributes);
    }

}
