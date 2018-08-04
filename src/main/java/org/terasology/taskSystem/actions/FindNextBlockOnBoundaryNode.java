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
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.registry.In;
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

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        int y = oreonTaskComponent.taskRegion.minY();

        for (Region3i region : buildingComponent.boundingRegions) {
            minX = Math.min(minX, region.minX());
            maxX = Math.max(maxX, region.maxX());
            minZ = Math.min(minZ, region.minZ());
            maxZ = Math.max(maxZ, region.maxZ());
        }

        Vector3i min = new Vector3i(minX - 1, y, minZ - 1);
        Vector3i max = new Vector3i(maxX + 1, y, maxZ + 1);
        oreonTaskComponent.taskRegion = Region3i.createFromMinMax(min, max);
        oreon.save(oreonTaskComponent);

        if (oreonTaskComponent.taskCompletionTime < time.getGameTime()) {
            return BehaviorState.SUCCESS;
        }

        setTargetToNearbyBoundaryBlock(oreon, oreonTaskComponent);
        return BehaviorState.RUNNING;
    }

    /**
     * Sets the Oreon's target in the {@link MinionMoveComponent} to a nearby block on the boundary of a building. Used for the
     * guard task.
     * @param oreon The character whose target is being set.
     */
    private void setTargetToNearbyBoundaryBlock(Actor oreon, TaskComponent taskComponent) {
        MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);

        LocationComponent locationComponent = oreon.getComponent(LocationComponent.class);
        Vector3f worldPosition = locationComponent.getWorldPosition();

        int maxX = taskComponent.taskRegion.maxX();
        int maxZ = taskComponent.taskRegion.maxZ();
        int minX = taskComponent.taskRegion.minX();
        int minZ = taskComponent.taskRegion.minZ();

        int y = taskComponent.taskRegion.minY();

        if (Math.round(worldPosition.x) == maxX) {
            if (Math.round(worldPosition.z) != maxZ) {
                moveComponent.target = new Vector3f(maxX + 1, y, maxZ + 1);
            }
            else {
                moveComponent.target = new Vector3f(minX - 1, y, maxZ + 1);
            }
        }
        else if (Math.round(worldPosition.z) == maxZ) {
            if (Math.round(worldPosition.x) != minX) {
                moveComponent.target = new Vector3f(minX - 1, y, maxZ + 1);
            }
            else {
                moveComponent.target = new Vector3f(minX - 1, y, minZ - 1);
            }
        }
        else if (Math.round(worldPosition.x) == minX) {
            if (Math.round(worldPosition.z) != minZ) {
                moveComponent.target = new Vector3f(minX - 1, y, minZ - 1);
            }
            else {
                moveComponent.target = new Vector3f(maxX + 1, y, minZ - 1);
            }
        }
        else {
            moveComponent.target = new Vector3f(maxX + 1, y, maxZ + 1);
        }

        oreon.save(moveComponent);
    }
}
