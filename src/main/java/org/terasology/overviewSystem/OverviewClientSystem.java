// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.overviewSystem;

import org.terasology.MooConstants;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class OverviewClientSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;

    @ReceiveEvent
    public void onToggleOverviewScreen(OverviewScreenButton event, EntityRef entityRef) {
        if (event.isDown()) {
            toggleOverviewScreen();
            event.consume();
        }
    }

    private void toggleOverviewScreen() {
        nuiManager.toggleScreen(MooConstants.OVERVIEW_SCREEN_URI);
    }
}
