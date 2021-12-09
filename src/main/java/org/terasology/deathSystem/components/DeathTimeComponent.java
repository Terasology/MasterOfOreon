// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.deathSystem.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component is attached to an Oreon entity which has reached zero health and would be destroyed after the death animation is
 * completed.
 */
public class DeathTimeComponent implements Component<DeathTimeComponent> {
    /**
     * The time after which the Oreon entity must be destroyed.
     */
    public float deathTime;

    @Override
    public void copyFrom(DeathTimeComponent other) {
        this.deathTime = other.deathTime;
    }
}
