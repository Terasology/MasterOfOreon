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
package org.terasology.taskSystem.tasks;

import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.Task;

public class SleepTask extends Task {
    private static final int HEALTH = -20;
    private static final int INTELLIGENCE = 0;
    private static final int STRENGTH = 0;
    private static final int HUNGER = -20;

    private static final float DURATION = 20;

    public SleepTask() {
        this.health = HEALTH;
        this.intelligence = INTELLIGENCE;
        this.strength = STRENGTH;
        this.hunger = HUNGER;

        this.taskDuration = DURATION;
        this.assignedTaskType = AssignedTaskType.SLEEP;
        this.isAdvanced = true;
    }
}
