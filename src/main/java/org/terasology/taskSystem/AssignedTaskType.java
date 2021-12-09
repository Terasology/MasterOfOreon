// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem;

public final class AssignedTaskType {

    public static final String NONE = "none";
    public static final String PLANT = "plant";
    public static final String BUILD = "build";
    public static final String HARVEST = "harvest";
    public static final String GUARD = "guard";
    public static final String EAT = "eat";
    public static final String SLEEP = "sleep";
    public static final String TRAIN_STRENGTH = "train_strength";
    public static final String TRAIN_INTELLIGENCE = "train_intelligence";
    public static final String UPGRADE = "upgrade";
    public static final String RESEARCH = "research";
    public static final String PLACE_BLOCKS_IN_CHEST = "placeBlocksInChest";
    public static final String GET_BLOCKS_FROM_CHEST = "getBlocksFromChest";

    private AssignedTaskType() {
        // UtilityClass
    }
}
