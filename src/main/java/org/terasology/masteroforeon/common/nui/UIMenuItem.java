// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.common.nui;


import org.terasology.nui.UIWidget;
import org.terasology.nui.itemRendering.StringTextRenderer;
import org.terasology.nui.widgets.ItemActivateEventListener;

public class UIMenuItem {
    private String text;
    private Runnable action;

    public UIMenuItem(String text, Runnable action) {
        super();
        this.text = text;
        this.action = action;
    }

    public static UIMenuItemRenderer getUIMenuItemRenderer() {
        return new UIMenuItemRenderer();
    }

    public static UiMenuItemActivateEventListener getUiMenuItemActivateEventListener() {
        return new UiMenuItemActivateEventListener();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void activate() {
        this.action.run();
    }

    public static class UIMenuItemRenderer extends StringTextRenderer<UIMenuItem> {
        @Override
        public String getString(UIMenuItem menuItem) {
            return menuItem.getText();
        }
    }

    public static class UiMenuItemActivateEventListener implements ItemActivateEventListener<UIMenuItem> {
        @Override
        public void onItemActivated(UIWidget widget, UIMenuItem menuItem) {
            menuItem.activate();
        }
    }

}
