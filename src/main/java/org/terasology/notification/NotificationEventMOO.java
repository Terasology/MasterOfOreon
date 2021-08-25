// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.notification;

import org.terasology.engine.entitySystem.event.Event;

/**
 * Notification event for Master of Oreon (MOO)
 */
@FunctionalInterface
public interface NotificationEventMOO extends Event {

    /**
     * @return The final notification message, combining all elements
     */
    String getFormattedString();
}
