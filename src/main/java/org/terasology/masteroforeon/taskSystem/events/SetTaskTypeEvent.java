// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.masteroforeon.taskSystem.BuildingType;
import org.terasology.masteroforeon.taskSystem.PlantType;

public class SetTaskTypeEvent implements Event {
    private String taskType;
    private BuildingType buildingType;
    private PlantType plantType;

    public SetTaskTypeEvent() {

    }

    public SetTaskTypeEvent(String assignedTaskType) {
        this.taskType = assignedTaskType;
    }

    public SetTaskTypeEvent(String assignedTaskType, BuildingType buildingType) {
        this.taskType = assignedTaskType;
        this.buildingType = buildingType;
    }

    public SetTaskTypeEvent(String assignedTaskType, PlantType plantType) {
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
