// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.world.block.BlockRegion;

public class OpenTaskSelectionScreenEvent implements Event {
    private final BlockRegion taskRegion;

    public OpenTaskSelectionScreenEvent(BlockRegion region) {
        this.taskRegion = region;
    }

    public BlockRegion getTaskRegion() {
        return taskRegion;
    }
}
