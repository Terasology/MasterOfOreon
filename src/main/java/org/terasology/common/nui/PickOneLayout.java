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

import java.util.Iterator;
import java.util.List;

import org.terasology.math.geom.Vector2i;
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

    @Override
    public void removeWidget(UIWidget element) {
        // TODO: Implement (added as compile fix after engine change)
    }
}
