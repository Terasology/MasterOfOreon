// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.tasks;

import org.terasology.nui.Color;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.Task;

public class PlantTask extends Task {

    private static final int HEALTH = 10;
    private static final int INTELLIGENCE = 0;
    private static final int STRENGTH = 0;
    private static final int HUNGER = 60;

    private static final float DURATION = 10;
    private static final Color COLOR = Color.GREEN.alterAlpha(90);

    public String cropToPlant;

    public PlantTask(String cropBlockURI) {
        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.taskColor = COLOR;
        this.assignedTaskType = AssignedTaskType.PLANT;

        this.cropToPlant = cropBlockURI;
    }
}
