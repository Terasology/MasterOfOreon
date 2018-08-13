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
package org.terasology.resources.system;

import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.math.geom.Vector3i;

import java.util.List;

/**
 * The system which handles the resource checks and addition to the buildings in the village.
 */
public class BuildingResourceSystem extends ResourceSystem {

    public boolean checkForAResource(EntityRef building, String resourceURI, int quantity) {
        EntityRef chestEntity = getChestEntity(building);
        List<EntityRef> slots = chestEntity.getComponent(InventoryComponent.class).itemSlots;

        return deductFromInventory(chestEntity, slots, resourceURI, quantity);
    }

    public boolean addAResource(EntityRef building, EntityRef item) {
        EntityRef chestEntity = getChestEntity(building);
        return addToInventory(chestEntity, item);
    }

    /**
     * Looks for the chest block in the given building
     * @param building The building entity to be searched
     * @return The chest block entity
     */
    private EntityRef getChestEntity(EntityRef building) {
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

        // Get the chest block in the building
        // TODO: Should not assume that Chest will be the first item in the ST prefab
        Vector3i chestPosition = buildingComponent.boundingRegions.get(MooConstants.CHEST_BLOCK_INDEX).max();
        return blockEntityRegistry.getBlockEntityAt(chestPosition);
    }
}
