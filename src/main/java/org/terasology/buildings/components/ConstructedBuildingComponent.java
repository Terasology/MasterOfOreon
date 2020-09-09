// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.network.Replicate;
import org.terasology.math.geom.Vector3i;
import org.terasology.taskSystem.BuildingType;

import java.util.ArrayList;
import java.util.List;

public class ConstructedBuildingComponent implements Component {
    @Replicate
    public List<Region3i> boundingRegions = new ArrayList<>();

    @Replicate
    public BuildingType buildingType;

    @Replicate
    public int currentLevel;

    @Replicate
    public Vector3i centerLocation;

}
