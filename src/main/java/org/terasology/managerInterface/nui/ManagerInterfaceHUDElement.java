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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.managerInterface.ManagerInterfaceSystem;
//import org.terasology.miniion.components.MinionComponent;
//import org.terasology.miniion.nui.layers.SummonMinionMenuSystem;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.AbstractWidget;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.CoreLayout;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.widgets.ActivateEventListener;

/**
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

    private UIToggleButton plantCommand;
    private UIToggleButton digCommand;
    private UIToggleButton cutTreeCommand;

    @Override
    public void initialise() {
        final CoreLayout<?> tabLayout = find("tabLayout", CoreLayout.class);

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
                    selectToggleButtonInColumnLayout(tabLayout, widget);
                    tabbedPane.setSelectedWidget(designTab);
                }
            });
        }

        if (researchTabCommand != null) {
            researchTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(tabLayout, widget);
                    tabbedPane.setSelectedWidget(researchTab);
                }
            });
        }

        if (summonTabCommand != null) {
            summonTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(tabLayout, widget);
                    tabbedPane.setSelectedWidget(summonTab);
                }
            });

            populateSummonMenus();
        }

        if (creatureTabCommand != null) {
            creatureTabCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(tabLayout, widget);
                    tabbedPane.setSelectedWidget(creatureTab);
                }
            });

            populateCreaturesMenu();
        }

        plantCommand = find("plantCommand", UIToggleButton.class);
        if (plantCommand == null) {
            logger.warn("No plantCommand widget defined");
        }
        if (plantCommand != null) {
            plantCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(designTab, widget);
                    ManagerInterfaceSystem managerInterfaceSystem = CoreRegistry.get(ManagerInterfaceSystem.class);
                    managerInterfaceSystem.setPlantMode();
                }
            });
        }

        digCommand = find("digCommand", UIToggleButton.class);
        if (digCommand == null) {
            logger.warn("No digCommand widget defined");
        }
        if (digCommand != null) {
            digCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(designTab, widget);
                    ManagerInterfaceSystem managerInterfaceSystem = CoreRegistry.get(ManagerInterfaceSystem.class);
                    managerInterfaceSystem.setDigMode();
                }
            });
        }

        cutTreeCommand = find("cutTreeCommand", UIToggleButton.class);
        if (cutTreeCommand == null) {
            logger.warn("No cutTreeCommand widget defined");
        }
        if (cutTreeCommand != null) {
            cutTreeCommand.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(designTab, widget);
                    ManagerInterfaceSystem managerInterfaceSystem = CoreRegistry.get(ManagerInterfaceSystem.class);
                    managerInterfaceSystem.setCutTreeMode();
                }
            });
        }
    }

    public void populateSummonMenus() {

        // TODO: We shouldn't assume this is a ColumnLayout
        ColumnLayout summonTabColumnLayout = (ColumnLayout) summonTab;

        Iterator<UIWidget> iterator = summonTab.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        PrefabManager prefMan = CoreRegistry.get(PrefabManager.class);

        //TODO Implement new MinionComponent and summon logic using Portals, currently SummonMinionMenuSystem has the spawn logic
        /*for (final Prefab prefab : prefMan.listPrefabs(MinionComponent.class)) {

            String[] tempstring = prefab.getName().split(":");
            if (tempstring.length == 2) {
                String minionName = tempstring[1];
                UIToggleButton selectMinionMenu = new UIToggleButton();
                selectMinionMenu.setText(minionName);
                selectMinionMenu.subscribe(new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        selectToggleButtonInColumnLayout(summonTab, widget);
                        SummonMinionMenuSystem summonMinionMenuSystem = CoreRegistry.get(SummonMinionMenuSystem.class);
                        summonMinionMenuSystem.createMinion(prefab);
                    }

                });

                summonTabColumnLayout.addWidget(selectMinionMenu);
            }
        }*/
    }

    // TODO: eventually this needs to be done by listening for minion create/destroy events,
    // probably in ManagerInterfaceSystem

    public void populateCreaturesMenu() {
        // TODO: We shouldn't assume this is a ColumnLayout
        ColumnLayout creatureTabColumnLayout = (ColumnLayout) creatureTab;

        Iterator<UIWidget> iterator = creatureTab.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        EntityManager entityManager = CoreRegistry.get(EntityManager.class);
        //TODO This loop lists out the Oreons that can be summoned
        /*Iterable<EntityRef> entityIterable = entityManager.getEntitiesWith(MinionComponent.class);
        for (EntityRef entityRef : entityIterable) {

            MinionComponent minionComponent = entityRef.getComponent(MinionComponent.class);

            String minionIdentity = minionComponent.flavortext + " - " + minionComponent.name;

            UIToggleButton existingMinionMenuItem = new UIToggleButton();
            existingMinionMenuItem.setText(minionIdentity);
            existingMinionMenuItem.subscribe(new ActivateEventListener() {
                @Override
                public void onActivated(UIWidget widget) {
                    selectToggleButtonInColumnLayout(creatureTab, widget);
                    // Go to minion? select minion?
                }
            });

            creatureTabColumnLayout.addWidget(existingMinionMenuItem);
        }*/
    }

    private void selectToggleButtonInColumnLayout(UIWidget layoutWidget, UIWidget selectedToggleButtonWidget) {
        if (!(layoutWidget instanceof CoreLayout)) {
            logger.warn(layoutWidget + " is not an instance of CoreLayout");
            return;
        }
        if (!(selectedToggleButtonWidget instanceof UIToggleButton)) {
            logger.warn(selectedToggleButtonWidget + " is not an instance of UIToggleButton");
            return;
        }

        CoreLayout<?> layout = (CoreLayout<?>) layoutWidget;

        Iterator<UIWidget> iterator = layout.iterator();
        while (iterator.hasNext()) {
            UIWidget widget = iterator.next();
            if (widget instanceof UIToggleButton) {
                UIToggleButton toggleButton = (UIToggleButton)widget;
                toggleButton.setSelected(toggleButton == selectedToggleButtonWidget);
            }
        }
    }

}
