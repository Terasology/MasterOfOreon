// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.deathSystem;

import org.terasology.deathSystem.components.DeathTimeComponent;
import org.terasology.deathSystem.components.DyingComponent;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.behavior.BehaviorComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;
import org.terasology.engine.rendering.logic.SkeletalMeshComponent;

@Share(DeathSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class DeathSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    @In
    Time time;

    @In
    EntityManager entityManager;

    @Override
    public void update(float delta) {
        float currentTime = time.getGameTime();

        for (EntityRef oreon : entityManager.getEntitiesWith(DeathTimeComponent.class)) {
            DeathTimeComponent deathTimeComponent = oreon.getComponent(DeathTimeComponent.class);

            //if the death time has already passed
            if (deathTimeComponent.deathTime < currentTime) {
                oreon.destroy();
            }
        }
    }

    public void destroyOreon(EntityRef oreon) {
        //remove the Behavior tree
        oreon.removeComponent(BehaviorComponent.class);

        //add the death animation
        SkeletalMeshComponent skeletalMeshComponent = oreon.getComponent(SkeletalMeshComponent.class);
        DyingComponent dyingComponent = oreon.getComponent(DyingComponent.class);

        skeletalMeshComponent.animation = null;
        skeletalMeshComponent.animationPool.clear();
        skeletalMeshComponent.animationPool.addAll(dyingComponent.animationPool);
        skeletalMeshComponent.loop = false;

        // Duration of animation
        float lifespan = 0;
        for (MeshAnimation meshAnimation : skeletalMeshComponent.animationPool) {
            lifespan += meshAnimation.getTimePerFrame() * (meshAnimation.getFrameCount() - 1);
        }

        // Set the death time
        DeathTimeComponent deathTimeComponent = new DeathTimeComponent();
        deathTimeComponent.deathTime = time.getGameTime() + lifespan;
        oreon.addComponent(deathTimeComponent);
    }
}
