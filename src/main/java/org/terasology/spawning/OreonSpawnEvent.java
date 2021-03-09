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
package org.terasology.spawning;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.network.ServerEvent;

/**
 * Event sent by the {@link org.terasology.spawning.nui.SpawnScreenLayer} after the player selects an Oreon to spawn.
 */
@ServerEvent
public class OreonSpawnEvent implements Event {
    private Prefab oreonPrefab;
    private Vector3f location = new Vector3f();

    public OreonSpawnEvent () {
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
