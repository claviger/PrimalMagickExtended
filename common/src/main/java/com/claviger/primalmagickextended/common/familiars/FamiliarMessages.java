package com.claviger.primalmagickextended.common.familiars;

import com.claviger.primalmagickextended.ConstantsPMX;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

/**
 * Chat feedback sent to players by the familiar manager. Keys live under
 * message.primalmagickextended.familiar.*
 */
public class FamiliarMessages {
    public static final String PREFIX = "message." + ConstantsPMX.MOD_ID + ".familiar.";

    /** Sent when a pet becomes the player's familiar. */
    public static Component bound(Entity familiar) {
        return Component.translatable(PREFIX + "bound", familiar.getDisplayName());
    }

    /** Sent when the player's familiar turns back into an ordinary pet. */
    public static Component released(Entity familiar) {
        return Component.translatable(PREFIX + "released", familiar.getDisplayName());
    }

    /** Sent when the player tries to bind their own familiar again. */
    public static Component already(Entity familiar) {
        return Component.translatable(PREFIX + "already", familiar.getDisplayName());
    }

    /** Sent when the player tries to summon a familiar but has none. */
    public static Component none() {
        return Component.translatable(PREFIX + "none");
    }

    /** Sent when the player tries to bind another player's familiar. */
    public static Component notYours() {
        return Component.translatable(PREFIX + "not_yours");
    }

    /** Sent when the stored familiar could not be recreated in the world. */
    public static Component summonFailed() {
        return Component.translatable(PREFIX + "summon_failed");
    }

    /** Sent when the stored familiar could not be restored to be released, and has been dropped. */
    public static Component lost() {
        return Component.translatable(PREFIX + "lost");
    }
}
