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
package org.terasology.resources.system;

import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * The system which handles resources in the Player's inventory
 */
public class PlayerResourceSystem extends ResourceSystem {

    public boolean checkForAResource(EntityRef player, String resourceName, int quantity) {
        return true;
    }

    public boolean addAResource(EntityRef player, EntityRef item) {
        addToInventory(player, item);
        return true;
    }
}
