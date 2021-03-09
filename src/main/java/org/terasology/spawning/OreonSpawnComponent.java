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
