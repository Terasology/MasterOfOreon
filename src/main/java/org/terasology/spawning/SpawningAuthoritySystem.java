/*
 * Copyright 2017 MovingBlocks
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.In;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemComponent;

import java.util.ArrayList;
import java.util.HashMap;

@RegisterSystem(RegisterMode.AUTHORITY)
public class SpawningAuthoritySystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SpawningAuthoritySystem.class);

    @In
    private BlockManager blockManager;

    @In
    private EntityManager entityManager;

    @In
    private InventoryManager inventoryManager;

    private Prefab prefabToSpawn;
    private Vector3f spawnPos;


    /**
     * Spawns the desired Oreon at the location of Portal which sends the event
     */
    @ReceiveEvent
    public void oreonSpawn(OreonSpawnEvent event, EntityRef player) {
        prefabToSpawn = event.getOreonPrefab();
        spawnPos = event.getSpawnPos();
        spawnPos.y = spawnPos.y - (float)0.5;

        boolean toSpawn = consumeItem(player, prefabToSpawn);
        if(toSpawn) {
            // spawn the new oreon into the world
            EntityRef newOreon = entityManager.create(prefabToSpawn, spawnPos);
            NetworkComponent netComp = new NetworkComponent();
            netComp.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;
            newOreon.addComponent(netComp);
            newOreon.getComponent(OreonSpawnComponent.class).parent = player;

            assignRandomAttributes(newOreon);

            logger.info("Player " + newOreon.getComponent(OreonSpawnComponent.class).parent + "Spawned a new Oreon of Type : " + prefabToSpawn);
        }
    }

    public boolean consumeItem(EntityRef player, Prefab prefab) {
        OreonSpawnComponent oreonSpawnComponent = prefab.getComponent(OreonSpawnComponent.class);
        HashMap<String, Integer> itemsHashMap = oreonSpawnComponent.itemsToConsume;

        if (oreonSpawnComponent.itemsToConsume != null) {
            ArrayList<String> neededItemList = new ArrayList(itemsHashMap.keySet());
            int numberOfItems = neededItemList.size();
            if (player.hasComponent(InventoryComponent.class)) {
                ArrayList<Integer> requiredSlots = getSlotsForRequiredItems(itemsHashMap, player);

                //all required items not in inventory
                if (requiredSlots.size() != numberOfItems) {
                    logger.info("Could not find all required items in inventory");
                    return false;
                }

                for (int slotNumber = 0; slotNumber < numberOfItems; slotNumber++) {
                    if(!removeNeededItem(itemsHashMap, requiredSlots.get(slotNumber), player)) {
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

    public ArrayList<Integer> getSlotsForRequiredItems(HashMap<String, Integer> items, EntityRef player) {
        ArrayList<Integer> requiredSlots = new ArrayList<>();

        int inventorySize = inventoryManager.getNumSlots(player);

        for(int slotNumber = 0; slotNumber <= inventorySize; slotNumber++) {
            EntityRef inventorySlot = inventoryManager.getItemInSlot(player, slotNumber);

            BlockItemComponent blockItemComponent = inventorySlot.getComponent(BlockItemComponent.class);
            if (blockItemComponent != null) {
                String blockFamily = blockItemComponent.blockFamily.toString();
                //if this item is required
                if (items.containsKey(blockFamily)) {
                    requiredSlots.add(slotNumber);
                }
            }
        }

        return requiredSlots;
    }

    public boolean removeNeededItem(HashMap<String, Integer> items, int slotNumber, EntityRef player) {
        EntityRef inventorySlot = inventoryManager.getItemInSlot(player, slotNumber);
        BlockItemComponent blockItemComponent = inventorySlot.getComponent(BlockItemComponent.class);
        String blockFamily = blockItemComponent.blockFamily.toString();

        logger.info("This Oreon has an item demand for spawning: " + blockFamily);
        logger.info("Found the item needed to spawn stuff! Decrementing by {}, then spawning", items.get(blockFamily));

        EntityRef result = inventoryManager.removeItem(player, player, inventorySlot, false, items.get(blockFamily));
        if(result == null) {
            logger.info("Could not decrement the required amount from inventory, not spawning");
            return false;
        }

        //successfully removed the required number of needed item
        return true;
    }

    public void assignRandomAttributes(EntityRef oreon) {
        OreonAttributeComponent oreonAttributes = new OreonAttributeComponent();

        MersenneRandom random = new MersenneRandom();

        if(prefabToSpawn.getName().equals("Oreons:OreonBuilder")) {
            oreonAttributes.intelligence = random.nextInt(oreonAttributes.maxIntelligence);
        }
        else {
            oreonAttributes.intelligence = 0;
        }
        oreonAttributes.strength = random.nextInt(oreonAttributes.maxStrength);

        oreon.addComponent(oreonAttributes);
    }

}
