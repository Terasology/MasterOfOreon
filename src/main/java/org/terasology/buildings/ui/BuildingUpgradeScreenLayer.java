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
package org.terasology.buildings.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.buildings.BuildingUpgradeSystem;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.GuardBuildingEvent;
import org.terasology.buildings.events.UpgradeBuildingEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;

public class BuildingUpgradeScreenLayer extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(BuildingUpgradeSystem.class);

    @In
    private Context context;

    @In
    LocalPlayer localPlayer;

    private UILabel buildingName;
    private UILabel buildingLevel;
    private UIButton upgradeBuildingButton;
    private UIButton guardBuildingButton;

    private BuildingUpgradeSystem buildingUpgradeSystem;

    @Override
    public void initialise() {
        buildingUpgradeSystem = context.get(BuildingUpgradeSystem.class);

        buildingName = find(MooConstants.BUILDING_NAME_UI_LABEL_ID, UILabel.class);
        buildingLevel = find(MooConstants.BUILDING_LEVEL_UI_LABEL_ID, UILabel.class);
        upgradeBuildingButton = find(MooConstants.BUILDING_UPGRADE_COMMAND_UI_ID, UIButton.class);
        guardBuildingButton = find(MooConstants.GUARD_BUILDING_COMMAND_UI_ID, UIButton.class);

        populateLabels();

        upgradeBuildingButton.subscribe(button -> {
            localPlayer.getCharacterEntity().send(new UpgradeBuildingEvent());
        });

        guardBuildingButton.subscribe(button -> {
            localPlayer.getCharacterEntity().send(new GuardBuildingEvent());
        });
    }

    private void populateLabels() {
        HoldingComponent holdingComponent = localPlayer.getCharacterEntity().getComponent(HoldingComponent.class);
        EntityRef building = holdingComponent.lastBuildingInteractedWith;
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
        if (buildingComponent == null) {
            logger.info("ConstructedBuildingComponent null");
            return;
        }


        Binding<String> buildingNameBinding = new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return buildingComponent.buildingType.toString();
            }
        };

        Binding<String> buildingLevelBinding = new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return String.valueOf(buildingComponent.currentLevel);
            }
        };

        buildingName.bindText(buildingNameBinding);
        buildingLevel.bindText(buildingLevelBinding);
    }

    @Override
    public void onScreenOpened() {
        super.onScreenOpened();
        populateLabels();
    }
}
