// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.actions;

import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.items.BlockItemFactory;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.tasks.GetBlocksFromChestTask;

@BehaviorAction(name = "get_blocks_from_chest")
public class GetBlocksFromChestNode extends BaseAction {

    @In
    private Context context;

    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockManager;

    private InventoryManager inventoryManager;

    @Override
    public void construct(Actor oreon) {
        inventoryManager = context.get(InventoryManager.class);
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
        GetBlocksFromChestTask oreonTask = (GetBlocksFromChestTask) oreonTaskComponent.task;

        BlockItemFactory blockItemFactory = new BlockItemFactory(entityManager);
        inventoryManager.removeItem(oreonTask.chestEntity, oreonTask.chestEntity,
                blockItemFactory.newInstance(blockManager.getBlockFamily(oreonTask.blocksToTransfer)),
                false);
        return BehaviorState.SUCCESS;
    }
}
