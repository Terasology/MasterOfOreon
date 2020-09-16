// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.tooltip;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.world.selection.BlockSelectionComponent;
import org.terasology.inventory.rendering.nui.layers.ingame.GetItemTooltip;
import org.terasology.masteroforeon.spawning.OreonAttributeComponent;
import org.terasology.masteroforeon.taskSystem.components.TaskComponent;
import org.terasology.masteroforeon.tooltip.components.OreonTooltipComponent;
import org.terasology.namegenerator.creature.CreatureNameComponent;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.worldlyTooltipAPI.events.GetTooltipIconEvent;
import org.terasology.worldlyTooltipAPI.events.GetTooltipNameEvent;

@RegisterSystem(RegisterMode.CLIENT)
public class TooltipClientSystem extends BaseComponentSystem {
    @ReceiveEvent(components = {OreonAttributeComponent.class, CreatureNameComponent.class})
    public void addAttributesToTooltip(GetItemTooltip event, EntityRef entity,
                                       OreonAttributeComponent oreonAttributeComponent,
                                       CreatureNameComponent oreonNameComponent) {
        event.getTooltipLines().add(new TooltipLine("Name : " + oreonNameComponent.firstName + " " + oreonNameComponent.lastName));
        event.getTooltipLines().add(new TooltipLine("Level : " + oreonAttributeComponent.currentLevel));
        event.getTooltipLines().add(new TooltipLine("Health : " + oreonAttributeComponent.health + " / " + oreonAttributeComponent.maxHealth));
        event.getTooltipLines().add(new TooltipLine("Hunger : " + oreonAttributeComponent.hunger));
        event.getTooltipLines().add(new TooltipLine("Strength : " + oreonAttributeComponent.strength + " / " + oreonAttributeComponent.maxStrength));
        event.getTooltipLines().add(new TooltipLine("Intelligence : " + oreonAttributeComponent.intelligence + " / " + oreonAttributeComponent.maxIntelligence));
        event.getTooltipLines().add(new TooltipLine("AssignedTask : " + entity.getComponent(TaskComponent.class).assignedTaskType));
    }

    @ReceiveEvent(components = BlockSelectionComponent.class)
    public void addAreaToTooltip(GetItemTooltip event, EntityRef entity,
                                 BlockSelectionComponent blockSelectionComponent) {
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
