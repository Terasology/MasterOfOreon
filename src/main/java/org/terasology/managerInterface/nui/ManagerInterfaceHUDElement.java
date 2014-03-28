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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.common.nui.PickOneLayout;
import org.terasology.common.nui.UIToggleButton;
import org.terasology.managerInterface.ManagerInterfaceSystem;
import org.terasology.miniion.nui.layers.CreatureMinionMenuSystem;
import org.terasology.miniion.nui.layers.SummonMinionMenuSystem;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.AbstractWidget;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;

/**
 * @author mkienenb
 */
public class ManagerInterfaceHUDElement extends CoreHudWidget implements ControlWidget {

    private static final Logger logger = LoggerFactory.getLogger(ManagerInterfaceHUDElement.class);

    private UIToggleButton designTabCommand;
    private UIToggleButton researchTabCommand;
    private UIToggleButton summonTabCommand;
    private UIToggleButton creatureTabCommand;

    private PickOneLayout tabbedPane;

    private AbstractWidget designTab;
    private AbstractWidget researchTab;
    private AbstractWidget summonTab;
    private AbstractWidget creatureTab;
    
    private UIButton plantCommand;

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
            
            populateSummonMenus();
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
            
            populateCreaturesMenu();
        }
        
        plantCommand = find("plantCommand", UIButton.class);

        if (plantCommand == null) {
            logger.warn("No plantCommand widget defined");
        }

        if (plantCommand != null) {
            plantCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    ManagerInterfaceSystem managerInterfaceSystem = CoreRegistry.get(ManagerInterfaceSystem.class);
                    managerInterfaceSystem.setPlantMode();
                }
            });
        }
    }

    public void populateSummonMenus() {
        
        // TODO: We shouldn't assume this is a ColumnLayout
        ColumnLayout summonTabColumnLayout = (ColumnLayout)summonTab;

        Iterator<UIWidget> iterator = summonTab.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        SummonMinionMenuSystem summonMinionMenuSystem = CoreRegistry.get(SummonMinionMenuSystem.class);
        summonMinionMenuSystem.populateSummonMenus(summonTabColumnLayout);
    }

    public void populateCreaturesMenu() {
        // TODO: We shouldn't assume this is a ColumnLayout
        ColumnLayout creatureTabColumnLayout = (ColumnLayout)creatureTab;

        Iterator<UIWidget> iterator = creatureTab.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        CreatureMinionMenuSystem creatureMinionMenuSystem = CoreRegistry.get(CreatureMinionMenuSystem.class);
        creatureMinionMenuSystem.populateCreatureMenus(creatureTabColumnLayout);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        populateCreaturesMenu();
    }
}
