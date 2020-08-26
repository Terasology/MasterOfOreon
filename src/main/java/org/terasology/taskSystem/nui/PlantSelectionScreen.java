/*
 * Copyright 2019 MovingBlocks
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

import org.terasology.logic.players.LocalPlayer;
import org.terasology.nui.widgets.ResettableUIText;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIList;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.PlantType;
import org.terasology.taskSystem.events.SetTaskTypeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlantSelectionScreen extends CoreScreenLayer {
    private UIButton plantButton;
    private UIList<String> uiList;
    private ResettableUIText searchBar;
    @In
    private NUIManager nuiManager;

    @In
    private LocalPlayer localPlayer;

    String plantSelected = "";

    @Override
    public void initialise() {
        plantButton = find("plant", UIButton.class);
        uiList = find("plantsList", UIList.class);
        searchBar = find("plantSearchBar", ResettableUIText.class);
        populatePlantScroll();

        uiList.subscribeSelection((widget, item) -> plantSelected = item);

        plantButton.subscribe(button -> {
            String task = AssignedTaskType.PLANT;
            if (plantSelected != "") {
                switch (plantSelected) {
                    case "Cookie Crops":
                        sendPlantTypeEvent(task, PlantType.CookieCrops);
                        break;
                    case "Fudge Flowers":
                        sendPlantTypeEvent(task, PlantType.FudgeFlowers);
                        break;
                    case "Random":
                        sendPlantTypeEvent(task, chooseRandomPlant());
                        break;
                    default:
                        sendPlantTypeEvent(task, PlantType.CookieCrops);
                        break;
                }
            }
        });

        searchBar.subscribe(button -> {
            filterSearchResults(searchBar.getText());
            uiList.select(0);
        });
    }

    private PlantType chooseRandomPlant() {
        String randomElement = uiList.getList().get(new Random().nextInt(uiList.getList().size()));
        switch (randomElement) {
            case "Cookie Crops":
                return PlantType.CookieCrops;
            case "Fudge Flowers":
                return PlantType.FudgeFlowers;
            default:
                return PlantType.CookieCrops;
        }
    }

    private void populatePlantScroll() {
        if (uiList == null) return;
        List<String> buttonList = new ArrayList<>();
        for (int i = 0; i < PlantType.values().length - 1; i++)
            buttonList.add(PlantType.values()[i].plantName);
        uiList.setEnabled(true);
        uiList.setList(buttonList);
        uiList.select(0);
    }

    private void filterSearchResults(String searchTerm) {
        List<String> buttonList = new ArrayList<>();
        for (int i = 0; i < PlantType.values().length - 1; i++) {
            if (PlantType.values()[i].name().toUpperCase().contains(searchTerm.toUpperCase())) {
                buttonList.add(PlantType.values()[i].plantName);
            }
        }
        if (buttonList.size() == 0) {
            uiList.setEnabled(false);
            buttonList.add(PlantType.None.plantName);
        } else {
            uiList.setEnabled(true);
        }
        uiList.setList(buttonList);
    }

    private void sendPlantTypeEvent(String assignedTask, PlantType plantType) {
        localPlayer.getCharacterEntity().send(new SetTaskTypeEvent(assignedTask, plantType));
        nuiManager.popScreen();
        nuiManager.closeAllScreens();
    }
}
