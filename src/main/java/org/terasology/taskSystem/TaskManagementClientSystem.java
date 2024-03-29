// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MooConstants;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.assets.texture.TextureUtil;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.UIScreenLayer;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.selection.BlockSelectionComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.nui.Color;
import org.terasology.taskSystem.events.CloseTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.OpenTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.SetTaskTypeEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class TaskManagementClientSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementClientSystem.class);
    @In
    private NUIManager nuiManager;
    @In
    private EntityManager entityManager;
    @In
    private TaskManagementSystem taskManagementSystem;
    private UIScreenLayer taskSelectionScreenLayer;
    private UIScreenLayer plantSelectionScreenLayer;
    private BlockRegion taskRegion;
    private EntityRef tempTaskEntity;

    @ReceiveEvent
    public void openTaskSelectionScreen(OpenTaskSelectionScreenEvent event, EntityRef player) {
        this.taskSelectionScreenLayer = nuiManager.createScreen(MooConstants.TASK_SELECTION_SCREEN_URI);
        this.taskRegion = event.getTaskRegion();

        // To persist the area rendering while task type is selected
        BlockSelectionComponent newBlockSelectionComponent = new BlockSelectionComponent();
        newBlockSelectionComponent.shouldRender = true;
        newBlockSelectionComponent.currentSelection = taskRegion;

        // A default color for tasks
        newBlockSelectionComponent.texture = Assets.get(TextureUtil.getTextureUriForColor(Color.BLUE.alterAlpha(100)), Texture.class).get();
        tempTaskEntity = entityManager.create(newBlockSelectionComponent);

        nuiManager.pushScreen(this.taskSelectionScreenLayer);
    }

    @ReceiveEvent
    public void closeTaskSelectionScreen(CloseTaskSelectionScreenEvent event, EntityRef player) {
        nuiManager.closeScreen(this.taskSelectionScreenLayer);
    }

    /**
     * Receives the {@link SetTaskTypeEvent} sent by the {@link org.terasology.taskSystem.nui.TaskSelectionScreenLayer} after the player
     * assigns a task to a selected area.
     *
     * @param event The event sent by the screen layer.
     * @param player The player entity adding the new task.
     */
    @ReceiveEvent
    public void receiveSetTaskTypeEvent(SetTaskTypeEvent event, EntityRef player) {
        player.send(new CloseTaskSelectionScreenEvent());

        String newTaskType = event.getTaskType();

        // When Cancel Selection button is used
        if (newTaskType == null) {
            tempTaskEntity.destroy();
            return;
        }

        taskManagementSystem.setTaskType(newTaskType, event.getBuildingType(), event.getPlantType(), this.taskRegion, player);

        tempTaskEntity.destroy();
    }
}
