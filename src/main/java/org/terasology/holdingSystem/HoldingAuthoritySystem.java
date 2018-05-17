/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.holdingSystem;

import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.holdingSystem.components.HoldingComponent;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.registry.Share;
import org.terasology.spawning.OreonSpawnComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(HoldingAuthoritySystem.class)
public class HoldingAuthoritySystem extends BaseComponentSystem {
    public HoldingComponent getOreonHolding (Actor actor) {
        OreonSpawnComponent oreonSpawnComponent = actor.getComponent(OreonSpawnComponent.class);

        return oreonSpawnComponent.parent.getComponent(HoldingComponent.class);
    }

}
