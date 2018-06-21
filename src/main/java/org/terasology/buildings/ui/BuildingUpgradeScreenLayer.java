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
import org.terasology.Constants;
import org.terasology.buildings.BuildingUpgradeSystem;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.UpgradeBuildingEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;

public class BuildingUpgradeScreenLayer extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(BuildingUpgradeSystem.class);

    @In
    private Context context;

    private UILabel buildingName;
    private UILabel buildingLevel;
    private UIButton upgradeBuildingButton;

    private BuildingUpgradeSystem buildingUpgradeSystem;

    @Override
    public void initialise() {
        buildingUpgradeSystem = context.get(BuildingUpgradeSystem.class);

        buildingName = find(Constants.BUILDING_NAME_UI_LABEL_ID, UILabel.class);
        buildingLevel = find(Constants.BUILDING_LEVEL_UI_LABEL_ID, UILabel.class);
        upgradeBuildingButton = find(Constants.BUILDING_UPGRADE_COMMAND_UI_ID, UIButton.class);

        populateLabels();

        upgradeBuildingButton.subscribe(button -> {
            buildingUpgradeSystem.getBuildingToUpgrade().send(new UpgradeBuildingEvent());
        });
    }

    private void populateLabels() {
        EntityRef building = buildingUpgradeSystem.getBuildingToUpgrade();
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
        if (buildingComponent == null) {
            logger.info("component null");
            return;
        }
        buildingName.setText(buildingComponent.buildingType.toString());
        buildingLevel.setText("" + buildingComponent.currentLevel);
    }
}
