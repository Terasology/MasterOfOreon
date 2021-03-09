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

import org.terasology.cities.parcels.Parcel;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;

public class BuildingParcel implements Parcel {

    private Orientation orientation;
    private BlockAreac shape;

    @Override
    public Orientation getOrientation() {
        return this.orientation;
    }

    @Override
    public BlockAreac getShape() {
        return this.shape;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setShape(BlockAreac shape) {
        this.shape = new BlockArea(shape);
    }
}
