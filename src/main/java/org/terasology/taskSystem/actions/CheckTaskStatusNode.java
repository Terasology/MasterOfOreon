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


import org.terasology.engine.Time;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.registry.In;
import org.terasology.taskSystem.components.TaskComponent;

@BehaviorAction(name = "check_task_status")
public class CheckTaskStatusNode extends BaseAction {
    @In
    private Time time;


    @Override
    public void construct(Actor oreon) {

    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        if (oreonTaskComponent.taskCompletionTime < time.getGameTime()) {
            return BehaviorState.SUCCESS;
        }

        return BehaviorState.RUNNING;
    }
}
