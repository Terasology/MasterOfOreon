// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.notification;

import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.nui.Canvas;
import org.terasology.nui.widgets.UIText;

/**
 * Controlling Notification Overlay UI for Master of Oreon (MOO)
 */
public class NotificationOverlayMOO extends CoreScreenLayer {

    private static final float TIME_VISIBLE_PER_CHAR = 0.08f;

    private static final float TIME_VISIBLE_BASE = 5.0f;

    private static final float TIME_FADE = 0.3f;
    private float time;
    private State state = State.HIDDEN;
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
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (isVisible()) {
            refresh();
        } else {
            hideImmediately();
        }
    }

    private void refresh() {
        switch (state) {
            case VISIBLE:
                time = 0;
                break;

            case FADE_IN:
                break;

            case FADE_OUT:
                state = State.FADE_IN;
                time = TIME_FADE - time;
                break;

            case HIDDEN:
                time = 0;
                state = State.FADE_IN;
                break;
        }
    }

    private void hideImmediately() {
        state = State.HIDDEN;
        time = 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        switch (state) {
            case FADE_IN:
                canvas.setAlpha(time / TIME_FADE);
                break;

            case FADE_OUT:
                canvas.setAlpha(1.0f - (time / TIME_FADE));
                break;

            case HIDDEN:
                return;

            case VISIBLE:
                break;
        }

        super.onDraw(canvas);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        time += delta;

        switch (state) {
            case FADE_IN:
                if (time > TIME_FADE) {
                    time = 0;
                    state = State.VISIBLE;
                }
                break;

            case FADE_OUT:
                if (time > TIME_FADE) {
                    time = 0;
                    state = State.HIDDEN;
                }
                break;

            case HIDDEN:
                break;

            case VISIBLE:
                int textLength = notificationText.getText().length();
                float maxTime = TIME_VISIBLE_BASE + (textLength * TIME_VISIBLE_PER_CHAR);

                if (time > maxTime) {
                    time = 0;
                    state = State.FADE_OUT;
                }
                break;
        }
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

    private enum State {
        FADE_IN,
        VISIBLE,
        FADE_OUT,
        HIDDEN
    }
}
