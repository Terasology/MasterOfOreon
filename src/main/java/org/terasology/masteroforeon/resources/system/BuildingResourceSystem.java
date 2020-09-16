// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.resources.system;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.inventory.logic.InventoryComponent;
import org.terasology.masteroforeon.MooConstants;
import org.terasology.masteroforeon.buildings.components.ConstructedBuildingComponent;
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
     *
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
