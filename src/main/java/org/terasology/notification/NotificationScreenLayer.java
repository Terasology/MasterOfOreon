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

public class NotificationScreenLayer extends CoreScreenLayer {

    @In
    private UIText notificationText;

    @In
    private NotificationMessageEvent notificationMessage;

    @Override
    public void initialise () {
        notificationText = find("notificationMessage", UIText.class);

        notificationText.setText(notificationMessage.getFormattedString());
    }

    @Override
    protected boolean isEscapeToCloseAllowed() {
        return false;
    }
}
