package org.terasology.common.nui;

import org.terasology.rendering.nui.LayoutConfig;
import org.terasology.rendering.nui.widgets.UIButton;

public class UIToggleButton extends UIButton {

    @LayoutConfig
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String getMode() {
        if (selected) {
            return DOWN_MODE;
        }

        return super.getMode();
    }

}
