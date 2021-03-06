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

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.inventory.events.DropItemEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.resources.system.BuildingResourceSystem;
import org.terasology.resources.system.ResourceSystem;
import org.terasology.taskSystem.BuildingType;

import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageBuildingSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(StorageBuildingSystem.class);

    @In
    private InventoryManager inventoryManager;

    @In
    private BlockEntityRegistry blockEntityRegistry;

    private ResourceSystem buildingResourceSystem;

    @Override
    public void postBegin() {
        buildingResourceSystem = new BuildingResourceSystem();
        buildingResourceSystem.initialize(blockEntityRegistry, inventoryManager);
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void onDropItem(DropItemEvent event, EntityRef item, ItemComponent itemComponent) {
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
        Vector3f location = locationComponent.getWorldPosition(new Vector3f());
        HoldingComponent holdingComponent = owner.getComponent(HoldingComponent.class);

        List<EntityRef> buildings = holdingComponent.constructedBuildings;

        for (EntityRef building : buildings) {
            ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

            if (buildingComponent.buildingType.equals(BuildingType.Storage)) {
                List<BlockRegion> regions = buildingComponent.boundingRegions;

                for (BlockRegion region : regions) {
                    if (region.contains(location.x, location.y - 1, location.z)) {
                        addItemToChest(item, building);
                        break;
                    }
                }
            }
        }
    }

    private void addItemToChest(EntityRef item, EntityRef building) {
        buildingResourceSystem.addAResource(building, item);
        ItemComponent itemComponent = item.getComponent(ItemComponent.class);

        // Remove the components so that the item cannot be picked up
        if (itemComponent != null) {
            for (Component component : itemComponent.pickupPrefab.iterateComponents()) {
                item.removeComponent(component.getClass());
            }
        }
    }
}
