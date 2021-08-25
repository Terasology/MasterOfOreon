// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
