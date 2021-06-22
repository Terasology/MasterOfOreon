// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.tasks;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.Task;

/**
 * This task fetches blocks from a chest and then the PlaceBlocksInChest can be added as a subsequent task to move these blocks
 * to a building which requires them.
 */
public class GetBlocksFromChestTask extends Task {
    private static final int HEALTH = 10;
    private static final int INTELLIGENCE = 0;
    private static final int STRENGTH = 0;
    private static final int HUNGER = 50;

    private static final float DURATION = 100;

    public String blocksToTransfer;
    public int numberOfBlocks;
    public EntityRef chestEntity;

    public GetBlocksFromChestTask(String blockToGet, int number, EntityRef chestBlockEntity) {
        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.GET_BLOCKS_FROM_CHEST;

        this.blocksToTransfer = blockToGet;
        this.numberOfBlocks = number;
        this.chestEntity = chestBlockEntity;

    }
}
