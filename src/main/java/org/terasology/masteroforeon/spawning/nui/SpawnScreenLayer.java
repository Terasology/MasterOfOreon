// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.masteroforeon.spawning.nui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.masteroforeon.MooConstants;
import org.terasology.masteroforeon.portals.PortalComponent;
import org.terasology.masteroforeon.spawning.OreonSpawnComponent;
import org.terasology.masteroforeon.spawning.OreonSpawnEvent;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;

import java.util.Map;

/**
 * The screen which is triggered when player interacts({@code e press}) with a Portal block.
 */
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

    private UILabel builderResourceRequired;
    private UILabel guardResourceRequired;
    private UILabel kingResourceRequired;

    private EntityRef portalEntity;

    @Override
    public void initialise() {
        summonOreonBuilderCommand = find(MooConstants.OREON_BUILDER_UI_ID, UIButton.class);
        summonOreonGuardCommand = find(MooConstants.OREON_GUARD_UI_ID, UIButton.class);
        summonOreonKingCommand = find(MooConstants.OREON_KING_UI_ID, UIButton.class);

        builderResourceRequired = find(MooConstants.OREON_BUILDER_RESOURCES_LABEL_ID, UILabel.class);
        guardResourceRequired = find(MooConstants.OREON_GUARD_RESOURCES_LABEL_ID, UILabel.class);
        kingResourceRequired = find(MooConstants.OREON_KING_RESOURCES_LABEL_ID, UILabel.class);

        populateUiLabels(MooConstants.OREON_BUILDER_PREFAB, builderResourceRequired);
        populateUiLabels(MooConstants.OREON_GUARD_PREFAB, guardResourceRequired);
        populateUiLabels(MooConstants.OREON_KING_PREFAB, kingResourceRequired);

        summonOreonBuilderCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab(MooConstants.OREON_BUILDER_PREFAB));
        });

        summonOreonGuardCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab(MooConstants.OREON_GUARD_PREFAB));
        });

        summonOreonKingCommand.subscribe(button -> {
            sendOreonSpawnEvent(prefabManager.getPrefab(MooConstants.OREON_KING_PREFAB));
        });
    }

    private void sendOreonSpawnEvent(Prefab prefabToSpawn) {
        setPortalEntity();
        LocationComponent portalLocation = portalEntity.getComponent(LocationComponent.class);
        if (portalLocation != null) {
            localPlayer.getCharacterEntity().send(new OreonSpawnEvent(prefabToSpawn,
                    portalLocation.getWorldPosition()));
        }
    }

    private void setPortalEntity() {
        for (EntityRef portal : entityManager.getEntitiesWith(PortalComponent.class, LocationComponent.class)) {
            portalEntity = portal;
            break;
        }
    }

    /**
     * Popultes the text label fields in the screen with the items required for spawning.
     *
     * @param prefab The Oreon prefab for which the label is being set.
     * @param label The label to be set.
     */
    private void populateUiLabels(String prefab, UILabel label) {
        Prefab oreonPrefab = prefabManager.getPrefab(prefab);
        OreonSpawnComponent oreonSpawnComponent = oreonPrefab.getComponent(OreonSpawnComponent.class);
        StringBuilder text = new StringBuilder("Requires ");

        if (oreonSpawnComponent != null) {
            Map<String, Integer> items = oreonSpawnComponent.itemsToConsume;
            int itemsRequired = items.size();

            for (String blockRequired : items.keySet()) {
                text.append(blockRequired);
                text.append(" : ");
                text.append(items.get(blockRequired) + ", ");
            }

            if (itemsRequired == 0) {
                text.append("nothing");
            }
        }

        label.setText(text.toString());
    }

}
