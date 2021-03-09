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
package org.terasology.taskSystem.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.context.Context;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.taskSystem.TaskManagementSystem;

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
