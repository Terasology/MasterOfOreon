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
package org.terasology.healthSystem;

import org.terasology.Constants;
import org.terasology.context.Context;
import org.terasology.deathSystem.DeathSystem;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.network.ColorComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.Color;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.taskSystem.DelayedNotificationSystem;
import org.terasology.taskSystem.actions.HealthReductionCause;

@Share(OreonHealthSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class OreonHealthSystem extends BaseComponentSystem {
    private static final float MAX_DELAY = 1;

    @In
    private Context context;
    @In
    private Time time;
    @In
    private EntityManager entityManager;

    private DelayedNotificationSystem delayedNotificationSystem;
    private EntityRef notificationMessageEntity;
    private DeathSystem deathSystem;

    @Override
    public void postBegin() {
        delayedNotificationSystem = context.get(DelayedNotificationSystem.class);
        deathSystem = context.get(DeathSystem.class);
        notificationMessageEntity = entityManager.create(Constants.NOTIFICATION_MESSAGE_PREFAB);

        ColorComponent colorComponent = notificationMessageEntity.getComponent(ColorComponent.class);
        colorComponent.color = Color.RED;

        notificationMessageEntity.saveComponent(colorComponent);
    }

    public void reduceHealth(Actor oreon, HealthReductionCause cause) {
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        switch (cause) {
            case Hunger :
                float lastHungerCheck = oreonAttributeComponent.lastHungerCheck;
                if (lastHungerCheck != 0 && time.getGameTime() - lastHungerCheck < MAX_DELAY) {
                    return;
                }
                oreonAttributeComponent.health -= 10;
                String message = "We are losing health due to hunger.";

                // Set display name to the Oreon's name
                DisplayNameComponent displayNameComponent = notificationMessageEntity.getComponent(DisplayNameComponent.class);
                displayNameComponent.name = oreon.getComponent(NameTagComponent.class).text;
                notificationMessageEntity.saveComponent(displayNameComponent);

                delayedNotificationSystem.sendNotificationNow(message, notificationMessageEntity);
                oreonAttributeComponent.lastHungerCheck = time.getGameTime();

        }
        oreon.save(oreonAttributeComponent);

        checkOreonHealth(oreon);

    }

    /**
     * Checks if the Oreon's health is zero, and destroys the entity if yes.
     * @param oreon The Oreon actor whose health is to be checked
     */
    private void checkOreonHealth(Actor oreon) {
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        if (oreonAttributeComponent.health <= 0) {
            deathSystem.destroyOreon(oreon.getEntity());
        }
    }

}
