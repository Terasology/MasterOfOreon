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
package org.terasology.taskSystem.taskCompletion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.buildings.events.BuildingConstructionCompletedEvent;
import org.terasology.structureTemplates.components.SpawnBlockRegionsComponent;
import org.terasology.structureTemplates.events.SpawnStructureEvent;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.structureTemplates.util.BlockRegionTransform;
import org.terasology.taskSystem.BuildingType;

import java.util.ArrayList;
import java.util.List;

public class ConstructingFromStructureTemplate implements BuildTaskCompletion {
    private static final Logger logger = LoggerFactory.getLogger(ConstructingFromStructureTemplate.class);

    private StructureTemplateProvider structureTemplateProvider;
    private EntityRef buildingTemplate;
    private EntityRef player;

    public ConstructingFromStructureTemplate(StructureTemplateProvider templateProvider, EntityRef playerEntity) {
        this.structureTemplateProvider = templateProvider;
        this.player = playerEntity;
    }

    public void constructBuilding(Region3i selectedRegion, BuildingType buildingType) {
        int minX = selectedRegion.minX();
        int maxX = selectedRegion.maxX();
        int minY = selectedRegion.minY();
        int minZ = selectedRegion.minZ();
        int maxZ = selectedRegion.maxZ();

        Vector3i centerBlockPosition = new Vector3i((minX + maxX) / 2, minY, (minZ + maxZ) / 2);
        logger.info("Center" + centerBlockPosition);

        constructBuilding(centerBlockPosition, buildingType, 0);

        sendConstructionCompleteEvent(centerBlockPosition, buildingType);
    }

    public void constructBuilding(Vector3i centerBlockPosition, BuildingType buildingType, int level) {
        selectBuilding(buildingType, level);

        logger.info("Placing Building : " + buildingTemplate.getParentPrefab().getName());

        buildingTemplate.send(new SpawnStructureEvent(BlockRegionTransform.createRotationThenMovement(Side.FRONT, Side.FRONT, centerBlockPosition)));
    }

    public void selectBuilding(BuildingType buildingType, int level) {
        switch (buildingType) {
            case Diner :
                buildingTemplate = structureTemplateProvider.getRandomTemplateOfType(Constants.STRUCTURE_TEMPLATE_TYPE_DINER + Integer.toString(level));
                break;

            case Storage:
                buildingTemplate = structureTemplateProvider.getRandomTemplateOfType(Constants.STRUCTURE_TEMPLATE_TYPE_STORAGE + Integer.toString(level));
        }
    }

    private void sendConstructionCompleteEvent(Vector3i centerBlock, BuildingType buildingType) {
        SpawnBlockRegionsComponent blockRegionsComponent = buildingTemplate.getComponent(SpawnBlockRegionsComponent.class);
        List<SpawnBlockRegionsComponent.RegionToFill> relativeRegions = blockRegionsComponent.regionsToFill;

        List<Region3i> absoluteRegions = new ArrayList<>();

        for (SpawnBlockRegionsComponent.RegionToFill regionToFill : relativeRegions) {
            Region3i relativeRegion = regionToFill.region;
            Region3i absoluteRegion = relativeRegion.move(centerBlock);
            absoluteRegions.add(absoluteRegion);
        }

        // Add this building's regions to the Holding
        player.send(new BuildingConstructionCompletedEvent(absoluteRegions, buildingType, centerBlock));
    }
}
