// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.spawning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;

@RegisterSystem(RegisterMode.CLIENT)
public class SpawningClientSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SpawningClientSystem.class);

    @In
    private NUIManager nuiManager;

    @In
    private LocalPlayer localPlayer;
}
