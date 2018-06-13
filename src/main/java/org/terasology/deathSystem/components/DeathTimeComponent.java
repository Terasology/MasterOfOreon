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
package org.terasology.deathSystem.components;

import org.terasology.entitySystem.Component;

/**
 * This component is attached to an Oreon entity which has reached zero health and would be destroyed after the death
 * animation is completed.
 */
public class DeathTimeComponent implements Component {
    /**
     * The time after which the Oreon entity must be destroyed.
     */
    public float deathTime;

    public DeathTimeComponent(float time) {
        this.deathTime = time;
    }
}
