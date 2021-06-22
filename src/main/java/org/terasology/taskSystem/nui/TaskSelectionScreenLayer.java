// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.nui;

import org.terasology.MooConstants;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.UIScreenLayer;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIList;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.events.SetTaskTypeEvent;

import java.util.ArrayList;
import java.util.List;

public class TaskSelectionScreenLayer extends CoreScreenLayer {
    @In
    private LocalPlayer localPlayer;

    @In
    private NUIManager nuiManager;

    private UIButton cancelButton;
    private UIButton tasksTabButton;
    private UIButton buildingsTabButton;
    private UIButton confirmButton;

    private UIList<String> taskSelectionScreenList;

    private UIList<String> tasksList;
    private UIList<String> buildingsList;
    private UIScreenLayer plantSelectionScreenLayer;

    @Override
    public void initialise() {
        cancelButton = find(MooConstants.CANCEL_BUTTON_ID, UIButton.class);
        tasksTabButton = find(MooConstants.TASKS_TAB_BUTTON, UIButton.class);
        buildingsTabButton = find(MooConstants.BUILDINGS_TAB_BUTTON, UIButton.class);
        confirmButton = find(MooConstants.CONFIRM_BUTTON_ID, UIButton.class);

        taskSelectionScreenList = find(MooConstants.TASK_SELECTION_SCREEN_LIST, UIList.class);

        populateTasksList();
        tasksTabButton.setActive(true);

        tasksTabButton.subscribe(button -> {
            populateTasksList();
        });

        buildingsTabButton.subscribe(button -> {
            populateBuildingsList();
        });

        cancelButton.subscribe(button -> {
            sendSetTaskTypeEvent();
        });

        confirmButton.subscribe(click -> performAction(taskSelectionScreenList.getSelection()));
    }

    private void sendSetTaskTypeEvent() {
        localPlayer.getCharacterEntity().send(new SetTaskTypeEvent());
    }

    private void sendSetTaskTypeEvent(String assignedTaskType) {
        localPlayer.getCharacterEntity().send(new SetTaskTypeEvent(assignedTaskType));
    }

    private void sendSetTaskTypeEvent(String assignedTaskType, BuildingType buildingType) {
        localPlayer.getCharacterEntity().send(new SetTaskTypeEvent(assignedTaskType, buildingType));
    }

    private void populateBuildingsList() {
        List<String> buttonList = new ArrayList<>();
        buttonList.add("Diner");
        buttonList.add("Storage");
        buttonList.add("Laboratory");
        buttonList.add("Hospital");
        buttonList.add("Jail");
        buttonList.add("Church");
        buttonList.add("Bedroom");
        taskSelectionScreenList.setList(buttonList);

        tasksTabButton.setActive(false);
        buildingsTabButton.setActive(true);
    }

    private void populateTasksList() {
        List<String> buttonList = new ArrayList<>();
        buttonList.add("Plant");
        buttonList.add("Guard");
        taskSelectionScreenList.setList(buttonList);

        buildingsTabButton.setActive(false);
        tasksTabButton.setActive(true);
    }

    private void performAction(String selection) {
        String task = AssignedTaskType.NONE;
        switch (selection) {
            case "Plant":
                nuiManager.closeAllScreens();
                plantSelectionScreenLayer = nuiManager.createScreen("plantSelectionScreen");
                nuiManager.pushScreen(plantSelectionScreenLayer);
                break;
            case "Guard":
                task = AssignedTaskType.GUARD;
                sendSetTaskTypeEvent(task);
                break;
            default:
                sendSetTaskTypeEvent(AssignedTaskType.BUILD, BuildingType.valueOf(selection));
        }
    }
}
