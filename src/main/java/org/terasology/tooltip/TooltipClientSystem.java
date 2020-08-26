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
package org.terasology.tooltip;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.namegenerator.creature.CreatureNameComponent;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.layers.ingame.inventory.GetItemTooltip;
import org.terasology.spawning.OreonAttributeComponent;
import org.terasology.taskSystem.components.TaskComponent;
import org.terasology.tooltip.components.OreonTooltipComponent;
import org.terasology.world.selection.BlockSelectionComponent;
import org.terasology.worldlyTooltipAPI.events.GetTooltipIconEvent;
import org.terasology.worldlyTooltipAPI.events.GetTooltipNameEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class TooltipClientSystem extends BaseComponentSystem {
    @ReceiveEvent(components = {OreonAttributeComponent.class, CreatureNameComponent.class})
    public void addAttributesToTooltip(GetItemTooltip event, EntityRef entity, OreonAttributeComponent oreonAttributeComponent,  CreatureNameComponent oreonNameComponent) {
        event.getTooltipLines().add(new TooltipLine("Name : " + oreonNameComponent.firstName + " " + oreonNameComponent.lastName));
        event.getTooltipLines().add(new TooltipLine("Level : " + oreonAttributeComponent.currentLevel));
        event.getTooltipLines().add(new TooltipLine("Health : " + oreonAttributeComponent.health + " / " + oreonAttributeComponent.maxHealth));
        event.getTooltipLines().add(new TooltipLine("Hunger : " + oreonAttributeComponent.hunger));
        event.getTooltipLines().add(new TooltipLine("Strength : " + oreonAttributeComponent.strength + " / " + oreonAttributeComponent.maxStrength ));
        event.getTooltipLines().add(new TooltipLine("Intelligence : " + oreonAttributeComponent.intelligence + " / " + oreonAttributeComponent.maxIntelligence ));
        event.getTooltipLines().add(new TooltipLine("AssignedTask : " + entity.getComponent(TaskComponent.class).assignedTaskType));
    }

    @ReceiveEvent(components = BlockSelectionComponent.class)
    public void addAreaToTooltip(GetItemTooltip event, EntityRef entity, BlockSelectionComponent blockSelectionComponent) {
        event.getTooltipLines().add(new TooltipLine("Area " + blockSelectionComponent.startPosition));
    }

    @ReceiveEvent(components = OreonTooltipComponent.class)
    public void setIcon(GetTooltipIconEvent event, EntityRef entityRef) {
        OreonTooltipComponent oreonTooltipComponent = entityRef.getComponent(OreonTooltipComponent.class);
        event.setIcon(oreonTooltipComponent.icon);
    }

    @ReceiveEvent(components = OreonTooltipComponent.class)
    public void setName(GetTooltipNameEvent event, EntityRef entityRef) {
        OreonTooltipComponent oreonTooltipComponent = entityRef.getComponent(OreonTooltipComponent.class);
        event.setName(oreonTooltipComponent.name);
    }
}
