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

import org.terasology.context.Context;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

@Share(PlantTaskCompletionSystem.class)
@RegisterSystem(RegisterMode.CLIENT)
public class PlantTaskCompletionSystem extends BaseComponentSystem {

    @In
    private Context context;

    @In
    private BlockManager blockManager;

    private BlockEntityRegistry blockEntityRegistry;

    @Override
    public void postBegin() {
        this.blockEntityRegistry = context.get(BlockEntityRegistry.class);
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
