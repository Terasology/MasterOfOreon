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
package org.terasology.buildings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.CloseUpgradeScreenEvent;
import org.terasology.buildings.events.OpenUpgradeScreenEvent;
import org.terasology.buildings.events.UpgradeBuildingEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.characters.CharacterHeldItemComponent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.taskSystem.taskCompletion.ConstructFromStructureTemplate;

import java.util.List;

@Share(BuildingUpgradeSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class BuildingUpgradeSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(BuildingUpgradeSystem.class);

    @In
    private Context context;

    private ConstructFromStructureTemplate constructFromStructureTemplate;

    private EntityRef buildingToUpgrade;

    @Override
    public void postBegin() {
        constructFromStructureTemplate = context.get(ConstructFromStructureTemplate.class);
    }

    @ReceiveEvent
    public void onActivateEvent(ActivateEvent event, EntityRef entityRef) {
        logger.info("event received activate");
        EntityRef player = event.getInstigator();
        CharacterHeldItemComponent heldItemComponent = player.getComponent(CharacterHeldItemComponent.class);

        EntityRef selectedItem = heldItemComponent.selectedItem;
        DisplayNameComponent displayNameComponent = selectedItem.getComponent(DisplayNameComponent.class);

        if (!displayNameComponent.name.equals(Constants.UPGRADE_TOOL_NAME)) {
            return;
        }

        Vector3f hitBlockPos = event.getTargetLocation();

        checkIfPartOfBuilding(hitBlockPos, player);
    }

    /**
     * Checks if the block activated using the upgrade tool is part of a building
     * @param blockPos The position of the block to be checked
     * @param player The entity which sends the event
     */
    private void checkIfPartOfBuilding(Vector3f blockPos, EntityRef player) {
        HoldingComponent holdingComponent = player.getComponent(HoldingComponent.class);
        List<EntityRef> buildings = holdingComponent.constructedBuildings;

        for (EntityRef building : buildings) {
            ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
            List<Region3i> buildingRegions = buildingComponent.boundingRegions;

            for (Region3i buildingRegion : buildingRegions) {
                Vector3i target = new Vector3i(blockPos);
                if (buildingRegion.encompasses(target)) {
                    logger.info("hit building" + buildingComponent.buildingType);
                    this.buildingToUpgrade = building;
                    building.send(new OpenUpgradeScreenEvent());
                }
            }
        }
    }

    public EntityRef getBuildingToUpgrade() {
        return buildingToUpgrade;
    }

    /**
     * Receives the {@link UpgradeBuildingEvent} triggered by the Upgrade Button on the BuildingUpgradeScreen
     * @param upgradeBuildingEvent The event received
     */
    @ReceiveEvent
    public void onReceiveBuildingUpgradeEvent(UpgradeBuildingEvent upgradeBuildingEvent, EntityRef building) {
        building.send(new CloseUpgradeScreenEvent());
        logger.info("building upgrade started");

        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);
        Vector3i centerLocation = buildingComponent.centerLocation;

        buildingComponent.currentLevel += 1;

        building.saveComponent(buildingComponent);

        constructFromStructureTemplate.constructBuilding(centerLocation, buildingComponent.buildingType, buildingComponent.currentLevel);
    }
}
