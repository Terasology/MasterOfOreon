// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings.events;

import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.taskSystem.BuildingType;

import java.util.List;

public class BuildingConstructionStartedEvent implements Event {

    public List<BlockRegion> absoluteRegions;
    public BuildingType buildingType;
    public Vector3i centerBlockPosition;
    public EntityRef constructedBuildingEntity;
    public long completionDelay;

    public BuildingConstructionStartedEvent(List<BlockRegion> regions, BuildingType building, Vector3i center, EntityRef buildingEntity, long delay) {
        this.absoluteRegions = regions;
        this.buildingType = building;
        this.centerBlockPosition = center;
        this.constructedBuildingEntity = buildingEntity;
        this.completionDelay = delay;
    }
}
