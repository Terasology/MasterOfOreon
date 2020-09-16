// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.tasks;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.masteroforeon.taskSystem.AssignedTaskType;
import org.terasology.masteroforeon.taskSystem.Task;

public class BuildingUpgradeTask extends Task {
    private static final int HEALTH = 40;
    private static final int INTELLIGENCE = 0;
    private static final int STRENGTH = 0;
    private static final int HUNGER = 30;

    private static final float DURATION = 1;

    public BuildingUpgradeTask(EntityRef building) {
        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.UPGRADE;

        this.requiredBuildingEntityID = building.getId();
    }
}
