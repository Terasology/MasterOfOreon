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
package org.terasology.holdingSystem.components;

import org.terasology.entitySystem.Component;
import org.terasology.math.Region3i;
import org.terasology.network.FieldReplicateType;
import org.terasology.network.Replicate;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;

/**
 * The component attached to a task entity which specifies the area selected for the task. All changes made to this task
 * are in the authority {@link org.terasology.taskSystem.TaskManagementSystem} so the fields replicated from the server to client.
 */
public class AssignedAreaComponent implements Component {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public Region3i assignedRegion;

    @Replicate
    public String assignedTaskType = AssignedTaskType.NONE;

    @Replicate
    public BuildingType buildingType = BuildingType.None;
}
