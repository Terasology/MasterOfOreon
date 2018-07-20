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
package org.terasology.overviewSystem.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.taskSystem.TaskStatusType;
import org.terasology.taskSystem.components.TaskComponent;

import java.util.ArrayList;
import java.util.List;

public class OverviewScreenLayer extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(OverviewScreenLayer.class);
    @In
    private EntityManager entityManager;

    @In
    private Time time;

    private UIList availableTasks;
    private UIList inProgressTasks;
    private UIList oreons;

    @Override
    public void initialise() {
        availableTasks = find(Constants.AVAILABLE_TASKS_LIST_ID, UIList.class);
        inProgressTasks = find(Constants.ON_GOING_TASKS_LIST_ID, UIList.class);
        oreons = find(Constants.OREONS_LIST_ID, UIList.class);

        populateLists();
    }

    @Override
    public boolean isReleasingMouse() {
        return false;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    private void populateLists() {
        Binding<List> availableTasksList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                List<String> result = new ArrayList<>();
                for (EntityRef taskEntity : entityManager.getEntitiesWith(TaskComponent.class)) {
                    TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);
                        if (taskComponent.task != null && taskComponent.taskStatus.equals(TaskStatusType.Available)) {
                            result.add(taskComponent.task.assignedTaskType);
                        }
                }

                if (result.isEmpty()) {
                    result.add("No tasks");
                }
                return result;
            }
        };

        Binding<List> inProgressTasksList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                List<String> result = new ArrayList<>();
                for (EntityRef taskEntity : entityManager.getEntitiesWith(TaskComponent.class)) {
                    TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);
                    if (taskComponent.task != null && taskComponent.taskStatus.equals(TaskStatusType.InProgress)) {
                        result.add(taskComponent.task.assignedTaskType + "\nRemaining Time : " + (taskComponent.taskCompletionTime - time.getGameTime()));
                    }
                }

                if (result.isEmpty()) {
                    result.add("No tasks");
                }
                return result;
            }
        };

        Binding<List> oreonsList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                List<String> result = new ArrayList<>();
                int numberOfBuilders = 0;
                int numberOfGuards = 0;
                int numberOfKings = 0;
                for (EntityRef oreonEntity : entityManager.getEntitiesWith(OreonSpawnComponent.class)) {
                    logger.info(oreonEntity.getParentPrefab().getName());
                    switch (oreonEntity.getParentPrefab().getName()) {
                        case Constants.OREON_BUILDER_PREFAB :
                            numberOfBuilders++;
                            break;
                        case Constants.OREON_GUARD_PREFAB :
                            numberOfGuards++;
                            break;
                        case Constants.OREON_KING_PREFAB :
                            numberOfKings++;
                    }
                }

                result.add("Builders : " + numberOfBuilders);
                result.add("Guards : " + numberOfGuards);
                result.add("Kings : " + numberOfKings);
                return result;
            }
        };

        availableTasks.bindList(availableTasksList);
        inProgressTasks.bindList(inProgressTasksList);
        oreons.bindList(oreonsList);
    }
}
