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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.buildings.events.BuildingUpgradeStartEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.holdingSystem.components.AssignedAreaComponent;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.math.Region3i;
import org.terasology.registry.In;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.taskCompletion.ConstructingFromBuildingGenerator;
import org.terasology.taskSystem.taskCompletion.ConstructingFromStructureTemplate;
import org.terasology.taskSystem.taskCompletion.PlantingTaskCompletion;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;
import org.terasology.world.selection.BlockSelectionComponent;

import java.util.List;


/**
 * Handles the actual task and its after effects like removal of the area render and changes to the Oreon attributes.
 */
@BehaviorAction(name = "perform_task")
public class PerformTaskNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(PerformTaskNode.class);

    @In
    private Context context;

    @In
    private BlockManager blockManager;

    private WorldProvider worldProvider;
    private BlockEntityRegistry blockEntityRegistry;
    private StructureTemplateProvider structureTemplateProvider;

    private PlantingTaskCompletion plantingTaskCompletion;
    private ConstructingFromStructureTemplate constructingFromStructureTemplate;
    private ConstructingFromBuildingGenerator constructingFromBuildingGenerator;

    @Override
    public void construct(Actor oreon) {
        worldProvider = context.get(WorldProvider.class);
        structureTemplateProvider = context.get(StructureTemplateProvider.class);
        blockEntityRegistry = context.get(BlockEntityRegistry.class);

        this.plantingTaskCompletion = new PlantingTaskCompletion(blockManager, blockEntityRegistry);

        EntityRef player = oreon.getComponent(OreonSpawnComponent.class).parent;
        this.constructingFromStructureTemplate = new ConstructingFromStructureTemplate(structureTemplateProvider, player);

        //this.constructingFromBuildingGenerator = new ConstructingFromBuildingGenerator(worldProvider, blockManager);
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
        logger.info("Perfoming Task of type : " + oreonTaskComponent.assignedTaskType);

        removeColorFromArea(oreon, oreonTaskComponent);

        changeOreonAttributes(oreon, oreonTaskComponent);

        completeTask(oreon, oreonTaskComponent);

        // Free the Oreon after performing task
        oreonTaskComponent.assignedTaskType = AssignedTaskType.None;
        oreon.save(oreonTaskComponent);

        logger.info("Task completed, the Oreon is now free!");

        return BehaviorState.SUCCESS;
    }

    /**
     * Removes the {@link BlockSelectionComponent} from the assigned area so that it no longer renders once the task is complete.
     * @param actor The Actor which calls this node
     */
    private void removeColorFromArea(Actor oreon, TaskComponent taskComponent) {
        OreonSpawnComponent oreonSpawnComponent = oreon.getComponent(OreonSpawnComponent.class);

        EntityRef player = oreonSpawnComponent.parent;

        HoldingComponent oreonHolding = player.getComponent(HoldingComponent.class);

        List<EntityRef> assignedAreas = oreonHolding.assignedAreas;
        for (EntityRef assignedArea : assignedAreas) {
            AssignedAreaComponent areaComponent = assignedArea.getComponent(AssignedAreaComponent.class);

            //check for the area where task got completed
            if (areaComponent.assignedRegion.equals(taskComponent.taskRegion)) {
                assignedAreas.remove(assignedArea);
                assignedArea.destroy();
                break;
            }
        }
    }

    /**
     * Changes a Oreon's attributes values after it completes a task.
     * @param oreon The Actor which calls this node
     */
    private void changeOreonAttributes(Actor oreon, TaskComponent taskComponent) {
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        String assignedTaskType = taskComponent.assignedTaskType;
        switch(assignedTaskType) {
            case AssignedTaskType.Eat:
                logger.info("eating task complete hunger {} to zero ", oreonAttributeComponent.hunger);
                oreonAttributeComponent.hunger = 0;
                break;

            case AssignedTaskType.Train_Strength:
                oreonAttributeComponent.strength += 10;
                if (oreonAttributeComponent.strength > oreonAttributeComponent.maxStrength) {
                    oreonAttributeComponent.strength = oreonAttributeComponent.maxStrength;
                }
                logger.info("Strength training complete, strength is now : {}", oreonAttributeComponent.strength);
                break;

            case AssignedTaskType.Train_Intelligence:
                oreonAttributeComponent.intelligence += 10;
                if (oreonAttributeComponent.intelligence > oreonAttributeComponent.maxIntelligence) {
                    oreonAttributeComponent.intelligence = oreonAttributeComponent.maxIntelligence;
                }
                logger.info("Intelligence training complete, intelligence is now : {}", oreonAttributeComponent.intelligence);
                break;

            case AssignedTaskType.Sleep:
                oreonAttributeComponent.health = 100;
                break;

            default:
                oreonAttributeComponent.hunger += 30;
        }

        oreon.save(oreonAttributeComponent);
    }

    /**
     * Places the required blocks in the selected area based on the task selected
     * @param oreon The Oreon entity working on the task
     * @param taskComponent The component with task information which just completed
     */
    private void completeTask(Actor oreon, TaskComponent taskComponent) {
        Region3i selectedRegion = taskComponent.taskRegion;
        String taskType = taskComponent.assignedTaskType;


        switch (taskType) {
            case AssignedTaskType.Plant :
                plantingTaskCompletion.placeCrops(selectedRegion, Constants.OREON_CROP_0_BLOCK);
                break;

            case AssignedTaskType.Build :
                constructingFromStructureTemplate.constructBuilding(selectedRegion, taskComponent.buildingType);
                //constructingFromBuildingGenerator.constructBuilding(selectedRegion, taskComponent.buildingType);
                break;

            case AssignedTaskType.Upgrade :
                oreon.getEntity().send(new BuildingUpgradeStartEvent());
        }
    }
}
