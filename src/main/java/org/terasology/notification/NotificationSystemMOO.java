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
package org.terasology.notification;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;

/**
 * The system for notifying the player through notification overlay for Master of Oreon (MOO)
 */
@RegisterSystem
public class NotificationSystemMOO extends BaseComponentSystem {

    private NotificationOverlayMOO overlay;

    @In
    private NUIManager nuiManager;

    @Override
    public void initialise() {
        overlay = nuiManager.addOverlay("notificationOverlayMOO", NotificationOverlayMOO.class);
    }

    @ReceiveEvent(components = ClientComponent.class)
    public void onNotification(NotificationEventMOO event, EntityRef entity) {
        ClientComponent client = entity.getComponent(ClientComponent.class);
        if (client.local) {
            overlay.setVisible(true);
            overlay.setNotificationText(event.getFormattedString());
        }
    }
}
