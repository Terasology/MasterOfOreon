package org.terasology.common.nui;

import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.widgets.ItemActivateEventListener;

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
