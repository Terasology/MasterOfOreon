// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.resources.system;

import org.joml.Vector3i;
import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.module.inventory.components.InventoryComponent;

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
        Vector3i chestPosition = buildingComponent.boundingRegions.get(MooConstants.CHEST_BLOCK_INDEX).getMax(new Vector3i());
        return blockEntityRegistry.getBlockEntityAt(chestPosition);
    }
}
