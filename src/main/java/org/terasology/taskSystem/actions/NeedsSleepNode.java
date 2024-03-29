// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.actions;

import org.terasology.MooConstants;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.nameTags.NameTagComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.network.ColorComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.time.WorldTime;
import org.terasology.notification.NotificationMessageEventMOO;
import org.terasology.nui.Color;
import org.terasology.taskSystem.TaskManagementSystem;
import org.terasology.taskSystem.tasks.SleepTask;

/**
 * Checks if it is night time in the world and there are no pending tasks in the village, if yes calls the task management system to assign
 * the Eat task.
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
