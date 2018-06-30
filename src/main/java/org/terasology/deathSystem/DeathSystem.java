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
package org.terasology.deathSystem;

import org.terasology.deathSystem.components.DeathTimeComponent;
import org.terasology.deathSystem.components.DyingComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.behavior.BehaviorComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.assets.animation.MeshAnimation;
import org.terasology.rendering.logic.SkeletalMeshComponent;

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
