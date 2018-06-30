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
package org.terasology.managerInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.managerInterface.nui.ManagerInterfaceHUDElement;
import org.terasology.managerInterface.nui.ToggleMouseGrabberButton;
import org.terasology.math.geom.Rect2f;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.Constants;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemFactory;

@Share(ManagerInterfaceSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class ManagerInterfaceSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ManagerInterfaceSystem.class);

    enum ManagerCommandMode {
        None,
        Plant,
        Dig,
        CutTree
    }

    private ManagerCommandMode currentManagerCommandMode = ManagerCommandMode.None;

    //TODO Implement new TaskManagementSystem
    //@In
    //private TaskManagementSystem taskManager;

    @In
    private NUIManager nuiManager;

    @In
    private EntityManager entityManager;

    @In
    private InventoryManager inventoryManager;

    @In
    private BlockManager blockManager;

    @Override
    public void postBegin() {
        toggleManagerInterface();
    }

    // TODO: this should be in a separate class, most likely.  Maybe ManagerInterface
    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player, InventoryComponent inventory) {
        BlockItemFactory blockItemFactory = new BlockItemFactory(entityManager);
        inventoryManager.giveItem(player, player, entityManager.create(Constants.SELECTION_TOOL_PREFAB));
        inventoryManager.giveItem(player, player, entityManager.create(Constants.BUILDING_UPGRADE_TOOL));
        inventoryManager.giveItem(player, player, blockItemFactory.newInstance(blockManager.getBlockFamily(Constants.PORTAL_PREFAB), 10));
        if (!player.hasComponent(HoldingComponent.class)) {
            player.addComponent(new HoldingComponent());
        }

        // Added for testing purposes
        inventoryManager.giveItem(player, player, blockItemFactory.newInstance(blockManager.getBlockFamily("Core:Sand"), 99));
        inventoryManager.giveItem(player, player, blockItemFactory.newInstance(blockManager.getBlockFamily("Core:Dirt"), 99));
    }

    /*@ReceiveEvent
    public void onSelection(ApplyBlockSelectionEvent event, EntityRef entity) {
        switch (currentManagerCommandMode) {
            case None:
                // do nothing
                break;
            case Plant:
                //requires task manager implementation
                break;
            case Dig:
                break;
        }
    }*/

    // Higher priority than critical because NUI grabs all input when mouse is released
    @ReceiveEvent(components = ClientComponent.class, priority=250)
    public void onToggleMouseGrabber(ToggleMouseGrabberButton event, EntityRef entity) {
        if (event.isDown()) {
            toggleManagerInterface();

            event.consume();
        }
    }

    private void toggleManagerInterface() {
        if (nuiManager.isOpen(Constants.MOUSE_CAPTURING_SCREEN_UIELEMENT_ID)) {
            ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(Constants.TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class);
            if (null != tabbedMenuHUDElement) {
                nuiManager.getHUD().removeHUDElement(tabbedMenuHUDElement);
            }
        } else {
            ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(Constants.TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class);
            if (null == tabbedMenuHUDElement) {
                tabbedMenuHUDElement = nuiManager.getHUD().addHUDElement(Constants.TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class, Rect2f.createFromMinAndSize(0, 0, 1, 1));
            }
        }
        nuiManager.toggleScreen(Constants.MOUSE_CAPTURING_SCREEN_UIELEMENT_ID);
    }

    // NOTE: this is not currently supported for HUD elements, and we would rather monitor when a minion component is created/destroyed in any case
    public void update(float delta) {
        if (nuiManager.isOpen(Constants.MOUSE_CAPTURING_SCREEN_UIELEMENT_ID)) {
            ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(Constants.TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class);
            if (null != tabbedMenuHUDElement) {
                tabbedMenuHUDElement.update(delta);
            }
        }
    }

    public void setPlantMode() {
        currentManagerCommandMode = ManagerCommandMode.Plant;
    }

    public void setDigMode() {
        currentManagerCommandMode = ManagerCommandMode.Dig;
    }

    public void setCutTreeMode() {
        currentManagerCommandMode = ManagerCommandMode.CutTree;
    }
}
