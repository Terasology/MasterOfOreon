package org.terasology.managerInterface;

/*
 * Copyright 2012 Benjamin Glatzel <benjamin.glatzel@me.com>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.managerInterface.nui.ManagerInterfaceHUDElement;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;

@Share(ManagerInterfaceSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class ManagerInterfaceSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ManagerInterfaceSystem.class);

    @In
    private NUIManager nuiManager;

    @Override
    public void initialise() {
        ManagerInterfaceHUDElement menuHUDElement = ManagerInterfaceHUDElement.getMenuHudElement();
    }
}
