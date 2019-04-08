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
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.managerInterface.managementBookComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.managerInterface.VillageEntity;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.spawning.OreonSpawnEvent;

import java.util.*;

/**
 * The screen which is triggered when player interacts({@code e press}) with a Management book block.
 */
public class ManagementScreenLayer extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(ManagementScreenLayer.class);

    @In
    private EntityManager entityManager;

    @In
    private LocalPlayer localPlayer;

    @In
    private PrefabManager prefabManager;

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

        List<String> cityNames = new ArrayList<String>();
        cityNames.add("Lordaeron");
        cityNames.add("Prague");
        cityNames.add("Kategad");
        cityNames.add("London");
        cityNames.add("Berlin");
        cityNames.add("Helsinki");
        //
        cityNames.add("Viena");
        cityNames.add("NewYork");
        cityNames.add("Brno");
        cityNames.add("Katowice");

        debugOwnedVillages = new ArrayList<VillageEntity>();
        for (int i = 0; i < 6; i++) {
            VillageEntity city = new VillageEntity(cityNames.get(i),"nightmaredev");
            debugOwnedVillages.add(city);
        }

        debugManagedVillages = new ArrayList<VillageEntity>();
        for (int i = 6; i < 10; i++) {
            VillageEntity city = new VillageEntity(cityNames.get(i),"xXxSlayer1337");
            debugManagedVillages.add(city);
        }

        ownedVillagesList.subscribeSelection((widget, item) -> {
            DisplayVillageInfo((VillageEntity)item);
        });
        managedVillagesList.subscribeSelection((widget, item) -> {
            DisplayVillageInfo((VillageEntity)item);
        });
        populateLists();
    }

    /**
     * Popultes the text label fields in the screen with the items required for spawning.
     * @param cityName The Oreon prefab for which the label is being set.
     * @param label The label to be set.
     */
    private void populateUiLabels(String cityName, UILabel label) {
        StringBuilder text = new StringBuilder("Teleport to " + cityName);
        label.setText(text.toString());
    }

    private void writeOutCity(String cityName) {
        logger.info("Teleported player to " + cityName);
    }

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

    public void DisplayVillageInfo(VillageEntity village){
        selectedVillageNameLabel.setText(village.VillageName);

        String infoText = String.format(
                "Owner: %s\nStatistics:\nNumber of Oreons: %d\nBuildings:\n", village.OwnerName, village.OreonCount
        );

        for(String buildingName : village.BuildingList){
            infoText += buildingName + "\n";
        }

        selectedVillageInfoLabel.setText(infoText);
    }
}
