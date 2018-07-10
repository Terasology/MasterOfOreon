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

import com.google.common.collect.ImmutableMap;
import org.terasology.persistence.typeHandling.*;
import org.terasology.rendering.nui.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RegisterTypeHandler
public class TaskTypeHandler extends SimpleTypeHandler<Task> {

    @Override
    public PersistedData serialize(Task task, SerializationContext context) {
        Map<String, PersistedData> dataMap = new ImmutableMap.Builder()
                .put("health", context.create(task.health))
                .put("intelligence", context.create(task.intelligence))
                .put("strength", context.create(task.strength))
                .put("hunger", context.create(task.hunger))
                .put("assignedTaskType", context.create(task.assignedTaskType))
                .put("taskDuration", context.create(task.taskDuration))
                .put("taskColor", context.create(task.taskColor.rgba()))
                .put("buildingType", context.create(task.buildingType.toString()))
                .put("requiredBuildingEntityID", context.create(task.requiredBuildingEntityID))
                .put("requiredBlocks", context.createStrings(task.requiredBlocks))
                .put("blockResult", context.create(task.blockResult))
                .build();

        return context.create(dataMap);
    }

    @Override
    public Task deserialize(PersistedData data, DeserializationContext context) {
        PersistedDataMap dataMap = data.getAsValueMap();

        Task task = new Task();
        task.health = dataMap.getAsInteger("health");
        task.intelligence = dataMap.getAsInteger("intelligence");
        task.strength = dataMap.getAsInteger("strength");
        task.hunger = dataMap.getAsInteger("hunger");
        task.assignedTaskType = dataMap.getAsString("assignedTaskType");
        task.taskDuration = dataMap.getAsFloat("taskDuration");
        task.taskColor = new Color(dataMap.getAsInteger("taskColor"));
        task.buildingType = BuildingType.valueOf(dataMap.getAsString("buildingType"));
        task.requiredBuildingEntityID = dataMap.getAsLong("requiredBuildingEntityID");

        if (dataMap.getAsArray("blocksRequired") != null) {
            List<String> blocksRequired = new ArrayList<>();
            for (PersistedData block : dataMap.getAsArray("blocksRequired")) {
                blocksRequired.add(block.getAsString());
            }
            task.requiredBlocks = blocksRequired;
        }

        task.blockResult = dataMap.getAsString("blockResult");

        return task;
    }
}
