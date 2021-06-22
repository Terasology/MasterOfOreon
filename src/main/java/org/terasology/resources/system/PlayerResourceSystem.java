// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
