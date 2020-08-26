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
package org.terasology.taskSystem.actions;

import org.terasology.MooConstants;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.network.ColorComponent;
import org.terasology.notification.NotificationMessageEventMOO;
import org.terasology.registry.In;
import org.terasology.nui.Color;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.tasks.SleepTask;
import org.terasology.world.WorldProvider;
import org.terasology.world.time.WorldTime;

/**
 * Checks if it is night time in the world and there are no pending tasks in the village, if yes calls the task management system to assign the Eat task.
 */
@BehaviorAction(name = "needs_sleep")
public class NeedsSleepNode extends BaseAction {
    @In
    Context context;
    @In
    EntityManager entityManager;

    private LocalPlayer localPlayer;

    private WorldProvider worldProvider;
    private WorldTime worldTime;

    private TaskManagementSystem taskManagementSystem;

    private EntityRef notificationMessageEntity;

    @Override
    public void construct(Actor oreon) {
        worldProvider = context.get(WorldProvider.class);
        worldTime = worldProvider.getTime();

        localPlayer = context.get(LocalPlayer.class);

        taskManagementSystem = context.get(TaskManagementSystem.class);

        notificationMessageEntity = entityManager.create(MooConstants.NOTIFICATION_MESSAGE_PREFAB);

        DisplayNameComponent displayNameComponent = notificationMessageEntity.getComponent(DisplayNameComponent.class);
        displayNameComponent.name = oreon.getComponent(NameTagComponent.class).text;

        ColorComponent colorComponent = notificationMessageEntity.getComponent(ColorComponent.class);
        colorComponent.color = Color.RED;

        notificationMessageEntity.saveComponent(displayNameComponent);
        notificationMessageEntity.saveComponent(colorComponent);
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        float days = worldTime.getDays();
        double daysCeiling = Math.ceil(days);
        double daysFloor = Math.floor(days);

        double diffCeiling = daysCeiling - days;
        double diffFloor = days - daysFloor;

        if (diffCeiling < 0.24 || diffFloor < 0.24) {
            taskManagementSystem.assignAdvancedTaskToOreon(oreon, new SleepTask());
            String message = "Sleepy time";
            localPlayer.getCharacterEntity().getOwner().send(new NotificationMessageEventMOO(message, notificationMessageEntity));
            return BehaviorState.FAILURE;
        }

        return BehaviorState.SUCCESS;
    }
}
