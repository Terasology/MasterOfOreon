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
package org.terasology.managerInterface.nui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.common.nui.PickOneLayout;
import org.terasology.common.nui.UIToggleButton;
import org.terasology.math.Rect2f;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.AbstractWidget;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.ActivateEventListener;

/**
 * @author mkienenb
 */
public class ManagerInterfaceHUDElement extends CoreHudWidget implements ControlWidget {

    private static final Logger logger = LoggerFactory.getLogger(ManagerInterfaceHUDElement.class);

    public static final String TABBED_MENU_WIDGET_ID = "managerinterface:tabbedMenu";

    private UIToggleButton designTabCommand;
    private UIToggleButton researchTabCommand;
    private UIToggleButton summonTabCommand;
    private UIToggleButton creatureTabCommand;

    private PickOneLayout tabbedPane;

    private AbstractWidget designTab;
    private AbstractWidget researchTab;
    private AbstractWidget summonTab;
    private AbstractWidget creatureTab;

    @Override
    public void initialise() {
        designTabCommand = find("designTabCommand", UIToggleButton.class);
        researchTabCommand = find("researchTabCommand", UIToggleButton.class);
        summonTabCommand = find("summonTabCommand", UIToggleButton.class);
        creatureTabCommand = find("creatureTabCommand", UIToggleButton.class);

        if (designTabCommand == null) {
            logger.warn("No designTabCommand widget defined");
        }
        if (researchTabCommand == null) {
            logger.warn("No researchTabCommand widget defined");
        }
        if (summonTabCommand == null) {
            logger.warn("No summonTabCommand widget defined");
        }
        if (creatureTabCommand == null) {
            logger.warn("No creatureTabCommand widget defined");
        }

        tabbedPane = find("tabbedPane", PickOneLayout.class);

        if (tabbedPane == null) {
            logger.warn("No tabbedPane widget defined");
        }

        designTab = find("designTab", AbstractWidget.class);
        researchTab = find("researchTab", AbstractWidget.class);
        summonTab = find("summonTab", AbstractWidget.class);
        creatureTab = find("creatureTab", AbstractWidget.class);

        if (designTab == null) {
            logger.warn("No designTab widget defined");
        }
        if (researchTab == null) {
            logger.warn("No researchTab widget defined");
        }
        if (summonTab == null) {
            logger.warn("No summonTab widget defined");
        }
        if (creatureTab == null) {
            logger.warn("No creatureTab widget defined");
        }

        if (designTabCommand != null) {
            designTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    designTabCommand.setSelected(true);
                    researchTabCommand.setSelected(false);
                    summonTabCommand.setSelected(false);
                    creatureTabCommand.setSelected(false);

                    tabbedPane.setSelectedWidget(designTab);
                }
            });
        }

        if (researchTabCommand != null) {
            researchTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    designTabCommand.setSelected(false);
                    researchTabCommand.setSelected(true);
                    summonTabCommand.setSelected(false);
                    creatureTabCommand.setSelected(false);

                    tabbedPane.setSelectedWidget(researchTab);
                }
            });
        }

        if (summonTabCommand != null) {
            summonTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    designTabCommand.setSelected(false);
                    researchTabCommand.setSelected(false);
                    summonTabCommand.setSelected(true);
                    creatureTabCommand.setSelected(false);

                    tabbedPane.setSelectedWidget(summonTab);
                }
            });
        }

        if (creatureTabCommand != null) {
            creatureTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    designTabCommand.setSelected(false);
                    researchTabCommand.setSelected(false);
                    summonTabCommand.setSelected(false);
                    creatureTabCommand.setSelected(true);

                    tabbedPane.setSelectedWidget(creatureTab);
                }
            });
        }
    }

    public static ManagerInterfaceHUDElement getMenuHudElement() {
        NUIManager nuiManager = CoreRegistry.get(NUIManager.class);

        // TODO: temporary workaround for bug:
        AssetUri uri = new AssetUri(AssetType.UI_ELEMENT, TABBED_MENU_WIDGET_ID);
        ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(uri, ManagerInterfaceHUDElement.class);

        if (null == tabbedMenuHUDElement) {
            tabbedMenuHUDElement = nuiManager.getHUD().addHUDElement(TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class, Rect2f.createFromMinAndSize(0, 0, 1, 1));
        }

        return tabbedMenuHUDElement;
    }
}
