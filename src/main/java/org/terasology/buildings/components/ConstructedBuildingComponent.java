// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings.components;

import com.google.common.collect.Lists;
import org.joml.Vector3i;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.taskSystem.BuildingType;

import java.util.ArrayList;
import java.util.List;

public class ConstructedBuildingComponent implements Component<ConstructedBuildingComponent> {
    @Replicate
    public List<BlockRegion> boundingRegions = new ArrayList<>();

    @Replicate
    public BuildingType buildingType;

    @Replicate
    public int currentLevel;

    @Replicate
    public Vector3i centerLocation = new Vector3i();

    @Override
    public void copy(ConstructedBuildingComponent other) {
        this.boundingRegions = Lists.newArrayList(other.boundingRegions);
        this.buildingType = other.buildingType;
        this.currentLevel = other.currentLevel;
        this.centerLocation.set(other.centerLocation);
    }
}
