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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public void initialise() {
        ownedVillagesList = find(Constants.OWNED_VILLAGES_LIST_ID, UIList.class);

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
        Binding<List> villageList = new ReadOnlyBinding<List>() {
            @Override
            public List get() {
                List<VillageEntity> villageNames = new ArrayList<>();

                /* DEBUG*/

                for (int i = 0; i < 6; i++) {
                    VillageEntity city = new VillageEntity("City"+i,"nightmare");
                    villageNames.add(city);
                }
                /* DEBUG*/

                return villageNames;
            }
        };

        ownedVillagesList.bindList(villageList);
    }
}
