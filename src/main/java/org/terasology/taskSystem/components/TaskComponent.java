// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.TaskStatusType;

/**
 * Component which gets attached to an Oreon entity when a task is assigned to it. All fields are replicated from server
 * to client since changes to this component happens always on the Authority TaskManagementSystem.
 */
public class TaskComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public String assignedTaskType;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public long creationTime;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public TaskStatusType taskStatus = TaskStatusType.Available;

    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Region3i taskRegion;

    public float taskCompletionTime;

    public Task task = new Task();

    // next task to be performed and the delay in ms
    public Task subsequentTask = new Task();
    public String subsequentTaskType;
    public Region3i subsequentTaskRegion;
    public long delayBeforeNextTask;
}
