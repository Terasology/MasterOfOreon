// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;

import java.util.ArrayList;
import java.util.List;

public class BuildComponent implements Component {
    public List<MeshAnimation> animationPool = new ArrayList<>();
}
