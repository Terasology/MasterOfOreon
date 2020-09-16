// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.tasks;

import org.terasology.masteroforeon.taskSystem.AssignedTaskType;
import org.terasology.masteroforeon.taskSystem.Task;

import java.util.List;

public class ResearchTask extends Task {
    private static final int MIN_INTELLIGENCE = 20;

    private static final int HEALTH = 10;
    private static final int INTELLIGENCE = 10;
    private static final int STRENGTH = 20;
    private static final int HUNGER = 30;

    private static final float DURATION = 100;

    public ResearchTask(List<String> requiredBlocks, String blockResult) {
        this.minimumAttributes.intelligence = MIN_INTELLIGENCE;
        this.recommendedAttributes.intelligence = MIN_INTELLIGENCE;

        this.attributeChanges.health = HEALTH;
        this.attributeChanges.intelligence = INTELLIGENCE;
        this.attributeChanges.strength = STRENGTH;
        this.attributeChanges.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.RESEARCH;

        this.requiredBlocks = requiredBlocks;
        this.blockResult = blockResult;
    }
}
