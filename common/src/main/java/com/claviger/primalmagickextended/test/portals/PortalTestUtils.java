package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Helpers shared by the in-world portal tests. Frames are 4 wide by 5 tall in the X-Y plane, enclosing a 2 by 3
 * interior.
 */
public class PortalTestUtils {
    /** Width of a frame built by {@link #buildFrame}, in blocks. */
    public static final int FRAME_WIDTH = 4;
    /** Height of a frame built by {@link #buildFrame}, in blocks. */
    public static final int FRAME_HEIGHT = 5;

    /**
     * Builds a closed rectangular frame of the given block in the X-Y plane at the origin's Z, with the origin as
     * its bottom-left corner, and clears its interior.
     *
     * @return the relative position of the interior's bottom-left block
     */
    public static BlockPos buildFrame(GameTestHelper helper, Block frame, BlockPos originRel) {
        for (int x = 0; x < FRAME_WIDTH; x++) {
            for (int y = 0; y < FRAME_HEIGHT; y++) {
                boolean edge = x == 0 || x == FRAME_WIDTH - 1 || y == 0 || y == FRAME_HEIGHT - 1;
                BlockPos pos = originRel.offset(x, y, 0);
                if (edge) {
                    helper.setBlock(pos, frame);
                } else {
                    helper.setBlock(pos, Blocks.AIR);
                }
            }
        }
        return originRel.offset(1, 1, 0);
    }

    /**
     * Uses a catalyst, held in the player's main hand, on the top face of the frame block below the interior's
     * bottom-left block, as a player clicking it would.
     */
    public static InteractionResult lightPortal(GameTestHelper helper, ServerPlayer player, Item catalyst, BlockPos interiorBottomLeftRel) {
        if (!player.getMainHandItem().is(catalyst)) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(catalyst));
        }
        BlockPos below = helper.absolutePos(interiorBottomLeftRel.below());
        UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(below), Direction.UP, below, false));
        return catalyst.useOn(ctx);
    }

    /** The registered portal containing the given relative position, or null if there is none. */
    @Nullable
    public static CustomPortal portalAt(GameTestHelper helper, BlockPos rel) {
        return PortalData.getPortalFromPos(helper.getLevel(), helper.absolutePos(rel));
    }

    /** The relative positions of the six interior blocks of a frame whose interior starts at the given position. */
    public static List<BlockPos> interior(BlockPos interiorBottomLeftRel) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = 0; x < FRAME_WIDTH - 2; x++) {
            for (int y = 0; y < FRAME_HEIGHT - 2; y++) {
                positions.add(interiorBottomLeftRel.offset(x, y, 0));
            }
        }
        return positions;
    }

    /**
     * Asserts that an item entity of the given item lies within the given distance of an absolute position. Unlike
     * {@link GameTestHelper#assertItemEntityPresent(Item, BlockPos, double)}, it takes an absolute position, so
     * positions read from the portal registry need no conversion.
     */
    public static void assertItemNear(GameTestHelper helper, Item item, BlockPos absolutePos, double distance) {
        boolean present = helper.getLevel().hasEntities(EntityType.ITEM, new AABB(absolutePos).inflate(distance),
                entity -> entity.isAlive() && entity.getItem().is(item));
        helper.assertTrue(present, Component.literal("Expected a " + item + " item entity near " + absolutePos));
    }
}
