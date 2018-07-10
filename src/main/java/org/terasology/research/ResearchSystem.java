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
package org.terasology.research;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.Constants;
import org.terasology.books.logic.BookComponent;
import org.terasology.books.logic.BookRecipeComponent;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.BuildingConstructionCompletedEvent;
import org.terasology.buildings.events.BuildingUpgradeStartEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.research.components.LaboratoryComponent;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.BuildingType;
import org.terasology.taskSystem.Task;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.tasks.ResearchTask;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ResearchSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ResearchSystem.class);

    @In
    BlockEntityRegistry blockEntityRegistry;
    @In
    InventoryManager inventoryManager;
    @In
    EntityManager entityManager;
    @In
    PrefabManager prefabManager;
    @In
    TaskManagementSystem taskManagementSystem;

    /**
     * This method adds books to a newly constructed Laboratory's bookcase
     * @param event The event sent.
     * @param player The player entity which triggered the building construction
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void onLaboratoryConstruction(BuildingConstructionCompletedEvent event, EntityRef player) {
        if (!event.buildingType.equals(BuildingType.Laboratory)) {
            return;
        }

        addBooksToCase(player, event.absoluteRegions, 0);

        Vector3i pedestalLocation = event.absoluteRegions.get(Constants.PEDESTAL_REGION_INDEX).max();
        EntityRef pedestalEntity = blockEntityRegistry.getBlockEntityAt(pedestalLocation);
        LaboratoryComponent laboratoryComponent = pedestalEntity.getComponent(LaboratoryComponent.class);
        laboratoryComponent.laboratoryEntity = event.constructedBuildingEntity;
        pedestalEntity.saveComponent(laboratoryComponent);

    }

    /**
     * This method adds new books to the case after the Laboratory is upgraded. Receives an event sent by the {@link org.terasology.taskSystem.actions.PerformTaskNode}
     * @param upgradeStartEvent The event sent.
     * @param oreon The Oreon performing the upgrade task.
     * @param taskComponent TaskComponent attached to the Oreon.
     */
    @ReceiveEvent(components = {TaskComponent.class}, priority = EventPriority.PRIORITY_TRIVIAL)
    public void onLaboratoryUpgrade(BuildingUpgradeStartEvent upgradeStartEvent, EntityRef oreon, TaskComponent taskComponent) {
        EntityRef building = entityManager.getEntity(taskComponent.task.requiredBuildingEntityID);
        ConstructedBuildingComponent buildingComponent = building.getComponent(ConstructedBuildingComponent.class);

        addBooksToCase(building.getOwner(), buildingComponent.boundingRegions, buildingComponent.currentLevel);
    }

    private void addBooksToCase(EntityRef player, List<Region3i> absoluteRegions, int level) {
        logger.debug("Adding books to laboratory");
        // Get the bookcase block
        Vector3i bookcaseLocation = absoluteRegions.get(Constants.BOOKCASE_REGION_INDEX).max();
        EntityRef bookcaseEntity = blockEntityRegistry.getBlockEntityAt(bookcaseLocation);

        for (EntityRef book : getBooksToAdd(player, level)) {
            inventoryManager.giveItem(bookcaseEntity, bookcaseEntity, book);
        }
    }

    /**
     * Specifies the book blocks to be added to the laboratory bookcase according to the Laboratory's current level
     * @param level Current level of the laboratory
     * @return A list of book entities to be added
     */
    private List<EntityRef> getBooksToAdd(EntityRef player, int level) {
        List<EntityRef> booksToAdd = new ArrayList<>();

        switch (level) {
            case 0 :
                EntityRef book = entityManager.create(Constants.COOKIE_CROP_RESEARCH_BOOK);
                book.setOwner(player);
                logger.debug("book owner" + book.getOwner());
                booksToAdd.add(book);
        }

        return booksToAdd;
    }

    /**
     * Adds a Research Task to the Holding when the player adds a Research Book to the pedestal inventory.
     * @param event The event received
     * @param inventoryEntity The entity whose InventoryComponent is changed.
     * @param inventoryComponent The changed component
     */
    @ReceiveEvent
    public void onBookPlacedInInventory(OnChangedComponent event, EntityRef inventoryEntity, InventoryComponent inventoryComponent) {

        if (inventoryEntity.getParentPrefab().getName().equals(Constants.PEDESTAL_PREFAB)) {
            for (EntityRef item : inventoryComponent.itemSlots) {
                DisplayNameComponent nameComponent = item.getComponent(DisplayNameComponent.class);

                // Check if item is research Book
                if (nameComponent != null && nameComponent.name.equals(Constants.RESEARCH_BOOK_NAME)) {
                    BookComponent bookComponent = item.getComponent(BookComponent.class);
                    List<String> textList = bookComponent.pages;

                    String recipePrefabName = "";
                    for (String text : textList) {
                        int i = text.indexOf("<recipe");
                        if (i != -1) {
                            text = text.substring(i);
                            i = text.indexOf(">");
                            recipePrefabName = text.substring("<recipe".length(), i).replaceAll("\\s","");
                            logger.info(recipePrefabName);
                            break;
                        }
                    }

                    Prefab recipe = prefabManager.getPrefab(recipePrefabName);
                    BookRecipeComponent recipeComponent = recipe.getComponent(BookRecipeComponent.class);

                    LaboratoryComponent laboratoryComponent = inventoryEntity.getComponent(LaboratoryComponent.class);
                    addResearchTask(recipeComponent.blockIngredientsList, recipeComponent.blockResult, item.getOwner().getOwner(), laboratoryComponent.laboratoryEntity);

                    // add exclamation point
                    EntityRef exclamationPoint = entityManager.create(Constants.EXCLAMATION_PREFAB);
                    LocationComponent locationComponent = exclamationPoint.getComponent(LocationComponent.class);
                    LocationComponent pedestalLocationComponent = inventoryEntity.getComponent(LocationComponent.class);
                    locationComponent.setWorldPosition(pedestalLocationComponent.getWorldPosition().addY(1f));

                    inventoryManager.giveItem(inventoryEntity, inventoryEntity, exclamationPoint);

                    break;
                }
            }
        }
    }

    private void addResearchTask(List<Block> blockList, Block result, EntityRef player, EntityRef laboratory) {
        List<String> blockNameList = new ArrayList<>();

        for (Block block : blockList) {
            blockNameList.add(block.getURI().toString());
        }

        Task researchTask = new ResearchTask(blockNameList, result.getURI().toString());

        TaskComponent taskComponent = new TaskComponent();
        taskComponent.assignedTaskType = AssignedTaskType.Research;
        taskComponent.task = researchTask;

        ConstructedBuildingComponent buildingComponent = laboratory.getComponent(ConstructedBuildingComponent.class);
        taskComponent.taskRegion = buildingComponent.boundingRegions.get(Constants.PEDESTAL_REGION_INDEX);

        taskManagementSystem.addTask(player, entityManager.create(taskComponent));

    }
}
