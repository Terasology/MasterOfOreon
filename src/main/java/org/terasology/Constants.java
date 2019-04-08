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
package org.terasology;

public class Constants {
    // Oreon prefabs
    public static final String OREON_BUILDER_PREFAB = "Oreons:OreonBuilder";
    public static final String OREON_GUARD_PREFAB = "Oreons:OreonGuard";
    public static final String OREON_KING_PREFAB = "Oreons:OreonKing";

    // Spawning button IDs
    public static final String OREON_BUILDER_UI_ID = "summonOreonBuilderCommand";
    public static final String OREON_GUARD_UI_ID = "summonOreonGuardCommand";
    public static final String OREON_KING_UI_ID = "summonOreonKingCommand";

    // City teleport button IDs
    public static final String CITY1_UI_ID = "city1Command";
    public static final String CITY2_UI_ID = "city2Command";
    public static final String CITY3_UI_ID = "city3Command";


    // Tools given to player on spawn
    public static final String SELECTION_TOOL_PREFAB = "MasterOfOreon:oreonSelectionTool";
    public static final String BUILDING_UPGRADE_TOOL = "MasterOfOreon:upgradeTool";
    public static final String PORTAL_PREFAB = "MasterOfOreon:portal";
    public static final String MANAGEMENT_BOOK_PREFAB = "MasterOfOreon:managementBook";

    public static final String UPGRADE_TOOL_NAME = "Building Upgrade Tool";

    // Required resources UI label ID
    public static final String OREON_BUILDER_RESOURCES_LABEL_ID = "oreonBuilderResourcesRequired";
    public static final String OREON_GUARD_RESOURCES_LABEL_ID = "oreonGuardResourcesRequired";
    public static final String OREON_KING_RESOURCES_LABEL_ID = "oreonKingResourcesRequired";

    // Required resources UI label ID
    public static final String CITY1_LABEL_ID = "city1TeleportLabel";
    public static final String CITY2_LABEL_ID = "city2TeleportLabel";
    public static final String CITY3_LABEL_ID = "city3TeleportLabel";

    // Task selection button IDs
    public static final String PLANT_COMMAND_UI_ID = "plantCommandButton";
    public static final String GUARD_COMMAND_UI_ID = "guardCommandButton";

    // Building type selection button IDs
    public static final String HOSPITAL_BUTTON_ID = "hospitalButton";
    public static final String DINER_BUTTON_ID = "dinerButton";
    public static final String GYM_BUTTON_ID = "gymButton";
    public static final String CLASSROOM_BUTTON_ID = "classroomButton";
    public static final String STORAGE_BUTTON_ID = "storageButton";
    public static final String LABORATORY_BUTTON_ID = "laboratoryButton";
    public static final String JAIL_BUTTON_ID = "jailButton";

    // Cancel Selection button on task selection screen
    public static final String CANCEL_BUTTON_ID = "cancelButton";

    // Upgrade screen labels and button
    public static final String BUILDING_NAME_UI_LABEL_ID = "buildingName";
    public static final String BUILDING_LEVEL_UI_LABEL_ID = "buildingLevel";
    public static final String BUILDING_UPGRADE_COMMAND_UI_ID = "upgradeBuildingCommand";
    public static final String GUARD_BUILDING_COMMAND_UI_ID = "guardBuildingCommand";

    // Crop to be placed after plant task
    public static final String OREON_CROP_0_BLOCK = "MasterOfOreon:OreonPlant0";

    // Crop to be checked for in the Diner before eat task
    public static final String COOKIE_CROP_URI = "MasterOfOreon:CookieCrop";

    // Prefab for notification messages
    public static final String NOTIFICATION_MESSAGE_PREFAB = "MasterOfOreon:notificationMessage";

    // Structure Template type prefabs for construction
    public static final String STRUCTURE_TEMPLATE_TYPE_DINER = "MasterOfOreon:diner";
    public static final String STRUCTURE_TEMPLATE_TYPE_STORAGE = "MasterOfOreon:storage";
    public static final String STRUCTURE_TEMPLATE_TYPE_LABORATORY = "MasterOfOreon:laboratory";
    public static final String STRUCTURE_TEMPLATE_TYPE_JAIL = "MasterOfOreon:jail";

    // Structure Template prefabs
    public static final String DINER_PREFAB = "MasterOfOreon:inn";
    public static final String STORAGE_PREFAB = "MasterOfOreon:storage";

    public static final String FENCE_BLOCK_URI = "Fences:Fence.0";
    public static final String TORCH_BLOCK_URI = "Core:Torch.TOP";

    // Required regions' index in a constructed building
    public static final int DINER_CHAIR_REGION_INDEX = 86;
    public static final int CHEST_BLOCK_INDEX = 0;
    public static final int BOOKCASE_REGION_INDEX = 1;
    public static final int PEDESTAL_REGION_INDEX = 2;
    public static final int LABORATORY_SLAB_REGION = 16;
    public static final int STORAGE_ENTRANCE_REGION =12;
    public static final int WALL_OF_JAIL_REGION = 0;

    public static final String AIR_BLOCK_URI = "engine:air";

    public static final String TASK_SELECTION_SCREEN_URI = "taskSelectionScreen";
    public static final String BUILDING_UPGRADE_SCREEN_URI = "buildingUpgradeScreen";
    public static final String OVERVIEW_SCREEN_URI = "MasterOfOreon:overviewScreen";

    // Lists in Overview Screen
    public static final String AVAILABLE_TASKS_LIST_ID = "availableTasksList";
    public static final String ON_GOING_TASKS_LIST_ID = "inProgressTasksList";
    public static final String OREONS_LIST_ID = "oreons";
    public static final String BUILDINGS_LIST_ID = "buildingsList";

    // List in Management Screen
    public static final String OWNED_VILLAGES_LIST_ID = "ownedVillagesList";

    public static final String PEDESTAL_PREFAB = "MasterOfOreon:pedestal";
    public static final String RESEARCH_BOOK_NAME = "Research Book";

    // Books' prefab
    public static final String COOKIE_CROP_RESEARCH_BOOK = "MasterOfOreon:cookieCropResearchBook";

    public static final String EXCLAMATION_PREFAB = "MasterOfOreon:exclamationMark";
}
