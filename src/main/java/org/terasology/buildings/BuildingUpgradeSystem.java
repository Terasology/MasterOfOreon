// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings;

import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.BuildingUpgradeStartEvent;
import org.terasology.buildings.events.CloseUpgradeScreenEvent;
import org.terasology.buildings.events.GuardBuildingEvent;
import org.terasology.buildings.events.OpenUpgradeScreenEvent;
import org.terasology.buildings.events.UpgradeBuildingEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.CharacterHeldItemComponent;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.TaskStatusType;
import org.terasology.taskSystem.actions.PerformTaskNode;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.taskCompletion.ConstructingFromStructureTemplate;
import org.terasology.taskSystem.tasks.BuildingUpgradeTask;
import org.terasology.taskSystem.tasks.GuardTask;

import java.util.List;

@Share(BuildingUpgradeSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class BuildingUpgradeSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(BuildingUpgradeSystem.class);

    @In
    private Context context;

    @In
    private EntityManager entityManager;

    @In
    private LocalPlayer localPlayer;

    private TaskManagementSystem taskManagementSystem;
    private ConstructingFromStructureTemplate constructingFromStructureTemplate;
    private StructureTemplateProvider structureTemplateProvider;

    private EntityRef buildingToUpgrade;

    @Override
    public void postBegin() {
        taskManagementSystem = context.get(TaskManagementSystem.class);
        structureTemplateProvider = context.get(StructureTemplateProvider.class);

        constructingFromStructureTemplate = new ConstructingFromStructureTemplate(structureTemplateProvider,
                localPlayer.getCharacterEntity());
    }

    @ReceiveEvent
    public void onActivateEvent(ActivateEvent event, EntityRef entityRef) {
        EntityRef player = event.getInstigator();
        CharacterHeldItemComponent heldItemComponent = player.getComponent(CharacterHeldItemComponent.class);

        EntityRef selectedItem = heldItemComponent.selectedItem;
        DisplayNameComponent displayNameComponent = selectedItem.getComponent(DisplayNameComponent.class);

        if (displayNameComponent == null || !displayNameComponent.name.equals(MooConstants.UPGRADE_TOOL_NAME)) {
            return;
        }

        Vector3f hitBlockPos = event.getTargetLocation();

        checkIfPartOfBuilding(hitBlockPos, player);
    }

    /**
     * Checks if the block activated using the upgrade tool is part of a building
     *
     * @param blockPos The position of the block to be checked
     * @param player The entity which sends the event
     */
    private void checkIfPartOfBuilding(Vector3f blockPos, EntityRef player) {
        HoldingComponent holdingComponent = player.getComponent(HoldingComponent.class);
        List<EntityRef> buildings = holdingComponent.constructedBuildings;

        for (EntityRef building : buildings) {
            ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
            List<BlockRegion> buildingRegions = buildingComponent.boundingRegions;

            final Vector3i target = new Vector3i(blockPos, RoundingMode.FLOOR);
            for (BlockRegion buildingRegion : buildingRegions) {
                if (buildingRegion.contains(target)) {
                    this.buildingToUpgrade = building;
                    holdingComponent.lastBuildingInteractedWith = building;
                    player.saveComponent(holdingComponent);

                    building.send(new OpenUpgradeScreenEvent());
                }
            }
        }
    }

    public EntityRef getBuildingToUpgrade() {
        return buildingToUpgrade;
    }

    /**
     * Receives the {@link UpgradeBuildingEvent} triggered by the Upgrade Button on the BuildingUpgradeScreen and adds a upgrade task to the
     * holding
     *
     * @param upgradeBuildingEvent The event received
     */
    @ReceiveEvent
    public void onReceiveBuildingUpgradeEvent(UpgradeBuildingEvent upgradeBuildingEvent, EntityRef player) {
        buildingToUpgrade.send(new CloseUpgradeScreenEvent());

        ConstructedBuildingComponent buildingComponent = buildingToUpgrade.getComponent(ConstructedBuildingComponent.class);

        TaskComponent taskComponent = new TaskComponent();
        taskComponent.assignedTaskType = AssignedTaskType.UPGRADE;
        // TODO: Assign a random region or a region based on blocks to be upgraded

        switch (buildingComponent.buildingType) {
            case Diner:
                taskComponent.taskRegion = buildingComponent.boundingRegions.get(MooConstants.DINER_CHAIR_REGION_INDEX);
                break;
            case Jail:
                taskComponent.taskRegion = buildingComponent.boundingRegions.get(MooConstants.WALL_OF_JAIL_REGION);
                break;
            case Storage:
                taskComponent.taskRegion = buildingComponent.boundingRegions.get(MooConstants.STORAGE_ENTRANCE_REGION);
                break;
            case Laboratory:
                taskComponent.taskRegion = buildingComponent.boundingRegions.get(MooConstants.LABORATORY_SLAB_REGION);
        }

        taskComponent.task = new BuildingUpgradeTask(buildingToUpgrade);

        taskComponent.taskCompletionTime = taskManagementSystem.getTaskCompletionTime(taskComponent.task);
        EntityRef task = entityManager.create(taskComponent);

        taskManagementSystem.addTask(player, task);
    }

    /**
     * This method handles the construction of the upgraded version of a building. The event is sent by {@link PerformTaskNode} after the
     * Oreon has completed an Upgrade task.
     *
     * @param event The upgrade event sent.
     * @param oreon The Oreon entity which completed the task.
     * @param taskComponent The task component attached to the Oreon.
     */
    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent(components = TaskComponent.class)
    public void onUpgradeStart(BuildingUpgradeStartEvent event, EntityRef oreon, TaskComponent taskComponent) {
        EntityRef building = entityManager.getEntity(taskComponent.task.requiredBuildingEntityID);
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

        buildingComponent.currentLevel += 1;
        building.saveComponent(buildingComponent);
        constructingFromStructureTemplate.constructBuilding(buildingComponent.centerLocation, buildingComponent.buildingType,
                buildingComponent.currentLevel, building, localPlayer.getCharacterEntity());

        taskManagementSystem.addBuildingToHolding(
                constructingFromStructureTemplate.getBuildingConstructionStartedEvent(
                        buildingComponent.centerLocation,
                        buildingComponent.buildingType,
                        building),
                localPlayer.getCharacterEntity());
    }

    @ReceiveEvent
    public void addGuardTask(GuardBuildingEvent event, EntityRef player) {
        TaskComponent taskComponent = new TaskComponent();
        taskComponent.assignedTaskType = AssignedTaskType.GUARD;

        ConstructedBuildingComponent buildingComponent = buildingToUpgrade.getComponent(ConstructedBuildingComponent.class);
        taskComponent.taskRegion = buildingComponent.boundingRegions.get(MooConstants.LABORATORY_SLAB_REGION);
        taskComponent.taskStatus = TaskStatusType.Available;

        taskComponent.task = new GuardTask(buildingToUpgrade.getId());
        taskComponent.taskCompletionTime = taskManagementSystem.getTaskCompletionTime(taskComponent.task);

        EntityRef taskEntity = entityManager.create(taskComponent);
        taskManagementSystem.addTask(player, taskEntity);
    }
}
