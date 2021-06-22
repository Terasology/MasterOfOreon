// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.actions;

import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.BuildingUpgradeStartEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.selection.BlockSelectionComponent;
import org.terasology.holdingSystem.components.AssignedAreaComponent;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.research.events.ResearchStartEvent;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.structureTemplates.components.SpawnBlockRegionsComponent;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.TaskStatusType;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.taskCompletion.ConstructingFromBuildingGenerator;
import org.terasology.taskSystem.taskCompletion.ConstructingFromStructureTemplate;
import org.terasology.taskSystem.taskCompletion.PlantingTaskCompletion;
import org.terasology.taskSystem.tasks.PlantTask;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Handles the actual task and its after effects like removal of the area render and changes to the Oreon attributes.
 */
@BehaviorAction(name = "perform_task")
public class PerformTaskNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(PerformTaskNode.class);
    private static final String ADD_TASK_DELAYED_ACTION_ID = "taskManagementSystem:addTask";

    @In
    private Context context;

    @In
    private BlockManager blockManager;

    @In
    private EntityManager entityManager;

    @In
    private DelayManager delayManager;

    @In
    private TaskManagementSystem taskManagementSystem;

    private InventoryManager inventoryManager;
    private WorldProvider worldProvider;
    private BlockEntityRegistry blockEntityRegistry;
    private StructureTemplateProvider structureTemplateProvider;

    private TaskComponent currentTaskComponent;

    private PlantingTaskCompletion plantingTaskCompletion;
    private ConstructingFromStructureTemplate constructingFromStructureTemplate;
    private ConstructingFromBuildingGenerator constructingFromBuildingGenerator;

    @Override
    public void construct(Actor oreon) {
        worldProvider = context.get(WorldProvider.class);
        structureTemplateProvider = context.get(StructureTemplateProvider.class);
        blockEntityRegistry = context.get(BlockEntityRegistry.class);
        delayManager = context.get(DelayManager.class);
        inventoryManager = context.get(InventoryManager.class);
        taskManagementSystem = context.get(TaskManagementSystem.class);

        this.plantingTaskCompletion = new PlantingTaskCompletion(blockManager, blockEntityRegistry);

        EntityRef player = oreon.getComponent(OreonSpawnComponent.class).parent;
        this.constructingFromStructureTemplate = new ConstructingFromStructureTemplate(structureTemplateProvider,
                player);
        this.constructingFromStructureTemplate = new ConstructingFromStructureTemplate(structureTemplateProvider,
                player);
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
        logger.info("Perfoming Task of type : " + oreonTaskComponent.assignedTaskType);

        removeColorFromArea(oreon, oreonTaskComponent);

        changeOreonAttributes(oreon, oreonTaskComponent);

        removeRenderedBlock(oreon, oreonTaskComponent);

        completeTask(oreon, oreonTaskComponent);

        // Free the Oreon after performing task
        oreonTaskComponent.assignedTaskType = AssignedTaskType.NONE;
        oreonTaskComponent.taskStatus = TaskStatusType.Completed;
        oreon.save(oreonTaskComponent);

        logger.info("Task completed, the Oreon is now free!");

        return BehaviorState.SUCCESS;
    }

    /**
     * Removes the {@link BlockSelectionComponent} from the assigned area so that it no longer renders once the task is
     * complete.
     *
     * @param oreon The Actor which calls this node
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
     *
     * @param oreon The Actor which calls this node
     */
    private void changeOreonAttributes(Actor oreon, TaskComponent taskComponent) {
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        Task completedTask = taskComponent.task;

        oreonAttributeComponent.strength += completedTask.attributeChanges.strength;
        oreonAttributeComponent.health += completedTask.attributeChanges.health;
        oreonAttributeComponent.intelligence += completedTask.attributeChanges.intelligence;
        oreonAttributeComponent.hunger += completedTask.attributeChanges.hunger;

        oreonAttributeComponent.strength = oreonAttributeComponent.strength > oreonAttributeComponent.maxStrength
                ? oreonAttributeComponent.maxStrength : oreonAttributeComponent.strength;

        oreonAttributeComponent.health = oreonAttributeComponent.health > oreonAttributeComponent.maxHealth
                ? oreonAttributeComponent.maxHealth : oreonAttributeComponent.health;

        oreonAttributeComponent.intelligence =
                oreonAttributeComponent.intelligence > oreonAttributeComponent.maxIntelligence
                ? oreonAttributeComponent.maxIntelligence : oreonAttributeComponent.intelligence;

        oreon.save(oreonAttributeComponent);
    }

    /**
     * Places the required blocks in the selected area based on the task selected
     *
     * @param oreon The Oreon entity working on the task
     * @param taskComponent The component with task information which just completed
     */
    private void completeTask(Actor oreon, TaskComponent taskComponent) {
        BlockRegion selectedRegion = taskComponent.taskRegion;
        String taskType = taskComponent.assignedTaskType;


        switch (taskType) {
            case AssignedTaskType.PLANT:
                PlantTask task = (PlantTask) taskComponent.task;
                plantingTaskCompletion.placeCrops(selectedRegion, task.cropToPlant);
                break;

            case AssignedTaskType.BUILD:
                constructingFromStructureTemplate.constructBuilding(selectedRegion, taskComponent.task.buildingType);
                //constructingFromBuildingGenerator.constructBuilding(selectedRegion, taskComponent.buildingType);
                break;

            case AssignedTaskType.UPGRADE:
                //TODO:figure out how to get region in the right area
                oreon.getEntity().send(new BuildingUpgradeStartEvent());

                EntityRef building = entityManager.getEntity(taskComponent.task.requiredBuildingEntityID);
                ConstructedBuildingComponent buildingComponent =
                        building.getComponent(ConstructedBuildingComponent.class);

                EntityRef buildingTemplate =
                        constructingFromStructureTemplate.selectAndReturnBuilding(buildingComponent.buildingType,
                                buildingComponent.currentLevel);

                if (buildingTemplate != null) {
                    final SpawnBlockRegionsComponent blockRegionsComponent =
                            buildingTemplate.getParentPrefab().getComponent(SpawnBlockRegionsComponent.class);
                    List<BlockRegion> regionsToFill = blockRegionsComponent.regionsToFill.stream()
                            .map(regionToFill -> new BlockRegion(regionToFill.region).translate(buildingComponent.centerLocation))
                            .collect(Collectors.toList());

                    Vector3i center = new Vector3i(regionsToFill.get(0).center(new Vector3f()), RoundingMode.FLOOR);
                    BlockRegion totalRegion =
                            new BlockRegion(center.x, taskManagementSystem.minYOverall, center.z).setSize(1, 1, 1);

                    for (BlockRegion baseRegion : regionsToFill) {

                        Vector3i min = new Vector3i(baseRegion.minX(),
                                taskManagementSystem.minYOverall + baseRegion.minY(), baseRegion.minZ());
                        Vector3i max = new Vector3i(baseRegion.maxX(),
                                baseRegion.maxY() + taskManagementSystem.minYOverall, baseRegion.maxZ());
                        totalRegion.union(min).union(max);
                    }
                    taskManagementSystem.placeFenceAroundRegion(totalRegion);
                }
                break;
            case AssignedTaskType.RESEARCH:
                oreon.getEntity().send(new ResearchStartEvent());
        }

        if (taskComponent.subsequentTaskType != null) {

            TaskComponent newTaskComponent = new TaskComponent();
            newTaskComponent.assignedTaskType = taskComponent.subsequentTaskType;
            newTaskComponent.taskRegion = taskComponent.taskRegion;
            if (taskComponent.subsequentTaskRegion != null) {
                newTaskComponent.taskRegion = taskComponent.subsequentTaskRegion;
            }
            newTaskComponent.task = taskComponent.subsequentTask;
            newTaskComponent.taskStatus = TaskStatusType.Available;

            EntityRef taskEntity = entityManager.create(newTaskComponent);
            OreonSpawnComponent oreonSpawnComponent = oreon.getComponent(OreonSpawnComponent.class);
            taskEntity.setOwner(oreonSpawnComponent.parent);

            delayManager.addDelayedAction(taskEntity, ADD_TASK_DELAYED_ACTION_ID, taskComponent.delayBeforeNextTask);
        }
    }

    /**
     * Removes the block from the Oreon's inventory
     *
     * @param oreon
     * @param oreonTaskComponent
     */
    private void removeRenderedBlock(Actor oreon, TaskComponent oreonTaskComponent) {
        inventoryManager.removeItem(oreon.getEntity(), oreon.getEntity(), 0, true, 1);
    }
}
