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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.BuildingConstructionCompletedEvent;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.HoldingAuthoritySystem;
import org.terasology.holdingSystem.components.AssignedAreaComponent;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.characters.CharacterHeldItemComponent;
import org.terasology.logic.characters.events.HorizontalCollisionEvent;
import org.terasology.logic.chat.ChatMessageEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.network.ColorComponent;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.Color;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.events.CloseTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.OpenTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.SetTaskTypeEvent;
import org.terasology.world.selection.BlockSelectionComponent;

import java.math.RoundingMode;
import java.util.List;
import java.util.Queue;

/**
 * The authority task management system which handles task creation, adding them to the correct Holding and assigning tasks
 * to the Oreons.
 */
@Share(TaskManagementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class TaskManagementSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private Time timer;

    @In
    private Context context;

    private HoldingAuthoritySystem holdingSystem;

    private EntityRef taskEntity;
    private EntityRef notificationMessageEntity;

    private Vector3f lastCollisionLocation;

    @Override
    public void postBegin() {
        notificationMessageEntity = entityManager.create(Constants.NOTIFICATION_MESSAGE_PREFAB);

        DisplayNameComponent displayNameComponent = notificationMessageEntity.getComponent(DisplayNameComponent.class);
        displayNameComponent.name = "Task System";

        ColorComponent colorComponent = notificationMessageEntity.getComponent(ColorComponent.class);
        colorComponent.color = Color.BLACK;

        notificationMessageEntity.saveComponent(displayNameComponent);
        notificationMessageEntity.saveComponent(colorComponent);

        holdingSystem = context.get(HoldingAuthoritySystem.class);
    }

    public boolean getTaskForOreon(Actor oreon) {
        HoldingComponent oreonHolding = holdingSystem.getOreonHolding(oreon);
        Queue<EntityRef> availableTasks = oreonHolding.availableTasks;
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        logger.debug("Looking for task in " + oreonHolding);

        if (!availableTasks.isEmpty()) {
            EntityRef taskEntityToAssign = availableTasks.remove();
            TaskComponent taskComponentToAssign = taskEntityToAssign.getComponent(TaskComponent.class);

            oreonTaskComponent.assignedTaskType = taskComponentToAssign.assignedTaskType;
            oreonTaskComponent.buildingType = taskComponentToAssign.buildingType;
            oreonTaskComponent.creationTime = taskComponentToAssign.creationTime;
            oreonTaskComponent.taskRegion = taskComponentToAssign.taskRegion;
            oreonTaskComponent.taskStatus = TaskStatusType.InProgress;
            oreonTaskComponent.assignedAreaIndex = taskComponentToAssign.assignedAreaIndex;
            oreonTaskComponent.taskCompletionTime = getTaskCompletionTime(oreonTaskComponent.assignedTaskType);
            oreonTaskComponent.buildingToUpgrade = taskComponentToAssign.buildingToUpgrade;

            oreon.save(oreonTaskComponent);

            setOreonTarget(oreon, oreonTaskComponent.taskRegion.min());

            //destroy the entity since this task is no longer required
            taskEntityToAssign.destroy();

            return true;
        }

        return false;
    }

    /**
     * Checks if the item used for selection of an area is an Oreon Selection Tool
     * @param player
     * @return True - If held item is an Oreon Selection Tool
     */
    private boolean checkHeldItem(EntityRef player) {
        CharacterHeldItemComponent heldItemComponent = player.getComponent(CharacterHeldItemComponent.class);

        EntityRef selectedItem = heldItemComponent.selectedItem;

        String selectedItemName = selectedItem.getComponent(DisplayNameComponent.class).name;

        if (selectedItemName.equals("Oreon Selection Tool")) {
            return true;
        }

        return false;
    }

    /**
     * Receives the {@link ApplyBlockSelectionEvent} which is sent after a block selection end point is set. Also checks
     * if the item used for selection is an Oreon Selection Tool, if not the area is intended for another purpose.
     * @param blockSelectionEvent Event triggered after a block selection has been completed
     * @param player The player entity which triggers the event
     */
    @ReceiveEvent
    public void receiveNewTask(ApplyBlockSelectionEvent blockSelectionEvent, EntityRef player) {
        //check if held item is an Oreon Selection Tool
        if (!checkHeldItem(player)) {
            String message = "Use the Oreon selection Tool to mark areas for a task";
            player.getOwner().send(new ChatMessageEvent(message, notificationMessageEntity));
            return;
        }

        logger.info("Adding a new Task");
        TaskComponent taskComponent = new TaskComponent();
        taskComponent.taskRegion = blockSelectionEvent.getSelection();
        taskComponent.creationTime = timer.getGameTimeInMs();

        BlockSelectionComponent newBlockSelectionComponent = new BlockSelectionComponent();
        logger.info("Block selection true " + newBlockSelectionComponent);
        newBlockSelectionComponent.shouldRender = true;
        newBlockSelectionComponent.currentSelection = taskComponent.taskRegion;

        //check if this area can be used
        if (!checkArea(newBlockSelectionComponent)) {
            return;
        }

        NetworkComponent networkComponent = new NetworkComponent();
        networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

        taskEntity = entityManager.create(networkComponent);
        taskEntity.addComponent(newBlockSelectionComponent);
        taskEntity.addComponent(taskComponent);

        player.send(new OpenTaskSelectionScreenEvent());
    }

    /**
     * Adds task to the corresponding player's Holding
     * @param player The player entity which owns the Holding Component
     */
    private void addTask(EntityRef player) {
        HoldingComponent oreonHolding = player.getComponent(HoldingComponent.class);

        TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);

        logger.info("Adding task to " + oreonHolding);
        player.getOwner().send(new ChatMessageEvent("Adding a new task of type : " + taskComponent.assignedTaskType, notificationMessageEntity));
        oreonHolding.availableTasks.add(taskEntity);
        player.saveComponent(oreonHolding);
    }

    /**
     * Adds task to the player's holding.
     * This method can be used by external systems to add tasks to the holding.
     * @param player The player entity which has the holding
     * @param task The task entity to be added
     */
    public void addTask(EntityRef player, EntityRef task) {
        this.taskEntity = task;
        addTask(player);
    }
    /**
     * Receives the {@link SetTaskTypeEvent} sent by the {@link org.terasology.taskSystem.nui.TaskSelectionScreenLayer}
     * after the player assigns a task a selected area.
     * @param event The event sent by the screen layer.
     * @param player The player entity adding the new task.
     */
    @ReceiveEvent
    public void receiveSetTaskTypeEvent(SetTaskTypeEvent event, EntityRef player) {
        player.send(new CloseTaskSelectionScreenEvent());

        String newTaskType = event.getTaskType();

        BlockSelectionComponent newBlockSelectionComponent = taskEntity.getComponent(BlockSelectionComponent.class);

        //when cancel selection button is used
        if (newTaskType == null) {
            taskEntity.destroy();
            return;
        }

        TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);

        taskComponent.assignedTaskType = newTaskType;

        if (newTaskType.equals(AssignedTaskType.Build)) {
            taskComponent.buildingType = event.getBuildingType();
        }
        //mark this area so that no other task can be assigned here
        markArea(newBlockSelectionComponent, taskComponent, player);

        taskEntity.saveComponent(taskComponent);

        addTask(player);
    }

    /**
     * Saves the selected area for a particular task to check for clashes later.
     * Attaches a {@link BlockSelectionComponent} to the assignedArea entity so that the assigned area remains colored
     * until the task is finished.
     * @param blockSelectionComponent The component which has information related to the area selected.
     */
    private void markArea(BlockSelectionComponent blockSelectionComponent, TaskComponent taskComponent, EntityRef player) {
        AssignedAreaComponent assignedAreaComponent = new AssignedAreaComponent();

        assignedAreaComponent.assignedRegion = blockSelectionComponent.currentSelection;

        assignedAreaComponent.assignedTaskType = taskComponent.assignedTaskType;
        assignedAreaComponent.buildingType = taskComponent.buildingType;

        EntityRef assignedArea = entityManager.create(assignedAreaComponent, blockSelectionComponent);

        HoldingComponent oreonHolding = player.getComponent(HoldingComponent.class);
        oreonHolding.assignedAreas.add(assignedArea);

        player.saveComponent(oreonHolding);

        logger.info("Adding new area to index : " + oreonHolding.assignedAreas.size() + " " + oreonHolding);
        taskComponent.assignedAreaIndex = oreonHolding.assignedAreas.size() - 1;
    }

    /**
     * Checks if the selected area can be used i.e not already assigned to some other task
     * @param blockSelectionComponent The component which has information related to the area selected.
     * @return A boolean value specifying whether the area is valid
     */
    private boolean checkArea(BlockSelectionComponent blockSelectionComponent) {
        return true;
    }

    /**
     * Looks for a building in the assignedAreas list.
     * @param buildingType The type of the building required by the Oreon
     * @return Returns a target for the Oreon to go to.
     */
    private Vector3i findRequiredBuilding(BuildingType buildingType, TaskComponent oreonTaskComponent, HoldingComponent oreonHolding) {
        List<EntityRef> buildings = oreonHolding.constructedBuildings;
        for (EntityRef building : buildings) {
            ConstructedBuildingComponent constructedBuildingComponent = building.getComponent(ConstructedBuildingComponent.class);

            if (constructedBuildingComponent.buildingType.equals(buildingType)) {
                oreonTaskComponent.taskRegion = constructedBuildingComponent.boundingRegions.get(86);

                return constructedBuildingComponent.boundingRegions.get(86).min();
            }
        }

        logger.info("Could not find required building");
        return null;
    }

    private void setOreonTarget(Actor oreon, Vector3i target) {
        MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);

        moveComponent.target = new Vector3f(target.x, target.y, target.z);

        logger.info("Set Oreon target to : " + moveComponent.target);

        oreon.save(moveComponent);
    }

    /**
     * Assigns advanced tasks like Eat and sleep to Oreon when it is free.
     * @param oreon The oreon Actor to which the task will be assigned
     * @param assignedTaskType The type of task to performed recieved based on priority of different tasks from its BT
     * @return A boolean value which signifies if the task was successfully assigned.
     */
    public boolean assignAdvancedTaskToOreon(Actor oreon, String assignedTaskType) {
            TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
            HoldingComponent oreonHolding = holdingSystem.getOreonHolding(oreon);

            Vector3i target = null;

            switch(assignedTaskType) {
                case AssignedTaskType.Eat :
                    target = findRequiredBuilding(BuildingType.Diner, oreonTaskComponent, oreonHolding);
                    break;

                case AssignedTaskType.Sleep :
                    Vector3f worldPosition = oreon.getComponent(LocationComponent.class).getWorldPosition();
                    target = new Vector3i(worldPosition, RoundingMode.DOWN);
                    break;

                case AssignedTaskType.Train_Strength :
                    target = findRequiredBuilding(BuildingType.Gym, oreonTaskComponent, oreonHolding);
                    break;

                case AssignedTaskType.Train_Intelligence :
                    target = findRequiredBuilding(BuildingType.Classroom, oreonTaskComponent, oreonHolding);
                    break;
            }

            // if a building required for the task like the Diner for Eat is not found
            if (target == null) {
                return false;
            }

            oreonTaskComponent.assignedTaskType = assignedTaskType;
            oreonTaskComponent.creationTime = timer.getGameTimeInMs();
            oreon.save(oreonTaskComponent);

            setOreonTarget(oreon, target);

            return true;
    }

    /**
     * Receives the HorizontalCollisionEvent and decides whether the Oreon should abandon tasks
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void receiveCollisionEvent(HorizontalCollisionEvent collisionEvent, EntityRef oreon, MinionMoveComponent moveComponent) {
        if (lastCollisionLocation == null) {
            lastCollisionLocation = collisionEvent.getLocation();
        } else {
            if (isSameCollisionLocation(lastCollisionLocation, collisionEvent.getLocation())) {
                logger.info("oreon stuck");
                moveComponent.target = null;
                oreon.saveComponent(moveComponent);
                abandonTask(oreon);
            } else {
                // If collision just took place once
                lastCollisionLocation = null;
            }
        }
    }

    private boolean isSameCollisionLocation(Vector3f lastLocation, Vector3f currentLocation) {
        float lastX = Float.floatToIntBits(lastLocation.getX());
        float lastZ = Float.floatToIntBits(lastLocation.getZ());
        float currentX = Float.floatToIntBits(currentLocation.getX());
        float currentZ = Float.floatToIntBits(currentLocation.getZ());

        return lastX == currentX && lastZ == currentZ;
    }

    private void abandonTask(EntityRef oreon) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        if (!oreonTaskComponent.assignedTaskType.equals(AssignedTaskType.None)) {
            String message = "Oreon " + oreon.getComponent(NameTagComponent.class).text + " got stuck. Abandoning task " + oreonTaskComponent.assignedTaskType;
            oreon.getComponent(OreonSpawnComponent.class).parent.getOwner().send(new ChatMessageEvent(message, notificationMessageEntity));
            // Create entity for abandoned task
            NetworkComponent networkComponent = new NetworkComponent();
            networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

            taskEntity = entityManager.create(networkComponent);

            TaskComponent taskComponent = new TaskComponent();
            taskComponent.assignedTaskType = oreonTaskComponent.assignedTaskType;
            taskComponent.assignedAreaIndex = oreonTaskComponent.assignedAreaIndex;
            taskComponent.taskRegion = oreonTaskComponent.taskRegion;
            taskComponent.creationTime = oreonTaskComponent.creationTime;
            taskComponent.buildingType = oreonTaskComponent.buildingType;
            taskComponent.taskStatus = oreonTaskComponent.taskStatus;

            taskEntity.addComponent(taskComponent);

            // Add task to the holding
            OreonSpawnComponent oreonSpawnComponent = oreon.getComponent(OreonSpawnComponent.class);
            addTask(oreonSpawnComponent.parent);

            // Free the Oreon
            oreonTaskComponent.assignedTaskType = AssignedTaskType.None;

            oreon.saveComponent(oreonTaskComponent);
        }
    }

    /**
     * Calculates the time at which the assigned task will be completed based on the assigned task type and current game
     * time.
     * @param assignedTaskType The type of task that is being assigned to the Oreon
     * @return The time at which the task will be completed
     */
    public float getTaskCompletionTime(String assignedTaskType) {
        float currentTime = timer.getGameTime();

        switch(assignedTaskType) {
            case AssignedTaskType.Plant :
                return currentTime + 50;

            default :
                return currentTime + 10;
        }
    }

    @ReceiveEvent
    public void addBuildingToHolding(BuildingConstructionCompletedEvent constructionCompletedEvent, EntityRef player) {
        ConstructedBuildingComponent constructedBuildingComponent = new ConstructedBuildingComponent();
        constructedBuildingComponent.boundingRegions = constructionCompletedEvent.absoluteRegions;
        constructedBuildingComponent.buildingType = constructionCompletedEvent.buildingType;
        constructedBuildingComponent.centerLocation = constructionCompletedEvent.centerBlockPosition;

        EntityRef buildingEntity = entityManager.create(constructedBuildingComponent);

        NetworkComponent networkComponent = new NetworkComponent();
        networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

        buildingEntity.addComponent(networkComponent);

        HoldingComponent holdingComponent = player.getComponent(HoldingComponent.class);
        holdingComponent.constructedBuildings.add(buildingEntity);

        constructionCompletedEvent.consume();
    }
}
