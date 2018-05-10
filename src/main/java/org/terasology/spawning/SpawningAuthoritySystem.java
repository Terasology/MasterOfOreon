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
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.In;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;


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

        // spawn the new oreon into the world
        //TODO Resource consuming spawn
        //TODO oreon still spawns mid-air
        logger.info("Recieved oreon spawn event");
        boolean toSpawn = consumeItem(player, prefabToSpawn);
        if(toSpawn) {
            EntityRef newOreon = entityManager.create(prefabToSpawn, spawnPos);
            NetworkComponent netComp = new NetworkComponent();
            netComp.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;
            newOreon.addComponent(netComp);
            newOreon.getComponent(OreonSpawnComponent.class).parent = player;
            logger.info("Player " + newOreon.getComponent(OreonSpawnComponent.class).parent.getId() + "Spawned a new Oreon of Type : " + prefabToSpawn);
        }
    }

    public boolean consumeItem(EntityRef player, Prefab prefab) {
        OreonSpawnComponent oreonSpawnComponent = prefab.getComponent(OreonSpawnComponent.class);
        String neededItem = oreonSpawnComponent.itemToConsume;
        if (neededItem != null) {
            logger.info("This Oreon has an item demand for spawning: {}", neededItem);
            if (player.hasComponent(InventoryComponent.class)) {
                BlockFamily neededFamily = blockManager.getBlockFamily(neededItem);
                logger.info("Needed block family: {}", neededFamily);

                EntityRef inventorySlot = inventoryManager.getItemInSlot(player, 0);
                int inventorySize = inventoryManager.getNumSlots(player);

                int slotNumber = 0;
                while (slotNumber <= inventorySize) {
                    DisplayNameComponent displayName = inventorySlot.getComponent(DisplayNameComponent.class);
                    if (displayName != null) {
                        if (neededFamily.getDisplayName().equals(displayName.name)) {
                            logger.info("Found the item needed to spawn stuff! Decrementing by 1 then spawning");

                            EntityRef result = inventoryManager.removeItem(player, player, inventorySlot, true, 1);
                            logger.info("Result from decrementing: {}", result);
                            logger.info("Successfully decremented an existing item stack - accepting item-based spawning");
                            return true;

                        }
                    }

                    slotNumber++;
                    logger.info("Next Slot number" + slotNumber);
                    inventorySlot = inventoryManager.getItemInSlot(player, slotNumber);
                }

                logger.info("Could not find required item in inventory, not spawning");
                return false;

            }

            logger.info("No inventory to source material from, cannot spawn");
            return false;
        }

        logger.info("No item required to spawn, spawning your Oreon");
        return true;
    }

}
