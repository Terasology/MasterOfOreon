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

import org.terasology.entitySystem.Component;
import org.terasology.network.Replicate;

/**
 * Defines the levels of various stats related to an Oreon
 */
public class OreonAttributeComponent implements Component {
    /**
     * Defines the current level of the Oreon.
     * Level of the Oreon decides the max value of a particular attribute that can be attained by Training.
     */
    @Replicate
    public int currentLevel = 1;

    @Replicate
    public int maxIntelligence = 100 * currentLevel;
    @Replicate
    public int maxStrength = 100 * currentLevel;

    @Replicate
    public int intelligence;
    @Replicate
    public int strength;

    @Replicate
    public int health = 100;

    @Replicate
    public int hunger = 0;

    @Replicate
    public float lastHealthCheck = 0;
}
