// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.tasks;

import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.Task;

public class TrainIntelligenceTask extends Task {
    private static final int HEALTH = 20;
    private static final int INTELLIGENCE = 30;
    private static final int STRENGTH = 0;
    private static final int HUNGER = 20;

    private static final float DURATION = 20;

    public TrainIntelligenceTask() {
        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.TRAIN_INTELLIGENCE;

        this.buildingType = BuildingType.Classroom;
        this.isAdvanced = true;
    }
}
