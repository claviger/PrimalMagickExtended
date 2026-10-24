package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.commands.FamiliarCommand;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

/**
 * Tests for the familiar administration commands.
 */
public class FamiliarCommandTests extends AbstractBaseTestPMX {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 2.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 2.0D, 1.5D);
    private static final String ROOT = "primalmagickextended familiar ";

    private static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    /** Runs a command as the player with the given permission level, returning its result or -1 if it did not parse. */
    private static int run(ServerPlayer player, int permission, String command) {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        FamiliarCommand.register(dispatcher);
        try {
            return dispatcher.execute(command, player.createCommandSourceStack().withPermission(permission));
        } catch (CommandSyntaxException e) {
            return -1;
        }
    }

    private static int run(ServerPlayer player, String command) {
        return run(player, Commands.LEVEL_OWNERS, command);
    }

    public static void summon_command_summons_dismissed_familiar(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarManager.dismiss(player, wolf);
        assertValueEqual(helper, 1, run(player, ROOT + "summon"), "Summon command result");
        helper.succeedWhen(() -> {
            assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves after the summon command");
            assertTrue(helper, FamiliarManager.findActive(player).isPresent(), "Summoned wolf is not the active familiar");
        });
    }

    public static void summon_command_refuses_when_familiar_is_already_in_play(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        assertValueEqual(helper, 0, run(player, ROOT + "summon"), "Summon command result with a familiar in play");
        assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves after a redundant summon command");
        helper.succeed();
    }

    public static void dismiss_command_dismisses_familiar_in_play(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        assertValueEqual(helper, 1, run(player, ROOT + "dismiss"), "Dismiss command result");
        assertTrue(helper, wolf.isRemoved(), "Familiar still present after the dismiss command");
        assertTrue(helper, FamiliarManager.hasBondedFamiliar(player), "Dismissing must keep the bond");
        assertValueEqual(helper, 0, run(player, ROOT + "dismiss"), "Dismiss command result with nothing in play");
        helper.succeed();
    }

    public static void clear_command_reverts_familiar_in_play_to_ordinary_pet(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.setCustomName(Component.literal("Rex"));
        assertValueEqual(helper, 1, run(player, ROOT + "clear"), "Clear command result");
        assertFalse(helper, FamiliarManager.hasBondedFamiliar(player), "Player still has a bond");
        assertFalse(helper, wolf.isRemoved(), "Cleared familiar was removed");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Cleared familiar is still bound");
        assertTrue(helper, wolf.isTame() && player.getUUID().equals(wolf.getOwnerUUID()), "Cleared familiar lost its owner");
        assertValueEqual(helper, "Rex", wolf.getCustomName().getString(), "Cleared familiar lost its name");
        helper.succeed();
    }

    public static void clear_command_restores_dismissed_familiar_as_ordinary_pet(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.setCustomName(Component.literal("Rex"));
        FamiliarManager.dismiss(player, wolf);
        assertValueEqual(helper, 1, run(player, ROOT + "clear"), "Clear command result");
        assertFalse(helper, FamiliarManager.hasBondedFamiliar(player), "Player still has a bond");
        helper.succeedWhen(() -> {
            var wolves = helper.getEntities(EntityType.WOLF);
            assertValueEqual(helper, 1, wolves.size(), "Wolves after clearing a dismissed familiar");
            Wolf restored = wolves.get(0);
            assertTrue(helper, ServicesPMX.CAPABILITIES.binding(restored).isEmpty(), "Restored pet is still bound");
            assertValueEqual(helper, "Rex", restored.getCustomName().getString(), "Restored pet lost its name");
            assertTrue(helper, restored.isTame(), "Restored pet is not tame");
        });
    }

    public static void commands_fail_when_there_is_no_familiar(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        assertValueEqual(helper, 0, run(player, ROOT + "summon"), "Summon command result with no bond");
        assertValueEqual(helper, 0, run(player, ROOT + "dismiss"), "Dismiss command result with no bond");
        assertValueEqual(helper, 0, run(player, ROOT + "clear"), "Clear command result with no bond");
        helper.succeed();
    }

    public static void commands_require_game_master_permission_and_accept_targets_and_alias(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        assertValueEqual(helper, -1, run(player, Commands.LEVEL_ALL, ROOT + "dismiss"), "Command ran without permission");
        assertFalse(helper, wolf.isRemoved(), "Command without permission dismissed the familiar");
        // The alias and an explicit target selector (@s is the player running the command)
        assertValueEqual(helper, 1, run(player, "pmx familiar dismiss @s"), "Alias with a target selector");
        assertTrue(helper, wolf.isRemoved(), "Alias command did not dismiss the familiar");
        helper.succeed();
    }
}
