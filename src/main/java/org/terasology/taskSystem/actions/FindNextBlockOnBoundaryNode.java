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
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.module.pathfindingbehaviors.move.MinionMoveComponent;
import org.terasology.taskSystem.components.TaskComponent;

@BehaviorAction(name = "find_next_block_on_boundary")
public class FindNextBlockOnBoundaryNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(CheckTaskStatusNode.class);

    @In
    private Time time;

    @In
    private EntityManager entityManager;

    @Override
    public void construct(Actor oreon) {

    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        EntityRef building = entityManager.getEntity(oreonTaskComponent.task.requiredBuildingEntityID);

        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

        int y = oreonTaskComponent.taskRegion.minY();

        //TODO: would be nice to replace the first two lines with `BlockRegion.union(buildingComponent.boundingRegions)`
        final BlockRegion taskRegion = buildingComponent.boundingRegions.stream()
                .reduce(new BlockRegion(BlockRegion.INVALID), BlockRegion::union, BlockRegion::union)
                .minY(y).maxY(y)
                .addToMin(-1, 0, -1)
                .addToMax(1, 0, 1);

        oreonTaskComponent.taskRegion = taskRegion;
        oreon.save(oreonTaskComponent);

        if (oreonTaskComponent.taskCompletionTime < time.getGameTime()) {
            return BehaviorState.SUCCESS;
        }

        setTargetToNearbyBoundaryBlock(oreon, oreonTaskComponent);
        return BehaviorState.RUNNING;
    }

    /**
     * Sets the Oreon's target in the {@link MinionMoveComponent} to a nearby block on the boundary of a building. Used
     * for the guard task.
     *
     * @param oreon The character whose target is being set.
     */
    private void setTargetToNearbyBoundaryBlock(Actor oreon, TaskComponent taskComponent) {
        MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);

        LocationComponent locationComponent = oreon.getComponent(LocationComponent.class);
        Vector3f worldPosition = locationComponent.getWorldPosition(new Vector3f());

        int maxX = taskComponent.taskRegion.maxX();
        int maxZ = taskComponent.taskRegion.maxZ();
        int minX = taskComponent.taskRegion.minX();
        int minZ = taskComponent.taskRegion.minZ();
        int y = taskComponent.taskRegion.minY();

        if (moveComponent.target == null) {
            moveComponent.target = new Vector3f();
        }
        if (Math.round(worldPosition.x) == maxX) {
            if (Math.round(worldPosition.z) != maxZ) {
                moveComponent.target.set(maxX + 1, y, maxZ + 1);
            } else {
                moveComponent.target.set(minX - 1, y, maxZ + 1);
            }
        } else if (Math.round(worldPosition.z) == maxZ) {
            if (Math.round(worldPosition.x) != minX) {
                moveComponent.target.set(minX - 1, y, maxZ + 1);
            } else {
                moveComponent.target.set(minX - 1, y, minZ - 1);
            }
        } else if (Math.round(worldPosition.x) == minX) {
            if (Math.round(worldPosition.z) != minZ) {
                moveComponent.target.set(minX - 1, y, minZ - 1);
            } else {
                moveComponent.target.set(maxX + 1, y, minZ - 1);
            }
        } else {
            moveComponent.target.set(maxX + 1, y, maxZ + 1);
        }

        oreon.save(moveComponent);
    }
}
