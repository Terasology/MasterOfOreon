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
package org.terasology.taskSystem.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.TaskStatusType;

/**
 * Component which gets attached to an Oreon entity when a task is assigned to it.
 * All fields are replicated from server to client since changes to this component happens always on the Authority TaskManagementSystem.
 */
public class TaskComponent implements Component {
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
}
