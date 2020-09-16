// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.deathSystem.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Consists of the animation pool defined in the prefab which must be used when the Death logic is triggered.
 */
public class DyingComponent implements Component {
    public List<MeshAnimation> animationPool = new ArrayList<>();
}
