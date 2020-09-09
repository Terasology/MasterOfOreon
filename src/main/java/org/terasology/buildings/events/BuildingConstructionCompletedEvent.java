// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.taskSystem.BuildingType;

import java.util.List;

public class BuildingConstructionCompletedEvent implements Event {

    public List<Region3i> absoluteRegions;
    public BuildingType buildingType;
    public Vector3i centerBlockPosition;
    public EntityRef constructedBuildingEntity;

    public BuildingConstructionCompletedEvent(List<Region3i> regions, BuildingType building, Vector3i center,
                                              EntityRef buildingEntity) {
        this.absoluteRegions = regions;
        this.buildingType = building;
        this.centerBlockPosition = center;
        this.constructedBuildingEntity = buildingEntity;
    }
}
