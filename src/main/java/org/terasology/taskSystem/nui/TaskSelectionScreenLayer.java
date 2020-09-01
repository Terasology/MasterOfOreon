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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.books.DefaultDocumentData;
import org.terasology.cities.bldg.Building;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIList;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIScreenLayer;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.PlantType;
import org.terasology.taskSystem.events.SetTaskTypeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TaskSelectionScreenLayer extends CoreScreenLayer {
    @In
    private LocalPlayer localPlayer;

    @In
    private NUIManager nuiManager;

    private UIButton cancelButton;
    private UIButton buildTabButton;
    private UIButton plantTabButton;
    private UIButton guardTabButton;
    private UIButton confirmButton;
    private ResettableUIText selectionSearchBar;
    private UIList<String> selectionList;
    private UIImage selectionImage;
    private BrowserWidget selectionText;

    private static final Logger logger = LoggerFactory.getLogger(TaskSelectionScreenLayer.class);

    @Override
    public void initialise() {
        // buttons
        cancelButton = find(MooConstants.CANCEL_BUTTON_ID, UIButton.class);
        buildTabButton = find(MooConstants.BUILD_TAB_BUTTON, UIButton.class);
        plantTabButton = find(MooConstants.PLANT_TAB_BUTTON, UIButton.class);
        guardTabButton = find(MooConstants.GUARD_TAB_BUTTON, UIButton.class);
        confirmButton = find(MooConstants.CONFIRM_BUTTON_ID, UIButton.class);
        selectionSearchBar = find(MooConstants.TASK_SELECTION_SEARCH_BAR, ResettableUIText.class);
        selectionList = find(MooConstants.TASK_SELECTION_SCREEN_LIST, UIList.class);
        selectionImage = find(MooConstants.TASK_SELECTION_PREVIEW_IMAGE, UIImage.class);
        selectionText = find(MooConstants.TASK_SELECTION_PREVIEW_TEXT, BrowserWidget.class);

        // active elements
        populateBuildingsList();
        buildTabButton.setActive(true);

        // subscribers
        cancelButton.subscribe(button -> {
            sendSetTaskTypeEvent();
        });
        buildTabButton.subscribe(button -> {
            populateBuildingsList();
        });
        plantTabButton.subscribe(button -> {
            populatePlantsList();
        });
        guardTabButton.subscribe(button -> {
            populateGuardInfo();
        });
        confirmButton.subscribe(click -> performAction(selectionList.getSelection()));
        selectionSearchBar.subscribe(button -> {
            filterSearchResults(selectionSearchBar.getText());
            selectionList.select(0);
        });
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

    private void sendSetTaskTypeEvent(String assignedTaskType, PlantType plantType) {
        localPlayer.getCharacterEntity().send(new SetTaskTypeEvent(assignedTaskType, plantType));
    }

    private void populateBuildingsList() {
        List<String> buttonList = new ArrayList<>();
        for (int i = 0; i < BuildingType.values().length - 1; i++) {
            buttonList.add(BuildingType.values()[i].buildingName);
        }
        selectionList.setList(buttonList);
        selectionList.select(0);
        selectionSearchBar.setText("Type and enter to search");

        plantTabButton.setActive(false);
        guardTabButton.setActive(false);
        buildTabButton.setActive(true);
    }

    private void populatePlantsList() {
        List<String> buttonList = new ArrayList<>();
        for (int i = 0; i < PlantType.values().length - 1; i++) {
            buttonList.add(PlantType.values()[i].plantName);
        }
        selectionList.setList(buttonList);
        selectionList.select(0);
        selectionSearchBar.setText("Type and enter to search");

        buildTabButton.setActive(false);
        guardTabButton.setActive(false);
        plantTabButton.setActive(true);
    }

    private void populateGuardInfo() {
        List<String> buttonList = new ArrayList<>();
        selectionList.setList(buttonList);
        selectionText.navigateTo(createDocument(MooConstants.TASK_SELECTION_TEXT_GUARD));

        buildTabButton.setActive(false);
        plantTabButton.setActive(false);
        guardTabButton.setActive(true);
    }

    private void filterSearchResults(String searchTerm){
        List<String> buttonList = new ArrayList<>();
        if (plantTabButton.isActive()) {
            for (int i = 0; i < PlantType.values().length - 1; i++) {
                if (PlantType.values()[i].name().toUpperCase().contains(searchTerm.toUpperCase())) {
                    buttonList.add(PlantType.values()[i].plantName);
                }
            }
            if (buttonList.size() == 0) {
                selectionList.setEnabled(false);
                buttonList.add(PlantType.None.plantName);
            } else {
                selectionList.setEnabled(true);
            }
        } else if (buildTabButton.isActive()) {
            for (int i = 0; i < BuildingType.values().length - 1; i++) {
                if (BuildingType.values()[i].name().toUpperCase().contains(searchTerm.toUpperCase())) {
                    buttonList.add(BuildingType.values()[i].buildingName);
                }
            }
            if (buttonList.size() == 0) {
                selectionList.setEnabled(false);
                buttonList.add(BuildingType.None.buildingName);
            } else {
                selectionList.setEnabled(true);
            }

        }
        selectionList.setList(buttonList);
    }

    private static DocumentData createDocument(String text) {
        DefaultDocumentData page = new DefaultDocumentData(null);
        Collection<ParagraphData> paragraphs = new ArrayList<ParagraphData>();
        paragraphs.add(HTMLLikeParser.parseHTMLLikeParagraph(null, "<c " + "198"/*Color.BLACK.getRepresentation()*/ + ">" + text.replace("\n", "<l>") + "</c>"));
        page.addParagraphs(paragraphs);
        return page;
    }

    private void performAction(String selection){
        switch(selection) {
            case "Plant" :
                sendSetTaskTypeEvent(AssignedTaskType.PLANT, PlantType.valueOf(selection));
                break;
            case "Guard":
                sendSetTaskTypeEvent(AssignedTaskType.GUARD);
                break;
            case "Build":
                sendSetTaskTypeEvent(AssignedTaskType.BUILD, BuildingType.valueOf(selection));
                break;
            default:
                logger.warn("Unknown task \"" + selection + "\" selected");
                return;
        }
        nuiManager.closeAllScreens();
    }
}
