// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.spawning;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Defines the levels of various stats related to an Oreon
 */
public class OreonAttributeComponent implements Component<OreonAttributeComponent> {
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
    public int maxHealth = 100 * currentLevel;

    @Replicate
    public int intelligence;
    @Replicate
    public int strength;

    @Replicate
    public int health = 100;

    @Replicate
    public int hunger = 0;

    @Replicate
    public float lastHungerCheck = 0;

    @Override
    public void copyFrom(OreonAttributeComponent other) {
        this.currentLevel = other.currentLevel;
        this.maxIntelligence = other.maxIntelligence;
        this.maxStrength = other.maxStrength;
        this.maxHealth = other.maxHealth;
        this.intelligence = other.intelligence;
        this.strength = other.strength;
        this.health = other.health;
        this.hunger = other.hunger;
        this.lastHungerCheck = other.lastHungerCheck;
    }
}
