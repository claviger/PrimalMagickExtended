package com.claviger.primalmagickextended.common.commands;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

/**
 * Administrative commands for familiars: {@code /primalmagickextended familiar <summon|dismiss|clear> [players]},
 * also reachable as {@code /pmx}. Without a player argument the command targets whoever runs it, so from the
 * console a player must be named. Requires game master permission, like Primal Magick's own commands.
 */
public class FamiliarCommand {
    /** Prefix of the translation keys of the command feedback. */
    public static final String PREFIX = "message." + ConstantsPMX.MOD_ID + ".command.familiar.";

    /** One command action applied to one player; it sends its own feedback. */
    @FunctionalInterface
    private interface Action {
        boolean apply(CommandSourceStack source, ServerPlayer target);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal(ConstantsPMX.MOD_ID)
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("familiar")
                        .then(action("summon", FamiliarCommand::summon))
                        .then(action("dismiss", FamiliarCommand::dismiss))
                        .then(action("clear", FamiliarCommand::clear))));
        dispatcher.register(Commands.literal("pmx").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).redirect(node));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> action(String name, Action action) {
        return Commands.literal(name)
                .executes(context -> run(context.getSource(), List.of(context.getSource().getPlayerOrException()), action))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> run(context.getSource(), EntityArgument.getPlayers(context, "targets"), action)));
    }

    /** Applies the action to every target and returns how many succeeded. */
    private static int run(CommandSourceStack source, Collection<ServerPlayer> targets, Action action) throws CommandSyntaxException {
        int successes = 0;
        for (ServerPlayer target : targets) {
            if (action.apply(source, target)) {
                successes++;
            }
        }
        return successes;
    }

    /** Calls the target's dismissed familiar back into the world. */
    private static boolean summon(CommandSourceStack source, ServerPlayer target) {
        if (!FamiliarManager.hasBondedFamiliar(target)) {
            return fail(source, "summon.none", target);
        } else if (FamiliarManager.isFamiliarInPlay(target)) {
            return fail(source, "summon.present", target);
        } else if (!FamiliarManager.summon(target)) {
            return fail(source, "summon.failed", target);
        }
        return succeed(source, "summon.ok", target);
    }

    /** Sends the target's familiar away, if it is in the world. */
    private static boolean dismiss(CommandSourceStack source, ServerPlayer target) {
        if (!FamiliarManager.dismissActive(target)) {
            return fail(source, "dismiss.none", target);
        }
        return succeed(source, "dismiss.ok", target);
    }

    /**
     * Removes the target's bond as if they had never bonded a familiar. The familiar itself reverts to an
     * ordinary, still-loyal pet; a dismissed one is called back first so that no pet is lost.
     */
    private static boolean clear(CommandSourceStack source, ServerPlayer target) {
        if (!FamiliarManager.hasBondedFamiliar(target)) {
            return fail(source, "clear.none", target);
        } else if (!FamiliarManager.release(target)) {
            return fail(source, "clear.failed", target);
        }
        return succeed(source, "clear.ok", target);
    }

    private static boolean succeed(CommandSourceStack source, String key, ServerPlayer target) {
        source.sendSuccess(() -> Component.translatable(PREFIX + key, target.getDisplayName()), true);
        return true;
    }

    private static boolean fail(CommandSourceStack source, String key, ServerPlayer target) {
        source.sendFailure(Component.translatable(PREFIX + key, target.getDisplayName()));
        return false;
    }
}
