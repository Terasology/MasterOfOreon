// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem;

import org.terasology.nui.Color;

import java.util.ArrayList;
import java.util.List;

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
