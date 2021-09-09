/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.buildings;

import org.terasology.MooConstants;
import org.terasology.buildings.events.CloseUpgradeScreenEvent;
import org.terasology.buildings.events.OpenUpgradeScreenEvent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.UIScreenLayer;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class BuildingUpgradeClientSystem extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;

    private UIScreenLayer upgradeScreenLayer;

    @ReceiveEvent
    public void openTaskSelectionScreen(OpenUpgradeScreenEvent event, EntityRef player) {
        this.upgradeScreenLayer = nuiManager.createScreen(MooConstants.BUILDING_UPGRADE_SCREEN_URI);
        nuiManager.pushScreen(this.upgradeScreenLayer);
    }

    @ReceiveEvent
    public void closeTaskSelectionScreen(CloseUpgradeScreenEvent event, EntityRef player) {
        nuiManager.closeScreen(this.upgradeScreenLayer);
    }
}
