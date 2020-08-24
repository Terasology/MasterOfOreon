/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.common.nui;


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

    public static UIMenuItemRenderer getUIMenuItemRenderer() {
        return new UIMenuItemRenderer();
    }

    public static class UIMenuItemRenderer extends StringTextRenderer<UIMenuItem> {
        @Override
        public String getString(UIMenuItem menuItem) {
            return menuItem.getText();
        }
    }

    public static UiMenuItemActivateEventListener getUiMenuItemActivateEventListener() {
        return new UiMenuItemActivateEventListener();
    }

    public static class UiMenuItemActivateEventListener implements ItemActivateEventListener<UIMenuItem> {
        @Override
        public void onItemActivated(UIWidget widget, UIMenuItem menuItem) {
            menuItem.activate();
        }
    }

}
