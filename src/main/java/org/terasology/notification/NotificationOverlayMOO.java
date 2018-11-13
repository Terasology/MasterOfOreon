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

import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.widgets.UIText;

/**
 * Controlling Notification Overlay UI for Master of Oreon (MOO)
 */
public class NotificationOverlayMOO extends CoreScreenLayer {

    @In
    private UIText notificationText;

    @Override
    public void initialise() {
        notificationText = find("notificationMessage", UIText.class);

        setNotificationText("NO NOTIFICATION");
    }

    public void setNotificationText(String message) {
        notificationText.setText(message);
    }

    @Override
    public boolean canBeFocus() {
        return false;
    }

    @Override
    protected boolean isEscapeToCloseAllowed() {
        return false;
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
