package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.IPortalTraveler;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import com.claviger.primalmagickextended.test.TestUtilsPMX;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests travel through a linked portal. Each test uses its own frame block, so its portals never link to those of
 * other tests running in the same world.
 */
public class PortalTravelTests extends AbstractBaseTestPMX {
    /** Origins of two frames 4 blocks apart in Z, one block above the floor. */
    private static final BlockPos FIRST_ORIGIN = TestUtilsPMX.TEMPLATE_ORIGIN.offset(1, 1, 1);
    private static final BlockPos SECOND_ORIGIN = TestUtilsPMX.TEMPLATE_ORIGIN.offset(1, 1, 5);

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
        assertValueEqual(helper, InteractionResult.CONSUME, PortalTestUtils.lightPortal(helper, player, catalyst, firstInterior), "first catalyst result");
        assertValueEqual(helper, InteractionResult.CONSUME, PortalTestUtils.lightPortal(helper, player, catalyst, secondInterior), "second catalyst result");
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

    /**
     * A player in a custom portal travels through the dimension-change flow even within one dimension, where vanilla
     * only moves the player; a player in no custom portal still takes the vanilla path. The flow marks the player as
     * changing dimension until the client confirms the teleport, which a mock player never does. The portal is not
     * registered or built, so it cannot link to any other test's portals.
     */
    public static void player_custom_portal_travel_uses_dimension_change_flow(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper, true);
        try {
            BlockPos start = helper.absolutePos(FIRST_ORIGIN.offset(1, 1, 0));
            player.moveTo(Vec3.atBottomCenterOf(start));

            // Control: no custom portal, so the vanilla same-dimension path only moves the player
            Vec3 firstDest = Vec3.atBottomCenterOf(helper.absolutePos(SECOND_ORIGIN.offset(1, 1, 0)));
            Entity firstResult = player.changeDimension(new DimensionTransition(helper.getLevel(), firstDest, Vec3.ZERO, 0.0F, 0.0F, DimensionTransition.DO_NOTHING));
            assertTrue(helper, firstResult == player, "Vanilla teleport should return the same player");
            assertFalse(helper, player.isChangingDimension(), "Vanilla same-dimension teleport should not change dimension");

            // In a custom portal: the dimension-change flow
            player.moveTo(Vec3.atBottomCenterOf(start));
            CustomPortal portal = new CustomPortal("minecraft:deepslate_tiles", "minecraft:overworld", PortalColor.CYAN.mapColor(),
                    start, new ArrayList<>(List.of(start)), 0.5F, 0.5F, UUID.randomUUID());
            player.setAsInsidePortal(BlocksPMX.portal(PortalColor.CYAN), start);
            ((IPortalTraveler) player).primalmagickextended$setInCustomPortal(portal);
            assertTrue(helper, ((IPortalTraveler) player).primalmagickextended$isInCustomPortal(), "Player should be in the custom portal");
            Vec3 secondDest = Vec3.atBottomCenterOf(helper.absolutePos(SECOND_ORIGIN.offset(2, 1, 0)));
            Entity secondResult = player.changeDimension(new DimensionTransition(helper.getLevel(), secondDest, Vec3.ZERO, 0.0F, 0.0F, DimensionTransition.DO_NOTHING));
            assertTrue(helper, secondResult == player, "Custom portal teleport should return the same player");
            assertTrue(helper, player.isChangingDimension(), "Custom portal teleport should use the dimension-change flow");
            assertTrue(helper, player.position().distanceTo(secondDest) < 0.01D, "Player at " + player.position() + " should be at " + secondDest);
            assertTrue(helper, player.serverLevel() == helper.getLevel(), "Player should stay in the test level");
            assertTrue(helper, helper.getLevel().getPlayers(p -> p == player).size() == 1, "Player should be back in the level's player list");
            assertTrue(helper, helper.getLevel().getEntity(player.getUUID()) == player, "Player should be back in the level's entity lookup");
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(player);
        }
        helper.succeed();
    }
}
