// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.holdingSystem.components;

import org.terasology.engine.network.FieldReplicateType;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;

/**
 * The component attached to a task entity which specifies the area selected for the task. All changes made to this task
 * are in the authority {@link org.terasology.taskSystem.TaskManagementSystem} so the fields replicated from the server to client.
 */
public class AssignedAreaComponent implements Component<AssignedAreaComponent> {
    @Replicate(FieldReplicateType.SERVER_TO_CLIENT)
    public BlockRegion assignedRegion = new BlockRegion(BlockRegion.INVALID);

    @Replicate
    public String assignedTaskType = AssignedTaskType.NONE;

    @Replicate
    public BuildingType buildingType = BuildingType.None;

    @Override
    public void copyFrom(AssignedAreaComponent other) {
        this.assignedRegion.set(other.assignedRegion);
        this.assignedTaskType = other.assignedTaskType;
        this.buildingType = other.buildingType;
    }
}
