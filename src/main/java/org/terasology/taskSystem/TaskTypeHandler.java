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
import java.util.Optional;

@RegisterTypeHandler
public class TaskTypeHandler extends SimpleTypeHandler<Task> {

    @Override
    public PersistedData serializeNonNull(Task task, PersistedDataSerializer serializer) {
        Map<String, PersistedData> dataMap = new ImmutableMap.Builder()
                .put("health", serializer.serialize(task.health))
                .put("intelligence", serializer.serialize(task.intelligence))
                .put("strength", serializer.serialize(task.strength))
                .put("hunger", serializer.serialize(task.hunger))
                .put("assignedTaskType", serializer.serialize(task.assignedTaskType))
                .put("taskDuration", serializer.serialize(task.taskDuration))
                .put("taskColor", serializer.serialize(task.taskColor.rgba()))
                .put("buildingType", serializer.serialize(task.buildingType.toString()))
                .put("requiredBuildingEntityID", serializer.serialize(task.requiredBuildingEntityID))
                .put("requiredBlocks", serializer.serializeStrings(task.requiredBlocks))
                .put("blockResult", serializer.serialize(task.blockResult))
                .put("isAdvanced", serializer.serialize(task.isAdvanced))
                .put("blockToRender", serializer.serialize(task.blockToRender))
                .build();

        return serializer.serialize(dataMap);
    }

    @Override
    public Optional<Task> deserialize(PersistedData data) {
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

        if (dataMap.getAsArray("requiredBlocks") != null) {
            List<String> blocksRequired = new ArrayList<>();
            for (PersistedData block : dataMap.getAsArray("requiredBlocks")) {
                blocksRequired.add(block.getAsString());
            }
            task.requiredBlocks = blocksRequired;
        }

        task.blockResult = dataMap.getAsString("blockResult");

        task.isAdvanced = dataMap.getAsBoolean("isAdvanced");
        task.blockToRender = dataMap.getAsString("blockToRender");
        return Optional.ofNullable(task);
    }
}
