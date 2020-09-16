// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.taskSystem.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.context.Context;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.masteroforeon.taskSystem.TaskManagementSystem;

@BehaviorAction(name = "look_for_task")
public class LookForTaskNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(LookForTaskNode.class);
    @In
    Context context;

    private TaskManagementSystem taskManagementSystem;

    @Override
    public void construct(Actor oreon) {
        this.taskManagementSystem = context.get(TaskManagementSystem.class);
    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        if (taskManagementSystem.getTaskForOreon(oreon)) {
            return BehaviorState.SUCCESS;
        }

        return BehaviorState.FAILURE;
    }
}
