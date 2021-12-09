// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.events;

import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.event.Event;

public class OpenTaskSelectionScreenEvent implements Event {
    private BlockRegion taskRegion;

    public OpenTaskSelectionScreenEvent(BlockRegion region) {
        this.taskRegion = region;
    }

    public BlockRegion getTaskRegion() {
        return taskRegion;
    }
}
