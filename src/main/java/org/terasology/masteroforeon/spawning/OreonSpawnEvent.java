// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.spawning;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.ServerEvent;
import org.terasology.masteroforeon.spawning.nui.SpawnScreenLayer;
import org.terasology.math.geom.Vector3f;

/**
 * Event sent by the {@link SpawnScreenLayer} after the player selects an Oreon to spawn.
 */
@ServerEvent
public class OreonSpawnEvent implements Event {
    private Prefab oreonPrefab;
    private Vector3f location;

    public OreonSpawnEvent() {
    }

    public OreonSpawnEvent(Prefab prefab, Vector3f location) {
        this.oreonPrefab = prefab;
        this.location = location;
    }

    public Prefab getOreonPrefab() {
        return this.oreonPrefab;
    }

    public Vector3f getSpawnPosition() {
        return this.location;
    }
}
