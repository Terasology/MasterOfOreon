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
package org.terasology.taskSystem.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.PlantType;

public class SetTaskTypeEvent implements Event {
    private String taskType;
    private BuildingType buildingType;
    private PlantType plantType;

    public SetTaskTypeEvent () {

    }

    public SetTaskTypeEvent (String assignedTaskType) {
        this.taskType = assignedTaskType;
    }

    public SetTaskTypeEvent (String assignedTaskType, BuildingType buildingType) {
        this.taskType = assignedTaskType;
        this.buildingType = buildingType;
    }

    public SetTaskTypeEvent (String assignedTaskType, PlantType plantType) {
        this.taskType = assignedTaskType;
        this.plantType = plantType;
    }

    public String getTaskType() {
        return taskType;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public PlantType getPlantType() {
        return plantType;
    }
}
