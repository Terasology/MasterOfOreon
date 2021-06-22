// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
