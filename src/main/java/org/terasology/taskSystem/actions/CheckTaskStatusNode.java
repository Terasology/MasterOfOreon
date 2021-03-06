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


import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.MersenneRandom;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.taskSystem.components.TaskComponent;

@BehaviorAction(name = "check_task_status")
public class CheckTaskStatusNode extends BaseAction {
    @In
    private Time time;

    private static final Logger logger = LoggerFactory.getLogger(CheckTaskStatusNode.class);

    private MersenneRandom mersenneRandom;

    @Override
    public void construct(Actor oreon) {
        mersenneRandom = new MersenneRandom();
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        if (oreonTaskComponent.taskCompletionTime < time.getGameTime()) {
            return BehaviorState.SUCCESS;
        }

        setTargetToNearbySelectedBlock(oreon, oreonTaskComponent);
        return BehaviorState.RUNNING;
    }

    /**
     * Sets the Oreon's target in the {@link MinionMoveComponent} to a nearby selected block so that the Oreon can move
     * to it.
     *
     * @param oreon The character whose target is being set.
     */
    private void setTargetToNearbySelectedBlock(Actor oreon, TaskComponent taskComponent) {
        MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);

        LocationComponent locationComponent = oreon.getComponent(LocationComponent.class);
        Vector3f worldPosition = locationComponent.getWorldPosition(new org.joml.Vector3f());

        int maxX = taskComponent.taskRegion.maxX();
        int maxZ = taskComponent.taskRegion.maxZ();
        int minX = taskComponent.taskRegion.minX();
        int minZ = taskComponent.taskRegion.minZ();

        // To randomly select direction to move
        int random = mersenneRandom.nextInt() % 2;

        // Consider all adjacent neighbors to Oreon's current location
        if (random == 0 && worldPosition.x + 1 >= minX && worldPosition.x + 1 <= maxX) {
            moveComponent.target = worldPosition.add(1, 0, 0);
        }

        if (random == 0 && worldPosition.z + 1 >= minZ && worldPosition.z + 1 <= maxZ) {
            moveComponent.target = worldPosition.add(0, 0, 1);
        }
        if (random == 1 && worldPosition.x - 1 >= minX && worldPosition.x - 1 <= maxX) {
            moveComponent.target = worldPosition.sub(1, 0, 0);
        }

        if (random == 1 && worldPosition.z - 1 >= minZ && worldPosition.z - 1 <= maxZ) {
            moveComponent.target = worldPosition.sub(0, 0, 1);
        }

        oreon.save(moveComponent);
    }
}
