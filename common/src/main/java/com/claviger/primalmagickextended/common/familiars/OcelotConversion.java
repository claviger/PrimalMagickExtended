package com.claviger.primalmagickextended.common.familiars;

import com.claviger.primalmagickextended.common.entities.CatVariantsPMX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

/**
 * Undocumented Easter egg: a Familiar Collar used on an ocelot turns it into a cat that keeps the
 * ocelot's appearance, tamed by the player, and binds that cat as the player's familiar.
 */
public class OcelotConversion {
    /**
     * Converts the ocelot into an ocelot-variant cat tamed by the player and binds it. Any current
     * familiar is released first; if that fails nothing happens. Vanilla's conversion carries over the
     * position, vehicle, age, name and flags; the leash, rider, effects, tags, team and movement state are carried
     * over here, as a full conversion would. If the conversion cannot start, the ocelot keeps its leash. If the
     * level refuses the converted cat (another mod vetoed the join), the ocelot is lost as in any vanilla
     * conversion, the previously released familiar stays an ordinary pet, no collar is consumed and nothing is
     * bound.
     *
     * @return true if the cat is now the player's familiar
     */
    public static boolean convertAndBind(ServerPlayer player, Ocelot ocelot, ItemStack collarStack) {
        if (FamiliarManager.data(player).flatMap(familiar -> familiar.getStored()).isPresent() && !FamiliarManager.release(player)) {
            return false;
        }
        ServerLevel level = player.serverLevel();
        // The ocelot's leash is moved to the cat by hand; drop it without the lead item first, so that the
        // discarded ocelot does not drop a lead
        Entity leashHolder = ocelot.getLeashHolder();
        if (leashHolder != null) {
            ocelot.dropLeash(true, false);
        }
        PlayerTeam team = ocelot.getTeam();
        // Discarding the ocelot dismounts its rider, so it is moved to the cat by hand
        Entity rider = ocelot.getFirstPassenger();
        // Ocelots carry no equipment worth keeping, and the cat keeps its own loot-pickup default
        Cat cat = ocelot.convertTo(EntityType.CAT, false);
        if (cat == null) {
            // The ocelot is untouched apart from its leash
            if (leashHolder != null) {
                ocelot.setLeashedTo(leashHolder, true);
            }
            return false;
        }
        // convertTo ignores whether the level accepted the new entity
        if (level.getEntity(cat.getUUID()) != cat) {
            return false;
        }
        cat.setVariant(CatVariantsPMX.OCELOT.getHolder());
        cat.tame(player);
        // The Ocelot variant alone keeps an unnamed cat called an ocelot (see OcelotCatNameMixin), without the nameplate a custom name would show
        carryOver(ocelot, cat, rider, leashHolder, team);
        return FamiliarManager.bind(player, cat, collarStack);
    }

    /**
     * Copies what vanilla's single-entity conversion copies on later versions but {@code Mob.convertTo} does not on
     * this one: the rider, fall distance, hurt timers, body rotation, on-ground state, sleeping position, leash,
     * effects, absorption, age, a few flags, tags and team.
     */
    private static void carryOver(Ocelot ocelot, Cat cat, Entity rider, Entity leashHolder, PlayerTeam team) {
        if (rider != null && !rider.isRemoved()) {
            rider.boardingCooldown = 0;
            rider.startRiding(cat);
        }
        cat.fallDistance = ocelot.fallDistance;
        cat.lastHurtByPlayerTime = ocelot.lastHurtByPlayerTime;
        cat.hurtTime = ocelot.hurtTime;
        cat.yBodyRot = ocelot.yBodyRot;
        cat.setOnGround(ocelot.onGround());
        ocelot.getSleepingPos().ifPresent(cat::setSleepingPos);
        if (leashHolder != null) {
            cat.setLeashedTo(leashHolder, true);
        }
        for (MobEffectInstance effect : ocelot.getActiveEffects()) {
            cat.addEffect(new MobEffectInstance(effect));
        }
        cat.setAbsorptionAmount(ocelot.getAbsorptionAmount());
        cat.setAge(ocelot.getAge());
        cat.setDeltaMovement(ocelot.getDeltaMovement());
        cat.setLeftHanded(ocelot.isLeftHanded());
        cat.setSharedFlagOnFire(ocelot.isOnFire());
        cat.setNoGravity(ocelot.isNoGravity());
        cat.setPortalCooldown(ocelot.getPortalCooldown());
        cat.setSilent(ocelot.isSilent());
        ocelot.getTags().forEach(cat::addTag);
        if (team != null) {
            Scoreboard scoreboard = cat.level().getScoreboard();
            scoreboard.addPlayerToTeam(cat.getStringUUID(), team);
            if (ocelot.getTeam() == team) {
                scoreboard.removePlayerFromTeam(ocelot.getStringUUID(), team);
            }
        }
    }
}
