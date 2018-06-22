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
import org.terasology.math.geom.Vector3i;
import org.terasology.buildings.events.BuildingConstructionCompletedEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.structureTemplates.components.SpawnBlockRegionsComponent;
import org.terasology.structureTemplates.events.SpawnStructureEvent;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.structureTemplates.util.transform.BlockRegionMovement;
import org.terasology.structureTemplates.util.transform.BlockRegionTransformationList;
import org.terasology.structureTemplates.util.transform.HorizontalBlockRegionRotation;
import org.terasology.taskSystem.BuildingType;
import org.terasology.world.BlockEntityRegistry;

import java.util.ArrayList;
import java.util.List;

public class ConstructingFromStructureTemplate implements BuildTaskCompletion {
    private static final Logger logger = LoggerFactory.getLogger(ConstructingFromStructureTemplate.class);

    @In
    private LocalPlayer localPlayer;

    private BlockEntityRegistry blockEntityRegistry;

    private StructureTemplateProvider structureTemplateProvider;

    private EntityRef buildingTemplate;

    public ConstructingFromStructureTemplate(StructureTemplateProvider templateProvider) {
        this.structureTemplateProvider = templateProvider;
    }

    public void constructBuilding(Region3i selectedRegion, BuildingType buildingType) {
        int minX = selectedRegion.minX();
        int maxX = selectedRegion.maxX();
        int minY = selectedRegion.minY();
        int minZ = selectedRegion.minZ();
        int maxZ = selectedRegion.maxZ();

        selectBuilding(buildingType);

        logger.info("Placing Building : " + buildingTemplate.getParentPrefab().getName());

        BlockRegionTransformationList transformationList = new BlockRegionTransformationList();

        Vector3i centerBlockPosition = new Vector3i((minX + maxX) / 2, minY, (minZ + maxZ) / 2);
        logger.info("Center" + centerBlockPosition);
        transformationList.addTransformation(new BlockRegionMovement(centerBlockPosition));

        int rotationAmount = 0;

        transformationList.addTransformation(new HorizontalBlockRegionRotation(rotationAmount));

        buildingTemplate.send(new SpawnStructureEvent(transformationList));

        sendConstructionCompleteEvent(centerBlockPosition, buildingType);

    }

    public void selectBuilding(BuildingType buildingType) {
        switch (buildingType) {
            case Diner :
                buildingTemplate = structureTemplateProvider.getRandomTemplateOfType(Constants.STRUCTURE_TEMPLATE_TYPE_DINER);
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
        localPlayer.getCharacterEntity().send(new BuildingConstructionCompletedEvent(absoluteRegions, buildingType, centerBlock));
    }
}
