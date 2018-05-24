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
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.AssignedAreaComponent;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.events.CloseTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.OpenTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.SetTaskTypeEvent;
import org.terasology.world.selection.BlockSelectionComponent;

import java.util.Queue;

@Share(TaskManagementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class TaskManagementSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private Time timer;

    private HoldingComponent oreonHolding;
    private String newTaskType;
    private TaskComponent taskComponent;
    private EntityRef taskEntity;

    public void setOreonHolding(HoldingComponent holding) {
        this.oreonHolding = holding;
    }

    public boolean getTaskForOreon(Actor oreon) {
        Queue<EntityRef> availableTasks = oreonHolding.availableTasks;
        logger.info("Looking for task in " + oreonHolding);
        if (!availableTasks.isEmpty()) {
            EntityRef taskEntityToAssign = availableTasks.remove();
            TaskComponent taskComponentToAssign = taskEntityToAssign.getComponent(TaskComponent.class);

            TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

            oreonTaskComponent.assignedTaskType = taskComponentToAssign.assignedTaskType;
            oreonTaskComponent.creationTime = taskComponentToAssign.creationTime;
            oreonTaskComponent.taskRegion = taskComponentToAssign.taskRegion;
            oreonTaskComponent.taskStatus = TaskStatusType.InProgress;
            oreonTaskComponent.assignedAreaIndex = taskComponentToAssign.assignedAreaIndex;

            oreon.save(oreonTaskComponent);

            MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);
            Vector3i target = oreonTaskComponent.taskRegion.min();

            moveComponent.target = new Vector3f(target.x, target.y, target.z);

            logger.info("Set Oreon target to : " + moveComponent.target);

            oreon.save(moveComponent);

            //destroy the entity since this task is no longer required
            taskEntityToAssign.destroy();

            return true;
        }

        return false;
    }

    @ReceiveEvent
    public void receiveNewTask(ApplyBlockSelectionEvent blockSelectionEvent, EntityRef player) {
        setOreonHolding(player.getComponent(HoldingComponent.class));

        logger.info("Adding a new Task");
        taskComponent = new TaskComponent();
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

        player.send(new OpenTaskSelectionScreenEvent());
    }

    private void addTask (EntityRef player) {
        if (oreonHolding == null) {
            oreonHolding = player.getComponent(HoldingComponent.class);
        }
        logger.info("Adding task to " + oreonHolding);
        oreonHolding.availableTasks.add(taskEntity);
        player.saveComponent(oreonHolding);
    }

    @ReceiveEvent
    public void receiveSetTaskTypeEvent (SetTaskTypeEvent event, EntityRef player) {
        player.send(new CloseTaskSelectionScreenEvent());

        newTaskType = event.getTaskType();

        BlockSelectionComponent newBlockSelectionComponent = taskEntity.getComponent(BlockSelectionComponent.class);

        //when cancel selection button is used
        if (newTaskType == null) {
            taskEntity.destroy();
            return;
        }

        //mark this area so that no other task can be assigned here
        markArea(newBlockSelectionComponent, player);

        taskComponent.assignedTaskType = newTaskType;

        if (newTaskType.equals(AssignedTaskType.Build)) {
            taskComponent.buildingType = event.getBuildingType();
        }

        taskEntity.addComponent(taskComponent);

        addTask(player);
    }

    /**
     * Saves the selected area for a particular task to check for clashes later.
     * Attaches a {@link BlockSelectionComponent} to the assignedArea entity so that the assigned area remains colored
     * until the task is finished.
     * @param blockSelectionComponent
     */
    private void markArea(BlockSelectionComponent blockSelectionComponent, EntityRef player) {
        AssignedAreaComponent assignedAreaComponent = new AssignedAreaComponent();

        assignedAreaComponent.assignedRegion = blockSelectionComponent.currentSelection;

        EntityRef assignedArea = entityManager.create(assignedAreaComponent, blockSelectionComponent);

        oreonHolding.assignedAreas.add(assignedArea);

        player.saveComponent(oreonHolding);

        logger.info("Adding new area to index : " + oreonHolding.assignedAreas.size() + " " + oreonHolding);
        taskComponent.assignedAreaIndex = oreonHolding.assignedAreas.size() - 1;
    }

    /**
     * Checks if the selected area can be used i.e not already assigned to some other task
     * @param blockSelectionComponent
     * @return A boolean value specifying whether the area is valid
     */
    private boolean checkArea(BlockSelectionComponent blockSelectionComponent) {
        //if oreonholding is null player is marking areas before spawning Oreons
        if (oreonHolding == null) {
            logger.info("Please spawn an Oreon before marking areas for tasks.");
            return false;
        }

        return true;
    }
}
