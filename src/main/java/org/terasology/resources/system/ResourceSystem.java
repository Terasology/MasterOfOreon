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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.items.BlockItemComponent;

import java.util.List;

;

/**
 * The system which consists of the logic common between the {@link PlayerResourceSystem} and {@link BuildingResourceSystem}
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
     * @param entity The entity which owns the Inventory
     * @param resourceName The name of the item to be checked
     * @param quantity The quantity of the item required
     * @return True - if the item is present in the inventory and in sufficient quantity
     */
    public abstract boolean checkForAResource(EntityRef entity, String resourceName, int quantity);

    /**
     * Adds a specified item to the give entity's inventory
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
