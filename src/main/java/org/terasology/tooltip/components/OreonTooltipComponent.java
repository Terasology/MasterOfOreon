// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tooltip.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.rendering.assets.texture.TextureRegionAsset;

public class OreonTooltipComponent implements Component {
    public TextureRegionAsset<?> icon;
    public String name;
}
