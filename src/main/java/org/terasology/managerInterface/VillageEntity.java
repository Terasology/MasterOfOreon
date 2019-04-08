/*
 * Copyright 2019 MovingBlocks
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
package org.terasology.managerInterface;

import org.terasology.buildings.components.ConstructedBuildingComponent;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class VillageEntity {
    public String VillageName;
    public String OwnerName;
    public int OreonCount;
    public List<String> BuildingList;

    public VillageEntity(String name, String player){
        VillageName = name;
        OwnerName = player;
        BuildingList = new ArrayList<String>();
        Random r = new Random();
        OreonCount = r.nextInt((10 - 0) + 1) + 0;
    }

    @Override
    public String toString(){
        return VillageName;
    }
}
