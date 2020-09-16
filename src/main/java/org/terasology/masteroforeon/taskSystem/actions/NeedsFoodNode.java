// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.actions;

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
import org.terasology.masteroforeon.MooConstants;
import org.terasology.masteroforeon.healthSystem.HealthReductionCause;
import org.terasology.masteroforeon.healthSystem.OreonHealthSystem;
import org.terasology.masteroforeon.spawning.OreonAttributeComponent;
import org.terasology.masteroforeon.taskSystem.DelayedNotificationSystem;
import org.terasology.masteroforeon.taskSystem.TaskManagementSystem;
import org.terasology.masteroforeon.taskSystem.tasks.EatTask;
import org.terasology.nui.Color;

/**
 * Checks if the Oreon is hungry, if yes calls the task management system to assign the Eat task.
 */
@BehaviorAction(name = "needs_food")
public class NeedsFoodNode extends BaseAction {
    @In
    Context context;
    @In
    EntityManager entityManager;

    private LocalPlayer localPlayer;

    private TaskManagementSystem taskManagementSystem;

    private DelayedNotificationSystem delayedNotificationSystem;
    private float lastNotification = 0;

    private OreonHealthSystem oreonHealthSystem;

    private EntityRef notificationMessageEntity;

    @Override
    public void construct(Actor oreon) {
        localPlayer = context.get(LocalPlayer.class);

        taskManagementSystem = context.get(TaskManagementSystem.class);

        delayedNotificationSystem = context.get(DelayedNotificationSystem.class);

        oreonHealthSystem = context.get(OreonHealthSystem.class);

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
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        if (oreonAttributeComponent.hunger > 50) {
            if (taskManagementSystem.assignAdvancedTaskToOreon(oreon, new EatTask())) {
                return BehaviorState.SUCCESS;
            } else {
                String message = "We are hungry. Build a diner!";
                lastNotification = delayedNotificationSystem.sendNotification(message, notificationMessageEntity,
                        lastNotification);

                //reduce health if required
                oreonHealthSystem.reduceHealth(oreon, HealthReductionCause.Hunger);
            }
        }

        return BehaviorState.FAILURE;
    }
}
