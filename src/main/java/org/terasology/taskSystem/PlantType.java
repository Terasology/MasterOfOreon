/*
 * Copyright 2019 MovingBlocks
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
package org.terasology.taskSystem;

public enum PlantType {
    CookieCrops("Cookie Crops", "MasterOfOreon:OreonPlant0"),
    FudgeFlowers("Fudge Flowers", "MasterOfOreon:FudgeFlower0"),
    Random("Random Plant", ""),
    None("No Plants Found, Try Again!", "");

    public final String plantName;
    public final String path;
    PlantType(String plantName, String path) {
        this.plantName = plantName;
        this.path = path;
    }
}