// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.assignment;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.masteroforeon.spawning.OreonAttributeComponent;

import java.util.Queue;

public interface AssignmentStrategy {
    EntityRef getBestTask(OreonAttributeComponent attributes, Queue<EntityRef> tasks);
}
