package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.events.FamiliarEvents;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import com.verdantartifice.primalmagick.test.util.RecordingServerPlayer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Tests that familiars can never be hurt or lost: damage dismisses them, stale copies are removed,
 * chunk unloads refresh the owner's snapshot, and logout stores them with the player.
 */
public class FamiliarProtectionTests extends AbstractBaseTest {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 1.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 1.0D, 1.5D);

    private static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static StoredFamiliar stored(ServerPlayer player) {
        return ServicesPMX.CAPABILITIES.familiar(player).orElseThrow().getStored().orElseThrow();
    }

    public static void familiar_dismisses_instead_of_taking_damage(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        float healthBefore = wolf.getHealth();
        boolean hurt = wolf.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 4.0F);
        assertFalse(helper, hurt, "Damage was applied");
        assertTrue(helper, wolf.isRemoved(), "Familiar still present after damage");
        assertTrue(helper, stored(player).activeEntityId().isEmpty(), "Familiar still marked active");
        assertValueEqual(helper, healthBefore, stored(player).entityData().getFloatOr("Health", 0.0F), "Snapshot health changed");
        helper.succeedWhen(() -> {
            helper.assertEntityNotPresent(EntityType.WOLF);
            helper.assertEntityNotPresent(EntityType.ITEM);
        });
    }

    public static void familiar_dismisses_on_generic_kill(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.kill(helper.getLevel());
        assertTrue(helper, wolf.isRemoved(), "Familiar still present after kill");
        assertFalse(helper, wolf.isDeadOrDying(), "Familiar died");
        assertTrue(helper, stored(player).activeEntityId().isEmpty(), "Familiar still marked active");
        helper.succeed();
    }

    public static void familiar_dismisses_on_fire_damage(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.igniteForSeconds(5.0F);
        helper.succeedWhen(() -> {
            assertTrue(helper, wolf.isRemoved(), "Burning familiar still present");
            assertTrue(helper, stored(player).activeEntityId().isEmpty(), "Familiar still marked active");
        });
    }

    public static void binding_new_familiar_releases_old_one_unharmed(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf oldWolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        oldWolf.setCustomName(Component.literal("Old"));
        Wolf newWolf = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 1.5D));
        assertTrue(helper, FamiliarManager.bind(player, newWolf, FamiliarTestUtils.collar()), "Second bind failed");

        assertFalse(helper, oldWolf.isRemoved(), "Old familiar was removed");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(oldWolf).isEmpty(), "Old familiar still bound");
        assertTrue(helper, oldWolf.isTame() && oldWolf.getOwnerReference().getUUID().equals(player.getUUID()), "Old familiar lost its owner");
        assertValueEqual(helper, "Old", oldWolf.getCustomName().getString(), "Old familiar lost its name");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(newWolf).isPresent(), "New familiar not bound");
        assertValueEqual(helper, Optional.of(newWolf.getUUID()), stored(player).activeEntityId(), "Stored active id is not the new familiar");
        helper.succeed();
    }

    public static void binding_new_familiar_summons_dismissed_old_one_first(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf oldWolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        oldWolf.setCustomName(Component.literal("Old"));
        FamiliarManager.dismiss(player, oldWolf);
        Wolf newWolf = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 1.5D));
        assertTrue(helper, FamiliarManager.bind(player, newWolf, FamiliarTestUtils.collar()), "Second bind failed");
        helper.succeedWhen(() -> {
            var wolves = helper.getEntities(EntityType.WOLF);
            assertValueEqual(helper, 2, wolves.size(), "Wolves in the world");
            Wolf released = wolves.stream().filter(w -> w != newWolf).findFirst().orElseThrow();
            assertValueEqual(helper, "Old", released.getCustomName().getString(), "Released familiar name");
            assertTrue(helper, ServicesPMX.CAPABILITIES.binding(released).isEmpty(), "Released familiar still bound");
            assertTrue(helper, released.isTame(), "Released familiar is not tame");
        });
    }

    public static void release_with_unrestorable_snapshot_clears_familiar_and_allows_bind(GameTestHelper helper) {
        RecordingServerPlayer player = RecordingServerPlayer.create(helper, true);
        FamiliarTestUtils.placePlayer(helper, player, PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        Wolf oldWolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarManager.dismiss(player, oldWolf);
        // An item entity is not a mob, so the snapshot can no longer be restored as a familiar
        IPlayerFamiliar familiar = ServicesPMX.CAPABILITIES.familiar(player).orElseThrow();
        StoredFamiliar broken = stored(player);
        familiar.setStored(Optional.of(new StoredFamiliar(EntityType.ITEM, broken.entityData(), broken.generation(), Optional.empty())));

        Wolf newWolf = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 1.5D));
        assertTrue(helper, FamiliarManager.bind(player, newWolf, FamiliarTestUtils.collar()), "Bind after an unrestorable familiar failed");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(newWolf).filter(b -> b.isOwnedBy(player.getUUID())).isPresent(), "New wolf is not bound to the player");
        StoredFamiliar stored = stored(player);
        assertValueEqual(helper, EntityType.WOLF, stored.type(), "Stored type after the new bind");
        assertValueEqual(helper, Optional.of(newWolf.getUUID()), stored.activeEntityId(), "Active familiar after the new bind");
        assertTrue(helper, player.hasSystemMessage("message.primalmagickextended.familiar.lost"), "Player not told the old familiar was lost");
        assertTrue(helper, player.hasSystemMessage("message.primalmagickextended.familiar.bound"), "Player not told the new familiar was bound");
        helper.succeedWhen(() -> {
            assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves in the world");
            helper.assertEntityNotPresent(EntityType.ITEM);
        });
    }

    public static void release_with_refused_join_aborts_bind_and_keeps_familiar(GameTestHelper helper) {
        RecordingServerPlayer player = RecordingServerPlayer.create(helper, true);
        FamiliarTestUtils.placePlayer(helper, player, PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        Wolf oldWolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarManager.dismiss(player, oldWolf);
        StoredFamiliar before = stored(player);

        Wolf newWolf = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 1.5D));
        ItemStack collar = FamiliarTestUtils.collar();
        boolean bound;
        // Make the level refuse the released pet, as a join vetoed by another mod would
        FamiliarManager.setAddFilterForTesting(mob -> false);
        try {
            bound = FamiliarManager.bind(player, newWolf, collar);
        } finally {
            FamiliarManager.setAddFilterForTesting(null);
        }
        assertFalse(helper, bound, "Bind succeeded although the old familiar could not be released");
        assertValueEqual(helper, 1, collar.getCount(), "Collar count after the aborted bind");
        StoredFamiliar after = stored(player);
        assertValueEqual(helper, before.generation(), after.generation(), "Stored generation after the aborted bind");
        assertValueEqual(helper, EntityType.WOLF, after.type(), "Stored type after the aborted bind");
        assertTrue(helper, after.activeEntityId().isEmpty(), "Old familiar marked active after the aborted bind");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(newWolf).isEmpty(), "New wolf was bound");
        assertTrue(helper, player.hasSystemMessage("message.primalmagickextended.familiar.summon_failed"), "Player not told the familiar could not answer");
        assertFalse(helper, player.hasSystemMessage("message.primalmagickextended.familiar.lost"), "Player told the familiar was lost");
        helper.succeedWhen(() -> assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves in the world"));
    }

    public static void stale_generation_familiar_is_removed_on_join(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        StoredFamiliar before = stored(player);
        Wolf stale = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 3.5D));
        ServicesPMX.CAPABILITIES.setBinding(stale, new FamiliarBinding(player.getUUID(), UUID.randomUUID()));
        assertTrue(helper, FamiliarEvents.onEntityJoin(stale, helper.getLevel()), "Stale copy was not discarded");
        assertTrue(helper, stale.isRemoved(), "Stale copy still present");
        assertValueEqual(helper, before.generation(), stored(player).generation(), "Stored generation changed");
        assertValueEqual(helper, Optional.of(wolf.getUUID()), stored(player).activeEntityId(), "Active id changed");
        helper.succeed();
    }

    public static void current_generation_familiar_is_adopted_on_join(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        UUID generation = stored(player).generation();
        wolf.discard();
        Wolf replacement = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 3.5D));
        ServicesPMX.CAPABILITIES.setBinding(replacement, new FamiliarBinding(player.getUUID(), generation));
        assertFalse(helper, FamiliarEvents.onEntityJoin(replacement, helper.getLevel()), "Current-generation copy was discarded");
        assertValueEqual(helper, Optional.of(replacement.getUUID()), stored(player).activeEntityId(), "Replacement was not adopted");
        helper.succeed();
    }

    public static void duplicate_familiar_is_removed_on_join(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        UUID generation = stored(player).generation();
        UUID activeBefore = stored(player).activeEntityId().orElseThrow();
        Wolf duplicate = FamiliarTestUtils.tamedWolf(helper, player, new Vec3(3.5D, 1.0D, 3.5D));
        ServicesPMX.CAPABILITIES.setBinding(duplicate, new FamiliarBinding(player.getUUID(), generation));
        assertTrue(helper, FamiliarEvents.onEntityJoin(duplicate, helper.getLevel()), "Duplicate was not discarded");
        assertValueEqual(helper, Optional.of(activeBefore), stored(player).activeEntityId(), "Active id changed");
        helper.succeed();
    }

    public static void familiar_with_offline_owner_is_removed_on_join(GameTestHelper helper) {
        Wolf orphan = helper.spawnWithNoFreeWill(EntityType.WOLF, PET_POS);
        ServicesPMX.CAPABILITIES.setBinding(orphan, new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID()));
        assertTrue(helper, FamiliarEvents.onEntityJoin(orphan, helper.getLevel()), "Orphan was not discarded");
        helper.succeed();
    }

    public static void logout_dismisses_familiar_in_play(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarEvents.onPlayerLogout(player);
        assertTrue(helper, wolf.isRemoved(), "Familiar still present after logout");
        assertTrue(helper, stored(player).activeEntityId().isEmpty(), "Familiar still marked active after logout");
        helper.succeed();
    }

    public static void chunk_unload_stores_familiar_and_rotates_generation(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        UUID generationBefore = stored(player).generation();
        wolf.setCustomName(Component.literal("Unloaded"));
        // remove(UNLOADED_TO_CHUNK) stops tracking the wolf, so the real leave-level listener already runs here;
        // the explicit call is then a no-op, because the generation has already rotated
        wolf.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        FamiliarEvents.onEntityLeaveLevel(wolf, helper.getLevel());
        StoredFamiliar after = stored(player);
        assertTrue(helper, after.activeEntityId().isEmpty(), "Familiar still marked active after unload");
        assertFalse(helper, generationBefore.equals(after.generation()), "Generation was not rotated on unload");
        assertTrue(helper, after.entityData().getString("CustomName").map(s -> s.contains("Unloaded")).orElse(false), "Snapshot not refreshed on unload");
        helper.succeed();
    }

    public static void chunk_hidden_leave_stores_familiar_without_removing_it(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        UUID generationBefore = stored(player).generation();
        wolf.setCustomName(Component.literal("Hidden"));
        // A chunk becoming hidden stops tracking its entities before they are saved and marked removed
        FamiliarEvents.onEntityLeaveLevel(wolf, helper.getLevel());
        StoredFamiliar after = stored(player);
        assertFalse(helper, wolf.isRemoved(), "Leave-level handler removed the familiar");
        assertTrue(helper, after.activeEntityId().isEmpty(), "Familiar still marked active after leaving");
        assertFalse(helper, generationBefore.equals(after.generation()), "Generation was not rotated on leaving");
        assertTrue(helper, after.entityData().getString("CustomName").map(s -> s.contains("Hidden")).orElse(false), "Snapshot not refreshed on leaving");
        assertTrue(helper, FamiliarEvents.onEntityJoin(wolf, helper.getLevel()), "Copy with the old generation was not stale");
        helper.succeed();
    }

    public static void leave_level_ignores_discarded_familiar(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        UUID generationBefore = stored(player).generation();
        // discard() fires the real leave-level listener with DISCARDED, which must return early
        wolf.discard();
        FamiliarEvents.onEntityLeaveLevel(wolf, helper.getLevel());
        assertValueEqual(helper, generationBefore, stored(player).generation(), "Generation rotated for a discarded familiar");
        assertValueEqual(helper, Optional.of(wolf.getUUID()), stored(player).activeEntityId(), "Active id cleared for a discarded familiar");
        helper.succeed();
    }

    public static void stale_generation_familiar_is_rejected_by_real_join_event(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        Wolf stale = EntityType.WOLF.create(helper.getLevel(), EntitySpawnReason.MOB_SUMMONED);
        if (stale == null) {
            helper.fail("Could not create the stale wolf");
            return;
        }
        Vec3 pos = helper.absoluteVec(new Vec3(3.5D, 1.0D, 3.5D));
        stale.absSnapTo(pos.x, pos.y, pos.z, 0.0F, 0.0F);
        ServicesPMX.CAPABILITIES.setBinding(stale, new FamiliarBinding(player.getUUID(), UUID.randomUUID()));
        assertFalse(helper, helper.getLevel().addFreshEntity(stale), "Level accepted the stale copy");
        assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves in the world");
        helper.succeed();
    }

    public static void stale_bound_mob_is_discarded_by_periodic_check(GameTestHelper helper) {
        Wolf wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, PET_POS);
        // Bound after joining, so only the periodic check can catch it
        ServicesPMX.CAPABILITIES.setBinding(wolf, new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID()));
        helper.succeedWhen(() -> assertTrue(helper, wolf.isRemoved(), "Stale bound wolf still present"));
    }

    public static void death_safety_net_dismisses_familiar(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.die(helper.getLevel().damageSources().generic());
        // No isDeadOrDying check: die() never lowers health, so it would pass whether or not death was prevented
        assertTrue(helper, wolf.isRemoved(), "Familiar still present after dying");
        assertTrue(helper, stored(player).activeEntityId().isEmpty(), "Familiar still marked active after dying");
        helper.succeed();
    }

    public static void zero_damage_hit_does_not_dismiss(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        boolean hurt = wolf.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 0.0F);
        assertFalse(helper, hurt, "Harmless hit was applied");
        assertFalse(helper, wolf.isRemoved(), "Harmless hit dismissed the familiar");
        assertValueEqual(helper, Optional.of(wolf.getUUID()), stored(player).activeEntityId(), "Familiar no longer active");
        helper.succeed();
    }

    public static void stale_familiar_in_boat_is_swept(GameTestHelper helper) {
        Entity boat = helper.spawn(EntityType.OAK_BOAT, PET_POS);
        Wolf wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, PET_POS);
        // Bound after joining, so only the periodic check can catch it; passengers get no tick event of their own
        ServicesPMX.CAPABILITIES.setBinding(wolf, new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID()));
        assertTrue(helper, wolf.startRiding(boat, true, false), "Wolf could not board the boat");
        helper.succeedWhen(() -> {
            assertTrue(helper, wolf.isRemoved(), "Stale familiar in a boat still present");
            boat.discard();
        });
    }
}
