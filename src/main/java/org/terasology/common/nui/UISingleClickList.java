package org.terasology.common.nui;

import org.terasology.rendering.nui.widgets.UIList;

public class UISingleClickList<T> extends UIList<T> {
    public void select(int index) {
        activate(index);
    }
}
