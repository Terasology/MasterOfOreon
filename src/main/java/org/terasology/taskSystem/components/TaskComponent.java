// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.components;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.TaskStatusType;

/**
 * Component which gets attached to an Oreon entity when a task is assigned to it.
 * All fields are replicated from server to client since changes to this component happens always on the Authority TaskManagementSystem.
 */
public class TaskComponent implements Component<TaskComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String assignedTaskType;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public long creationTime;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public TaskStatusType taskStatus = TaskStatusType.Available;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public BlockRegion taskRegion;

    public float taskCompletionTime;

    public Task task = new Task();

    // next task to be performed and the delay in ms
    public Task subsequentTask = new Task();
    public String subsequentTaskType;
    public BlockRegion subsequentTaskRegion;
    public long delayBeforeNextTask;

    @Override
    public void copy(TaskComponent other) {
        this.assignedTaskType = other.assignedTaskType;
        this.creationTime = other.creationTime;
        this.taskStatus = other.taskStatus;
        this.taskRegion = other.taskRegion;
        this.taskCompletionTime = other.taskCompletionTime;
        this.task = other.task;
        this.subsequentTask = other.subsequentTask;
        this.subsequentTaskType = other.subsequentTaskType;
        this.subsequentTaskRegion = other.subsequentTaskRegion;
        this.delayBeforeNextTask = other.delayBeforeNextTask;
    }
}
