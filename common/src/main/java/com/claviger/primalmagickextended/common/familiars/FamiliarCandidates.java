package com.claviger.primalmagickextended.common.familiars;

import com.claviger.primalmagickextended.common.tags.EntityTypeTagsPMX;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Decides which creatures a player may bind with a Familiar Collar: a mob whose type is in the
 * familiar_candidates tag and which is tamed by (or, for foxes, trusts) that player.
 */
public class FamiliarCandidates {
    /** True if the entity is a mob whose type is in the familiar_candidates tag. */
    public static boolean isCandidateType(Entity entity) {
        return entity instanceof Mob && entity.getType().is(EntityTypeTagsPMX.FAMILIAR_CANDIDATES);
    }

    /**
     * True if the mob is tamed by the player (tamable animals and horse-likes) or, for foxes, trusts
     * the player. Compares owner UUIDs rather than resolving the owner, so offline owners still count.
     * Uses server-side ownership data; see {@link #looksOwnedOnClient} for the client.
     */
    public static boolean isOwnedBy(Mob mob, Player player) {
        UUID playerId = player.getUUID();
        if (mob instanceof TamableAnimal tamable) {
            return tamable.isTame() && playerId.equals(tamable.getOwnerUUID());
        } else if (mob instanceof AbstractHorse horse) {
            return horse.isTamed() && playerId.equals(horse.getOwnerUUID());
        } else if (mob instanceof Fox fox) {
            return fox.trusts(playerId);   // made public by the access transformer
        } else {
            return false;
        }
    }

    /**
     * Client-side prediction of {@link #isOwnedBy}, limited to what the server syncs. A tamable
     * animal's owner and a fox's trusted list are synced, so those are checked exactly. A horse-like's
     * owner is not synced, so any tamed horse-like counts. The server makes the real decision. When the
     * client predicts wrongly, the server falls back to vanilla interaction, which it syncs.
     */
    public static boolean looksOwnedOnClient(Mob mob, Player player) {
        if (mob instanceof TamableAnimal tamable) {
            return tamable.isTame() && player.getUUID().equals(tamable.getOwnerUUID());
        } else if (mob instanceof AbstractHorse horse) {
            return horse.isTamed();
        } else if (mob instanceof Fox fox) {
            return fox.trusts(player.getUUID());
        } else {
            return false;
        }
    }

    /**
     * True if the player may bind the target: a candidate type owned by the player. This deliberately
     * omits the "no existing binding" rule, because callers route already-bound mobs to
     * {@link FamiliarManager#bind} first, which reports them to the player.
     */
    public static boolean isEligible(Player player, Entity target) {
        return isCandidateType(target) && target instanceof Mob mob && isOwnedBy(mob, player);
    }
}
