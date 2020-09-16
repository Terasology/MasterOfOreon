// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.buildings;

import org.terasology.cities.parcels.Parcel;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

public class BuildingParcel implements Parcel {

    private Orientation orientation;
    private Rect2i shape;

    @Override
    public Orientation getOrientation() {
        return this.orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public Rect2i getShape() {
        return this.shape;
    }

    public void setShape(Rect2i shape) {
        this.shape = shape;
    }
}
