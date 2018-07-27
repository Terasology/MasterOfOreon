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

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.terasology.Constants;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.math.Region3i;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.taskSystem.tasks.HarvestTask;

public class AddCropTransferToStorageTaskNode extends BaseAction {

    @Override
    public void construct(Actor oreon) {

    }

    @Override
    public BehaviorState modify(Actor oreon, BehaviorState result) {
        TaskComponent oreonTaskComponent = oreon.getComponent(TaskComponent.class);

        Region3i plantRegion = oreonTaskComponent.taskRegion;

        int numberOfBlocks = plantRegion.sizeX() * plantRegion.sizeZ();

        HarvestTask harvestTask = (HarvestTask) oreonTaskComponent.task;
        harvestTask.numberOfCropBlocksHarvested = numberOfBlocks;

        oreon.save(oreonTaskComponent);

        return BehaviorState.SUCCESS;
    }
}
