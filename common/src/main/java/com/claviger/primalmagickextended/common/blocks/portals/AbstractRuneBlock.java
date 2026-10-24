package com.claviger.primalmagickextended.common.blocks.portals;

import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.common.portals.PortalHelper;
import com.claviger.primalmagickextended.common.portals.PortalRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Base class for the portal runes: thin blocks attached to a face of a portal frame block. A rune counts for the
 * portal whose frame it is attached to. Whenever a rune is added or removed, by any means, the affected portals'
 * rune counts are recounted from the world, and runes already on a frame are counted when the portal is built. The
 * rune type is identified by the subclass. Ported from Custom Portals.
 */
public class AbstractRuneBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final MapCodec<AbstractRuneBlock> CODEC = simpleCodec(AbstractRuneBlock::new);
    protected static final VoxelShape CEILING_SHAPE;
    protected static final VoxelShape FLOOR_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;

    @Override
    public MapCodec<? extends AbstractRuneBlock> codec() {
        return CODEC;
    }

    protected AbstractRuneBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.relative(getConnectedDirection(state).getOpposite());
        return world.getBlockState(blockPos).isCollisionShapeFullBlock(world, blockPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (state.getValue(FACE)) {
            case FLOOR:
                return FLOOR_SHAPE;
            case WALL:
                switch (direction) {
                    case EAST:
                        return EAST_SHAPE;
                    case WEST:
                        return WEST_SHAPE;
                    case SOUTH:
                        return SOUTH_SHAPE;
                    case NORTH:
                    default:
                        return NORTH_SHAPE;
                }
            case CEILING:
            default:
                return CEILING_SHAPE;
        }
    }

    /**
     * Recounts the runes of the portal(s) this rune is mounted on, whenever a rune is set by any means: by a player,
     * a command or a structure. A rune replacing another rune in a different orientation also recounts the portals
     * the old one was mounted on.
     */
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide()) return;
        Set<CustomPortal> portals = PortalHelper.getPortalsAroundFrame(level, getBlockMountedPos(pos, state));
        if (oldState != state && oldState.getBlock() instanceof AbstractRuneBlock oldRune) {
            portals.addAll(PortalHelper.getPortalsAroundFrame(level, oldRune.getBlockMountedPos(pos, oldState)));
        }
        updatePortals(level, portals);
    }

    /**
     * Recounts the runes of the portal(s) this rune was mounted on, whenever a rune is removed by any means: by a
     * player, a piston, an explosion, a command or losing its support. Runs on the server only, after the rune is
     * gone from the world. A rune replaced by the same rune in another orientation is left to {@link #onPlace}.
     */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level.isClientSide() || state.is(newState.getBlock())) return;
        updatePortals(level, PortalHelper.getPortalsAroundFrame(level, getBlockMountedPos(pos, state)));
    }

    /**
     * Recounts every given portal's runes from the world, then relinks each portal whose count of a link-affecting
     * rune (gate or enhancer) changed, its former partner first. All counts are updated before any relinking, so no
     * link decision sees another affected portal's stale counts. Syncs the registry once if any count changed.
     */
    private static void updatePortals(Level level, Set<CustomPortal> portals) {
        if (portals.isEmpty()) return;
        List<CustomPortal> toRelink = new ArrayList<>();
        boolean changed = false;
        for (CustomPortal portal : portals) {
            int haste = portal.getHasteRunes();
            int gate = portal.getGateRunes();
            int weak = portal.getWeakEnhancerRunes();
            int strong = portal.getStrongEnhancerRunes();
            int infinity = portal.getInfinityRunes();
            PortalHelper.recountRunes(level, portal);
            boolean linkChanged = gate != portal.getGateRunes() || weak != portal.getWeakEnhancerRunes()
                    || strong != portal.getStrongEnhancerRunes() || infinity != portal.getInfinityRunes();
            if (linkChanged)
                toRelink.add(portal);
            changed |= linkChanged || haste != portal.getHasteRunes();
        }
        PortalRegistry registry = PortalData.get(level);
        for (CustomPortal portal : toRelink) {
            if (portal.hasLinked())
                registry.tryWithAll(portal.getLinked());
            registry.tryWithAll(portal);
        }
        if (changed)
            PortalData.markChanged(level);
    }

    /** The position of the block this rune is attached to. */
    public BlockPos getBlockMountedPos(BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        AttachFace orientation = state.getValue(FACE);
        switch (orientation) {
            case FLOOR:
                return pos.below();
            case WALL:
                switch (direction) {
                    case EAST: return pos.west();
                    case WEST: return pos.east();
                    case SOUTH: return pos.north();
                    default:
                    case NORTH: return pos.south();
                }
            default:
            case CEILING: return pos.above();
        }
    }

    static {
        CEILING_SHAPE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        FLOOR_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
        NORTH_SHAPE = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
        SOUTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
        WEST_SHAPE = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        EAST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    }
}
