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
package org.terasology.resources.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.registry.In;
import org.terasology.rendering.nui.properties.TextField;
import org.terasology.resources.system.BuildingResourceSystem;
import org.terasology.resources.system.ResourceSystem;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.world.BlockEntityRegistry;

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

    @Override
    public void construct(Actor actor) {
        blockEntityRegistry = context.get(BlockEntityRegistry.class);
        inventoryManager = context.get(InventoryManager.class);

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
                    // Free the Oreon because the required resources not found in building
                    taskComponent.assignedTaskType = AssignedTaskType.None;
                    logger.info("Can't find a building with the required resources. Abandoning task");

                    return BehaviorState.FAILURE;
                }

            }

            return BehaviorState.SUCCESS;
        }

        logger.debug("Specify a correct checkIn type. Can be building.");
        return BehaviorState.FAILURE;
    }
}