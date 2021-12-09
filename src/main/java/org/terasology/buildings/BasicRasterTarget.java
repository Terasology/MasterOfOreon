// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings;

import org.joml.Vector3i;
import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockType;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.block.BlockRegion;

import java.util.Set;

public class BasicRasterTarget implements RasterTarget {

    private WorldProvider worldProvider;
    private BlockArea affectedArea;
    private BlockRegion affectedRegion;
    private BlockTheme blockTheme;

    public BasicRasterTarget(WorldProvider worldProvider, BlockAreac area, BlockTheme blockTheme) {
        this.worldProvider = worldProvider;
        this.affectedArea = new BlockArea(area);
        this.blockTheme = blockTheme;

        this.affectedRegion = new BlockRegion(affectedArea.minX(), -255, affectedArea.minY(), affectedArea.maxX(), 255,
                affectedArea.maxY());
    }

    public void setBlock(int x, int y, int z, BlockType type, Set<Side> side) {
        setBlock(x, y, z, blockTheme.apply(type, side));
    }

    public void setBlock(int x, int y, int z, BlockType type) {
        worldProvider.setBlock(new Vector3i(x, y, z), blockTheme.apply(type));
    }

    public void setBlock(int x, int y, int z, Block block) {
        worldProvider.setBlock(new Vector3i(x, y, z), block);
    }

    public BlockAreac getAffectedArea() {
        return affectedArea;
    }

    public BlockRegion getAffectedRegion() {
        return affectedRegion;
    }
}
