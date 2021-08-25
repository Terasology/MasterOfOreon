// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.research.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

public class LaboratoryComponent implements Component<LaboratoryComponent> {
    public EntityRef laboratoryEntity;

    @Override
    public void copyFrom(LaboratoryComponent other) {
        this.laboratoryEntity = other.laboratoryEntity;
    }
}
