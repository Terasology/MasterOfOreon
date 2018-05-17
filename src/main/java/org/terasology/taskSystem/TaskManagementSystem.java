/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.taskSystem;

import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.registry.Share;
import org.terasology.taskSystem.components.TaskComponent;

import java.util.List;

@Share(TaskManagementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class TaskManagementSystem extends BaseComponentSystem {
    public boolean getTaskForOreon(HoldingComponent oreonHolding, Actor oreon) {
        List<TaskComponent> availableTasks = oreonHolding.availableTasks;
        //TODO sort List by creationTime

        if(!availableTasks.isEmpty()) {
            TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
            TaskComponent availableTaskComponent = availableTasks.remove(0);

            oreonTaskComponent.assignedTaskType = availableTaskComponent.assignedTaskType;
            oreonTaskComponent.creationTime = availableTaskComponent.creationTime;

            oreon.save(oreonTaskComponent);
            availableTaskComponent.taskStatus = TaskStatusType.InProgress;
            return true;
        }

        return false;
    }

    public void addTask(HoldingComponent oreonHolding, TaskComponent taskComponent) {
        taskComponent.taskStatus = TaskStatusType.Available;
        oreonHolding.availableTasks.add(taskComponent);
    }
}
