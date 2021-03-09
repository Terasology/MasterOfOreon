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
package org.terasology.holdingSystem.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The component which is attached to the player and consits of info related to the happenings in the village.
 * All changes made to this task are in the authority {@link org.terasology.taskSystem.TaskManagementSystem} so the fields replicated from the server to client.
 */
public class HoldingComponent implements Component {
    @Replicate
    public Queue<EntityRef> availableTasks = new LinkedList<>();

    @Replicate
    public List<EntityRef> assignedAreas = new ArrayList<>();

    @Replicate
    public List<EntityRef> constructedBuildings = new ArrayList<>();

    @Replicate
    public EntityRef lastBuildingInteractedWith = EntityRef.NULL;
}
