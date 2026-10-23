package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import com.verdantartifice.primalmagick.test.util.RecordingServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tests for the familiar manager's bind, dismiss, summon and toggle operations.
 */
public class FamiliarManagerTests extends AbstractBaseTest {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 1.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 1.0D, 1.5D);

    private static ServerPlayer survivalPlayer(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static IPlayerFamiliar data(ServerPlayer player) {
        return ServicesPMX.CAPABILITIES.familiar(player).orElseThrow();
    }

    public static void bind_marks_pet_and_stores_snapshot(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.tamedWolf(helper, player, PET_POS);
        ItemStack collar = FamiliarTestUtils.collar();
        assertTrue(helper, FamiliarManager.bind(player, wolf, collar), "Bind failed");

        FamiliarBinding binding = ServicesPMX.CAPABILITIES.binding(wolf).orElseThrow();
        assertValueEqual(helper, player.getUUID(), binding.ownerId(), "Binding owner");
        StoredFamiliar stored = data(player).getStored().orElseThrow();
        assertValueEqual(helper, EntityType.WOLF, stored.type(), "Stored type");
        assertValueEqual(helper, binding.generation(), stored.generation(), "Stored generation matches binding");
        assertValueEqual(helper, Optional.of(wolf.getUUID()), stored.activeEntityId(), "Stored active entity id");
        assertTrue(helper, stored.entityData().contains("Owner"), "Snapshot lacks the owner");
        assertTrue(helper, collar.isEmpty(), "Collar was not consumed in survival");
        assertTrue(helper, wolf.isPersistenceRequired(), "Familiar may despawn");
        helper.succeed();
    }

    public static void bind_does_not_consume_collar_in_creative(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        player.setGameMode(GameType.CREATIVE);
        Wolf wolf = FamiliarTestUtils.tamedWolf(helper, player, PET_POS);
        ItemStack collar = FamiliarTestUtils.collar();
        assertTrue(helper, FamiliarManager.bind(player, wolf, collar), "Bind failed");
        assertValueEqual(helper, 1, collar.getCount(), "Collar count after creative bind");
        helper.succeed();
    }

    public static void dismiss_removes_entity_and_updates_snapshot(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.setCustomName(Component.literal("Fluffy"));
        FamiliarManager.dismiss(player, wolf);

        assertTrue(helper, wolf.isRemoved(), "Wolf still in world after dismissal");
        StoredFamiliar stored = data(player).getStored().orElseThrow();
        assertTrue(helper, stored.activeEntityId().isEmpty(), "Active entity id not cleared");
        assertTrue(helper, stored.entityData().getString("CustomName").isPresent(), "Snapshot lacks the custom name");
        assertTrue(helper, FamiliarManager.findActive(player).isEmpty(), "Active familiar found after dismissal");
        helper.succeedWhen(() -> helper.assertEntityNotPresent(EntityType.WOLF));
    }

    public static void summon_restores_familiar_beside_player_with_name_and_new_identity(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.setCustomName(Component.literal("Fluffy"));
        wolf.setComponent(DataComponents.WOLF_COLLAR, DyeColor.RED);
        wolf.setHealth(1.0F);
        UUID oldUuid = wolf.getUUID();
        UUID oldGeneration = data(player).getStored().orElseThrow().generation();
        FamiliarManager.dismiss(player, wolf);

        assertTrue(helper, FamiliarManager.summon(player), "Summon failed");
        helper.succeedWhen(() -> {
            List<Wolf> wolves = helper.getEntities(EntityType.WOLF, new BlockPos(2, 1, 2), 2.0D);
            assertValueEqual(helper, 1, wolves.size(), "Wolves beside the player");
            Wolf summoned = wolves.get(0);
            assertValueEqual(helper, "Fluffy", summoned.getCustomName().getString(), "Summoned wolf name");
            assertValueEqual(helper, DyeColor.RED, summoned.getCollarColor(), "Summoned wolf collar color");
            assertValueEqual(helper, summoned.getMaxHealth(), summoned.getHealth(), "Summoned wolf health");
            assertTrue(helper, summoned.isTame() && summoned.getOwnerReference() != null && summoned.getOwnerReference().getUUID().equals(player.getUUID()), "Summoned wolf is not the player's pet");
            assertFalse(helper, oldUuid.equals(summoned.getUUID()), "Summoned wolf reused the old UUID");
            FamiliarBinding binding = ServicesPMX.CAPABILITIES.binding(summoned).orElseThrow();
            assertFalse(helper, oldGeneration.equals(binding.generation()), "Summoned wolf reused the old generation");
            StoredFamiliar stored = data(player).getStored().orElseThrow();
            assertValueEqual(helper, binding.generation(), stored.generation(), "Stored generation after summon");
            assertValueEqual(helper, Optional.of(summoned.getUUID()), stored.activeEntityId(), "Stored active id after summon");
        });
    }

    public static void toggle_dismisses_when_in_play_and_summons_when_absent(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarManager.toggle(player);
        assertTrue(helper, wolf.isRemoved(), "Toggle did not dismiss the familiar");
        helper.runAfterDelay(42, () -> {
            FamiliarManager.toggle(player);
            helper.succeedWhen(() -> {
                List<Wolf> wolves = helper.getEntities(EntityType.WOLF);
                assertValueEqual(helper, 1, wolves.size(), "Wolves after second toggle");
                Wolf summoned = wolves.get(0);
                assertTrue(helper, ServicesPMX.CAPABILITIES.binding(summoned).isPresent(), "Summoned wolf has no binding");
                assertValueEqual(helper, Optional.of(summoned.getUUID()), data(player).getStored().orElseThrow().activeEntityId(), "Stored active id after second toggle");
            });
        });
    }

    public static void toggle_twice_in_one_tick_toggles_once(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarManager.toggle(player);
        FamiliarManager.toggle(player);
        assertTrue(helper, wolf.isRemoved(), "First toggle did not dismiss");
        assertTrue(helper, FamiliarManager.findActive(player).isEmpty(), "Second toggle in the same tick re-summoned the familiar");
        helper.succeed();
    }

    public static void toggle_within_debounce_window_is_ignored(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        FamiliarManager.toggle(player);
        assertTrue(helper, wolf.isRemoved(), "First toggle did not dismiss");
        helper.runAfterDelay(20, () -> {
            FamiliarManager.toggle(player);
            assertTrue(helper, FamiliarManager.findActive(player).isEmpty(), "Toggle within the debounce window re-summoned the familiar");
            helper.runAfterDelay(42, () -> {
                FamiliarManager.toggle(player);
                helper.succeedWhen(() -> assertTrue(helper, FamiliarManager.findActive(player).isPresent(), "Toggle after the debounce window did not summon"));
            });
        });
    }

    public static void summon_with_no_familiar_sends_message(GameTestHelper helper) {
        RecordingServerPlayer player = RecordingServerPlayer.create(helper, false);
        assertFalse(helper, FamiliarManager.summon(player), "Summon succeeded with no familiar");
        assertTrue(helper, player.hasSystemMessage("message.primalmagickextended.familiar.none"), "No 'none' message sent");
        helper.succeed();
    }

    public static void dismiss_ejects_riding_owner(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, PET_POS);
        horse.setTamed(true);
        horse.setOwner(player);
        assertTrue(helper, FamiliarManager.bind(player, horse, FamiliarTestUtils.collar()), "Bind failed");
        assertTrue(helper, player.startRiding(horse, true, false), "Player could not mount the horse");
        FamiliarManager.dismiss(player, horse);
        assertFalse(helper, player.isPassenger(), "Player still riding after dismissal");
        assertTrue(helper, horse.isRemoved(), "Horse still present after dismissal");
        helper.succeed();
    }

    public static void bind_snapshot_strips_passengers(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, PET_POS);
        horse.setTamed(true);
        horse.setOwner(player);
        ArmorStand rider = helper.spawn(EntityType.ARMOR_STAND, PET_POS);
        assertTrue(helper, rider.startRiding(horse, true, false), "Armor stand could not mount the horse");
        assertTrue(helper, FamiliarTestUtils.snapshot(horse).contains("Passengers"), "Raw save lacks the rider, so this test proves nothing");
        assertTrue(helper, FamiliarManager.bind(player, horse, FamiliarTestUtils.collar()), "Bind failed");
        assertFalse(helper, data(player).getStored().orElseThrow().entityData().contains("Passengers"), "Snapshot kept the rider");
        rider.discard();
        helper.succeed();
    }

    public static void summon_when_in_play_does_not_duplicate(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        UUID generation = data(player).getStored().orElseThrow().generation();
        assertTrue(helper, FamiliarManager.summon(player), "Summon of an in-play familiar reported failure");
        assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves after summoning an in-play familiar");
        assertValueEqual(helper, generation, data(player).getStored().orElseThrow().generation(), "Generation after summoning an in-play familiar");
        helper.succeed();
    }

    public static void summon_clears_harmful_effects(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        wolf.addEffect(new MobEffectInstance(MobEffects.POISON, 600));
        FamiliarManager.dismiss(player, wolf);
        assertTrue(helper, FamiliarManager.summon(player), "Summon failed");
        helper.runAfterDelay(40, () -> helper.succeedWhen(() -> {
            List<Wolf> wolves = helper.getEntities(EntityType.WOLF);
            assertValueEqual(helper, 1, wolves.size(), "Wolves 40 ticks after summon");
            assertFalse(helper, wolves.get(0).hasEffect(MobEffects.POISON), "Summoned wolf is still poisoned");
        }));
    }

    public static void dismiss_stores_live_entity_type(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        IPlayerFamiliar familiar = data(player);
        StoredFamiliar stored = familiar.getStored().orElseThrow();
        // Stand-in for a polymorphed familiar: same binding, different entity type
        Sheep sheep = helper.spawnWithNoFreeWill(EntityType.SHEEP, PLAYER_POS);
        ServicesPMX.CAPABILITIES.setBinding(sheep, new FamiliarBinding(player.getUUID(), stored.generation()));
        familiar.setStored(Optional.of(stored.withActiveEntityId(Optional.of(sheep.getUUID()))));
        FamiliarManager.dismiss(player, sheep);
        assertValueEqual(helper, EntityType.SHEEP, familiar.getStored().orElseThrow().type(), "Stored type after dismissing the polymorphed familiar");
        assertTrue(helper, sheep.isRemoved(), "Sheep still present after dismissal");
        wolf.discard();
        helper.succeed();
    }
}
