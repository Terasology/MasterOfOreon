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
package org.terasology.buildings;

import org.joml.Vector3i;
import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockType;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.math.Side;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockRegion;

import java.util.Set;

public class BasicRasterTarget implements RasterTarget {

    private WorldProvider worldProvider;
    private Rect2i affectedArea;
    private BlockRegion affectedRegion;
    private BlockTheme blockTheme;

    public BasicRasterTarget(WorldProvider worldProvider,Rect2i area, BlockTheme blockTheme) {
        this.worldProvider = worldProvider;
        this.affectedArea = area;
        this.blockTheme = blockTheme;

        this.affectedRegion = new BlockRegion(affectedArea.minX(), -255, affectedArea.minY(), affectedArea.maxX(), 255, affectedArea.maxY());
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

    public BlockRegion getAffectedRegion() {
        return affectedRegion;
    }
}
