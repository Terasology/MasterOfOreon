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

public class Constants {
    private static final String OREON_BUILDER_PREFAB = "Oreons:OreonBuilder";
    private static final String OREON_GUARD_PREFAB = "Oreons:OreonGuard";
    private static final String OREON_KING_PREFAB = "Oreons:OreonKing";

    private static final String OREON_BUILDER_UI_ID = "summonOreonBuilderCommand";
    private static final String OREON_GUARD_UI_ID = "summonOreonGuardCommand";
    private static final String OREON_KING_UI_ID = "summonOreonKingCommand";

    private static final String MOUSE_CAPTURING_SCREEN_UIELEMENT_ID = "masteroforeon:mouseCapturingScreen";
    private static final String TABBED_MENU_WIDGET_ID = "masteroforeon:tabbedMenu";

    private static final String SELECTION_TOOL_PREFAB = "MasterOfOreon:selectionTool";
    private static final String PORTAL_PREFAB = "MasterOfOreon:portal";


    public static String getOreonBuilderPrefab() {
        return OREON_BUILDER_PREFAB;
    }

    public static String getOreonGuardPrefab() {
        return OREON_GUARD_PREFAB;
    }

    public static String getOreonKingPrefab() {
        return OREON_KING_PREFAB;
    }

    public static String getOreonBuilderUiId() {
        return OREON_BUILDER_UI_ID;
    }

    public static String getOreonGuardUiId() {
        return OREON_GUARD_UI_ID;
    }

    public static String getOreonKingUiId() {
        return OREON_KING_UI_ID;
    }

    public static String getMouseCapturingScreenUielementId() {
        return MOUSE_CAPTURING_SCREEN_UIELEMENT_ID;
    }

    public static String getTabbedMenuWidgetId() {
        return TABBED_MENU_WIDGET_ID;
    }

    public static String getSelectionToolPrefab() {
        return SELECTION_TOOL_PREFAB;
    }

    public static String getPortalPrefab() {
        return PORTAL_PREFAB;
    }

}
