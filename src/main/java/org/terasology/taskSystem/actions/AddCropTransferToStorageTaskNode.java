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
package org.terasology.taskSystem.actions;

import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.network.ColorComponent;
import org.terasology.registry.In;
import org.terasology.nui.Color;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.DelayedNotificationSystem;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.tasks.HarvestTask;
import org.terasology.taskSystem.tasks.PlaceBlocksInChestTask;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;

import java.util.List;

@BehaviorAction(name = "add_crop_transfer_to_storage_task")
public class AddCropTransferToStorageTaskNode extends BaseAction {

    @In
    private BlockManager blockManager;

    @In
    private Context context;

    @In
    private EntityManager entityManager;

    private BlockEntityRegistry blockEntityRegistry;
    private EntityRef notificationMessageEntity;
    private DelayedNotificationSystem  delayedNotificationSystem;

    @Override
    public void construct(Actor oreon) {
        blockEntityRegistry = context.get(BlockEntityRegistry.class);
        delayedNotificationSystem = context.get(DelayedNotificationSystem.class);

        notificationMessageEntity = entityManager.create(MooConstants.NOTIFICATION_MESSAGE_PREFAB);

        DisplayNameComponent displayNameComponent = notificationMessageEntity.getComponent(DisplayNameComponent.class);
        displayNameComponent.name = "Oreons";

        ColorComponent colorComponent = notificationMessageEntity.getComponent(ColorComponent.class);
        colorComponent.color = Color.BLACK;

        notificationMessageEntity.saveComponent(displayNameComponent);
        notificationMessageEntity.saveComponent(colorComponent);
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        List<Region3i> storageBuildingRegions = getStorageBuildingRegion(oreon);

        // Abandon task if storage not found
        if (storageBuildingRegions == null) {
            String message = "Build a Storage for the harvested crops";
            delayedNotificationSystem.sendNotificationNow(message, notificationMessageEntity);

            oreonTaskComponent.assignedTaskType = AssignedTaskType.NONE;
            oreon.save(oreonTaskComponent);

            return BehaviorState.FAILURE;
        }

        Region3i plantRegion = oreonTaskComponent.taskRegion;

        HarvestTask harvestTask = (HarvestTask) oreonTaskComponent.task;

        // Calculate number of crop blocks harvested from the area selected
        harvestTask.numberOfCropBlocksHarvested = plantRegion.sizeX() * plantRegion.sizeZ();

        // Get the type of crop harvested
        EntityRef plantBlockEntity = blockEntityRegistry.getBlockEntityAt(new Vector3i(plantRegion.minX(), plantRegion.minY() + 1, plantRegion.minZ()));
        BlockComponent blockComponent = plantBlockEntity.getComponent(BlockComponent.class);
        harvestTask.harvestedCrop = blockComponent.getBlock().getURI().toString();

        Vector3i chestBlockLocation = storageBuildingRegions.get(MooConstants.CHEST_BLOCK_INDEX).min();

        oreonTaskComponent.subsequentTask = new PlaceBlocksInChestTask(harvestTask.harvestedCrop,
                harvestTask.numberOfCropBlocksHarvested,
                blockEntityRegistry.getBlockEntityAt(chestBlockLocation));
        oreonTaskComponent.subsequentTaskType = AssignedTaskType.PLACE_BLOCKS_IN_CHEST;

        removeCropBlocks(oreon);

        oreonTaskComponent.subsequentTaskRegion = storageBuildingRegions.get(MooConstants.STORAGE_ENTRANCE_REGION);
        oreon.save(oreonTaskComponent);

        return BehaviorState.SUCCESS;
    }

    private List<Region3i> getStorageBuildingRegion(Actor oreon) {
        OreonSpawnComponent oreonSpawnComponent = oreon.getComponent(OreonSpawnComponent.class);
        HoldingComponent oreonHolding = oreonSpawnComponent.parent.getComponent(HoldingComponent.class);

        List<EntityRef> buildings = oreonHolding.constructedBuildings;

        for (EntityRef building : buildings) {
            ConstructedBuildingComponent constructedBuildingComponent = building.getComponent(ConstructedBuildingComponent.class);

            if (constructedBuildingComponent.buildingType.equals(BuildingType.Storage)) {
                return constructedBuildingComponent.boundingRegions;
            }
        }

        return null;
    }

    private void removeCropBlocks(Actor oreon) {
        TaskComponent taskComponent = oreon.getComponent(TaskComponent.class);
        Region3i selectedRegion = taskComponent.taskRegion;

        int minX = selectedRegion.minX();
        int maxX = selectedRegion.maxX();
        int minZ = selectedRegion.minZ();
        int maxZ = selectedRegion.maxZ();

        int y = selectedRegion.minY();

        Block block = blockManager.getBlock(BlockManager.AIR_ID);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                blockEntityRegistry.setBlockForceUpdateEntity(new Vector3i(x, y + 1, z), block);
            }
        }
    }
}
