// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
