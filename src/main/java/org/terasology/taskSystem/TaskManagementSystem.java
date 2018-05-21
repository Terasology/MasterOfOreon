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
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIScreenLayer;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.events.SetTaskTypeEvent;
import org.terasology.world.selection.BlockSelectionComponent;

import java.util.List;

@Share(TaskManagementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class TaskManagementSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private NUIManager nuiManager;

    @In
    private Time timer;

    private HoldingComponent oreonHolding;
    private String newTaskType;
    private TaskComponent task;
    private UIScreenLayer taskSelectionScreenLayer;

    public void setOreonHolding(HoldingComponent holding) {
        this.oreonHolding = holding;
    }

    public boolean getTaskForOreon(Actor oreon) {
        List<EntityRef> availableTasks = oreonHolding.availableTasks;
        logger.debug("Looking for task in " + oreonHolding);
        if (!availableTasks.isEmpty()) {
            //TODO sort list by creationTime

            EntityRef taskEntity = availableTasks.remove(0);
            TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);

            TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

            oreonTaskComponent.assignedTaskType = taskComponent.assignedTaskType;
            oreonTaskComponent.creationTime = taskComponent.creationTime;
            oreonTaskComponent.taskRegion = taskComponent.taskRegion;
            oreonTaskComponent.taskStatus = TaskStatusType.InProgress;

            oreon.save(oreonTaskComponent);

            MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);
            Vector3i target = oreonTaskComponent.taskRegion.min();

            moveComponent.target = new Vector3f(target.x, target.y, target.z);

            logger.info("Set Oreon target to : " + moveComponent.target);

            oreon.save(moveComponent);

            return true;
        }

        return false;
    }

    @ReceiveEvent
    public void receiveNewTask(ApplyBlockSelectionEvent blockSelectionEvent, EntityRef player) {
        logger.info("Adding a new Task");
        task = new TaskComponent();
        task.taskRegion = blockSelectionEvent.getSelection();
        task.creationTime = timer.getGameTimeInMs();

        EntityRef itemEntity = blockSelectionEvent.getSelectedItemEntity();
        BlockSelectionComponent blockSelectionComponent = itemEntity.getComponent(BlockSelectionComponent.class);

        blockSelectionComponent.shouldRender = true;
        taskSelectionScreenLayer = nuiManager.createScreen("taskSelectionScreen");
        nuiManager.pushScreen(taskSelectionScreenLayer);
    }

    private void addTask (EntityRef task, EntityRef player) {
        if (oreonHolding == null) {
            oreonHolding = player.getComponent(HoldingComponent.class);
        }
        logger.info("Adding task to " + oreonHolding);
        oreonHolding.availableTasks.add(task);
    }

    @ReceiveEvent
    public void receiveSetTaskTypeEvent (SetTaskTypeEvent event, EntityRef player) {
        nuiManager.closeScreen(taskSelectionScreenLayer);

        newTaskType = event.getTaskType();

        //when cancel selection button is used
        if (newTaskType == null) {
            return;
        }

        task.assignedTaskType = newTaskType;

        if (newTaskType.equals(AssignedTaskType.Build)) {
            task.buildingType = event.getBuildingType();
        }

        EntityRef taskEntity = entityManager.create(task);

        addTask(taskEntity, player);
    }
}
