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
