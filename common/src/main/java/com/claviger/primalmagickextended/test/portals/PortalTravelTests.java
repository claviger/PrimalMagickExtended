package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests travel through a linked portal. Each test uses its own frame block, so its portals never link to those of
 * other tests running in the same world.
 */
public class PortalTravelTests extends AbstractBaseTest {
    /** Origins of two frames 4 blocks apart in Z. */
    private static final BlockPos FIRST_ORIGIN = new BlockPos(1, 1, 1);
    private static final BlockPos SECOND_ORIGIN = new BlockPos(1, 1, 5);

    private static boolean isLit(GameTestHelper helper, BlockPos interiorRel) {
        for (BlockPos pos : PortalTestUtils.interior(interiorRel)) {
            BlockState state = helper.getBlockState(pos);
            if (!(state.getBlock() instanceof PortalBlock) || !state.getValue(PortalBlock.LIT)) {
                return false;
            }
        }
        return true;
    }

    /**
     * A non-player entity standing in a lit portal is sent to the linked portal's arrival point. Non-players have
     * no transition delay, so this exercises the portal block's destination and the entity mixin without a player.
     */
    public static void entity_teleports_through_linked_portal(GameTestHelper helper) {
        Item catalyst = ItemsPMX.catalyst(PortalColor.CYAN);
        ServerPlayer player = makeMockServerPlayer(helper);
        BlockPos firstInterior = PortalTestUtils.buildFrame(helper, Blocks.DEEPSLATE_BRICKS, FIRST_ORIGIN);
        BlockPos secondInterior = PortalTestUtils.buildFrame(helper, Blocks.DEEPSLATE_BRICKS, SECOND_ORIGIN);
        assertValueEqual(helper, InteractionResult.SUCCESS, PortalTestUtils.lightPortal(helper, player, catalyst, firstInterior), "first catalyst result");
        assertValueEqual(helper, InteractionResult.SUCCESS, PortalTestUtils.lightPortal(helper, player, catalyst, secondInterior), "second catalyst result");
        CustomPortal second = PortalTestUtils.portalAt(helper, secondInterior);
        assertTrue(helper, second != null, "Expected a registered portal in the second frame");

        // Spawn the pig only once both portals are lit, so it cannot stand in an unlit portal first
        BlockPos dest = second.getSpawnPos();
        Vec3 expected = new Vec3(dest.getX() + second.offsetX, dest.getY(), dest.getZ() + second.offsetZ);
        AtomicReference<Entity> pig = new AtomicReference<>();
        helper.startSequence()
                .thenWaitUntil(() -> assertTrue(helper, isLit(helper, firstInterior) && isLit(helper, secondInterior), "Both portals should be lit"))
                .thenExecute(() -> pig.set(helper.spawnWithNoFreeWill(EntityType.PIG, firstInterior)))
                .thenWaitUntil(() -> assertTrue(helper, pig.get().position().distanceTo(expected) <= 1.5D,
                        "Pig at " + pig.get().position() + " should be within 1.5 blocks of " + expected))
                .thenSucceed();
    }
}
