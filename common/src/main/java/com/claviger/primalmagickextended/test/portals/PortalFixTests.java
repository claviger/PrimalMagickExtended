package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.AbstractRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.common.portals.PortalRegistry;
import com.claviger.primalmagickextended.common.portals.PortalSavedData;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Regression tests for the defects fixed after the port. The portal registry is shared by every test in a run, so
 * each test uses its own frame block, and portals from different tests never link.
 */
public class PortalFixTests extends AbstractBaseTest {
    /** Origin of a single frame, centred in the 7x7 floor. */
    private static final BlockPos SINGLE_ORIGIN = new BlockPos(1, 1, 3);
    /** The frame block below the interior's bottom-left block. */
    private static final BlockPos FRAME_REL = SINGLE_ORIGIN.offset(1, 0, 0);
    /** The space on the outward (north) face of that frame block, where the tests put their rune. */
    private static final BlockPos RUNE_REL = FRAME_REL.north();
    /** Origins of two frames 4 blocks apart in Z. */
    private static final BlockPos FIRST_ORIGIN = new BlockPos(1, 1, 1);
    private static final BlockPos SECOND_ORIGIN = new BlockPos(1, 1, 5);

    private static CustomPortal buildAndLight(GameTestHelper helper, ServerPlayer player, Block frame, PortalColor color) {
        return buildAndLight(helper, player, frame, SINGLE_ORIGIN, color);
    }

    private static CustomPortal buildAndLight(GameTestHelper helper, ServerPlayer player, Block frame, BlockPos origin, PortalColor color) {
        BlockPos interior = PortalTestUtils.buildFrame(helper, frame, origin);
        InteractionResult result = PortalTestUtils.lightPortal(helper, player, ItemsPMX.catalyst(color), interior);
        assertValueEqual(helper, InteractionResult.SUCCESS, result, "catalyst result");
        CustomPortal portal = PortalTestUtils.portalAt(helper, interior);
        assertTrue(helper, portal != null, "Expected a registered portal at " + interior);
        return portal;
    }

    /** F1: a rune removed by anything other than a player must still leave the portal's counts. */
    public static void rune_removed_without_player_updates_count(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal portal = buildAndLight(helper, player, Blocks.MUD_BRICKS, PortalColor.PURPLE);

        // Place a weak enhancer rune on the outward (north) face of the frame block, as a player would
        BlockPos frameAbs = helper.absolutePos(FRAME_REL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemsPMX.WEAK_ENHANCER_RUNE.get()));
        Vec3 hitVec = Vec3.atCenterOf(frameAbs).relative(Direction.NORTH, 0.5D);
        UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(hitVec, Direction.NORTH, frameAbs, false));
        InteractionResult placed = ItemsPMX.WEAK_ENHANCER_RUNE.get().useOn(ctx);
        assertTrue(helper, placed.consumesAction(), "Rune placement should succeed");
        assertTrue(helper, helper.getBlockState(RUNE_REL).is(BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get()), "Weak enhancer rune should be placed at " + RUNE_REL);
        assertValueEqual(helper, 1, portal.getWeakEnhancerRunes(), "weak enhancer runes after placing");

        // Remove it with no player involved
        helper.setBlock(RUNE_REL, Blocks.AIR);

        helper.succeedWhen(() -> assertValueEqual(helper, 0, portal.getWeakEnhancerRunes(), "weak enhancer runes after removal"));
    }

    /** F1: a rune put in place without a player, as by a command, must still count for the portal. */
    public static void rune_set_by_command_is_counted(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal portal = buildAndLight(helper, player, Blocks.POLISHED_BLACKSTONE_BRICKS, PortalColor.BROWN);
        assertValueEqual(helper, 0, portal.getGateRunes(), "gate runes before placing");

        // A wall rune facing north is mounted on the block to its south, the frame block
        BlockState rune = BlocksPMX.GATE_RUNE_BLOCK.get().defaultBlockState()
                .setValue(AbstractRuneBlock.FACE, AttachFace.WALL)
                .setValue(AbstractRuneBlock.FACING, Direction.NORTH);
        helper.setBlock(RUNE_REL, rune);
        assertTrue(helper, helper.getBlockState(RUNE_REL).is(BlocksPMX.GATE_RUNE_BLOCK.get()), "Gate rune should be set at " + RUNE_REL);

        helper.succeedWhen(() -> assertValueEqual(helper, 1, portal.getGateRunes(), "gate runes after setting"));
    }

    /**
     * F1: a recount that changes a link-affecting rune count must relink the portal. A second portal of the same
     * colour and frame is registered directly, 400 blocks above the built one (out of default range, within enhancer
     * range, and above build height so it can never shadow a real block). Setting a weak enhancer rune links the two;
     * removing it unlinks them. The far portal is unregistered at the end.
     */
    public static void rune_recount_relinks_portal(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal near = buildAndLight(helper, player, Blocks.QUARTZ_BRICKS, PortalColor.LIGHT_GRAY);
        BlockPos farSpawn = near.getSpawnPos().above(400);
        CustomPortal far = new CustomPortal(near.getFrameId(), near.getDimensionId(), near.getColor(), farSpawn,
                new ArrayList<>(List.of(farSpawn)), 0.5F, 0.5F, near.getCreatorId());
        PortalRegistry registry = PortalData.get(helper.getLevel());
        registry.register(far);
        assertFalse(helper, near.hasLinked() || far.hasLinked(), "Portals 400 blocks apart should not link without a rune");

        BlockState rune = BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get().defaultBlockState()
                .setValue(AbstractRuneBlock.FACE, AttachFace.WALL)
                .setValue(AbstractRuneBlock.FACING, Direction.NORTH);

        helper.startSequence()
                .thenExecute(() -> helper.setBlock(RUNE_REL, rune))
                .thenWaitUntil(() -> {
                    assertValueEqual(helper, 1, near.getWeakEnhancerRunes(), "weak enhancer runes after setting");
                    assertTrue(helper, near.getLinked() == far && far.getLinked() == near, "Setting a weak enhancer rune should link the portals");
                })
                .thenExecute(() -> helper.setBlock(RUNE_REL, Blocks.AIR))
                .thenWaitUntil(() -> {
                    assertValueEqual(helper, 0, near.getWeakEnhancerRunes(), "weak enhancer runes after removal");
                    assertFalse(helper, near.hasLinked() || far.hasLinked(), "Removing the weak enhancer rune should unlink the portals");
                })
                .thenExecute(() -> {
                    registry.unregister(far);
                    PortalData.markChanged(helper.getLevel());
                })
                .thenSucceed();
    }

    /** True if every interior block of the frame is a portal block in the given lit state. */
    private static boolean allLit(GameTestHelper helper, BlockPos interiorRel, boolean lit) {
        for (BlockPos pos : PortalTestUtils.interior(interiorRel)) {
            BlockState state = helper.getBlockState(pos);
            if (!(state.getBlock() instanceof PortalBlock) || state.getValue(PortalBlock.LIT) != lit) {
                return false;
            }
        }
        return true;
    }

    /**
     * Simulates a server restart for two portals: round-trips the server's saved portal data through its
     * persistence codec, then swaps the two live portal objects for the freshly loaded ones, whose runtime-only
     * state is reset as after a real load. Only these two portals are swapped, so portals of concurrently running
     * tests keep their objects. Their frame and colour are unique to the calling test, so they link only with each
     * other.
     *
     * @return the reloaded first portal
     */
    private static CustomPortal reload(GameTestHelper helper, CustomPortal first, CustomPortal second) {
        PortalSavedData saved = PortalSavedData.get(helper.getLevel().getServer());
        Tag tag = PortalSavedData.CODEC.encodeStart(NbtOps.INSTANCE, saved).getOrThrow();
        PortalRegistry loaded = PortalSavedData.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow().getRegistry();
        CustomPortal loadedFirst = loaded.getPortalFromPos(first.getDimensionId(), first.getPortalBlocks().getFirst());
        CustomPortal loadedSecond = loaded.getPortalFromPos(second.getDimensionId(), second.getPortalBlocks().getFirst());
        assertTrue(helper, loadedFirst != null && loadedSecond != null, "Both portals should survive the save round trip");

        PortalRegistry live = saved.getRegistry();
        live.unregister(first);
        live.unregister(second);
        live.register(loadedFirst);
        live.register(loadedSecond);
        return loadedFirst;
    }

    /** F2: a portal kept dark by redstone under the default OFF mode must stay dark after a reload. */
    public static void powered_portal_stays_dark_after_reload(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal first = buildAndLight(helper, player, Blocks.TUFF_BRICKS, FIRST_ORIGIN, PortalColor.BLUE);
        CustomPortal second = buildAndLight(helper, player, Blocks.TUFF_BRICKS, SECOND_ORIGIN, PortalColor.BLUE);
        BlockPos firstInterior = FIRST_ORIGIN.offset(1, 1, 0);
        BlockPos secondInterior = SECOND_ORIGIN.offset(1, 1, 0);

        // Power the first portal from in front of (north of) its bottom-left portal block
        helper.setBlock(firstInterior.north(), Blocks.REDSTONE_BLOCK);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    assertTrue(helper, first.getLinked() == second, "First portal should link to the second");
                    assertTrue(helper, first.hasRedstoneSignal(), "First portal should be powered");
                    assertTrue(helper, allLit(helper, firstInterior, false), "Powered portal should be dark");
                    assertTrue(helper, allLit(helper, secondInterior, true), "Unpowered portal should be lit");
                })
                .thenExecute(() -> {
                    CustomPortal reloaded = reload(helper, first, second);
                    assertTrue(helper, reloaded != first, "Reloaded portal should be a new object");
                    assertTrue(helper, reloaded.hasLinked(), "Reloaded portal should be linked");
                    assertFalse(helper, reloaded.hasRedstoneSignal(), "Reloaded portal should start without a signal, as after a load");
                })
                .thenExecuteFor(20, () -> assertTrue(helper, allLit(helper, firstInterior, false), "Powered portal should stay dark after a reload"))
                .thenSucceed();
    }
}
