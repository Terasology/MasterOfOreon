package org.terasology.common.nui;

import java.util.Iterator;
import java.util.List;

import org.terasology.math.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreLayout;
import org.terasology.rendering.nui.LayoutHint;
import org.terasology.rendering.nui.UIWidget;

import com.google.common.collect.Lists;

public class PickOneLayout extends CoreLayout<LayoutHint> {

    private UIWidget selectedWidget;

    private List<UIWidget> widgetList = Lists.newArrayList();

    public UIWidget getSelectedWidget() {
        return selectedWidget;
    }

    public void setSelectedWidget(UIWidget selectedWidget) {
        this.selectedWidget = selectedWidget;
        // TODO: do we need to change the visibility of each widget?
    }

    @Override
    public void addWidget(UIWidget element, LayoutHint hint) {
        widgetList.add(element);

        if (null == selectedWidget) {
            setSelectedWidget(element);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (null != selectedWidget) {
            selectedWidget.onDraw(canvas);
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        // TODO: we should probably use the preferred content size of the maximum of the width/height for each component

        if (null != selectedWidget) {
            return selectedWidget.getPreferredContentSize(canvas, sizeHint);
        }

        return new Vector2i();
    }

    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        if (null != selectedWidget) {
            return selectedWidget.getMaxContentSize(canvas);
        }

        return new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Iterator<UIWidget> iterator() {
        return widgetList.iterator();
    }

}
