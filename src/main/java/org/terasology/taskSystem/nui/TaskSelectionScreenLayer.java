/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.taskSystem.nui;

import org.terasology.MooConstants;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIList;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIScreenLayer;
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
