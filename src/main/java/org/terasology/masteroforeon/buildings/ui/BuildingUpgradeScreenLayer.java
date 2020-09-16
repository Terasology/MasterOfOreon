// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.buildings.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.masteroforeon.MooConstants;
import org.terasology.masteroforeon.buildings.BuildingUpgradeSystem;
import org.terasology.masteroforeon.buildings.components.ConstructedBuildingComponent;
import org.terasology.masteroforeon.buildings.events.GuardBuildingEvent;
import org.terasology.masteroforeon.buildings.events.UpgradeBuildingEvent;
import org.terasology.masteroforeon.holdingSystem.components.HoldingComponent;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;

public class BuildingUpgradeScreenLayer extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(BuildingUpgradeSystem.class);
    @In
    LocalPlayer localPlayer;
    @In
    private Context context;
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
