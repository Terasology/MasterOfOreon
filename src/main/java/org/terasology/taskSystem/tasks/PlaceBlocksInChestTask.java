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
package org.terasology.taskSystem.tasks;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.Task;

/**
 * This task when assigned to the Oreons moves the specified blocks and the number into the chest entity.
 * For example : Currently after harvesting the crops are placed into the Storage building's chest.
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
