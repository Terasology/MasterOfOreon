// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.taskSystem.taskCompletion;

import com.google.common.math.DoubleMath;
import org.joml.Vector2ic;
import org.joml.Vector3ic;
import org.terasology.buildings.BasicRasterTarget;
import org.terasology.buildings.BuildingParcel;
import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.cities.bldg.Building;
import org.terasology.cities.bldg.BuildingPart;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.bldg.gen.BuildingGenerator;
import org.terasology.cities.bldg.gen.SimpleChurchGenerator;
import org.terasology.cities.common.Edges;
import org.terasology.cities.deco.ColumnDecoration;
import org.terasology.cities.deco.Decoration;
import org.terasology.cities.deco.SingleBlockDecoration;
import org.terasology.cities.door.Door;
import org.terasology.cities.door.WingDoor;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.PentRoof;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.raster.BuildingPens;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.Pens;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.cities.window.RectWindow;
import org.terasology.cities.window.SimpleWindow;
import org.terasology.cities.window.Window;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.math.TeraMath;
import org.terasology.taskSystem.BuildingType;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.terasology.commonworld.Orientation.EAST;
import static org.terasology.commonworld.Orientation.WEST;

// TODO: This class should be moved to the Cities module
public class ConstructingFromBuildingGenerator implements BuildTaskCompletion {

    private BlockManager blockManager;

    private WorldProvider worldProvider;

    private BuildingGenerator compositeBuildingGenerator;

    public ConstructingFromBuildingGenerator(WorldProvider world, BlockManager manager) {
        this.worldProvider = world;
        this.blockManager = manager;
    }

    public void constructBuilding(BlockRegion selectedRegion, BuildingType buildingType) {
        BuildingParcel buildingParcel = new BuildingParcel();
        buildingParcel.setOrientation(Orientation.NORTH);

        int minX = selectedRegion.minX();
        int maxX = selectedRegion.maxX();
        int minY = selectedRegion.minY();

        BlockArea shape = new BlockArea(minX, selectedRegion.minY(), maxX, selectedRegion.maxY());
        buildingParcel.setShape(shape);

        HeightMap heightMap = HeightMaps.constant(minY);

        selectBuilding(buildingType, 0);

        Building compositeBuilding = compositeBuildingGenerator.generate(buildingParcel, heightMap);

        buildParts(compositeBuilding, shape, heightMap);
    }

    public void selectBuilding(BuildingType buildingType, int level) {
        switch (buildingType) {
            case Diner:
                SimpleChurchGenerator simpleChurchGenerator = new SimpleChurchGenerator(100);
                compositeBuildingGenerator = simpleChurchGenerator;
        }

    }

    /**
     * Generates the BlockTheme with the blocks required for the construction of different buildings
     *
     * @return The genrated theme
     */
    private BlockTheme buildBlockTheme() {
        return BlockTheme.builder(blockManager)
                .register(DefaultBlockType.ROAD_FILL, "CoreAssets:Dirt")
                .register(DefaultBlockType.ROAD_SURFACE, "CoreAssets:Gravel")
                .register(DefaultBlockType.LOT_EMPTY, "CoreAssets:Dirt")
                .register(DefaultBlockType.BUILDING_WALL, "StructuralResources:StoneBlocks")
                .register(DefaultBlockType.BUILDING_FLOOR, "StructuralResources:StoneBlocksDark")
                .register(DefaultBlockType.BUILDING_FOUNDATION, "CoreAssets:Gravel")
                .register(DefaultBlockType.TOWER_STAIRS, "CoreAssets:CobbleStone")
                .register(DefaultBlockType.ROOF_FLAT, "StructuralResources:RoofTilesLarge")
                .register(DefaultBlockType.ROOF_HIP, "StructuralResources:PlanksEvenDark")
                .register(DefaultBlockType.ROOF_SADDLE, "StructuralResources:PlanksEvenDark")
                .register(DefaultBlockType.ROOF_DOME, "CoreAssets:Plank")
                .register(DefaultBlockType.ROOF_GABLE, "CoreAssets:Plank")
                .register(DefaultBlockType.SIMPLE_DOOR, BlockManager.AIR_ID)
                .register(DefaultBlockType.WING_DOOR, BlockManager.AIR_ID)
                .register(DefaultBlockType.WINDOW_GLASS, BlockManager.AIR_ID)
                .register(DefaultBlockType.TOWER_WALL, "StructuralResources:StoneBlocks")

                // -- requires Fences module
                .registerFamily(DefaultBlockType.FENCE, "Fences:Fence")
                .registerFamily(DefaultBlockType.FENCE_GATE, BlockManager.AIR_ID)  // there is no fence gate :-(
                .registerFamily(DefaultBlockType.TOWER_STAIRS, "CoreAssets:CobbleStone:engine:stair")
                .registerFamily(DefaultBlockType.BARREL, "StructuralResources:Barrel")
                .registerFamily(DefaultBlockType.LADDER, "CoreAssets:Ladder")
                .registerFamily(DefaultBlockType.PILLAR_BASE, "CoreAssets:CobbleStone:StructuralResources:pillarBase")
                .registerFamily(DefaultBlockType.PILLAR_MIDDLE, "CoreAssets:CobbleStone:StructuralResources:pillar")
                .registerFamily(DefaultBlockType.PILLAR_TOP, "CoreAssets:CobbleStone:StructuralResources:pillarTop")
                .registerFamily(DefaultBlockType.TORCH, "CoreAssets:Torch")
                .build();
    }

    /**
     * Constructs the different parts of a building like its doors, windows etc.
     *
     * @param compositeBuilding The Building object which is to be built
     * @param shape The bounding rectangle inside which construction is to be done
     * @param heightMap The height map of the base on which the building is being constructed
     */
    private void buildParts(Building compositeBuilding, BlockAreac shape, HeightMap heightMap) {
        BlockTheme theme = buildBlockTheme();

        BasicRasterTarget rasterTarget = new BasicRasterTarget(worldProvider, shape, theme);

        for (BuildingPart part : compositeBuilding.getParts()) {
            if (RectBuildingPart.class.isInstance(part)) {
                RectBuildingPart rectBuildingPart = RectBuildingPart.class.cast(part);
                buildRectPart(rasterTarget, rectBuildingPart, heightMap);
            }

            buildDoors(rasterTarget, part);

            buildWindows(rasterTarget, part);

            buildDecorations(rasterTarget, part);

            buildRoofs(rasterTarget, part);
        }
    }

    //TODO: Move the following methods to separate classes i.e different classes for Doors, Windows etc so that different types of these
    // parts can be handled at one place

    /**
     * Places the {@link RectBuildingPart} of a building into the world
     *
     * @param rasterTarget
     * @param rectBuildingPart The part object which is being built
     * @param heightMap Height map of the base
     */
    private void buildRectPart(RasterTarget rasterTarget, RectBuildingPart rectBuildingPart, HeightMap heightMap) {

        BlockAreac rc = rectBuildingPart.getShape();

        if (!rc.intersectsBlockArea(rasterTarget.getAffectedArea())) {
            return;
        }

        int baseHeight = rectBuildingPart.getBaseHeight();
        int wallHeight = rectBuildingPart.getWallHeight();

        Pen floorPen = BuildingPens.floorPen(rasterTarget, heightMap, baseHeight, DefaultBlockType.BUILDING_FLOOR);
        RasterUtil.fillRect(floorPen, rc);

        // create walls
        Pen wallPen = Pens.fill(rasterTarget, baseHeight, baseHeight + wallHeight, DefaultBlockType.BUILDING_WALL);
        RasterUtil.drawRect(wallPen, rc);
    }

    private void buildDoors(RasterTarget rasterTarget, BuildingPart part) {
        for (Door door : part.getDoors()) {

            // For a Wing Door
            if (WingDoor.class.isInstance(door)) {
                WingDoor wingDoor = WingDoor.class.cast(door);
                Pen pen = Pens.fill(rasterTarget, wingDoor.getBaseHeight(), wingDoor.getTopHeight(), DefaultBlockType.WING_DOOR);
                RasterUtil.fillRect(pen, wingDoor.getArea());
            }
        }
    }

    private void buildWindows(RasterTarget rasterTarget, BuildingPart part) {
        for (Window window : part.getWindows()) {

            // For RectWindow
            if (RectWindow.class.isInstance(window)) {
                RectWindow rectWindow = RectWindow.class.cast(window);
                Pen pen = Pens.fill(rasterTarget, rectWindow.getBaseHeight(), rectWindow.getTopHeight(), rectWindow.getBlockType());
                RasterUtil.fillRect(pen, rectWindow.getArea());
            }

            if (SimpleWindow.class.isInstance(window)) {
                SimpleWindow simpleWindow = SimpleWindow.class.cast(window);
                int x = simpleWindow.getPos().x();
                int y = simpleWindow.getHeight();
                int z = simpleWindow.getPos().y();

                if (rasterTarget.getAffectedRegion().contains(x, y, z)) {
                    rasterTarget.setBlock(x, y, z, DefaultBlockType.WINDOW_GLASS);
                }
            }
        }
    }

    private void buildDecorations(RasterTarget rasterTarget, BuildingPart part) {
        for (Decoration decoration : part.getDecorations()) {

            if (SingleBlockDecoration.class.isInstance(decoration)) {
                SingleBlockDecoration blockDecoration = SingleBlockDecoration.class.cast(decoration);
                if (rasterTarget.getAffectedRegion().contains(blockDecoration.getPos())) {
                    rasterTarget.setBlock(blockDecoration.getPos(), blockDecoration.getType(),
                            Collections.singleton(blockDecoration.getSide()));
                }
            }

            if (ColumnDecoration.class.isInstance(decoration)) {
                ColumnDecoration columnDecoration = ColumnDecoration.class.cast(decoration);
                Vector3ic pos = columnDecoration.getBasePos();
                int y = pos.y();
                if (rasterTarget.getAffectedArea().contains(pos.x(), pos.z())) {
                    if (y + columnDecoration.getBlockTypes().size() - 1 >= rasterTarget.getMinHeight() && y <= rasterTarget.getMaxHeight()) {
                        for (int i = 0; i < columnDecoration.getHeight(); i++) {
                            BlockType type = columnDecoration.getBlockTypes().get(i);
                            Side side = columnDecoration.getSides().get(i);
                            Set<Side> sides = (side == null) ? EnumSet.noneOf(Side.class) : EnumSet.of(side);
                            rasterTarget.setBlock(pos.x(), y, pos.z(), type, sides);
                            y++;
                        }
                    }
                }
            }
        }
    }

    private void buildRoofs(RasterTarget rasterTarget, BuildingPart part) {
        Roof roof = part.getRoof();

        if (SaddleRoof.class.isInstance(roof)) {
            SaddleRoof saddleRoof = SaddleRoof.class.cast(roof);
            BlockAreac area = saddleRoof.getArea();

            if (!area.intersectsBlockArea(rasterTarget.getAffectedArea())) {
                return;
            }

            final boolean alongX = saddleRoof.getOrientation() == EAST || saddleRoof.getOrientation() == WEST;

            HeightMap heightMapTop = new HeightMap() {

                @Override
                public int apply(int x, int z) {
                    int rx = x - area.minX();
                    int rz = z - area.minY();

                    int y = saddleRoof.getBaseHeight();

                    // distance to border of the roof
                    int borderDistX = Math.min(rx, area.getSizeX() - 1 - rx);
                    int borderDistZ = Math.min(rz, area.getSizeY() - 1 - rz);

                    if (alongX) {
                        y += borderDistZ / saddleRoof.getPitch();
                    } else {
                        y += borderDistX / saddleRoof.getPitch();
                    }

                    return y;
                }
            };

            HeightMap heightMapBottom = HeightMaps.offset(heightMapTop, -1);
            Pen pen = Pens.fill(rasterTarget, heightMapBottom, heightMapTop, DefaultBlockType.ROOF_SADDLE);
            RasterUtil.fillRect(pen, area);

            BlockAreac wallRect = saddleRoof.getBaseArea();

            HeightMap heightMapGableBottom = new HeightMap() {

                @Override
                public int apply(int x, int z) {
                    int h0 = saddleRoof.getBaseHeight();
                    if (alongX) {
                        int left = wallRect.minX();
                        int right = wallRect.maxX();

                        if (x == left || x == right) {
                            return h0;
                        }
                    } else {
                        int top = wallRect.minY();
                        int bottom = wallRect.maxY();
                        if (z == top || z == bottom) {
                            return h0;
                        }
                    }

                    return heightMapBottom.apply(x, z);        // return top-height to get a no-op
                }
            };

            pen = Pens.fill(rasterTarget, heightMapGableBottom, heightMapBottom, DefaultBlockType.ROOF_GABLE);
            RasterUtil.fillRect(pen, area);
        }

        if (HipRoof.class.isInstance(roof)) {
            HipRoof hipRoof = HipRoof.class.cast(roof);
            BlockAreac area = hipRoof.getArea();

            if (!area.intersectsBlockArea(rasterTarget.getAffectedArea())) {
                return;
            }

            HeightMap heightMapBottom = new HeightMap() {

                @Override
                public int apply(int x, int z) {
                    int dist = Edges.getDistanceToBorder(area, x, z);
                    int y = TeraMath.floorToInt(hipRoof.getBaseHeight() + dist * hipRoof.getPitch());
                    return Math.min(y, hipRoof.getMaxHeight());
                }
            };

            HeightMap heightMapTop = HeightMaps.offset(heightMapBottom, TeraMath.ceilToInt(hipRoof.getPitch()));
            Pen pen = Pens.fill(rasterTarget, heightMapBottom, heightMapTop, DefaultBlockType.ROOF_HIP);
            RasterUtil.fillRect(pen, area);
        }

        if (PentRoof.class.isInstance(roof)) {
            PentRoof pentRoof = PentRoof.class.cast(roof);

            BlockAreac area = pentRoof.getArea();

            if (!area.intersectsBlockArea(rasterTarget.getAffectedArea())) {
                return;
            }

            final HeightMap heightMapBottom = new HeightMap() {

                @Override
                public int apply(int x, int z) {
                    int rx = x - area.minX();
                    int rz = z - area.minY();

                    Vector2ic dir = pentRoof.getOrientation().direction();

                    if (dir.x() < 0) {
                        rx -= area.getSizeX() - 1;  // maxX
                    }

                    if (dir.y() < 0) {
                        rz -= area.getSizeY() - 1; // maxY
                    }

                    int hx = rx * dir.x();
                    int hz = rz * dir.y();

                    int h = DoubleMath.roundToInt(Math.max(hx, hz) * pentRoof.getPitch(), RoundingMode.HALF_UP);

                    return pentRoof.getBaseHeight() + h;
                }
            };

            int thickness = TeraMath.ceilToInt(pentRoof.getPitch());
            HeightMap heightMapTop = HeightMaps.offset(heightMapBottom, thickness);
            Pen pen = Pens.fill(rasterTarget, heightMapBottom, heightMapTop, DefaultBlockType.ROOF_HIP);
            RasterUtil.fillRect(pen, area);

            final BlockAreac wallRect = pentRoof.getBaseArea();

            HeightMap heightMapGableBottom = new HeightMap() {

                @Override
                public int apply(int x, int z) {
                    int h0 = pentRoof.getBaseHeight();

                    boolean onZ = (x == wallRect.minX() || x == wallRect.maxX());
                    boolean zOk = (z >= wallRect.minY() && z <= wallRect.maxY());

                    if (onZ && zOk) {
                        return h0;
                    }

                    boolean onX = (z == wallRect.minY() || z == wallRect.maxY());
                    boolean xOk = (x >= wallRect.minX() && x <= wallRect.maxX());

                    if (onX && xOk) {
                        return h0;
                    }

                    return heightMapBottom.apply(x, z); // return top-height to get a no-op
                }
            };

            pen = Pens.fill(rasterTarget, heightMapGableBottom, heightMapBottom, DefaultBlockType.ROOF_GABLE);
            RasterUtil.fillRect(pen, area);
        }
    }
}
