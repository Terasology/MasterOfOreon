// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.resources.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.inventory.logic.InventoryManager;

import java.util.List;

/**
 * The system which consists of the logic common between the {@link PlayerResourceSystem} and {@link
 * BuildingResourceSystem}
 */
public abstract class ResourceSystem {
    private static final Logger logger = LoggerFactory.getLogger(ResourceSystem.class);
    BlockEntityRegistry blockEntityRegistry;
    InventoryManager inventoryManager;

    public void initialize(BlockEntityRegistry blockRegistry, InventoryManager inventory) {
        this.blockEntityRegistry = blockRegistry;
        this.inventoryManager = inventory;
    }

    /**
     * Checks if the specified item is present in the given entity's inventory
     *
     * @param entity The entity which owns the Inventory
     * @param resourceName The name of the item to be checked
     * @param quantity The quantity of the item required
     * @return True - if the item is present in the inventory and in sufficient quantity
     */
    public abstract boolean checkForAResource(EntityRef entity, String resourceName, int quantity);

    /**
     * Adds a specified item to the give entity's inventory
     *
     * @param entity The entity which owns the Inventory
     * @param item The item to be added
     * @return True - if the addition was successful
     */
    public abstract boolean addAResource(EntityRef entity, EntityRef item);

    boolean deductFromInventory(EntityRef inventory, List<EntityRef> slots, String resourceURI, int quantity) {

        int size = inventoryManager.getNumSlots(inventory);

        for (int slotNumber = 0; slotNumber < size; slotNumber++) {
            EntityRef item = inventoryManager.getItemInSlot(inventory, slotNumber);

            if (item.equals(EntityRef.NULL)) {
                continue;
            }

            BlockItemComponent blockItemComponent = item.getComponent(BlockItemComponent.class);
            if (blockItemComponent.blockFamily.getURI().toString().equals(resourceURI) && inventoryManager.getStackSize(item) >= quantity) {
                inventoryManager.removeItem(inventory, inventory, slotNumber, false, quantity);
                return true;
            }
        }

        return false;
    }

    boolean addToInventory(EntityRef inventory, EntityRef item) {
        return inventoryManager.giveItem(inventory, inventory, item);
    }
}
