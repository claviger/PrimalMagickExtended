package com.claviger.primalmagickextended.common.blocks.portals;

import com.claviger.primalmagickextended.client.fx.particles.ParticleTypesPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.common.portals.PortalHasteMode;
import com.claviger.primalmagickextended.common.portals.PortalHelper;
import com.claviger.primalmagickextended.common.tiles.BlockEntityTypesPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A coloured portal block. Portals are filled with these by a portal catalyst; the portal they belong to is looked
 * up in the portal registry by position. A lit portal block sends entities standing in it to the linked portal.
 * Removing a frame block collapses the portal and drops a catalyst of its colour; sneak-using a portal block with
 * an empty hand sets the portal's arrival point. Ported from Custom Portals.
 */
public class PortalBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, Portal {
    public static final BooleanProperty LIT;
    public static final BooleanProperty WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS;
    protected static final VoxelShape X_SHAPE;
    protected static final VoxelShape Z_SHAPE;
    protected static final VoxelShape Y_SHAPE;

    public PortalBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(LIT, false).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(AXIS)) {
            case Z:
                return Z_SHAPE;
            case Y:
                return Y_SHAPE;
            case X:
            default:
                return X_SHAPE;
        }
    }

    /**
     * Sneak-using a portal block with an empty hand sets the portal's arrival point to that block. The server
     * updates the portal; the client shows the confirmation message.
     */
    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level world, BlockPos blockPos, Player playerEntity, InteractionHand hand, BlockHitResult blockHitResult) {
        if (playerEntity.isShiftKeyDown() && itemStack.isEmpty()) {
            CustomPortal portal = PortalData.getPortalFromPos(world, blockPos);
            if (portal == null) return InteractionResult.FAIL;
            if (world.isClientSide()) {
                playerEntity.sendSystemMessage(Component.translatable("message.primalmagickextended.portal_spawn_set", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            } else {
                portal.setSpawnPos(blockPos);
                PortalData.markChanged(world);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    /** A portal linked to the Nether or the End occasionally spawns that dimension's mob, at nether-portal odds. */
    @Override
    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        CustomPortal portal = PortalData.getPortalFromPos(serverLevel, pos);
        if (portal == null)
            return;
        if (portal.isInterdimensional()) {
            if (portal.getLinked().getDimensionId().equals("minecraft:the_nether") && serverLevel.isSpawningMonsters() && random.nextInt(2000) < serverLevel.getDifficulty().getId()) {
                while (serverLevel.getBlockState(pos).is(this)) {
                    pos = pos.below();
                }

                if (serverLevel.getBlockState(pos).isValidSpawn(serverLevel, pos, EntityType.ZOMBIFIED_PIGLIN)) {
                    Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(serverLevel, pos.above(), EntitySpawnReason.STRUCTURE);
                    if (entity != null) {
                        entity.setPortalCooldown();
                    }
                }
            }
            if (portal.getLinked().getDimensionId().equals("minecraft:the_end") && serverLevel.isSpawningMonsters() && random.nextInt(2000) < serverLevel.getDifficulty().getId()) {
                while (serverLevel.getBlockState(pos).is(this)) {
                    pos = pos.below();
                }

                if (serverLevel.getBlockState(pos).isValidSpawn(serverLevel, pos, EntityType.ENDERMAN)) {
                    Entity entity = EntityType.ENDERMAN.spawn(serverLevel, pos.above(), EntitySpawnReason.STRUCTURE);
                    if (entity != null) {
                        entity.setPortalCooldown();
                    }
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(world, pos, state, player);
        if (world.isClientSide())
            return state;
        CustomPortal portal = PortalData.getPortalFromPos(world, pos);
        if (portal != null) {
            PortalData.get(world).unregister(portal);
            PortalData.markChanged(world);
        }
        return state;
    }

    /** Drops one catalyst of this block's colour at the portal's arrival point; unknown colours drop yellow. */
    private void dropCatalyst(CustomPortal portal, Level world) {
        Item catalyst = PortalColor.byMapColorId(this.defaultMapColor().id).map(ItemsPMX::catalyst).orElse(ItemsPMX.catalyst(PortalColor.YELLOW));
        ItemStack itemStack = new ItemStack(catalyst);
        Block.popResource(world, portal.getSpawnPos(), itemStack);
    }

    /**
     * Collapses the portal when a block in its plane is replaced by anything other than a portal block or the
     * portal's own frame material, unregistering it and dropping a catalyst.
     */
    @Override
    public BlockState updateShape(BlockState state, LevelReader worldView, ScheduledTickAccess scheduledTickView, BlockPos pos, Direction direction, BlockPos posFrom, BlockState newState, RandomSource random) {
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = state.getValue(AXIS);
        Level world = (Level) worldView;
        boolean bl = axis2 == Direction.Axis.Y ? axis2 == axis && axis.isVertical() : axis2 != axis && axis.isHorizontal();
        if (!bl && !newState.is(this) && !PortalShape.findAnyShape(worldView, pos, axis2).isComplete()) {
            CustomPortal portal = PortalData.getPortalFromPos(world, pos);
            if (portal != null) {
                if (BuiltInRegistries.BLOCK.getKey(newState.getBlock()).toString().equals(portal.getFrameId()))
                    return super.updateShape(state, worldView, scheduledTickView, pos, direction, posFrom, newState, random);
                if (!world.isClientSide()) {
                    PortalData.get(world).unregister(portal);
                    PortalData.markChanged(world);
                    dropCatalyst(portal, world);
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, worldView, scheduledTickView, pos, direction, posFrom, newState, random);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level world, BlockPos blockPos, Block block, @Nullable Orientation wireOrientation, boolean bl) {
        if (world.isClientSide())
            return;
        CustomPortal portal = PortalData.getPortalFromPos(world, blockPos);
        if (portal == null)
            return;
        // Always set, so the portal is marked as checked even when the signal is unchanged
        portal.setHasRedstoneSignal(PortalHelper.checkRedstoneSignal(world, portal));
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier entityCollisionHandler, boolean bl) {
        if (!state.getValue(LIT))
            return;
        CustomPortal portal = PortalData.getPortalFromPos(world, pos);
        if (portal != null && entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
            ((IPortalTraveler) entity).primalmagickextended$setInCustomPortal(portal);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT))
            return;
        if (!ServicesPMX.CONFIG.portalsMuteAmbientSounds() && random.nextInt(100) == 0) {
            world.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }
        ParticleOptions particle = particleForColor(this.defaultMapColor().id);
        for (int i = 0; i < 4; ++i) {
            double d = (double) pos.getX() + random.nextDouble();
            double e = (double) pos.getY() + random.nextDouble();
            double f = (double) pos.getZ() + random.nextDouble();
            double g = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double h = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double j = ((double) random.nextFloat() - 0.5D) * 0.5D;
            int k = random.nextInt(2) * 2 - 1;
            if (!world.getBlockState(pos.west()).is(this) && !world.getBlockState(pos.east()).is(this) && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Z) {
                d = (double) pos.getX() + 0.5D + 0.25D * (double) k;
                g = (double) (random.nextFloat() * 2.0F * (float) k);
            } else if (!world.getBlockState(pos.above()).is(this) && !world.getBlockState(pos.below()).is(this)) {
                e = (double) pos.getY() + 0.5D + 0.25D * (double) k;
                h = (double) (random.nextFloat() * 2.0F * (float) k);
            } else {
                f = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
                j = (double) (random.nextFloat() * 2.0F * (float) k);
            }
            world.addParticle(particle, d, e, f, g, h, j);
        }
    }

    /** Purple portals use the vanilla portal particle; unknown colours use yellow's. */
    private static ParticleOptions particleForColor(int mapColorId) {
        return ParticleTypesPMX.particle(PortalColor.byMapColorId(mapColorId).orElse(PortalColor.YELLOW));
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity livingEntity, BlockGetter blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return false;
    }

    @Override
    protected boolean canBeReplaced(BlockState blockState, Fluid fluid) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            // use COUNTERCLOCKWISE_90 to rotate around Y axis
            case COUNTERCLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case Z:
                    case X:
                        return state.setValue(AXIS, Direction.Axis.Y);
                    case Y:
                        return state.setValue(AXIS, Direction.Axis.X);
                    default:
                        return state;
                }
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case Z:
                        return state.setValue(AXIS, Direction.Axis.X);
                    case X:
                    case Y:
                        return state.setValue(AXIS, Direction.Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, LIT, WATERLOGGED);
    }

    static {
        LIT = BlockStateProperties.LIT;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        AXIS = BlockStateProperties.AXIS;
        X_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
        Z_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
        Y_SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalBlockEntity(pos, state);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntityTypesPMX.PORTAL.get() ? (BlockEntityTicker<T>) (BlockEntityTicker<PortalBlockEntity>) PortalBlockEntity::tick : null;
    }

    @Nullable
    @Override
    public TeleportTransition getPortalDestination(ServerLevel serverWorld, Entity entity, BlockPos blockPos) {
        CustomPortal portal = PortalData.getPortalFromPos(serverWorld, blockPos);
        if (portal == null) return null;
        CustomPortal destPortal = portal.getLinked();
        if (destPortal == null) return null;
        MinecraftServer minecraftServer = serverWorld.getServer();
        String dimensionId = portal.getDimensionId();
        String destDimensionId = destPortal.getDimensionId();
        ServerLevel serverWorld2 = null;
        if (!destDimensionId.equals(dimensionId)) {
            for (ResourceKey<Level> registryKey : minecraftServer.levelKeys()) {
                if (registryKey.identifier().toString().equals(destDimensionId)) {
                    serverWorld2 = minecraftServer.getLevel(registryKey);
                }
            }
        } else serverWorld2 = serverWorld;
        if (serverWorld2 == null) {
            return null;
        } else {
            BlockPos dest = destPortal.getSpawnPos();
            float destX = (float) dest.getX();
            float destY = (float) dest.getY();
            float destZ = (float) dest.getZ();
            destX += destPortal.offsetX;
            destZ += destPortal.offsetZ;
            /* When the player is going from the Overworld to the End, the Y coordinate gets decreased by 1
             * for an unknown reason. This corrects it. */
            if (destPortal.getDimensionId().equals("minecraft:the_end") && serverWorld2.dimension() == Level.OVERWORLD)
                destY += 1.0f;
            return new TeleportTransition(serverWorld2, new Vec3(destX, destY, destZ), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel serverLevel, Entity entity) {
        CustomPortal destPortal = ((IPortalTraveler) entity).primalmagickextended$getDestPortal();
        if (entity instanceof Player playerEntity && destPortal != null) {
            if (ServicesPMX.CONFIG.portalsAlwaysHaste() == PortalHasteMode.CREATIVE) {
                return Math.max(1, playerEntity.getAbilities().invulnerable ? serverLevel.getGameRules().get(GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY) : destPortal.getPlayerTeleportDelay());
            } else return destPortal.getPlayerTeleportDelay();
        }
        return 0;
    }
}
