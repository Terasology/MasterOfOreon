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
package org.terasology.spawning;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Vector3f;
import org.terasology.portals.PortalComponent;
import org.terasology.registry.In;


@RegisterSystem(RegisterMode.AUTHORITY)
public class SpawningAuthoritySystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SpawningAuthoritySystem.class);

    @In
    private EntityManager entityManager;
    @In
    private LocalPlayer localPlayer;

    private Prefab prefabToSpawn;
    private Vector3f spawnPos;

    /**
     *  Sets the position in the world where Oreon has to be spawned.
     *  The position is same as the Portal which is used to spawn
     */
    public void setSpawnPos(EntityRef oreon) {
        LocationComponent locationComponent = oreon.getComponent(LocationComponent.class);
        spawnPos = locationComponent.getWorldPosition();
    }

    public void setPrefabToSpawn(EntityRef oreon) {
        OreonSpawnComponent oreonSpawnComponent = oreon.getComponent(OreonSpawnComponent.class);
        prefabToSpawn = oreonSpawnComponent.oreonPrefab;
        logger.info("Setting the requested prefab for Oreon: " + prefabToSpawn);
    }

    /**
     * Sends a {@link ActivateSpawnScreenEvent} to client which activates Portal
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH, components = PortalComponent.class)
    public void sendActivateScreenEvent(ActivateEvent event, EntityRef entityRef) {
        event.getInstigator().send(new ActivateSpawnScreenEvent(entityRef));
        event.consume();
    }

    /**
     * Spawns the desired Oreon at the location of Portal which sends the event
     */
    @ReceiveEvent(components = OreonSpawnComponent.class)
    public void oreonSpawn(OreonSpawnEvent event, EntityRef entityRef) {
        EntityRef oreon = entityRef;

        setSpawnPos(oreon);
        setPrefabToSpawn(oreon);

        logger.info("Recieved Oreon spawn event" + oreon);

        // spawn the new oreon into the world
        //TODO Resource consuming spawn
        EntityRef newOreon = entityManager.create(prefabToSpawn, spawnPos);
        OreonSpawnComponent oreonSpawnComponent = newOreon.getComponent(OreonSpawnComponent.class);
        oreonSpawnComponent.parent = localPlayer.getCharacterEntity();
    }

}
