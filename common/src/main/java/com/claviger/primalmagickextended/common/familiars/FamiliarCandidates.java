package com.claviger.primalmagickextended.common.familiars;

import com.claviger.primalmagickextended.common.tags.EntityTypeTagsPMX;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Decides which creatures a player may bind with a Familiar Collar: a mob whose type is in the
 * familiar_candidates tag and which is tamed by (or, for foxes, trusts) that player.
 */
public class FamiliarCandidates {
    /** True if the entity is a mob whose type is in the familiar_candidates tag. */
    public static boolean isCandidateType(Entity entity) {
        return entity instanceof Mob && entity.is(EntityTypeTagsPMX.FAMILIAR_CANDIDATES);
    }

    /**
     * True if the mob is tamed by the player (tamable animals and horse-likes) or, for foxes, trusts
     * the player. Uses server-side ownership data; see {@link #looksOwnedOnClient} for the client.
     */
    public static boolean isOwnedBy(Mob mob, Player player) {
        UUID playerId = player.getUUID();
        if (mob instanceof TamableAnimal tamable) {
            return tamable.isTame() && matches(tamable.getOwnerReference(), playerId);
        } else if (mob instanceof AbstractHorse horse) {
            return horse.isTamed() && matches(horse.getOwnerReference(), playerId);
        } else if (mob instanceof Fox fox) {
            return fox.trusts(player);   // made public by the access transformer
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
            return tamable.isTame() && matches(tamable.getOwnerReference(), player.getUUID());
        } else if (mob instanceof AbstractHorse horse) {
            return horse.isTamed();
        } else if (mob instanceof Fox fox) {
            return fox.trusts(player);
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

    /** Compares by UUID rather than resolving the owner, so offline owners still count. */
    private static boolean matches(@Nullable EntityReference<LivingEntity> reference, UUID playerId) {
        return reference != null && playerId.equals(reference.getUUID());
    }
}
