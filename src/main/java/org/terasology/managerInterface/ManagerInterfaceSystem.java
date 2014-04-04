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
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.action.GiveItemAction;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.managerInterface.nui.ManagerInterfaceHUDElement;
import org.terasology.managerInterface.nui.ToggleMouseGrabberButton;
import org.terasology.math.Rect2f;
import org.terasology.miniion.components.AssignedTaskType;
import org.terasology.miniion.componentsystem.controllers.RevisedSimpleMinionAISystem;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;

@Share(ManagerInterfaceSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class ManagerInterfaceSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ManagerInterfaceSystem.class);
    public static final String MOUSE_CAPTURING_SCREEN_UIELEMENT_ID = "managerinterface:mouseCapturingScreen";
    public static final String TABBED_MENU_WIDGET_ID = "managerinterface:tabbedMenu";

    enum ManagerCommandMode {
        None,
        Plant,
        Dig,
        CutTree
    }
    
    private ManagerCommandMode currentManagerCommandMode = ManagerCommandMode.None;
    
    @In 
    private RevisedSimpleMinionAISystem aiSystem;
    
    @In
    private NUIManager nuiManager;
    
    @In
    private EntityManager entityManager;

    @Override
    public void postBegin() {
        toggleManagerInterface();
    }
    
    // TODO: this should be in a separate class, most likely.  Maybe ManagerInterface
    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player, InventoryComponent inventory) {
        createAndGiveItemToPlayerIfPossible("managerinterface" + ":" + "zonetool", player);
    }

    private void createAndGiveItemToPlayerIfPossible(String uri, EntityRef player) {
        EntityRef item = entityManager.create(uri);
        GiveItemAction action = new GiveItemAction(EntityRef.NULL, item);
        player.send(action);
        if (!action.isConsumed()) {
            logger.warn(uri + " could not be created and given to player.");
            item.destroy();
        }
            
    }

    @ReceiveEvent
    public void onSelection(ApplyBlockSelectionEvent event, EntityRef entity) {
        switch (currentManagerCommandMode) {
            case None:
                // do nothing
                break;
            case Plant:
                aiSystem.createAssignedTask(AssignedTaskType.Plant, event.getSelection());
                break;
            case Dig:
                aiSystem.createAssignedTask(AssignedTaskType.Dig, event.getSelection());
                break;
        }
    }

    // Higher priority than critical because NUI grabs all input when mouse is released
    @ReceiveEvent(components = ClientComponent.class, priority=250)
    public void onToggleMouseGrabber(ToggleMouseGrabberButton event, EntityRef entity) {
        if (event.isDown()) {
            toggleManagerInterface();

            event.consume();
        }
    }

    private void toggleManagerInterface() {
        if (nuiManager.isOpen(MOUSE_CAPTURING_SCREEN_UIELEMENT_ID)) {
            ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class);
            if (null != tabbedMenuHUDElement) {
                nuiManager.getHUD().removeHUDElement(tabbedMenuHUDElement);
            }
        } else {
            ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class);
            if (null == tabbedMenuHUDElement) {
                tabbedMenuHUDElement = nuiManager.getHUD().addHUDElement(TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class, Rect2f.createFromMinAndSize(0, 0, 1, 1));
            }
        }
        nuiManager.toggleScreen(MOUSE_CAPTURING_SCREEN_UIELEMENT_ID);
    }
    
    // NOTE: this is not currently supported for HUD elements, and we would rather monitor when a minion component is created/destroyed in any case
    public void update(float delta) {
        if (nuiManager.isOpen(MOUSE_CAPTURING_SCREEN_UIELEMENT_ID)) {
            ManagerInterfaceHUDElement tabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(TABBED_MENU_WIDGET_ID, ManagerInterfaceHUDElement.class);
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
