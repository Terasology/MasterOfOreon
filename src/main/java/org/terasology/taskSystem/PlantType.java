// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
