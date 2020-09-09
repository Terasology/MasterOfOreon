// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.overviewSystem;

import org.terasology.engine.input.BindButtonEvent;
import org.terasology.engine.input.DefaultBinding;
import org.terasology.engine.input.RegisterBindButton;
import org.terasology.nui.input.InputType;
import org.terasology.nui.input.Keyboard;

@RegisterBindButton(id = "toggleOverviewScreen", description = "Triggers the Overview Screen")
@DefaultBinding(type = InputType.KEY, id = Keyboard.KeyId.O)
public class OverviewScreenButton extends BindButtonEvent {
}
