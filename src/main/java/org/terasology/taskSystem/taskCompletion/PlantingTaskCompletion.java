// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.taskCompletion;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.math.geom.Vector3i;

public class PlantingTaskCompletion {
    private final BlockManager blockManager;

    private final BlockEntityRegistry blockEntityRegistry;

    public PlantingTaskCompletion(BlockManager manager, BlockEntityRegistry blockRegistry) {
        this.blockManager = manager;
        this.blockEntityRegistry = blockRegistry;
    }

    public void placeCrops(Region3i selectedRegion, String cropToPlace) {
        int minX = selectedRegion.minX();
        int maxX = selectedRegion.maxX();
        int minZ = selectedRegion.minZ();
        int maxZ = selectedRegion.maxZ();

        int y = selectedRegion.minY();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = blockManager.getBlock(cropToPlace);
                blockEntityRegistry.setBlockForceUpdateEntity(new Vector3i(x, y + 1, z), block);
            }
        }
    }
}
