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
import org.terasology.engine.logic.players.PlayerUtil;
import org.terasology.engine.network.OwnerEvent;

/**
 * A notification message for Master of Oreon (MOO)
 */
@OwnerEvent
public class NotificationMessageEventMOO implements NotificationEventMOO {
    private String message;
    private EntityRef from;

    protected NotificationMessageEventMOO() {
    }

    public NotificationMessageEventMOO(String message, EntityRef from) {
        this.message = message;
        this.from = from;
    }

    @Override
    public String getFormattedString() {
        String name = PlayerUtil.getColoredPlayerName(from);

        return String.format("%s: %s", name, message);
    }

    public String getMessage() {
        return message;
    }

    public EntityRef getFrom() {
        return from;
    }
}
