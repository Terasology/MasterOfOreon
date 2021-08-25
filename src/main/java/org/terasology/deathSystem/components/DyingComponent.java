// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.deathSystem.components;

import com.google.common.collect.Lists;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Consists of the animation pool defined in the prefab which must be used when the Death logic is triggered.
 */
public class DyingComponent implements Component<DyingComponent> {
    public List<MeshAnimation> animationPool = new ArrayList<>();

    @Override
    public void copyFrom(DyingComponent other) {
        this.animationPool = Lists.newArrayList(other.animationPool);
    }
}
