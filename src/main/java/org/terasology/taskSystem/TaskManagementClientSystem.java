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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.Region3i;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.nui.Color;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIScreenLayer;
import org.terasology.resources.system.ResourceSystem;
import org.terasology.taskSystem.events.*;
import org.terasology.utilities.Assets;
import org.terasology.world.selection.BlockSelectionComponent;

@RegisterSystem(RegisterMode.CLIENT)
public class TaskManagementClientSystem extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;

    @In
    private EntityManager entityManager;

    @In
    private TaskManagementSystem taskManagementSystem;

    private UIScreenLayer taskSelectionScreenLayer;
    private UIScreenLayer plantSelectionScreenLayer;

    private static final Logger logger = LoggerFactory.getLogger(TaskManagementClientSystem.class);

    private Region3i taskRegion;
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
     * Receives the {@link SetTaskTypeEvent} sent by the {@link org.terasology.taskSystem.nui.TaskSelectionScreenLayer}
     * after the player assigns a task to a selected area.
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
