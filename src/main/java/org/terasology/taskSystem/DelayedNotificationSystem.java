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
package org.terasology.taskSystem;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.notification.NotificationMessageEventMOO;

@Share(DelayedNotificationSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class DelayedNotificationSystem extends BaseComponentSystem {
    // TODO : Modify implementation to use DelayManager
    // TODO : Use this in TaskManagementSystem for notifications
    // TODO : Modify method implementations to generate message entity on demand and then destroy them

    @In
    private Time time;

    @In
    private LocalPlayer localPlayer;

    /**
     * The maximum delay time after which notification is sent again.
     */
    private long maxDelay = 10;

    public float sendNotification(String message, EntityRef notificationMessageEntity, float lastNotification) {
        if (lastNotification == 0 || time.getGameTime() - lastNotification > maxDelay) {
            localPlayer.getCharacterEntity().getOwner().send(new NotificationMessageEventMOO(message, notificationMessageEntity));
            return time.getGameTime();
        }

        return lastNotification;
    }

    public void sendNotificationNow(String message, EntityRef notificationMessageEntity) {
        localPlayer.getCharacterEntity().getOwner().send(new NotificationMessageEventMOO(message, notificationMessageEntity));

    }
}
