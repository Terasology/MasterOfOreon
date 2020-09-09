// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.buildings;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockType;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;

import java.util.Set;

public class BasicRasterTarget implements RasterTarget {

    private final WorldProvider worldProvider;
    private final Rect2i affectedArea;
    private final Region3i affectedRegion;
    private final BlockTheme blockTheme;

    public BasicRasterTarget(WorldProvider worldProvider, Rect2i area, BlockTheme blockTheme) {
        this.worldProvider = worldProvider;
        this.affectedArea = area;
        this.blockTheme = blockTheme;

        Vector3i min = new Vector3i(affectedArea.minX(), -255, affectedArea.minY());
        Vector3i max = new Vector3i(affectedArea.maxX(), 255, affectedArea.maxY());
        this.affectedRegion = Region3i.createFromMinMax(min, max);
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

    public Rect2i getAffectedArea() {
        return affectedArea;
    }

    public Region3i getAffectedRegion() {
        return affectedRegion;
    }
}
