// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology;

public class MooConstants {
    // Oreon prefabs
    public static final String OREON_BUILDER_PREFAB = "Oreons:OreonBuilder";
    public static final String OREON_GUARD_PREFAB = "Oreons:OreonGuard";
    public static final String OREON_KING_PREFAB = "Oreons:OreonKing";

    // Spawning button IDs
    public static final String OREON_BUILDER_UI_ID = "summonOreonBuilderCommand";
    public static final String OREON_GUARD_UI_ID = "summonOreonGuardCommand";
    public static final String OREON_KING_UI_ID = "summonOreonKingCommand";

    public static final String UPGRADE_TOOL_NAME = "Building Upgrade Tool";

    // Required resources UI label ID
    public static final String OREON_BUILDER_RESOURCES_LABEL_ID = "oreonBuilderResourcesRequired";
    public static final String OREON_GUARD_RESOURCES_LABEL_ID = "oreonGuardResourcesRequired";
    public static final String OREON_KING_RESOURCES_LABEL_ID = "oreonKingResourcesRequired";

    // Lists on Task Selection Screen
    public static final String TASK_SELECTION_SCREEN_LIST = "taskSelectionScreenList";
    public static final String BUILDINGS_LIST_ID = "buildingsList";

    //Tab buttons on Task Selection Screen
    public static final String TASKS_TAB_BUTTON = "tasksTabButton";
    public static final String BUILDINGS_TAB_BUTTON = "buildingsTabButton";

    // Cancel Selection button on task selection screen
    public static final String CANCEL_BUTTON_ID = "cancelButton";

    // Confirm Selection button on task selection screen
    public static final String CONFIRM_BUTTON_ID = "confirmButton";

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
    public static final String STRUCTURE_TEMPLATE_TYPE_CHURCH = "MasterOfOreon:church";
    public static final String STRUCTURE_TEMPLATE_TYPE_DINER = "MasterOfOreon:diner";
    public static final String STRUCTURE_TEMPLATE_TYPE_HOSPITAL = "MasterOfOreon:hospital";
    public static final String STRUCTURE_TEMPLATE_TYPE_JAIL = "MasterOfOreon:jail";
    public static final String STRUCTURE_TEMPLATE_TYPE_LABORATORY = "MasterOfOreon:laboratory";
    public static final String STRUCTURE_TEMPLATE_TYPE_STORAGE = "MasterOfOreon:storage";
    public static final String STRUCTURE_TEMPLATE_TYPE_BEDROOM = "MasterOfOreon:bedroom";

    // Structure Template prefabs
    public static final String DINER_PREFAB = "MasterOfOreon:inn";
    public static final String STORAGE_PREFAB = "MasterOfOreon:storehouse";
    public static final String LABORATORY_PREFAB = "MasterOfOreon:lab";

    public static final String FENCE_BLOCK_URI = "Fences:Fence.0";
    public static final String TORCH_BLOCK_URI = "CoreAssets:Torch.TOP";

    // Required regions' index in a constructed building
    public static final int DINER_CHAIR_REGION_INDEX = 86;
    public static final int CHEST_BLOCK_INDEX = 0;
    public static final int BOOKCASE_REGION_INDEX = 1;
    public static final int PEDESTAL_REGION_INDEX = 2;
    public static final int LABORATORY_SLAB_REGION = 16;
    public static final int STORAGE_ENTRANCE_REGION =12;
    public static final int WALL_OF_JAIL_REGION = 0;

    public static final String TASK_SELECTION_SCREEN_URI = "taskSelectionScreen";
    public static final String BUILDING_UPGRADE_SCREEN_URI = "buildingUpgradeScreen";
    public static final String OVERVIEW_SCREEN_URI = "MasterOfOreon:overviewScreen";

    // Lists in Overview Screen
    public static final String AVAILABLE_TASKS_LIST_ID = "availableTasksList";
    public static final String ON_GOING_TASKS_LIST_ID = "inProgressTasksList";
    public static final String OREONS_LIST_ID = "oreons";
    public static final String CONSTRUCTED_BUILDINGS_LIST_ID = "constructedBuildingsList";

    public static final String PEDESTAL_PREFAB = "MasterOfOreon:pedestal";
    public static final String RESEARCH_BOOK_NAME = "Research Book";

    // Books' prefab
    public static final String COOKIE_CROP_RESEARCH_BOOK = "MasterOfOreon:cookieCropResearchBook";
    public static final String COOKIE_CROP_RESEARCH_BOOK2 = "MasterOfOreon:cookieCropResearchBook2";
    public static final String PORTAL_RESEARCH_BOOK = "MasterOfOreon:portalResearchBook";

    public static final String EXCLAMATION_PREFAB = "MasterOfOreon:exclamationMark";
}
