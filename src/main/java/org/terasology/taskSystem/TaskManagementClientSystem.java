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

import org.terasology.Constants;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIScreenLayer;
import org.terasology.taskSystem.events.CloseTaskSelectionScreenEvent;
import org.terasology.taskSystem.events.OpenTaskSelectionScreenEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class TaskManagementClientSystem extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;

    private UIScreenLayer taskSelectionScreenLayer;

    @ReceiveEvent
    public void openTaskSelectionScreen(OpenTaskSelectionScreenEvent event, EntityRef player) {
        this.taskSelectionScreenLayer = nuiManager.createScreen(Constants.TASK_SELECTION_SCREEN_URI);
        nuiManager.pushScreen(this.taskSelectionScreenLayer);
    }

    @ReceiveEvent
    public void closeTaskSelectionScreen(CloseTaskSelectionScreenEvent event, EntityRef player) {
        nuiManager.closeScreen(this.taskSelectionScreenLayer);
    }
}
