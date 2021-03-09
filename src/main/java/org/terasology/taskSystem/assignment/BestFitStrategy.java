/*
 * Copyright 2019 MovingBlocks
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
