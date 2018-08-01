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
package org.terasology.buildings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.*;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.characters.CharacterHeldItemComponent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.TaskStatusType;
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

    private TaskManagementSystem taskMangementSystem;
    private ConstructingFromStructureTemplate constructingFromStructureTemplate;
    private StructureTemplateProvider structureTemplateProvider;

    private EntityRef buildingToUpgrade;

    @Override
    public void postBegin() {
        taskMangementSystem = context.get(TaskManagementSystem.class);
        structureTemplateProvider = context.get(StructureTemplateProvider.class);

        constructingFromStructureTemplate = new ConstructingFromStructureTemplate(structureTemplateProvider, localPlayer.getCharacterEntity());
    }

    @ReceiveEvent
    public void onActivateEvent(ActivateEvent event, EntityRef entityRef) {
        logger.info("event received activate");
        EntityRef player = event.getInstigator();
        CharacterHeldItemComponent heldItemComponent = player.getComponent(CharacterHeldItemComponent.class);

        EntityRef selectedItem = heldItemComponent.selectedItem;
        DisplayNameComponent displayNameComponent = selectedItem.getComponent(DisplayNameComponent.class);

        if (displayNameComponent == null || !displayNameComponent.name.equals(Constants.UPGRADE_TOOL_NAME)) {
            return;
        }

        Vector3f hitBlockPos = event.getTargetLocation();

        checkIfPartOfBuilding(hitBlockPos, player);
    }

    /**
     * Checks if the block activated using the upgrade tool is part of a building
     * @param blockPos The position of the block to be checked
     * @param player The entity which sends the event
     */
    private void checkIfPartOfBuilding(Vector3f blockPos, EntityRef player) {
        HoldingComponent holdingComponent = player.getComponent(HoldingComponent.class);
        List<EntityRef> buildings = holdingComponent.constructedBuildings;

        for (EntityRef building : buildings) {
            ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
            List<Region3i> buildingRegions = buildingComponent.boundingRegions;

            for (Region3i buildingRegion : buildingRegions) {
                Vector3i target = new Vector3i(blockPos);
                if (buildingRegion.encompasses(target)) {
                    logger.info("hit building" + buildingComponent.buildingType);
                    this.buildingToUpgrade = building;
                    building.send(new OpenUpgradeScreenEvent());
                }
            }
        }
    }

    public EntityRef getBuildingToUpgrade() {
        return buildingToUpgrade;
    }

    /**
     * Receives the {@link UpgradeBuildingEvent} triggered by the Upgrade Button on the BuildingUpgradeScreen and adds a
     * upgrade task to the holding
     * @param upgradeBuildingEvent The event received
     */
    @ReceiveEvent
    public void onReceiveBuildingUpgradeEvent(UpgradeBuildingEvent upgradeBuildingEvent, EntityRef player) {
        buildingToUpgrade.send(new CloseUpgradeScreenEvent());
        logger.info("adding building upgrade task");

        ConstructedBuildingComponent buildingComponent = buildingToUpgrade.getComponent(ConstructedBuildingComponent.class);

        TaskComponent taskComponent = new TaskComponent();
        taskComponent.assignedTaskType = AssignedTaskType.Upgrade;
        // TODO: Assign a random region or a region based on blocks to be upgraded
        taskComponent.taskRegion = buildingComponent.boundingRegions.get(Constants.DINER_CHAIR_REGION_INDEX);
        taskComponent.task = new BuildingUpgradeTask(buildingToUpgrade);
        taskComponent.taskCompletionTime = taskMangementSystem.getTaskCompletionTime(taskComponent.task);
        EntityRef task = entityManager.create(taskComponent);

        taskMangementSystem.addTask(player, task);
    }

    /**
     * This method handles the construction of the upgraded version of a building. The event is sent by {@link PerformTaskNode}
     * after the Oreon has completed an Upgrade task.
     *
     * @param event The upgrade event sent.
     * @param oreon The Oreon entity which completed the task.
     * @param taskComponent The task component attached to the Oreon.
     */
    @ReceiveEvent(components = {TaskComponent.class}, priority = EventPriority.PRIORITY_HIGH)
    public void onUpgradeStart(BuildingUpgradeStartEvent event, EntityRef oreon, TaskComponent taskComponent) {
        logger.info("upgrade event start");
        EntityRef building = entityManager.getEntity(taskComponent.task.requiredBuildingEntityID);
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

        buildingComponent.currentLevel += 1;
        building.saveComponent(buildingComponent);
        constructingFromStructureTemplate.constructBuilding(buildingComponent.centerLocation, buildingComponent.buildingType, buildingComponent.currentLevel);
    }

    @ReceiveEvent
    public void addGuardTask(GuardBuildingEvent event, EntityRef player) {
        TaskComponent taskComponent = new TaskComponent();
        taskComponent.assignedTaskType = AssignedTaskType.Guard;

        ConstructedBuildingComponent buildingComponent = buildingToUpgrade.getComponent(ConstructedBuildingComponent.class);
        taskComponent.taskRegion = buildingComponent.boundingRegions.get(Constants.LABORATORY_SLAB_REGION);
        taskComponent.taskStatus = TaskStatusType.Available;

        taskComponent.task = new GuardTask();
        taskComponent.taskCompletionTime = taskMangementSystem.getTaskCompletionTime(taskComponent.task);

        EntityRef taskEntity = entityManager.create(taskComponent);
        taskMangementSystem.addTask(player, taskEntity);
    }
}
