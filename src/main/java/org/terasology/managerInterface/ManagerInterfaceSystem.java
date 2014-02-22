package org.terasology.managerInterface;

/*
 * Copyright 2012 Benjamin Glatzel <benjamin.glatzel@me.com>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.input.binds.general.HideHUDButton;
import org.terasology.managerInterface.nui.EmptyNonModalScreenLayer;
import org.terasology.managerInterface.nui.ManagerInterfaceHUDElement;
import org.terasology.managerInterface.nui.ToggleMouseGrabberButton;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;

@Share(ManagerInterfaceSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class ManagerInterfaceSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ManagerInterfaceSystem.class);

    @In
    private NUIManager nuiManager;

    private EmptyNonModalScreenLayer EMPTY_NON_MODAL_SCREEN = new EmptyNonModalScreenLayer();
    private boolean isEmptyNonModalScreenLayerOpen;

    @Override
    
    public void postBegin() {
        ManagerInterfaceHUDElement menuHUDElement = ManagerInterfaceHUDElement.getMenuHudElement();
    }
    
    // Higher priority than critical because NUI grabs all input when mouse is released
    @ReceiveEvent(components = ClientComponent.class, priority=250)
    public void onToggleMouseGrabber(ToggleMouseGrabberButton event, EntityRef entity) {
        if (event.isDown()) {
            
            if (isEmptyNonModalScreenLayerOpen) {
                nuiManager.closeScreen(EMPTY_NON_MODAL_SCREEN);
                isEmptyNonModalScreenLayerOpen = false;
            } else {
                nuiManager.pushScreen(EMPTY_NON_MODAL_SCREEN);
                isEmptyNonModalScreenLayerOpen = true;
            }

            event.consume();
        }
    }

}
