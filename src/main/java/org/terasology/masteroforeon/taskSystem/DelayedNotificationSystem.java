// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.masteroforeon.notification.NotificationMessageEventMOO;

@Share(DelayedNotificationSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class DelayedNotificationSystem extends BaseComponentSystem {
    // TODO : Modify implementation to use DelayManager
    // TODO : Use this in TaskManagementSystem for notifications
    // TODO : Modify method implementations to generate message entity on demand and then destroy them

    /**
     * The maximum delay time after which notification is sent again.
     */
    private final long maxDelay = 10;
    @In
    private Time time;
    @In
    private LocalPlayer localPlayer;

    public float sendNotification(String message, EntityRef notificationMessageEntity, float lastNotification) {
        if (lastNotification == 0 || time.getGameTime() - lastNotification > maxDelay) {
            localPlayer.getCharacterEntity().getOwner().send(new NotificationMessageEventMOO(message,
                    notificationMessageEntity));
            return time.getGameTime();
        }

        return lastNotification;
    }

    public void sendNotificationNow(String message, EntityRef notificationMessageEntity) {
        localPlayer.getCharacterEntity().getOwner().send(new NotificationMessageEventMOO(message,
                notificationMessageEntity));

    }
}
