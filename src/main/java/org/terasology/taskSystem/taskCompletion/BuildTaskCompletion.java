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
package org.terasology.taskSystem.taskCompletion;

import org.terasology.math.Region3i;
import org.terasology.taskSystem.BuildingType;

public interface BuildTaskCompletion {

    /**
     * Constructs the building by placing the required blocks in the world
     * @param selectedRegion The region assigned for the building
     * @param  buildingType The type of building to be constructed
     */
    void constructBuilding(Region3i selectedRegion, BuildingType buildingType);

    /**
     * Selects a building based on the type of building to be constructed
     * @param buildingType The type of building to be constructed
     * @param level The level of the building to be constructed
     */
    void selectBuilding(BuildingType buildingType, int level);

}
