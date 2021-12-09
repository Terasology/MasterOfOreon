// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.healthSystem;

import org.terasology.MooConstants;
import org.terasology.deathSystem.DeathSystem;
import org.terasology.engine.context.Context;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.nameTags.NameTagComponent;
import org.terasology.engine.network.ColorComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.nui.Color;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.taskSystem.DelayedNotificationSystem;

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
        notificationMessageEntity = entityManager.create(MooConstants.NOTIFICATION_MESSAGE_PREFAB);

        ColorComponent colorComponent = notificationMessageEntity.getComponent(ColorComponent.class);
        colorComponent.color = Color.RED;

        notificationMessageEntity.saveComponent(colorComponent);
    }

    public void reduceHealth(Actor oreon, HealthReductionCause cause) {
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        switch (cause) {
            case Hunger:
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
     *
     * @param oreon The Oreon actor whose health is to be checked
     */
    private void checkOreonHealth(Actor oreon) {
        OreonAttributeComponent oreonAttributeComponent = oreon.getComponent(OreonAttributeComponent.class);

        if (oreonAttributeComponent.health <= 0) {
            deathSystem.destroyOreon(oreon.getEntity());
        }
    }

}
