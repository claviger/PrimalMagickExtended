package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.wands.IManaContainer;
import com.verdantartifice.primalmagick.common.wands.WandCap;
import com.verdantartifice.primalmagick.common.wands.WandCore;
import com.verdantartifice.primalmagick.common.wands.WandGem;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

/**
 * Tests that an in-play familiar within range grants a +5 point mana cost modifier.
 */
public class FamiliarManaEfficiencyTests extends AbstractBaseTestPMX {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 2.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 2.0D, 1.5D);
    private static final int FAMILIAR_BONUS = 5;

    private static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static int modifier(GameTestHelper helper, ItemStack wandStack, ServerPlayer player) {
        IManaContainer wand = (IManaContainer)wandStack.getItem();
        return wand.getTotalCostModifier(wandStack, player, Sources.EARTH, helper.getLevel().registryAccess());
    }

    /** Runs the proximity update the server tick would run. */
    private static void tickProximity(ServerPlayer player) {
        FamiliarManager.updateProximity(player);
    }

    public static void familiar_nearby_adds_five_to_wand_cost_modifier(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        ItemStack mundane = new ItemStack(ItemsPM.MUNDANE_WAND.get());
        ItemStack modular = FamiliarTestUtils.makeModularCaster(ItemsPM.MODULAR_WAND.get(), WandCore.HEARTWOOD, WandCap.IRON, WandGem.APPRENTICE);
        int mundaneBefore = modifier(helper, mundane, player);
        int modularBefore = modifier(helper, modular, player);

        FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        tickProximity(player);
        assertTrue(helper, FamiliarManager.isFamiliarNearby(player), "Familiar not flagged nearby");
        assertValueEqual(helper, mundaneBefore + FAMILIAR_BONUS, modifier(helper, mundane, player), "Mundane wand modifier with familiar");
        assertValueEqual(helper, modularBefore + FAMILIAR_BONUS, modifier(helper, modular, player), "Modular wand modifier with familiar");
        helper.succeed();
    }

    public static void familiar_far_away_adds_nothing(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        ItemStack mundane = new ItemStack(ItemsPM.MUNDANE_WAND.get());
        int before = modifier(helper, mundane, player);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        // Move the player, not the familiar: relocating the familiar into an unloaded chunk would store it as dismissed
        Vec3 home = player.position();
        try {
            player.setPos(home.add(FamiliarManager.NEARBY_RANGE + 20.0D, 0.0D, 0.0D));
            tickProximity(player);
            assertFalse(helper, FamiliarManager.isFamiliarNearby(player), "Distant familiar flagged nearby");
            assertValueEqual(helper, before, modifier(helper, mundane, player), "Modifier with distant familiar");
            player.setPos(home);
            tickProximity(player);
            assertTrue(helper, FamiliarManager.isFamiliarNearby(player), "Returned familiar not flagged nearby");
        } finally {
            player.setPos(home);
            wolf.discard();
        }
        helper.succeed();
    }

    public static void dismissed_familiar_adds_nothing(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        ItemStack mundane = new ItemStack(ItemsPM.MUNDANE_WAND.get());
        int before = modifier(helper, mundane, player);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        tickProximity(player);
        FamiliarManager.dismiss(player, wolf);
        tickProximity(player);
        assertFalse(helper, FamiliarManager.isFamiliarNearby(player), "Dismissed familiar flagged nearby");
        assertValueEqual(helper, before, modifier(helper, mundane, player), "Modifier after dismissal");
        helper.succeed();
    }

    public static void familiar_bonus_reduces_spell_mana_consumed(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        ItemStack wandStack = FamiliarTestUtils.makeModularCaster(ItemsPM.MODULAR_WAND.get(), WandCore.HEARTWOOD, WandCap.IRON, WandGem.APPRENTICE);
        IManaContainer wand = (IManaContainer)wandStack.getItem();
        int baseCost = 1000;   // centimana
        int modifierWithout = modifier(helper, wandStack, player);
        int without = wand.getModifiedCost(wandStack, player, Sources.EARTH, baseCost, helper.getLevel().registryAccess());
        FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        tickProximity(player);
        int with = wand.getModifiedCost(wandStack, player, Sources.EARTH, baseCost, helper.getLevel().registryAccess());
        int expected = (int)Math.floor(baseCost / (1 + (modifierWithout + FAMILIAR_BONUS) / 100.0D));
        assertTrue(helper, with < without, "Familiar did not reduce the modified cost (" + with + " vs " + without + ")");
        assertValueEqual(helper, expected, with, "Modified cost with familiar");
        helper.succeed();
    }
}
