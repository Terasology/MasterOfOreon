// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.tasks;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.masteroforeon.taskSystem.AssignedTaskType;
import org.terasology.masteroforeon.taskSystem.Task;

/**
 * This task when assigned to the Oreons moves the specified blocks and the number into the chest entity. For example :
 * Currently after harvesting the crops are placed into the Storage building's chest.
 */
public class PlaceBlocksInChestTask extends Task {
    private static final int HEALTH = 10;
    private static final int INTELLIGENCE = 0;
    private static final int STRENGTH = 0;
    private static final int HUNGER = 50;

    private static final float DURATION = 10;

    public String blocksToTransfer;
    public int numberOfBlocks;
    public EntityRef chestEntity;

    public PlaceBlocksInChestTask(String blockToAdd, int number, EntityRef chestBlockEntity) {
        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.PLACE_BLOCKS_IN_CHEST;

        this.blocksToTransfer = blockToAdd;
        this.numberOfBlocks = number;
        this.chestEntity = chestBlockEntity;

        this.blockToRender = blockToAdd;
    }
}
