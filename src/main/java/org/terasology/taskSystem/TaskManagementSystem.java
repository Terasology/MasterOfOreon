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
package org.terasology.taskSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.buildings.events.BuildingConstructionCompletedEvent;
import org.terasology.buildings.events.BuildingConstructionStartedEvent;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.HoldingAuthoritySystem;
import org.terasology.holdingSystem.components.AssignedAreaComponent;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.input.cameraTarget.CameraTargetSystem;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.characters.CharacterHeldItemComponent;
import org.terasology.logic.characters.events.HorizontalCollisionEvent;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.logic.selection.MovableSelectionEndEvent;
import org.terasology.logic.selection.MovableSelectionStartEvent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.network.ColorComponent;
import org.terasology.network.NetworkComponent;
import org.terasology.notification.NotificationMessageEventMOO;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.nui.Color;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.structureTemplates.components.SpawnBlockRegionsComponent;
import org.terasology.taskSystem.assignment.AssignmentStrategy;
import org.terasology.taskSystem.assignment.BestFitStrategy;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.events.OpenTaskSelectionScreenEvent;
import org.terasology.taskSystem.tasks.BuildTask;
import org.terasology.taskSystem.tasks.HarvestTask;
import org.terasology.taskSystem.tasks.PlantTask;
import org.terasology.utilities.Assets;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemFactory;
import org.terasology.world.selection.BlockSelectionComponent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The authority task management system which handles task creation, adding them to the correct Holding and assigning tasks
 * to the Oreons.
 */
@Share(TaskManagementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class TaskManagementSystem extends BaseComponentSystem {
    public int minYOverall;

    private static final Logger logger = LoggerFactory.getLogger(TaskManagementSystem.class);
    private static final String CONSTRUCTION_COMPLETE_EVENT_ID = "taskManagementSystem:constructionComplete";
    private static final String ADD_TASK_DELAYED_ACTION_ID = "taskManagementSystem:addTask";

    @In
    private EntityManager entityManager;

    @In
    private Time timer;

    @In
    private Context context;

    @In
    private PrefabManager prefabManager;

    @In
    private DelayManager delayManager;

    @In
    private InventoryManager inventoryManager;

    @In
    private CameraTargetSystem cameraTargetSystem;

    private BlockManager blockManager;
    private BlockEntityRegistry blockEntityRegistry;
    private HoldingAuthoritySystem holdingSystem;
    private EntityRef notificationMessageEntity;
    private Vector3f lastCollisionLocation;
    private EntityRef pendingBuildTaskEntity;
    private Task pendingTask;

    @Override
    public void postBegin() {
        notificationMessageEntity = entityManager.create(MooConstants.NOTIFICATION_MESSAGE_PREFAB);

        DisplayNameComponent displayNameComponent = notificationMessageEntity.getComponent(DisplayNameComponent.class);
        displayNameComponent.name = "Task System";

        ColorComponent colorComponent = notificationMessageEntity.getComponent(ColorComponent.class);
        colorComponent.color = Color.BLACK;

        notificationMessageEntity.saveComponent(displayNameComponent);
        notificationMessageEntity.saveComponent(colorComponent);

        holdingSystem = context.get(HoldingAuthoritySystem.class);

        blockManager = context.get(BlockManager.class);
        blockEntityRegistry = context.get(BlockEntityRegistry.class);

        pendingBuildTaskEntity = EntityRef.NULL;
    }

    public boolean getTaskForOreon(Actor oreon) {
        return getTaskForOreon(oreon, new BestFitStrategy());
    }

    public boolean getTaskForOreon(Actor oreon, AssignmentStrategy strategy) {
        HoldingComponent oreonHolding = holdingSystem.getOreonHolding(oreon);
        Queue<EntityRef> availableTasks = oreonHolding.availableTasks;
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        logger.debug("Looking for task in " + oreonHolding);

        while (!availableTasks.isEmpty()) {
            Queue<EntityRef> possibleTasks = new LinkedList<>();
            Queue<EntityRef> recommendedTasks = new LinkedList<>();

            OreonAttributeComponent oreonAttributes = oreon.getComponent(OreonAttributeComponent.class);
            for (EntityRef taskEntity : availableTasks) {
                if (taskEntity == null) {
                    logger.warn("Null entity in task queue.");
                    availableTasks.remove(taskEntity);
                    continue;
                }

                TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);
                if (taskComponent == null) {
                    logger.warn("Task entity has no TaskComponent.");
                    availableTasks.remove(taskEntity);
                    continue;
                }

                if (taskMeetsRequirements(taskComponent.task, oreonAttributes)) {
                    if (taskIsRecommended(taskComponent.task, oreonAttributes)) {
                        recommendedTasks.add(taskEntity);
                    } else {
                        possibleTasks.add(taskEntity);
                    }
                }
            }

            if (recommendedTasks.isEmpty() && possibleTasks.isEmpty()) {
                continue;
            }

            EntityRef taskEntityToAssign;
            if (!recommendedTasks.isEmpty()) {
                taskEntityToAssign = strategy.getBestTask(oreonAttributes, recommendedTasks);
            } else {
                taskEntityToAssign = strategy.getBestTask(oreonAttributes, possibleTasks);
            }

            if (taskEntityToAssign == null) {
                continue;
            } else {
                availableTasks.remove(taskEntityToAssign);
            }


            TaskComponent taskComponentToAssign = taskEntityToAssign.getComponent(TaskComponent.class);

            oreonTaskComponent.task = taskComponentToAssign.task;

            oreonTaskComponent.assignedTaskType = taskComponentToAssign.assignedTaskType;
            oreonTaskComponent.creationTime = taskComponentToAssign.creationTime;
            oreonTaskComponent.taskRegion = taskComponentToAssign.taskRegion;
            oreonTaskComponent.taskStatus = TaskStatusType.InProgress;
            oreonTaskComponent.taskCompletionTime = getTaskCompletionTime(oreonTaskComponent.task);
            oreonTaskComponent.subsequentTaskType = taskComponentToAssign.subsequentTaskType;
            oreonTaskComponent.subsequentTask = taskComponentToAssign.subsequentTask;
            oreonTaskComponent.delayBeforeNextTask = taskComponentToAssign.delayBeforeNextTask;
            oreonTaskComponent.subsequentTaskRegion = taskComponentToAssign.subsequentTaskRegion;

            if (oreonTaskComponent.task.blockToRender != null) {
                placeBlockToRenderInInventory(oreon, oreonTaskComponent.task);
            }

            oreon.save(oreonTaskComponent);

            setOreonTarget(oreon, oreonTaskComponent.taskRegion.min());

            //destroy the entity since this task is no longer required
            taskEntityToAssign.destroy();

            return true;
        }

        return false;
    }

    private boolean taskMeetsRequirements(Task task, OreonAttributeComponent attributes) {
        return (attributes.intelligence >= task.minimumAttributes.intelligence &&
                attributes.strength >= task.minimumAttributes.strength &&
                attributes.health >= task.minimumAttributes.health &&
                attributes.hunger >= task.minimumAttributes.hunger);
    }

    private boolean taskIsRecommended(Task task, OreonAttributeComponent attributes) {
        return (attributes.intelligence >= task.recommendedAttributes.intelligence &&
                attributes.strength >= task.recommendedAttributes.strength &&
                attributes.health >= task.recommendedAttributes.health &&
                attributes.hunger >= task.recommendedAttributes.hunger);
    }

    /**
     * Checks if the item used for selection of an area is an Oreon Selection Tool
     * @param player
     * @return True - If held item is an Oreon Selection Tool
     */
    private boolean checkHeldItem(EntityRef player) {
        CharacterHeldItemComponent heldItemComponent = player.getComponent(CharacterHeldItemComponent.class);

        EntityRef selectedItem = heldItemComponent.selectedItem;

        String selectedItemName = selectedItem.getComponent(DisplayNameComponent.class).name;

        if (selectedItemName.equals("Oreon Selection Tool")) {
            return true;
        }

        return false;
    }

    /**
     * Receives the {@link ApplyBlockSelectionEvent} which is sent after a block selection end point is set. Also checks
     * if the item used for selection is an Oreon Selection Tool, if not the area is intended for another purpose.
     * @param blockSelectionEvent Event triggered after a block selection has been completed
     * @param player The player entity which triggers the event
     */
    @ReceiveEvent
    public void receiveNewTask(ApplyBlockSelectionEvent blockSelectionEvent, EntityRef player) {
        //check if held item is an Oreon Selection Tool
        if (!checkHeldItem(player)) {
            String message = "Use the Oreon selection Tool to mark areas for a task";
            player.getOwner().send(new NotificationMessageEventMOO(message, notificationMessageEntity));
            return;
        }

        //check if this area can be used
        if (!checkArea(blockSelectionEvent.getSelection())) {
            return;
        }

        player.send(new OpenTaskSelectionScreenEvent(blockSelectionEvent.getSelection()));
    }

    /**
     * Adds task to the player's holding.
     * This method can be used by external systems to add tasks to the holding.
     * @param player The player entity which has the holding
     * @param taskEntity The task entity to be added
     */
    public void addTask(EntityRef player, EntityRef taskEntity) {
        HoldingComponent oreonHolding = player.getComponent(HoldingComponent.class);

        TaskComponent taskComponent = taskEntity.getComponent(TaskComponent.class);

        logger.info("Adding task to " + oreonHolding);
        player.getOwner().send(new NotificationMessageEventMOO("Adding a new task of type : " + taskComponent.assignedTaskType, notificationMessageEntity));
        oreonHolding.availableTasks.add(taskEntity);
        player.saveComponent(oreonHolding);
    }

    public void setTaskType(String newTaskType, BuildingType buildingType, PlantType plantType, Region3i region, EntityRef player) {
        logger.info("Adding a new Task");

        // if the building extends to an area which is not suitable
//        if (newTaskType == AssignedTaskType.BUILD) {
//            region = getBuildingExtents(buildingType, region);
//
//            if (region == null) {
//                player.getOwner().send(
//                        new NotificationMessageEventMOO("Please select an open area which can accomadate the entire building",
//                                notificationMessageEntity));
//                return;
//            }
//        }

        TaskComponent taskComponent = new TaskComponent();
        taskComponent.taskRegion = region;
        taskComponent.creationTime = timer.getGameTimeInMs();

        BlockSelectionComponent newBlockSelectionComponent = new BlockSelectionComponent();
        newBlockSelectionComponent.shouldRender = true;

        taskComponent.assignedTaskType = newTaskType;
        Task newTask;

        switch (newTaskType) {
            case AssignedTaskType.PLANT :
                newTask = new PlantTask(plantType.path);
                taskComponent.subsequentTask = new HarvestTask();
                taskComponent.subsequentTaskType = AssignedTaskType.HARVEST;
                taskComponent.delayBeforeNextTask = 50000;

                newBlockSelectionComponent.currentSelection = taskComponent.taskRegion;

                taskComponent.task = newTask;

                placeFenceAroundRegion(taskComponent.taskRegion);

                newBlockSelectionComponent.texture = getAreaTexture(newTask);

                //mark this area so that no other task can be assigned here
                markArea(newBlockSelectionComponent, newTask, taskComponent, player);

                NetworkComponent networkComponent = new NetworkComponent();
                networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

                EntityRef task = entityManager.create(taskComponent, networkComponent);

                addTask(player, task);
                break;

            case AssignedTaskType.BUILD :
                pendingTask = new BuildTask(buildingType);
                newBlockSelectionComponent.currentSelection = getBuildingExtents(buildingType, region);
                newBlockSelectionComponent.isMovable = true;
                pendingBuildTaskEntity = entityManager.create(newBlockSelectionComponent);
                pendingBuildTaskEntity.setOwner(player);

                pendingBuildTaskEntity.send(new MovableSelectionStartEvent());
                break;

            default :
                newTask = new PlantTask(MooConstants.OREON_CROP_0_BLOCK);
        }
    }

    public void setTaskType(String newTaskType, PlantType plantType, Region3i region, EntityRef player) {
        logger.info("Adding a new Task");

        TaskComponent taskComponent = new TaskComponent();
        taskComponent.taskRegion = region;
        taskComponent.creationTime = timer.getGameTimeInMs();

        BlockSelectionComponent newBlockSelectionComponent = new BlockSelectionComponent();
        newBlockSelectionComponent.shouldRender = true;

        taskComponent.assignedTaskType = newTaskType;
        Task newTask;

        newTask = new PlantTask(MooConstants.OREON_CROP_0_BLOCK);
        taskComponent.subsequentTask = new HarvestTask();
        taskComponent.subsequentTaskType = AssignedTaskType.HARVEST;
        taskComponent.delayBeforeNextTask = 50000;

        newBlockSelectionComponent.currentSelection = taskComponent.taskRegion;

        taskComponent.task = newTask;

        placeFenceAroundRegion(taskComponent.taskRegion);

        newBlockSelectionComponent.texture = getAreaTexture(newTask);

        //mark this area so that no other task can be assigned here
        markArea(newBlockSelectionComponent, newTask, taskComponent, player);

        NetworkComponent networkComponent = new NetworkComponent();
        networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

        EntityRef task = entityManager.create(taskComponent, networkComponent);

        addTask(player, task);
    }

    @ReceiveEvent
    public void onMovableSelectionEnd(MovableSelectionEndEvent event, EntityRef entity) {
        if (pendingBuildTaskEntity != EntityRef.NULL) {
            EntityRef player = pendingBuildTaskEntity.getOwner();
            pendingBuildTaskEntity.destroy();

            BlockSelectionComponent blockSelectionComponent = new BlockSelectionComponent();
            blockSelectionComponent.currentSelection = event.getFinalRegion();

            // Create new build task
            TaskComponent taskComponent = new TaskComponent();
            taskComponent.taskRegion = blockSelectionComponent.currentSelection;
            taskComponent.creationTime = timer.getGameTimeInMs();

            taskComponent.assignedTaskType = AssignedTaskType.BUILD;
            Task newTask = pendingTask;
            taskComponent.task = newTask;

            placeFenceAroundRegion(taskComponent.taskRegion);

            blockSelectionComponent.texture = getAreaTexture(newTask);

            //mark this area so that no other task can be assigned here
            markArea(blockSelectionComponent, newTask, taskComponent, player);

            NetworkComponent networkComponent = new NetworkComponent();
            networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

            EntityRef task = entityManager.create(taskComponent, networkComponent);

            addTask(player, task);
        }
    }

    public void placeFenceAroundRegion(Region3i region) {
        logger.info("placing fence");
        int minX = region.minX();
        int maxX = region.maxX();
        int minZ = region.minZ();
        int maxZ = region.maxZ();
        int Y = region.minY();
        minYOverall = Y;

        Region3i leftRegion = Region3i.createFromMinMax(new Vector3i(minX - 2, Y, minZ - 2), new Vector3i(minX - 2, Y, maxZ + 2));
        Region3i rightRegion = Region3i.createFromMinMax(new Vector3i(maxX + 2, Y, minZ - 2), new Vector3i(maxX + 2, Y, maxZ + 2));
        Region3i topRegion = Region3i.createFromMinMax(new Vector3i(minX - 1, Y, maxZ + 2), new Vector3i(maxX, Y, maxZ + 2));
        Region3i bottomRegion = Region3i.createFromMinMax(new Vector3i(minX - 1, Y, minZ - 2), new Vector3i(maxX + 1, Y, minZ - 2));

        Block airBlock = blockManager.getBlock("engine:air");

        for (int x = minX - 2; x <= maxX + 2; x++) {
            for (int z = minZ - 2; z <= maxZ + 2; z++) {
                blockEntityRegistry.setBlockForceUpdateEntity(new Vector3i(x, Y + 1, z), airBlock);
            }
        }

        placeFenceBlocks(topRegion, false);
        placeFenceBlocks(bottomRegion, false);
        placeFenceBlocks(leftRegion, true);
        placeFenceBlocks(rightRegion, true);
    }

    private void placeFenceBlocks(Region3i region, boolean placeTorch) {
        int minX = region.minX();
        int maxX = region.maxX();
        int minZ = region.minZ();
        int maxZ = region.maxZ();
        int y = region.minY();

        Block block = blockManager.getBlock(MooConstants.FENCE_BLOCK_URI);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                blockEntityRegistry.setBlockForceUpdateEntity(new Vector3i(x, y + 1, z), block);
            }
        }

        // Place torches on corners
        if (placeTorch) {
            block = blockManager.getBlock(MooConstants.TORCH_BLOCK_URI);
            blockEntityRegistry.setBlockForceUpdateEntity(new Vector3i(minX, y + 2, minZ), block);
            blockEntityRegistry.setBlockForceUpdateEntity(new Vector3i(maxX, y + 2, maxZ), block);
        }
    }

    private Region3i getBuildingExtents(BuildingType buildingType, Region3i region) {
        Prefab buildingPrefab = prefabManager.getPrefab(buildingType.toString());

        SpawnBlockRegionsComponent blockRegionsComponent = buildingPrefab.getComponent(SpawnBlockRegionsComponent.class);
        List<SpawnBlockRegionsComponent.RegionToFill> regionsToFill = blockRegionsComponent.regionsToFill;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (SpawnBlockRegionsComponent.RegionToFill regionToFill : regionsToFill) {
            minX = Math.min(minX, regionToFill.region.minX());
            maxX = Math.max(maxX, regionToFill.region.maxX());
            minZ = Math.min(minZ, regionToFill.region.minZ());
            maxZ = Math.max(maxZ, regionToFill.region.maxZ());
        }

        Vector3f center = region.center();
        Vector3f extents = new Vector3f((maxX - minX) / 2, 0, (maxZ - minZ) / 2);
        return Region3i.createFromCenterExtents(center, extents);

//        minX = buildingRegion.minX();
//        maxX = buildingRegion.maxX();
//        minZ = buildingRegion.minZ();
//        maxZ = buildingRegion.maxZ();
//        int y = buildingRegion.minY();
//
//        // Invalidate area if any corner block is air
//        if (checkIfTargetable(new Vector3f(minX - 2, y, maxZ + 2))
//            && checkIfTargetable(new Vector3f(maxX + 2, y, maxZ + 2))
//            && checkIfTargetable(new Vector3f(minX - 2, y, minZ - 2))
//            && checkIfTargetable(new Vector3f(maxX + 2, y, minZ - 2))) {
//            return buildingRegion;
//        }
//
//        return null;

    }

    private boolean checkIfTargetable(Vector3f blockLocation) {
        EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(blockLocation);
        BlockComponent blockComponent = blockEntity.getComponent(BlockComponent.class);

        return blockComponent.block.isTargetable();
    }

    private Texture getAreaTexture(Task newTask) {
        Color taskColor = newTask.taskColor;
        return Assets.get(TextureUtil.getTextureUriForColor(taskColor), Texture.class).get();
    }

    /**
     * Saves the selected area for a particular task to check for clashes later.
     * Attaches a {@link BlockSelectionComponent} to the assignedArea entity so that the assigned area remains colored
     * until the task is finished.
     * @param blockSelectionComponent The component which has information related to the area selected.
     * @param newTask The new Task object which is being created
     * @param taskComponent The TaskComponent which will be added to the task entity
     * @param player The player entity which triggered the task creation.
     */
    private void markArea(BlockSelectionComponent blockSelectionComponent, Task newTask, TaskComponent taskComponent, EntityRef player) {
        AssignedAreaComponent assignedAreaComponent = new AssignedAreaComponent();

        assignedAreaComponent.assignedRegion = blockSelectionComponent.currentSelection;

        assignedAreaComponent.assignedTaskType = newTask.assignedTaskType;
        assignedAreaComponent.buildingType = newTask.buildingType;

        EntityRef assignedArea = entityManager.create(assignedAreaComponent, blockSelectionComponent);

        HoldingComponent oreonHolding = player.getComponent(HoldingComponent.class);
        oreonHolding.assignedAreas.add(assignedArea);

        player.saveComponent(oreonHolding);
    }

    /**
     * Checks if the selected area can be used i.e not already assigned to some other task
     * @param selectedRegion The region to be checked
     * @return A boolean value specifying whether the area is valid
     */
    private boolean checkArea(Region3i selectedRegion) {
        return true;
    }

    /**
     * Looks for a building in the assignedAreas list.
     * @param buildingType The type of the building required by the Oreon
     * @return Returns a target for the Oreon to go to.
     */
    private Vector3i findRequiredBuilding(BuildingType buildingType, TaskComponent oreonTaskComponent, HoldingComponent oreonHolding) {
        List<EntityRef> buildings = oreonHolding.constructedBuildings;
        for (EntityRef building : buildings) {
            ConstructedBuildingComponent constructedBuildingComponent = building.getComponent(ConstructedBuildingComponent.class);

            if (constructedBuildingComponent.buildingType.equals(buildingType)) {
                oreonTaskComponent.taskRegion = constructedBuildingComponent.boundingRegions.get(MooConstants.DINER_CHAIR_REGION_INDEX);
                oreonTaskComponent.task.requiredBuildingEntityID = building.getId();
                return constructedBuildingComponent.boundingRegions.get(MooConstants.DINER_CHAIR_REGION_INDEX).min();
            }
        }

        logger.info("Could not find required building");
        return null;
    }

    private void setOreonTarget(Actor oreon, Vector3i target) {
        MinionMoveComponent moveComponent = oreon.getComponent(MinionMoveComponent.class);

        moveComponent.target = new Vector3f(target.x, target.y, target.z);
        moveComponent.type = MinionMoveComponent.Type.DIRECT;

        logger.info("Set Oreon target to : " + moveComponent.target);

        oreon.save(moveComponent);
    }

    /**
     * Assigns advanced tasks like Eat and sleep to Oreon when it is free.
     * @param oreon The oreon Actor to which the task will be assigned
     * @param newTask The type of task to performed received based on priority of different tasks from its BT
     * @return A boolean value which signifies if the task was successfully assigned.
     */
    public boolean assignAdvancedTaskToOreon(Actor oreon, Task newTask) {
            TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);
            HoldingComponent oreonHolding = holdingSystem.getOreonHolding(oreon);

            Vector3i target = findRequiredBuilding(newTask.buildingType, oreonTaskComponent, oreonHolding);

            // if a building required for the task like the Diner for Eat is not found
            if (target == null) {
                return false;
            }

            newTask.requiredBuildingEntityID = oreonTaskComponent.task.requiredBuildingEntityID;
            oreonTaskComponent.task = newTask;
            oreonTaskComponent.assignedTaskType = newTask.assignedTaskType;
            oreonTaskComponent.taskCompletionTime = getTaskCompletionTime(newTask);


            oreonTaskComponent.creationTime = timer.getGameTimeInMs();
            oreon.save(oreonTaskComponent);

            setOreonTarget(oreon, target);

            return true;
    }

    /**
     * Receives the HorizontalCollisionEvent and decides whether the Oreon should abandon tasks
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void receiveCollisionEvent(HorizontalCollisionEvent collisionEvent, EntityRef oreon, MinionMoveComponent moveComponent) {
        if (lastCollisionLocation == null) {
            lastCollisionLocation = collisionEvent.getLocation();
        } else {
            if (isSameCollisionLocation(lastCollisionLocation, collisionEvent.getLocation())) {
                logger.info("oreon stuck");
                moveComponent.target = null;
                oreon.saveComponent(moveComponent);
                abandonTask(oreon);
            } else {
                // If collision just took place once
                lastCollisionLocation = null;
            }
        }
    }

    private boolean isSameCollisionLocation(Vector3f lastLocation, Vector3f currentLocation) {
        float lastX = Float.floatToIntBits(lastLocation.getX());
        float lastZ = Float.floatToIntBits(lastLocation.getZ());
        float currentX = Float.floatToIntBits(currentLocation.getX());
        float currentZ = Float.floatToIntBits(currentLocation.getZ());

        return lastX == currentX && lastZ == currentZ;
    }

    public void abandonTask(EntityRef oreon) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        if (!oreonTaskComponent.assignedTaskType.equals(AssignedTaskType.NONE)) {

            String message = "Oreon " + oreon.getComponent(NameTagComponent.class).text + " got stuck. Abandoning task " + oreonTaskComponent.assignedTaskType;

            oreon.getComponent(OreonSpawnComponent.class).parent.getOwner().send(new NotificationMessageEventMOO(message, notificationMessageEntity));

            if (!oreonTaskComponent.task.isAdvanced) {
                // Create entity for abandoned task
                NetworkComponent networkComponent = new NetworkComponent();
                networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

                EntityRef taskEntity = entityManager.create(networkComponent);

                TaskComponent taskComponent = new TaskComponent();
                taskComponent.assignedTaskType = oreonTaskComponent.assignedTaskType;
                taskComponent.taskRegion = oreonTaskComponent.taskRegion;
                taskComponent.creationTime = oreonTaskComponent.creationTime;
                taskComponent.task = oreonTaskComponent.task;
                taskComponent.taskStatus = TaskStatusType.Available;

                taskEntity.addComponent(taskComponent);

                // Add task to the holding
                OreonSpawnComponent oreonSpawnComponent = oreon.getComponent(OreonSpawnComponent.class);
                addTask(oreonSpawnComponent.parent, taskEntity);
            }

            // Free the Oreon
            oreonTaskComponent.assignedTaskType = AssignedTaskType.NONE;
            inventoryManager.removeItem(oreon, oreon, 0, true, 1);

            oreon.saveComponent(oreonTaskComponent);
        }
    }

    /**
     * Calculates the time at which the assigned task will be completed based on the assigned task type and current game
     * time.
     * @param newTask The type of task that is being assigned to the Oreon
     * @return The time at which the task will be completed
     */
    public float getTaskCompletionTime(Task newTask) {
        float currentTime = timer.getGameTime();

        return currentTime + newTask.taskDuration;
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH)
    public void addBuildingToHolding(BuildingConstructionStartedEvent constructionStartedEvent, EntityRef player) {
        if (constructionStartedEvent.constructedBuildingEntity == EntityRef.NULL) {
            // When a new building is constructed in the village
            ConstructedBuildingComponent constructedBuildingComponent = new ConstructedBuildingComponent();
            constructedBuildingComponent.boundingRegions = constructionStartedEvent.absoluteRegions;
            constructedBuildingComponent.buildingType = constructionStartedEvent.buildingType;
            constructedBuildingComponent.centerLocation = constructionStartedEvent.centerBlockPosition;

            constructionStartedEvent.constructedBuildingEntity = entityManager.create(constructedBuildingComponent);

            NetworkComponent networkComponent = new NetworkComponent();
            networkComponent.replicateMode = NetworkComponent.ReplicateMode.ALWAYS;

            constructionStartedEvent.constructedBuildingEntity.addComponent(networkComponent);

            constructionStartedEvent.constructedBuildingEntity.setOwner(player);
        }
        else {
            // When a building is upgraded
            // Update the extents of the building in the ConstructedBuildingComponent after upgrade
            ConstructedBuildingComponent constructedBuildingComponent = constructionStartedEvent.constructedBuildingEntity.getComponent(ConstructedBuildingComponent.class);
            constructedBuildingComponent.boundingRegions = constructionStartedEvent.absoluteRegions;
            constructedBuildingComponent.centerLocation = constructionStartedEvent.centerBlockPosition;
            constructionStartedEvent.constructedBuildingEntity.saveComponent(constructedBuildingComponent);
        }

        delayManager.addDelayedAction(constructionStartedEvent.constructedBuildingEntity, CONSTRUCTION_COMPLETE_EVENT_ID, constructionStartedEvent.completionDelay);
    }

    @ReceiveEvent
    public void onDelayedStructureCompletionTrigger(DelayedActionTriggeredEvent event, EntityRef constructedBuilding) {
        if (!event.getActionId().equals(CONSTRUCTION_COMPLETE_EVENT_ID)) {
            return;
        }
        ConstructedBuildingComponent constructedBuildingComponent = constructedBuilding.getComponent(ConstructedBuildingComponent.class);

        HoldingComponent holdingComponent = constructedBuilding.getOwner().getComponent(HoldingComponent.class);
        holdingComponent.constructedBuildings.add(constructedBuilding);

        constructedBuilding.getOwner().send(new BuildingConstructionCompletedEvent(constructedBuildingComponent.boundingRegions,
                constructedBuildingComponent.buildingType, constructedBuildingComponent.centerLocation, constructedBuilding));
    }

    @ReceiveEvent(components = TaskComponent.class)
    public void addTaskToHolding(DelayedActionTriggeredEvent event, EntityRef taskEntity) {
        if (!event.getActionId().equals(ADD_TASK_DELAYED_ACTION_ID)) {
            return;
        }

        addTask(taskEntity.getOwner(), taskEntity);
    }

    /**
     * Adds a block to the Oreon's inventory which is rendered as an indication for the task being being performed
     * @param oreon Oreon entity performing the task
     * @param task Task being performed
     */
    private void placeBlockToRenderInInventory(Actor oreon, Task task) {
        BlockItemFactory blockItemFactory = new BlockItemFactory(entityManager);
        inventoryManager.giveItem(oreon.getEntity(), oreon.getEntity(),
                blockItemFactory.newInstance(blockManager.getBlockFamily(task.blockToRender),
                        1));
    }
}
