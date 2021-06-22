// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.resources.action;

import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.nui.properties.TextField;
import org.terasology.resources.system.BuildingResourceSystem;
import org.terasology.resources.system.ResourceSystem;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.TaskStatusType;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.tasks.GetBlocksFromChestTask;
import org.terasology.taskSystem.tasks.PlaceBlocksInChestTask;

import java.util.List;

@BehaviorAction(name = "check_required_resources")
public class CheckRequiredResourcesNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(CheckRequiredResourcesNode.class);

    @TextField
    public String checkIn;

    @In
    private Context context;

    @In
    private EntityManager entityManager;

    private InventoryManager inventoryManager;
    private BlockEntityRegistry blockEntityRegistry;
    private ResourceSystem buildingResourceSystem;
    private TaskManagementSystem taskManagementSystem;

    @Override
    public void construct(Actor actor) {
        blockEntityRegistry = context.get(BlockEntityRegistry.class);
        inventoryManager = context.get(InventoryManager.class);
        taskManagementSystem = context.get(TaskManagementSystem.class);

        buildingResourceSystem = new BuildingResourceSystem();
        buildingResourceSystem.initialize(blockEntityRegistry, inventoryManager);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        TaskComponent taskComponent = actor.getComponent(TaskComponent.class);
        if (checkIn.equals("building")) {
            EntityRef building = entityManager.getEntity(taskComponent.task.requiredBuildingEntityID);

            boolean resourceDeducted = false;
            for (String requiredResource : taskComponent.task.requiredBlocks) {
                resourceDeducted = buildingResourceSystem.checkForAResource(building, requiredResource, 1);

                if (!resourceDeducted) {
                    // Free the Oreon because the required resources not found in building and add task to holding if not an advanced task
                    if (!taskComponent.task.isAdvanced) {
                        taskManagementSystem.abandonTask(actor.getEntity());
                    } else {
                        taskComponent.assignedTaskType = AssignedTaskType.NONE;
                        taskComponent.task = new Task();
                    }
                    logger.info("Can't find a building with the required resources. Abandoning task");

                    checkForResourceInStorage(actor, requiredResource, building);
                    return BehaviorState.FAILURE;
                }

            }

            return BehaviorState.SUCCESS;
        }

        logger.debug("Specify a correct checkIn type. Can be building.");
        return BehaviorState.FAILURE;
    }

    /**
     * Checks if a required Resource is present in the Storage, if yes adds a task to the Holding to fetch it
     *
     * @param oreon
     * @param requiredResource
     */
    private void checkForResourceInStorage(Actor oreon, String requiredResource, EntityRef building) {
        OreonSpawnComponent spawnComponent = oreon.getComponent(OreonSpawnComponent.class);
        HoldingComponent holdingComponent = spawnComponent.parent.getComponent(HoldingComponent.class);

        List<BlockRegion> storageRegion = getStorageRegion(holdingComponent);

        // If storage building is not constructed
        if (storageRegion == null) {
            return;
        }
        EntityRef chestEntity = blockEntityRegistry.getBlockEntityAt(storageRegion.get(MooConstants.CHEST_BLOCK_INDEX).getMax(new Vector3i()));

        TaskComponent taskComponent = new TaskComponent();
        taskComponent.assignedTaskType = AssignedTaskType.GET_BLOCKS_FROM_CHEST;
        taskComponent.taskRegion = storageRegion.get(MooConstants.STORAGE_ENTRANCE_REGION);
        taskComponent.taskStatus = TaskStatusType.Available;

        Task getBlocksTask = new GetBlocksFromChestTask(requiredResource, 1, chestEntity);

        // Add place blocks into the required building as a subsequent task.
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
        EntityRef targetChestEntity = blockEntityRegistry.getBlockEntityAt(buildingComponent.boundingRegions.get(MooConstants.CHEST_BLOCK_INDEX).getMax(new Vector3i()));
        taskComponent.subsequentTask = new PlaceBlocksInChestTask(requiredResource, 1, targetChestEntity);
        taskComponent.subsequentTaskType = AssignedTaskType.PLACE_BLOCKS_IN_CHEST;
        taskComponent.subsequentTaskRegion = buildingComponent.boundingRegions.get(MooConstants.DINER_CHAIR_REGION_INDEX);

        taskComponent.task = getBlocksTask;

        oreon.save(taskComponent);

        EntityRef taskEntity = entityManager.create(taskComponent);
        taskManagementSystem.addTask(spawnComponent.parent, taskEntity);
    }

    private List<BlockRegion> getStorageRegion(HoldingComponent oreonHolding) {
        List<EntityRef> buildings = oreonHolding.constructedBuildings;
        for (EntityRef building : buildings) {
            ConstructedBuildingComponent constructedBuildingComponent = building.getComponent(ConstructedBuildingComponent.class);

            if (constructedBuildingComponent.buildingType.equals(BuildingType.Storage)) {
                return constructedBuildingComponent.boundingRegions;
            }
        }

        return null;
    }
}
