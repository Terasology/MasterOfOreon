// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.assignment;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.taskSystem.OreonAttributes;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.components.TaskComponent;

import java.util.Queue;

public class BestFitStrategy implements AssignmentStrategy {
    @Override
    public EntityRef getBestTask(OreonAttributeComponent attributes, Queue<EntityRef> tasks) {
        int bestScore = Integer.MAX_VALUE;
        EntityRef bestTask = null;
        if (!tasks.isEmpty()) {
            while (!tasks.isEmpty()) {
                EntityRef taskEntity = tasks.remove();
                Task task = taskEntity.getComponent(TaskComponent.class).task;
                int score = calculateTaskSuitability(task.recommendedAttributes, attributes);
                if (score >= 0 && score < bestScore) {
                    bestScore = score;
                    bestTask = taskEntity;
                }
            }
        }

        return bestTask;
    }

    private int calculateTaskSuitability(OreonAttributes target, OreonAttributeComponent attributes) {
        // The suitability rating is the arithmetic mean of the difference between the current value and the target value
        // Smaller scores are better
        return ((attributes.intelligence - target.intelligence) +
                (attributes.strength - target.strength) +
                (attributes.health - target.health) +
                (attributes.hunger - target.hunger)) / 4;
    }
}
