// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tooltip.components;

import org.terasology.engine.rendering.assets.texture.TextureRegionAsset;
import org.terasology.gestalt.entitysystem.component.Component;

public class OreonTooltipComponent implements Component<OreonTooltipComponent> {
    public TextureRegionAsset<?> icon;
    public String name;

    @Override
    public void copy(OreonTooltipComponent other) {
        this.icon = other.icon;
        this.name = other.name;
    }
}
