// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.holdingSystem.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The component which is attached to the player and consits of info related to the happenings in the village.
 * All changes made to this task are in the authority {@link org.terasology.taskSystem.TaskManagementSystem} so the fields replicated from the server to client.
 */
public class HoldingComponent implements Component<HoldingComponent> {
    @Replicate
    public Queue<EntityRef> availableTasks = new LinkedList<>();

    @Replicate
    public List<EntityRef> assignedAreas = new ArrayList<>();

    @Replicate
    public List<EntityRef> constructedBuildings = new ArrayList<>();

    @Replicate
    public EntityRef lastBuildingInteractedWith = EntityRef.NULL;
}
