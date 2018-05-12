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
import org.terasology.spawning.OreonSpawnEvent;


public class SpawnScreenLayer extends CoreScreenLayer {

    private static final Logger logger = LoggerFactory.getLogger(SpawnScreenLayer.class);

    private static final String OREON_BUILDER_PREFAB = "Oreons:OreonBuilder";
    private static final String OREON_GUARD_PREFAB = "Oreons:OreonGuard";
    private static final String OREON_KING_PREFAB = "Oreons:OreonKing";

    private static final String OREON_BUILDER_UI_ID = "summonOreonBuilderCommand";
    private static final String OREON_GUARD_UI_ID = "summonOreonGuardCommand";
    private static final String OREON_KING_UI_ID = "summonOreonKingCommand";

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
        summonOreonBuilderCommand = find(OREON_BUILDER_UI_ID, UIButton.class);
        summonOreonGuardCommand = find(OREON_GUARD_UI_ID, UIButton.class);
        summonOreonKingCommand = find(OREON_KING_UI_ID, UIButton.class);

        summonOreonBuilderCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab(OREON_BUILDER_PREFAB));
        });

        summonOreonGuardCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab(OREON_GUARD_PREFAB));
        });

        summonOreonKingCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab(OREON_KING_PREFAB));
        });
    }

    public void sendOreonSpawnEvent(Prefab prefabToSpawn) {
        setPortalEntity();
        boolean portalHasALocation = portalEntity.hasComponent(LocationComponent.class);
        if (portalHasALocation) {
            localPlayer.getCharacterEntity().send(new OreonSpawnEvent(prefabToSpawn, portalEntity.getComponent(LocationComponent.class).getWorldPosition()));
        }
    }

    public void setPortalEntity() {
        for(EntityRef portal : entityManager.getEntitiesWith(PortalComponent.class, LocationComponent.class)){
            portalEntity = portal;
        }
    }

}
