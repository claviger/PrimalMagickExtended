package com.claviger.primalmagickextended.common.portals;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.AbstractRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.EnhancerRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.GateRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.HasteRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.InfinityRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.blocks.portals.StrongEnhancerRuneBlock;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

/**
 * Builds portals: finds the closed frame around a starting position, fills it with portal blocks, counts any runes
 * already on its frame and registers the portal. Also recounts a portal's runes from the world, which rune blocks
 * call whenever one is added or removed. Ported from Custom Portals.
 */
public class PortalHelper {

    private static BlockPos getUp(BlockPos pos, Direction.Axis axis) {
        return axis == Direction.Axis.Y ? pos.north() : pos.above();
    }

    private static BlockPos getDown(BlockPos pos, Direction.Axis axis) {
        return axis == Direction.Axis.Y ? pos.south() : pos.below();
    }

    private static BlockPos getLeft(BlockPos pos, Direction.Axis axis) {
        return axis == Direction.Axis.X ? pos.south() : pos.east();
    }

    private static BlockPos getRight(BlockPos pos, Direction.Axis axis) {
        return axis == Direction.Axis.X ? pos.north() : pos.west();
    }

    private static int getTranslatedX(BlockPos pos, Direction.Axis axis) {
        return axis == Direction.Axis.X ? pos.getZ() : pos.getX();
    }

    private static int getTranslatedY(BlockPos pos, Direction.Axis axis) {
        return axis == Direction.Axis.Y ? pos.getZ() : pos.getY();
    }

    private static List<BlockPos> getAdjacentsOnPlane(BlockPos blockPos, Direction.Axis axis) {
        List<BlockPos> adjacents = new ArrayList<>();
        adjacents.add(getUp(blockPos, axis));
        adjacents.add(getLeft(blockPos, axis));
        adjacents.add(getRight(blockPos, axis));
        adjacents.add(getDown(blockPos, axis));
        return adjacents;
    }

    private static List<BlockPos> getAdjacents(BlockPos blockPos) {
        List<BlockPos> adjacents = new ArrayList<>();
        adjacents.add(blockPos.above());
        adjacents.add(blockPos.below());
        adjacents.add(blockPos.north());
        adjacents.add(blockPos.south());
        adjacents.add(blockPos.east());
        adjacents.add(blockPos.west());
        return adjacents;
    }

    private static SpawnPosData determineSpawnPos(List<BlockPos> portalBlocks, Block portalBlock, Direction.Axis axis, Level world) {
        int leftmost = getTranslatedX(portalBlocks.get(0), axis), rightmost = getTranslatedX(portalBlocks.get(0), axis);
        for (BlockPos blockPos : portalBlocks) {
            int x = getTranslatedX(blockPos, axis);
            if (x < leftmost) leftmost = x;
            if (x > rightmost) rightmost = x;
        }
        int xSpawn = (rightmost + leftmost) / 2;
        int length = rightmost - leftmost + 1;
        if (length % 2 == 0 && leftmost >= 0) xSpawn++;
        List<BlockPos> midPortalBlocks = new ArrayList<>();
        for (BlockPos blockPos : portalBlocks) {
            int x = getTranslatedX(blockPos, axis);
            if (x == xSpawn) midPortalBlocks.add(blockPos);
        }
        BlockPos bottom = midPortalBlocks.get(0);
        for (BlockPos blockPos : midPortalBlocks) {
            if (getTranslatedY(blockPos, axis) < getTranslatedY(bottom, axis)) bottom = blockPos;
        }
        int ySpawn = getTranslatedY(bottom, axis);
        int height = 1; // unused unless portal is oriented along Y axis
        if (axis == Direction.Axis.Y) {
            BlockPos pos = bottom;
            while (world.getBlockState(getDown(pos, axis)).is(portalBlock)) {
                pos = getDown(pos, axis);
                height++;
            }
            ySpawn += height / 2;
        }
        int trueXSpawn = axis == Direction.Axis.X ? bottom.getX() : xSpawn;
        int trueYSpawn = axis == Direction.Axis.Y ? bottom.getY() : ySpawn;
        int trueZSpawn = axis == Direction.Axis.Z ? bottom.getZ() : axis == Direction.Axis.X ? xSpawn : ySpawn;
        float offsetX = 0.0f;
        float offsetZ = 0.0f;
        switch (axis) {
            case X -> {
                offsetX = 0.5f;
                if (length % 2 != 0) offsetZ = 0.5f;
            }
            case Y -> {
                if (length % 2 != 0) offsetX = 0.5f;
                if (height % 2 != 0) offsetZ = 0.5f;
            }
            case Z -> {
                offsetZ = 0.5f;
                if (length % 2 != 0) offsetX = 0.5f;
            }
        }
        BlockPos spawnPos = new BlockPos(new Vec3i(trueXSpawn, trueYSpawn, trueZSpawn));
        return new SpawnPosData(spawnPos, offsetX, offsetZ);
    }

    /**
     * Tries to build a portal of the given block in the frame around the start position, trying each plane in
     * turn: X, then Y (horizontal), then Z.
     *
     * @return true if a portal was built
     */
    public static boolean buildPortal(BlockPos startPos, Block portalBlock, UUID creatorId, Level world) {
        if (buildPortal(startPos, Direction.Axis.X, portalBlock, creatorId, world)) return true;
        if (buildPortal(startPos, Direction.Axis.Y, portalBlock, creatorId, world)) return true;
        return buildPortal(startPos, Direction.Axis.Z, portalBlock, creatorId, world);
    }

    /**
     * Tries to build a portal in the plane normal to the given axis. The frame material is taken from the blocks
     * next to the start position, or found by walking down up to 31 blocks; the frame must be a single full-block
     * material allowed by the frame filter and enclose at most 900 blocks.
     *
     * @return true if a portal was built
     */
    public static boolean buildPortal(BlockPos blockPos, Direction.Axis axis, Block portalBlock, UUID creatorId, Level world) {

        if (!world.getBlockState(blockPos).isAir()) return false;

        // figure out which block will make up the frame of the portal
        List<BlockPos> adjacents = getAdjacentsOnPlane(blockPos, axis);
        Block frameMaterial = Blocks.AIR;
        for (BlockPos pos : adjacents) {
            BlockState blockState = world.getBlockState(pos);
            if (!blockState.isAir()) {
                if (frameMaterial != Blocks.AIR) {
                    if (!frameMaterial.equals(blockState.getBlock())) return false;
                }
                else if (blockState.isCollisionShapeFullBlock(world, pos)) frameMaterial = blockState.getBlock();
                else return false;
            }
        }

        BlockPos startPos = blockPos;

        // if we haven't found a frame block, go down on the portal until we find one
        if (frameMaterial == Blocks.AIR) {
            for (int i = 0; world.getBlockState(getDown(startPos, axis)).isAir(); i++) {
                if (i > 30) return false;
                startPos = getDown(startPos, axis);
            }
            if (!world.getBlockState(getDown(startPos, axis)).isCollisionShapeFullBlock(world, getDown(startPos, axis))) return false;
            frameMaterial = world.getBlockState(getDown(startPos, axis)).getBlock();
        }

        // verify that the frame block is a valid portal frame block
        String frameId = BuiltInRegistries.BLOCK.getKey(frameMaterial).toString();
        List<? extends String> blockIds = ServicesPMX.CONFIG.portalsFilteredFrameBlocks();
        if (ServicesPMX.CONFIG.portalsFrameFilterIsWhitelist()) {
            if (!blockIds.contains(frameId)) return false;
        } else {
            if (blockIds.contains(frameId)) return false;
        }

        List<BlockPos> portalBlocks = new ArrayList<>();

        // DFS algorithm to find the block positions of all the portal blocks
        Stack<BlockPos> s = new Stack<>();
        Set<BlockPos> visited = new HashSet<>();
        s.push(startPos);
        while (!s.isEmpty()) {
            BlockPos curPos = s.pop();
            if (!visited.add(curPos)) continue;
            if (visited.size() > 900) return false;
            List<BlockPos> curAdjacents = getAdjacentsOnPlane(curPos, axis);
            for (BlockPos pos : curAdjacents) {
                BlockState blockState = world.getBlockState(pos);
                if (blockState.isAir()) {
                    if (!visited.contains(pos))
                        s.push(pos);
                }
                else {
                    if (!blockState.is(frameMaterial))
                        return false;
                }
            }
            portalBlocks.add(curPos);
        }

        // rotate blockState to ensure portal blocks are facing the proper direction
        BlockState portalState = switch (axis) {
            case X -> portalBlock.defaultBlockState().rotate(Rotation.CLOCKWISE_90);
            case Y -> portalBlock.defaultBlockState().rotate(Rotation.COUNTERCLOCKWISE_90);
            case Z -> portalBlock.defaultBlockState();
        };

        for (BlockPos pos : portalBlocks) {
            world.setBlockAndUpdate(pos, portalState);
        }

        // the registry is server-authoritative; clients receive the new portal through the sync payload
        if (!world.isClientSide()) {
            SpawnPosData spawnPosData = determineSpawnPos(portalBlocks, portalBlock, axis, world);

            CustomPortal portal = new CustomPortal(frameId, world.dimension().identifier().toString(),
                    portalBlock.defaultMapColor(), spawnPosData.blockPos, portalBlocks, spawnPosData.offsetX,
                    spawnPosData.offsetZ, creatorId);
            // count any runes already on the frame before registering, so the first link attempt sees them
            recountRunes(world, portal);
            PortalData.get(world).register(portal);
            PortalData.markChanged(world);
        }
        return true;
    }

    /**
     * Whether a neighbour in the given direction of a portal block lies in the portal's plane, as a frame block
     * does. False if the state is not a {@link PortalBlock}, such as a portal block already removed from the world.
     * A portal block's {@code AXIS} is X or Z for an upright portal spanning that axis and Y for a horizontal one;
     * the excluded direction is the portal's normal.
     */
    public static boolean isInPlane(Direction direction, BlockState portalState) {
        if (!(portalState.getBlock() instanceof PortalBlock)) return false;
        Direction.Axis normal = switch (portalState.getValue(PortalBlock.AXIS)) {
            case X -> Direction.Axis.Z;
            case Y -> Direction.Axis.Y;
            case Z -> Direction.Axis.X;
        };
        return direction.getAxis() != normal;
    }

    /**
     * The registered portals that have the given block as part of their frame: those with a portal block next to it
     * in the portal's plane. Usually one portal, at most a few.
     */
    public static Set<CustomPortal> getPortalsAroundFrame(Level world, BlockPos framePos) {
        Set<CustomPortal> portals = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = framePos.relative(direction);
            CustomPortal portal = PortalData.getPortalFromPos(world, adjacent);
            if (portal != null && isInPlane(direction, world.getBlockState(adjacent)))
                portals.add(portal);
        }
        return portals;
    }

    /**
     * Recounts the portal's runes from the world and replaces its counts. The frame blocks are the non-air blocks
     * next to the portal's blocks in its plane; a rune counts once if it is mounted on one of them. Does not relink
     * or sync.
     */
    public static void recountRunes(Level world, CustomPortal portal) {
        Set<BlockPos> portalBlocks = new HashSet<>(portal.getPortalBlocks());
        Set<BlockPos> frames = new HashSet<>();
        for (BlockPos pos : portalBlocks) {
            BlockState portalState = world.getBlockState(pos);
            for (Direction direction : Direction.values()) {
                BlockPos adjacent = pos.relative(direction);
                if (isInPlane(direction, portalState) && !portalBlocks.contains(adjacent) && !world.getBlockState(adjacent).isAir())
                    frames.add(adjacent);
            }
        }

        Set<BlockPos> runes = new HashSet<>();
        int haste = 0, gate = 0, weak = 0, strong = 0, infinity = 0;
        for (BlockPos framePos : frames) {
            for (BlockPos runePos : getAdjacents(framePos)) {
                BlockState runeState = world.getBlockState(runePos);
                if (!(runeState.getBlock() instanceof AbstractRuneBlock rune)) continue;
                if (!rune.getBlockMountedPos(runePos, runeState).equals(framePos) || !runes.add(runePos)) continue;
                if (rune instanceof HasteRuneBlock) haste++;
                else if (rune instanceof GateRuneBlock) gate++;
                else if (rune instanceof EnhancerRuneBlock) weak++;
                else if (rune instanceof StrongEnhancerRuneBlock) strong++;
                else if (rune instanceof InfinityRuneBlock) infinity++;
            }
        }
        portal.setRuneCounts(haste, gate, weak, strong, infinity);
    }

    /** Whether any of the portal's blocks receives a redstone signal from a neighbour. */
    public static boolean checkRedstoneSignal(Level world, CustomPortal portal) {
        for (BlockPos pos : portal.getPortalBlocks()) {
            if (world.hasNeighborSignal(pos))
                return true;
        }
        return false;
    }

    /** The portal block of the colour with the given map colour id, or the nether portal for an unknown id. */
    public static Block getPortalBlockFromColorId(int colorId) {
        return PortalColor.byMapColorId(colorId).<Block>map(BlocksPMX::portal).orElse(Blocks.NETHER_PORTAL);
    }

    static class SpawnPosData {
        protected BlockPos blockPos;
        protected float offsetX;
        protected float offsetZ;

        protected SpawnPosData(BlockPos blockPos, float offsetX, float offsetZ) {
            this.blockPos = blockPos;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
        }
    }
}
