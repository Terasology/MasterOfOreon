// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.taskCompletion;

import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.buildings.events.BuildingConstructionStartedEvent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.structureTemplates.components.CompletionTimeComponent;
import org.terasology.structureTemplates.components.SpawnBlockRegionsComponent;
import org.terasology.structureTemplates.events.SpawnStructureEvent;
import org.terasology.structureTemplates.interfaces.StructureTemplateProvider;
import org.terasology.structureTemplates.util.BlockRegionTransform;
import org.terasology.taskSystem.BuildingType;

import java.util.List;
import java.util.stream.Collectors;

public class ConstructingFromStructureTemplate implements BuildTaskCompletion {
    private static final Logger logger = LoggerFactory.getLogger(ConstructingFromStructureTemplate.class);

    private final StructureTemplateProvider structureTemplateProvider;
    private EntityRef buildingTemplate;
    private final EntityRef player;

    public ConstructingFromStructureTemplate(StructureTemplateProvider templateProvider, EntityRef playerEntity) {
        this.structureTemplateProvider = templateProvider;
        this.player = playerEntity;
    }

    public void constructBuilding(BlockRegion selectedRegion, BuildingType buildingType) {
        int minX = selectedRegion.minX();
        int maxX = selectedRegion.maxX();
        int minY = selectedRegion.minY();
        int minZ = selectedRegion.minZ();
        int maxZ = selectedRegion.maxZ();

        Vector3i centerBlockPosition = new Vector3i((minX + maxX) / 2, minY, (minZ + maxZ) / 2);
        logger.info("Center" + centerBlockPosition);

        constructBuilding(centerBlockPosition, buildingType, 0, EntityRef.NULL, player);

    }

    public void constructBuilding(Vector3i centerBlockPosition, BuildingType buildingType, int level, EntityRef building, EntityRef playerEntity) {
        buildingTemplate = selectAndReturnBuilding(buildingType, level);

        if (buildingTemplate == null) {
            logger.info("Could not find a prefab for the selected building");
            return;
        }

        logger.info("Placing Building : " + buildingTemplate.getParentPrefab().getName());

        buildingTemplate.send(new SpawnStructureEvent(BlockRegionTransform.createRotationThenMovement(Side.FRONT, Side.FRONT, centerBlockPosition)));

        sendConstructionStartEvent(centerBlockPosition, buildingType, building, playerEntity);
    }

    public void selectBuilding(BuildingType buildingType, int level) {
        buildingTemplate = selectAndReturnBuilding(buildingType, level);
    }

    public EntityRef selectAndReturnBuilding(BuildingType buildingType, int level) {
        EntityRef building = EntityRef.NULL;
        switch (buildingType) {
            case Diner :
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_DINER+"Level" + level);
                break;
            case Storage :
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_STORAGE+"Level" + level);
                break;
            case Laboratory :
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_LABORATORY+"Level" + level);
                break;
            case Jail :
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_JAIL+"Level" + level);
                break;
            case Church :
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_CHURCH+"Level" + level);
                break;
            case Hospital:
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_HOSPITAL+"Level"+ level);
                break;
            case Bedroom :
                building = structureTemplateProvider.getRandomTemplateOfType(MooConstants.STRUCTURE_TEMPLATE_TYPE_BEDROOM+"Level" + level);
                break;
        }

        return building;
    }

    private void sendConstructionStartEvent(Vector3i centerBlock, BuildingType buildingType, EntityRef building, EntityRef playerEntity) {
        // Add this building's regions to the Holding
        playerEntity.send(getBuildingConstructionStartedEvent(centerBlock, buildingType, building));
    }

    public BuildingConstructionStartedEvent getBuildingConstructionStartedEvent(Vector3i centerBlock, BuildingType buildingType, EntityRef building) {
        SpawnBlockRegionsComponent blockRegionsComponent = buildingTemplate.getComponent(SpawnBlockRegionsComponent.class);
        List<SpawnBlockRegionsComponent.RegionToFill> relativeRegions = blockRegionsComponent.regionsToFill;

        List<BlockRegion> absoluteRegions = relativeRegions.stream()
                .map(regionToFill -> new BlockRegion(regionToFill.region).translate(centerBlock))
                .collect(Collectors.toList());

        long delay = buildingTemplate.getComponent(CompletionTimeComponent.class).completionDelay;

        return new BuildingConstructionStartedEvent(absoluteRegions, buildingType, centerBlock, building, delay);
    }
}
