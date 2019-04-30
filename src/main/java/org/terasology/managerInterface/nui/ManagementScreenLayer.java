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
package org.terasology.managerInterface.nui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.namegenerator.creature.CreatureAssetTheme;
import org.terasology.namegenerator.creature.CreatureNameProvider;
import org.terasology.namegenerator.town.TownNameProvider;
import org.terasology.namegenerator.town.TownAssetTheme;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.managerInterface.VillageEntity;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.terasology.taskSystem.BuildingType;

/**
 * The screen which is triggered when player interacts({@code e press}) with a Management book block.
 */
public class ManagementScreenLayer extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(ManagementScreenLayer.class);

    @In
    private LocalPlayer localPlayer;

    private UIList ownedVillagesList;
    private UIList managedVillagesList;
    private UILabel selectedVillageNameLabel;
    private UILabel selectedVillageInfoLabel;

    private List<VillageEntity> debugOwnedVillages;
    private List<VillageEntity> debugManagedVillages;


    @Override
    public void initialise() {
        ownedVillagesList = find(Constants.OWNED_VILLAGES_LIST_ID, UIList.class);
        managedVillagesList = find(Constants.MANAGED_VILLAGES_LIST_ID, UIList.class);
        selectedVillageNameLabel = find(Constants.SELECTED_VILLAGE_NAME_LABEL_ID, UILabel.class);
        selectedVillageInfoLabel = find(Constants.SELECTED_VILLAGE_INFO_LABEL_ID, UILabel.class);
        selectedVillageNameLabel.setText("");
        selectedVillageInfoLabel.setText("");

        CreateDebugData();

        ownedVillagesList.subscribeSelection((widget, item) -> {
            DisplayVillageInfo((UIList)widget, (VillageEntity)item);
        });
        managedVillagesList.subscribeSelection((widget, item) -> {
            DisplayVillageInfo((UIList)widget, (VillageEntity)item);
        });
        populateLists();
    }

    /* DEBUG WILL BE REMOVED AFTER LIST OF VILLAGES IN WORLD IS INTRODUCED */
    private void CreateDebugData(){
        CreatureNameProvider creatureNameProvider = new CreatureNameProvider(1337, CreatureAssetTheme.DEFAULT);
        TownNameProvider townNameProvider = new TownNameProvider(1337, TownAssetTheme.FANTASY);

        List<BuildingType> availableTypes = new ArrayList<BuildingType>();
        BuildingType[] allTypes = BuildingType.values();
        for(int i = 1; i < allTypes.length; i++){
            availableTypes.add(allTypes[i]);
        }

        String localPlayerName = "LocalPlayer";

        Random rnd = new Random();

        debugOwnedVillages = new ArrayList<VillageEntity>();
        for (int i = 0; i < 10; i++) {
            int OreonCount = rnd.nextInt((50 - 2) + 1) + 2;
            List<String> buildingsInTown = new ArrayList<String>();

            for(BuildingType bt : availableTypes){
                if(rnd.nextInt((5 - 0) + 1) + 0 < 1){
                    buildingsInTown.add(bt.name());
                }
            }

            VillageEntity city = new VillageEntity(townNameProvider.generateName(), localPlayerName, OreonCount, buildingsInTown);
            debugOwnedVillages.add(city);
        }

        debugManagedVillages = new ArrayList<VillageEntity>();
        for (int i = 10; i < 16; i++) {
            int OreonCount = rnd.nextInt((50 - 2) + 1) + 2;
            List<String> buildingsInTown = new ArrayList<String>();

            for(BuildingType bt : availableTypes){
                if(rnd.nextInt((5 - 0) + 1) + 0 < 1){
                    buildingsInTown.add(bt.name());
                }
            }

            VillageEntity city = new VillageEntity(townNameProvider.generateName(),creatureNameProvider.generateName(), OreonCount, buildingsInTown);
            debugManagedVillages.add(city);
        }
    }
    /* DEBUG WILL BE REMOVED AFTER LIST OF VILLAGES IN WORLD IS INTRODUCED */

    private void populateLists() {
        Binding<List> ownedVillageList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                return getVillages(debugOwnedVillages);
            }
        };

        Binding<List> managedVillageList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                return getVillages(debugManagedVillages);
            }
        };

        ownedVillagesList.bindList(ownedVillageList);
        managedVillagesList.bindList(managedVillageList);
    }

    private List<VillageEntity> getVillages(List<VillageEntity> veList){
        List<VillageEntity> villages = new ArrayList<>();

        for (VillageEntity ve : veList) {
            villages.add(ve);
        }
        return villages;
    }

    public void DisplayVillageInfo(UIList widget, VillageEntity village){
        if(village == null) return;
        if(widget == ownedVillagesList){
            managedVillagesList.setSelection(null);
        } else {
            ownedVillagesList.setSelection(null);
        }

        selectedVillageNameLabel.setText(village.VillageName);

        String infoText = String.format(
                "Owner: %s\nStatistics:\nNumber of Oreons: %d\nBuildings:\n", village.OwnerName, village.OreonCount
        );

        if(village.BuildingList.size() > 0){
            for(String buildingName : village.BuildingList){
                infoText += buildingName + "\n";
            }
        } else {
            infoText += "No building in village" + "\n";
        }

        selectedVillageInfoLabel.setText(infoText);
    }
}
