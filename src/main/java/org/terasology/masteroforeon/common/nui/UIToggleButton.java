// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.common.nui;

import org.terasology.nui.LayoutConfig;
import org.terasology.nui.widgets.UIButton;

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
