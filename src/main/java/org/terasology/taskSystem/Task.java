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
package org.terasology.taskSystem;

import org.terasology.nui.Color;

import java.util.List;
import java.util.ArrayList;

public class Task {

    // The minimum attributes required to perform a task
    public OreonAttributes minimumAttributes = new OreonAttributes();
    // Effects on different Oreon attributes after task completion
    public OreonAttributes attributeChanges = new OreonAttributes();
    // The attributes needed to perform a task efficiently
    public OreonAttributes recommendedAttributes = new OreonAttributes();

    // Task specifications
    public String assignedTaskType = AssignedTaskType.NONE;
    public float taskDuration;
    public Color taskColor = Color.MAGENTA.alterAlpha(90);
    public BuildingType buildingType = BuildingType.None;
    public long requiredBuildingEntityID;
    public List<String> requiredBlocks = new ArrayList<>();
    public String blockResult = "";

    public boolean isAdvanced;

    // URI of the block to render as an indication for the task being performed
    public String blockToRender = "";
}
