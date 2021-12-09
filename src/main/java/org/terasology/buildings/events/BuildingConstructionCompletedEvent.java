// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings.events;

import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.taskSystem.BuildingType;

import java.util.List;

public class BuildingConstructionCompletedEvent implements Event {

    public List<BlockRegion> absoluteRegions;
    public BuildingType buildingType;
    public Vector3i centerBlockPosition;
    public EntityRef constructedBuildingEntity;

    public BuildingConstructionCompletedEvent(List<BlockRegion> regions, BuildingType building, Vector3i center, EntityRef buildingEntity) {
        this.absoluteRegions = regions;
        this.buildingType = building;
        this.centerBlockPosition = center;
        this.constructedBuildingEntity = buildingEntity;
    }
}
