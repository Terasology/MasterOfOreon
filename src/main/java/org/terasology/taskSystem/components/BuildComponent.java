// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.components;

import com.google.common.collect.Lists;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;

public class BuildComponent implements Component<BuildComponent> {
    public List<MeshAnimation> animationPool = new ArrayList<>();

    @Override
    public void copy(BuildComponent other) {
        this.animationPool.clear();
        this.animationPool.addAll(other.animationPool);
    }
}
