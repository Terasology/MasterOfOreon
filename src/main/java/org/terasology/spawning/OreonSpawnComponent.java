// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.spawning;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is attached to every Oreon(defined in its prefab)
 */
public class OreonSpawnComponent implements Component {
    public Map<String, Integer> itemsToConsume = new HashMap<>();

    public Prefab oreonPrefab;

    /**
     * The player entity which owns the Oreon i.e. the player who is spawning it
     */
    public EntityRef parent = EntityRef.NULL;
}
