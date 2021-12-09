// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.holdingSystem;

import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.registry.Share;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.spawning.OreonSpawnComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(HoldingAuthoritySystem.class)
public class HoldingAuthoritySystem extends BaseComponentSystem {
    public HoldingComponent getOreonHolding(Actor actor) {
        OreonSpawnComponent oreonSpawnComponent = actor.getComponent(OreonSpawnComponent.class);

        return oreonSpawnComponent.parent.getComponent(HoldingComponent.class);
    }

}
