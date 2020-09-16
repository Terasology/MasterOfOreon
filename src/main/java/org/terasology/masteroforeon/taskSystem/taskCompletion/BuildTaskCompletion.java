// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.taskCompletion;

import org.terasology.engine.math.Region3i;
import org.terasology.masteroforeon.taskSystem.BuildingType;

public interface BuildTaskCompletion {

    /**
     * Constructs the building by placing the required blocks in the world
     *
     * @param selectedRegion The region assigned for the building
     * @param buildingType The type of building to be constructed
     */
    void constructBuilding(Region3i selectedRegion, BuildingType buildingType);

    /**
     * Selects a building based on the type of building to be constructed
     *
     * @param buildingType The type of building to be constructed
     * @param level The level of the building to be constructed
     */
    void selectBuilding(BuildingType buildingType, int level);

}
