package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import com.claviger.primalmagickextended.test.TestUtilsPMX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests building portals with catalysts in the world, linking and lighting them, collapsing them by breaking the
 * frame, attaching runes and setting the arrival point. The portal registry is shared by every test in a run, so
 * each test uses its own frame block, and portals from different tests never link.
 */
public class PortalWorldTests extends AbstractBaseTestPMX {
    private static final String OVERWORLD = "minecraft:overworld";
    /** Origin of a single frame, centred in the 7x7 floor, one block above the floor. */
    private static final BlockPos SINGLE_ORIGIN = TestUtilsPMX.TEMPLATE_ORIGIN.offset(1, 1, 3);
    /** Origins of two frames 4 blocks apart in Z. */
    private static final BlockPos FIRST_ORIGIN = TestUtilsPMX.TEMPLATE_ORIGIN.offset(1, 1, 1);
    private static final BlockPos SECOND_ORIGIN = TestUtilsPMX.TEMPLATE_ORIGIN.offset(1, 1, 5);

    private static void assertPortalBlocks(GameTestHelper helper, BlockPos interiorRel, PortalColor color, String label) {
        for (BlockPos pos : PortalTestUtils.interior(interiorRel)) {
            assertTrue(helper, helper.getBlockState(pos).is(BlocksPMX.portal(color)), label + ": expected a " + color.getSerializedName() + " portal block at " + pos);
        }
    }

    private static void assertNoPortalBlocks(GameTestHelper helper, BlockPos interiorRel, String label) {
        for (BlockPos pos : PortalTestUtils.interior(interiorRel)) {
            assertTrue(helper, helper.getBlockState(pos).isAir(), label + ": expected air at " + pos);
        }
    }

    private static void assertLit(GameTestHelper helper, BlockPos interiorRel, boolean lit, String label) {
        for (BlockPos pos : PortalTestUtils.interior(interiorRel)) {
            BlockState state = helper.getBlockState(pos);
            assertTrue(helper, state.getBlock() instanceof PortalBlock, label + ": expected a portal block at " + pos);
            assertValueEqual(helper, lit, state.getValue(PortalBlock.LIT), label + ": lit state at " + pos);
        }
    }

    private static CustomPortal requirePortal(GameTestHelper helper, BlockPos interiorRel, String label) {
        CustomPortal portal = PortalTestUtils.portalAt(helper, interiorRel);
        assertTrue(helper, portal != null, label + ": expected a registered portal at " + interiorRel);
        return portal;
    }

    private static ServerPlayer playerWith(GameTestHelper helper, Item item, int count) {
        ServerPlayer player = makeMockServerPlayer(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, count));
        return player;
    }

    private static CustomPortal buildAndLight(GameTestHelper helper, ServerPlayer player, Block frame, BlockPos origin, PortalColor color) {
        BlockPos interior = PortalTestUtils.buildFrame(helper, frame, origin);
        InteractionResult result = PortalTestUtils.lightPortal(helper, player, ItemsPMX.catalyst(color), interior);
        assertValueEqual(helper, InteractionResult.CONSUME, result, "catalyst result for the frame at " + origin);
        return requirePortal(helper, interior, "frame at " + origin);
    }

    public static void catalyst_builds_portal_in_closed_frame(GameTestHelper helper) {
        Item catalyst = ItemsPMX.catalyst(PortalColor.WHITE);
        ServerPlayer player = playerWith(helper, catalyst, 3);
        BlockPos interior = PortalTestUtils.buildFrame(helper, Blocks.STONE, SINGLE_ORIGIN);

        InteractionResult result = PortalTestUtils.lightPortal(helper, player, catalyst, interior);

        assertValueEqual(helper, InteractionResult.CONSUME, result, "catalyst result");
        // The catalyst shrinks the stack by one on success, whatever the player's game mode
        assertValueEqual(helper, 2, player.getMainHandItem().getCount(), "catalysts left in hand");
        assertPortalBlocks(helper, interior, PortalColor.WHITE, "built portal");
        CustomPortal portal = requirePortal(helper, interior, "built portal");
        assertValueEqual(helper, "minecraft:stone", portal.getFrameId(), "frame id");
        assertValueEqual(helper, OVERWORLD, portal.getDimensionId(), "dimension id");
        assertTrue(helper, portal.getColor() == PortalColor.WHITE.mapColor(), "Portal colour should be white");
        assertValueEqual(helper, 6, portal.getPortalBlocks().size(), "portal block count");
        for (BlockPos pos : PortalTestUtils.interior(interior)) {
            assertTrue(helper, portal.getPortalBlocks().contains(helper.absolutePos(pos)), "Portal should contain " + pos);
        }
        helper.succeed();
    }

    public static void catalyst_fails_on_mixed_frame(GameTestHelper helper) {
        Item catalyst = ItemsPMX.catalyst(PortalColor.WHITE);
        ServerPlayer player = playerWith(helper, catalyst, 3);
        BlockPos interior = PortalTestUtils.buildFrame(helper, Blocks.COBBLESTONE, SINGLE_ORIGIN);
        helper.setBlock(SINGLE_ORIGIN.offset(1, PortalTestUtils.FRAME_HEIGHT - 1, 0), Blocks.MOSSY_COBBLESTONE);

        InteractionResult result = PortalTestUtils.lightPortal(helper, player, catalyst, interior);

        assertValueEqual(helper, InteractionResult.PASS, result, "catalyst result");
        assertValueEqual(helper, 3, player.getMainHandItem().getCount(), "catalysts left in hand");
        assertNoPortalBlocks(helper, interior, "mixed frame");
        assertTrue(helper, PortalTestUtils.portalAt(helper, interior) == null, "No portal should be registered in a mixed frame");
        helper.succeed();
    }

    public static void catalyst_fails_on_open_frame(GameTestHelper helper) {
        Item catalyst = ItemsPMX.catalyst(PortalColor.WHITE);
        ServerPlayer player = playerWith(helper, catalyst, 3);
        BlockPos interior = PortalTestUtils.buildFrame(helper, Blocks.ANDESITE, SINGLE_ORIGIN);
        helper.setBlock(SINGLE_ORIGIN.offset(1, PortalTestUtils.FRAME_HEIGHT - 1, 0), Blocks.AIR);

        InteractionResult result = PortalTestUtils.lightPortal(helper, player, catalyst, interior);

        assertValueEqual(helper, InteractionResult.PASS, result, "catalyst result");
        assertValueEqual(helper, 3, player.getMainHandItem().getCount(), "catalysts left in hand");
        assertNoPortalBlocks(helper, interior, "open frame");
        assertTrue(helper, PortalTestUtils.portalAt(helper, interior) == null, "No portal should be registered in an open frame");
        helper.succeed();
    }

    public static void two_matching_portals_link_and_light(GameTestHelper helper) {
        ServerPlayer player = playerWith(helper, ItemsPMX.catalyst(PortalColor.ORANGE), 2);
        buildAndLight(helper, player, Blocks.DIORITE, FIRST_ORIGIN, PortalColor.ORANGE);
        buildAndLight(helper, player, Blocks.DIORITE, SECOND_ORIGIN, PortalColor.ORANGE);
        BlockPos firstInterior = FIRST_ORIGIN.offset(1, 1, 0);
        BlockPos secondInterior = SECOND_ORIGIN.offset(1, 1, 0);

        helper.succeedWhen(() -> {
            CustomPortal first = requirePortal(helper, firstInterior, "first portal");
            CustomPortal second = requirePortal(helper, secondInterior, "second portal");
            assertTrue(helper, first.getLinked() == second, "First portal should link to the second");
            assertTrue(helper, second.getLinked() == first, "Second portal should link to the first");
            assertLit(helper, firstInterior, true, "first portal");
            assertLit(helper, secondInterior, true, "second portal");
        });
    }

    public static void portals_of_different_colours_do_not_link(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal magenta = buildAndLight(helper, player, Blocks.GRANITE, FIRST_ORIGIN, PortalColor.MAGENTA);
        CustomPortal lightBlue = buildAndLight(helper, player, Blocks.GRANITE, SECOND_ORIGIN, PortalColor.LIGHT_BLUE);
        assertFalse(helper, magenta.hasLinked(), "Magenta portal should not be linked");
        assertFalse(helper, lightBlue.hasLinked(), "Light blue portal should not be linked");

        helper.runAfterDelay(20, () -> {
            assertFalse(helper, magenta.hasLinked(), "Magenta portal should still not be linked");
            assertFalse(helper, lightBlue.hasLinked(), "Light blue portal should still not be linked");
            assertLit(helper, FIRST_ORIGIN.offset(1, 1, 0), false, "magenta portal");
            assertLit(helper, SECOND_ORIGIN.offset(1, 1, 0), false, "light blue portal");
            helper.succeed();
        });
    }

    public static void portals_of_different_frames_do_not_link(GameTestHelper helper) {
        ServerPlayer player = playerWith(helper, ItemsPMX.catalyst(PortalColor.YELLOW), 2);
        CustomPortal oak = buildAndLight(helper, player, Blocks.OAK_PLANKS, FIRST_ORIGIN, PortalColor.YELLOW);
        CustomPortal spruce = buildAndLight(helper, player, Blocks.SPRUCE_PLANKS, SECOND_ORIGIN, PortalColor.YELLOW);
        assertValueEqual(helper, "minecraft:oak_planks", oak.getFrameId(), "first frame id");
        assertValueEqual(helper, "minecraft:spruce_planks", spruce.getFrameId(), "second frame id");
        assertFalse(helper, oak.hasLinked(), "Oak-framed portal should not be linked");
        assertFalse(helper, spruce.hasLinked(), "Spruce-framed portal should not be linked");

        helper.runAfterDelay(20, () -> {
            assertFalse(helper, oak.hasLinked(), "Oak-framed portal should still not be linked");
            assertFalse(helper, spruce.hasLinked(), "Spruce-framed portal should still not be linked");
            assertLit(helper, FIRST_ORIGIN.offset(1, 1, 0), false, "oak-framed portal");
            assertLit(helper, SECOND_ORIGIN.offset(1, 1, 0), false, "spruce-framed portal");
            helper.succeed();
        });
    }

    public static void breaking_frame_collapses_portal_and_drops_catalyst(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal portal = buildAndLight(helper, player, Blocks.BIRCH_PLANKS, SINGLE_ORIGIN, PortalColor.LIME);
        BlockPos interior = SINGLE_ORIGIN.offset(1, 1, 0);
        BlockPos spawnPos = portal.getSpawnPos();
        List<BlockPos> portalBlocks = new ArrayList<>(portal.getPortalBlocks());

        // Break the middle block of the frame's left side
        boolean broken = player.gameMode.destroyBlock(helper.absolutePos(SINGLE_ORIGIN.offset(0, 2, 0)));

        assertTrue(helper, broken, "Frame block should be broken");
        assertNoPortalBlocks(helper, interior, "collapsed portal");
        for (BlockPos pos : portalBlocks) {
            assertTrue(helper, PortalData.getPortalFromPos(helper.getLevel(), pos) == null, "No portal should remain registered at " + pos);
        }
        // The catalyst drops at the portal's arrival point
        PortalTestUtils.assertItemNear(helper, ItemsPMX.catalyst(PortalColor.LIME), spawnPos, 2.0);
        helper.succeed();
    }

    public static void placing_and_breaking_rune_updates_portal(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal portal = buildAndLight(helper, player, Blocks.BRICKS, SINGLE_ORIGIN, PortalColor.PINK);
        assertValueEqual(helper, 0, portal.getGateRunes(), "gate runes before placing");

        // Place a gate rune on the outward (north) face of the frame block below the interior's bottom-left block
        BlockPos frameRel = SINGLE_ORIGIN.offset(1, 0, 0);
        BlockPos runeRel = frameRel.north();
        BlockPos frameAbs = helper.absolutePos(frameRel);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemsPMX.GATE_RUNE.get()));
        Vec3 hitVec = Vec3.atCenterOf(frameAbs).relative(Direction.NORTH, 0.5D);
        UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(hitVec, Direction.NORTH, frameAbs, false));
        InteractionResult placed = ItemsPMX.GATE_RUNE.get().useOn(ctx);

        assertTrue(helper, placed.consumesAction(), "Rune placement should succeed");
        assertTrue(helper, helper.getBlockState(runeRel).is(BlocksPMX.GATE_RUNE_BLOCK.get()), "Gate rune block should be placed at " + runeRel);
        assertValueEqual(helper, 1, portal.getGateRunes(), "gate runes after placing");

        // Break it in survival, since creative breaking drops nothing
        player.setGameMode(GameType.SURVIVAL);
        boolean broken = player.gameMode.destroyBlock(helper.absolutePos(runeRel));

        assertTrue(helper, broken, "Rune block should be broken");
        assertTrue(helper, helper.getBlockState(runeRel).isAir(), "Rune block should be gone");
        assertValueEqual(helper, 0, portal.getGateRunes(), "gate runes after breaking");
        helper.assertItemEntityPresent(ItemsPMX.GATE_RUNE.get(), runeRel, 2.0);
        helper.succeed();
    }

    public static void sneak_use_sets_arrival_point(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        CustomPortal portal = buildAndLight(helper, player, Blocks.SANDSTONE, SINGLE_ORIGIN, PortalColor.GRAY);
        BlockPos targetRel = SINGLE_ORIGIN.offset(1, PortalTestUtils.FRAME_HEIGHT - 2, 0);
        BlockPos targetAbs = helper.absolutePos(targetRel);
        BlockState state = helper.getBlockState(targetRel);
        PortalBlock block = assertInstanceOf(helper, state.getBlock(), PortalBlock.class, "Target should be a portal block");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(targetAbs), Direction.NORTH, targetAbs, false);
        ItemInteractionResult result = block.useItemOn(ItemStack.EMPTY, state, helper.getLevel(), targetAbs, player, InteractionHand.MAIN_HAND, hit);

        assertValueEqual(helper, ItemInteractionResult.CONSUME, result, "sneak-use result");
        assertValueEqual(helper, targetAbs, portal.getSpawnPos(), "arrival point");
        assertValueEqual(helper, 0.5F, portal.getOffsetX(), "arrival X offset");
        assertValueEqual(helper, 0.5F, portal.getOffsetZ(), "arrival Z offset");
        helper.succeed();
    }
}
