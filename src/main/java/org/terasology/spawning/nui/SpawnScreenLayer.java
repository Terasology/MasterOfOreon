/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.spawning.nui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.portals.PortalComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.spawning.OreonSpawnComponent;
import org.terasology.spawning.OreonSpawnEvent;


public class SpawnScreenLayer extends CoreScreenLayer {

    private static final Logger logger = LoggerFactory.getLogger(SpawnScreenLayer.class);

    @In
    private EntityManager entityManager;
    @In
    private LocalPlayer localPlayer;
    @In
    private PrefabManager prefabManager;

    private UIButton summonOreonBuilderCommand;
    private UIButton summonOreonGuardCommand;
    private UIButton summonOreonKingCommand;

    private EntityRef portalEntity;

    @Override
    public void initialise() {
        summonOreonBuilderCommand = find("summonOreonBuilderCommand", UIButton.class);
        summonOreonGuardCommand = find("summonOreonGuardCommand", UIButton.class);
        summonOreonKingCommand = find("summonOreonKingCommand", UIButton.class);

        summonOreonBuilderCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab("Oreons:OreonBuilder"));
        });

        summonOreonGuardCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab("Oreons:OreonGuard"));
        });

        summonOreonKingCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab("Oreons:OreonKing"));
        });
    }

    public void sendOreonSpawnEvent(Prefab prefabToSpawn) {
        setPortalEntity();
        if(portalEntity.hasComponent(LocationComponent.class)) {
            EntityRef oreon = entityManager.create(portalEntity.getComponent(LocationComponent.class));
            OreonSpawnComponent oreonSpawnComponent = new OreonSpawnComponent();
            oreonSpawnComponent.oreonPrefab = prefabToSpawn;
            oreon.addComponent(oreonSpawnComponent);
            logger.info("sending spawn event");
            localPlayer.getCharacterEntity().send(new OreonSpawnEvent(prefabToSpawn, portalEntity.getComponent(LocationComponent.class).getWorldPosition()));
        }
    }

    public void setPortalEntity() {
        for(EntityRef portal : entityManager.getEntitiesWith(PortalComponent.class, LocationComponent.class)){
            portalEntity = portal;
        }
    }

}
