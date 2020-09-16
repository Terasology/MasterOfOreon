// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.math.Region3i;

public class OpenTaskSelectionScreenEvent implements Event {
    private final Region3i taskRegion;

    public OpenTaskSelectionScreenEvent(Region3i region) {
        this.taskRegion = region;
    }

    public Region3i getTaskRegion() {
        return taskRegion;
    }
}
