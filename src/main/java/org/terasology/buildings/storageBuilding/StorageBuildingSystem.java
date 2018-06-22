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
package org.terasology.buildings.storageBuilding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.common.lifespan.LifespanComponent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.PickupComponent;
import org.terasology.logic.inventory.events.DropItemEvent;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.taskSystem.BuildingType;
import org.terasology.world.BlockEntityRegistry;

import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageBuildingSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(StorageBuildingSystem.class);

    @In
    Context context;
    private BlockEntityRegistry blockEntityRegistry;

    @Override
    public void postBegin() {
        blockEntityRegistry = context.get(BlockEntityRegistry.class);
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void onDropItem(DropItemEvent event, EntityRef item, ItemComponent itemComponent) {
        Vector3f location = event.getPosition();
        EntityRef owner = item.getOwner();

        if (!owner.hasComponent(HoldingComponent.class)) {
            logger.info("Can't add to a Storage Building player has no Holding");
        }

        checkIfInsideStorageBuilding(owner, item);
    }

    private void checkIfInsideStorageBuilding(EntityRef owner, EntityRef item) {
        logger.info("Checking if player is inside storage");
        LocationComponent locationComponent = owner.getComponent(LocationComponent.class);

        if (locationComponent == null) {
            return;
        }
        Vector3f location = locationComponent.getWorldPosition();
        HoldingComponent holdingComponent = owner.getComponent(HoldingComponent.class);

        List<EntityRef> buildings = holdingComponent.constructedBuildings;

        for (EntityRef building : buildings) {
            ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

            if (buildingComponent.buildingType.equals(BuildingType.Storage)) {
                List<Region3i> regions = buildingComponent.boundingRegions;

                for (Region3i region : regions) {
                    if (region.encompasses(new Vector3i(location.x, location.y - 1, location.z))) {
                        // TODO: Should not assume that Chest will be the first item in the ST prefab
                        addItemToChest(item, regions.get(0));
                        break;
                    }
                }
            }
        }
    }

    private void addItemToChest(EntityRef item, Region3i region) {
        int x = region.maxX();
        int y = region.maxY();
        int z = region.maxZ();

        logger.info("looking for chest");
        EntityRef block = blockEntityRegistry.getBlockEntityAt(new Vector3i(x, y, z));

        logger.info("block location" + item.toFullDescription());

        if (block.getParentPrefab().getName().equals("MasterOfOreon:oreonChest")) {
            // Remove components so that the item is not destroyed after a while
            item.removeComponent(PickupComponent.class);
            item.removeComponent(LifespanComponent.class);

            // Remove item from world
            // TODO: resolve RigidBody NPE by adding some delay between drop and addition to chest
            item.removeComponent(LocationComponent.class);
            block.getComponent(InventoryComponent.class).itemSlots.add(item);
        }
    }
}
