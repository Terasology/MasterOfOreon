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

import org.terasology.Constants;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.healthSystem.OreonHealthSystem;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.network.ColorComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.Color;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.taskSystem.AssignedTaskType;
import org.terasology.taskSystem.DelayedNotificationSystem;
import org.terasology.taskSystem.TaskManagementSystem;

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

        oreonHealthSystem  = context.get(OreonHealthSystem.class);

        notificationMessageEntity = entityManager.create(Constants.NOTIFICATION_MESSAGE_PREFAB);

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
            if (taskManagementSystem.assignAdvancedTaskToOreon(oreon, AssignedTaskType.Eat)) {
                return BehaviorState.SUCCESS;
            } else {
                String message = "We are hungry. Build a diner!";
                lastNotification = delayedNotificationSystem.sendNotification(message, notificationMessageEntity, lastNotification);

                //reduce health if required
                oreonHealthSystem.reduceHealth(oreon, HealthReductionCause.Hunger);
            }
        }

        return BehaviorState.FAILURE;
    }
}
