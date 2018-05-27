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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.registry.In;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.world.selection.BlockSelectionComponent;

import java.util.List;

/**
 * Handles the actual task and its after effects like removal of the area render and changes to the Oreon attributes.
 */
@BehaviorAction(name = "perform_task")
public class PerformTaskNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(PerformTaskNode.class);

    @In
    EntityManager entityManager;

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
        logger.info("Perfoming Task of type : " + oreonTaskComponent.assignedTaskType);

        removeColorFromArea(oreon, oreonTaskComponent);

        changeOreonAttributes(oreon, oreonTaskComponent);

        //free the Oreon after perfoming task
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
        if (!assignedAreas.isEmpty()) {
            EntityRef assignedArea = assignedAreas.get(taskComponent.assignedAreaIndex);
            if (assignedArea.hasComponent(BlockSelectionComponent.class)) {
                logger.info("Removing color " + taskComponent.assignedAreaIndex + " " + oreonHolding);
                assignedArea.removeComponent(BlockSelectionComponent.class);
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

            case AssignedTaskType.Sleep:
                oreonAttributeComponent.health = 100;
                break;

            default:
                oreonAttributeComponent.hunger += 30;
        }

        oreon.save(oreonAttributeComponent);
    }
}
