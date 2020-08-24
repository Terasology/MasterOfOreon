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
import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIList;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.taskSystem.AssignedTaskType;
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
    private UIList buildings;

    @Override
    public void initialise() {
        availableTasks = find(MooConstants.AVAILABLE_TASKS_LIST_ID, UIList.class);
        inProgressTasks = find(MooConstants.ON_GOING_TASKS_LIST_ID, UIList.class);
        oreons = find(MooConstants.OREONS_LIST_ID, UIList.class);
        buildings = find(MooConstants.CONSTRUCTED_BUILDINGS_LIST_ID, UIList.class);

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
                    if (!taskComponent.assignedTaskType.equals(AssignedTaskType.NONE) && taskComponent.taskStatus.equals(TaskStatusType.Available)) {
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
                    switch (oreonEntity.getParentPrefab().getName()) {
                        case MooConstants.OREON_BUILDER_PREFAB :
                            numberOfBuilders++;
                            break;
                        case MooConstants.OREON_GUARD_PREFAB :
                            numberOfGuards++;
                            break;
                        case MooConstants.OREON_KING_PREFAB :
                            numberOfKings++;
                    }
                }

                result.add("Builders : " + numberOfBuilders);
                result.add("Guards : " + numberOfGuards);
                result.add("Kings : " + numberOfKings);
                return result;
            }
        };

        Binding<List> buildingsList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                List<String> result = new ArrayList<>();

                int numberOfDiners = 0;
                int numberOfStorage = 0;
                int numberOfLaboratories = 0;
                int numberOfClassrooms = 0;
                int numberOfGyms = 0;
                int numberOfHospitals = 0;
                int numberOfJails = 0;
                int numberOfChurches = 0;
                int numberOfBedrooms = 0;

                for (EntityRef building : entityManager.getEntitiesWith(ConstructedBuildingComponent.class)) {
                    ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
                    switch (buildingComponent.buildingType) {
                        case Diner :
                            numberOfDiners++;
                            break;
                        case Storage :
                            numberOfStorage++;
                            break;
                        case Laboratory :
                            numberOfLaboratories++;
                            break;
                        case Classroom :
                            numberOfClassrooms++;
                            break;
                        case Gym :
                            numberOfGyms++;
                            break;
                        case Hospital :
                            numberOfHospitals++;
                            break;
                        case Jail :
                            numberOfJails++;
                            break;
                        case Church :
                            numberOfChurches++;
                            break;
                        case Bedroom :
                            numberOfBedrooms++;
                            break;
                    }
                }

                result.add("Diners : " + numberOfDiners);
                result.add("Storage : " + numberOfStorage);
                result.add("Laboratories : " + numberOfLaboratories);
                result.add("Classrooms : " + numberOfClassrooms);
                result.add("Gyms : " + numberOfGyms);
                result.add("Hospitals : " + numberOfHospitals);
                result.add("Jails : " + numberOfJails);
                result.add("Churches : " + numberOfChurches);
                result.add("Bedrooms :" + numberOfBedrooms);

                return result;
            }
        };

        availableTasks.bindList(availableTasksList);
        inProgressTasks.bindList(inProgressTasksList);
        oreons.bindList(oreonsList);
        buildings.bindList(buildingsList);
    }
}
