// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.spawning;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event sent by the {@link org.terasology.spawning.nui.SpawnScreenLayer} after the player selects an Oreon to spawn.
 */
@ServerEvent
public class OreonSpawnEvent implements Event {
    private Prefab oreonPrefab;
    private Vector3f location = new Vector3f();

    public OreonSpawnEvent() {
    }

    public OreonSpawnEvent(Prefab prefab, Vector3fc location) {
        this.oreonPrefab = prefab;
        this.location.set(location);
    }

    public Prefab getOreonPrefab() {
        return this.oreonPrefab;
    }

    public Vector3fc getSpawnPosition() {
        return this.location;
    }
}
