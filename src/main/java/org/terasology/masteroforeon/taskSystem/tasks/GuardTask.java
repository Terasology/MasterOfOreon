// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.tasks;

import org.terasology.masteroforeon.taskSystem.AssignedTaskType;
import org.terasology.masteroforeon.taskSystem.Task;

public class GuardTask extends Task {
    private static final int HEALTH = 20;
    private static final int INTELLIGENCE = 0;
    private static final int STRENGTH = 10;
    private static final int HUNGER = 40;

    private static final float DURATION = 100;

    public GuardTask(long buildingEntityId) {
        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.GUARD;
        this.isAdvanced = false;

        this.requiredBuildingEntityID = buildingEntityId;
    }
}
