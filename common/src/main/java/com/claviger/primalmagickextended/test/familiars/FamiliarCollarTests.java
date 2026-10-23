package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.entities.CatVariantsPMX;
import com.claviger.primalmagickextended.common.events.FamiliarEvents;
import com.claviger.primalmagickextended.common.familiars.FamiliarCandidates;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import com.verdantartifice.primalmagick.test.util.RecordingServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

/**
 * Tests for the Familiar Collar item and its application to pets.
 */
public class FamiliarCollarTests extends AbstractBaseTest {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 1.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 1.0D, 1.5D);

    public static void familiar_collar_item_is_registered(GameTestHelper helper) {
        assertValueEqual(helper, ResourceUtilsPMX.loc("familiar_collar"), BuiltInRegistries.ITEM.getKey(ItemsPMX.FAMILIAR_COLLAR.get()), "Familiar collar registry id");
        assertValueEqual(helper, "item.primalmagickextended.familiar_collar", ItemsPMX.FAMILIAR_COLLAR.get().getDescriptionId(), "Familiar collar description id");
        assertValueEqual(helper, 16, ItemsPMX.FAMILIAR_COLLAR.get().getDefaultMaxStackSize(), "Familiar collar max stack size");
        helper.succeed();
    }

    private static ServerPlayer holdingCollar(GameTestHelper helper, ItemStack collar) {
        return holdingCollar(helper, makeMockServerPlayer(helper, true), collar);
    }

    /** Places the given mock player, puts it in survival and gives it the collar in its main hand. */
    private static <P extends ServerPlayer> P holdingCollar(GameTestHelper helper, P player, ItemStack collar) {
        FamiliarTestUtils.placePlayer(helper, player, PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, collar);
        return player;
    }

    /** Simulates the entity-interact event the way the NeoForge listener does. */
    private static boolean useCollar(ServerPlayer player, Entity target) {
        return FamiliarEvents.onEntityInteract(player, InteractionHand.MAIN_HAND, target);
    }

    public static void collar_binds_own_tamed_wolf(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Wolf wolf = FamiliarTestUtils.tamedWolf(helper, player, PET_POS);
        assertTrue(helper, useCollar(player, wolf), "Interaction was not consumed");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).map(b -> b.isOwnedBy(player.getUUID())).orElse(false), "Wolf not bound to player");
        assertTrue(helper, collar.isEmpty(), "Collar not consumed");
        helper.succeed();
    }

    public static void collar_binds_own_tamed_horse(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, PET_POS);
        horse.setTamed(true);
        horse.setOwner(player);
        assertTrue(helper, useCollar(player, horse), "Interaction was not consumed");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(horse).map(b -> b.isOwnedBy(player.getUUID())).orElse(false), "Horse not bound to player");
        assertTrue(helper, collar.isEmpty(), "Collar not consumed");
        helper.succeed();
    }

    public static void collar_binds_trusting_fox(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Fox fox = helper.spawnWithNoFreeWill(EntityType.FOX, PET_POS);
        FamiliarTestUtils.trust(fox, player);
        assertTrue(helper, useCollar(player, fox), "Interaction was not consumed");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(fox).map(b -> b.isOwnedBy(player.getUUID())).orElse(false), "Fox not bound to player");
        assertTrue(helper, collar.isEmpty(), "Collar not consumed");
        helper.succeed();
    }

    public static void collar_ignores_untrusting_fox(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Fox fox = helper.spawnWithNoFreeWill(EntityType.FOX, PET_POS);
        assertFalse(helper, useCollar(player, fox), "Interaction consumed for an untrusting fox");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(fox).isEmpty(), "Untrusting fox was bound");
        assertValueEqual(helper, 1, collar.getCount(), "Collar consumed");
        helper.succeed();
    }

    public static void collar_ignores_untamed_wolf(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Wolf wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, PET_POS);
        assertFalse(helper, useCollar(player, wolf), "Interaction consumed for an untamed wolf");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Untamed wolf was bound");
        assertValueEqual(helper, 1, collar.getCount(), "Collar consumed");
        helper.succeed();
    }

    public static void collar_ignores_wolf_owned_by_another_player(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        ServerPlayer other = makeMockServerPlayer(helper);
        Wolf wolf = FamiliarTestUtils.tamedWolf(helper, other, PET_POS);
        assertFalse(helper, useCollar(player, wolf), "Interaction consumed for another player's wolf");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Another player's wolf was bound");
        assertValueEqual(helper, 1, collar.getCount(), "Collar consumed");
        helper.succeed();
    }

    public static void collar_ignores_non_candidate_animal(GameTestHelper helper) {
        // A camel is a horse-like the player can own, but it is outside the familiar_candidates tag
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Camel camel = helper.spawnWithNoFreeWill(EntityType.CAMEL, PET_POS);
        camel.setTamed(true);
        camel.setOwner(player);
        assertFalse(helper, useCollar(player, camel), "Interaction consumed for a camel");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(camel).isEmpty(), "Camel was bound");
        assertValueEqual(helper, 1, collar.getCount(), "Collar consumed");
        helper.succeed();
    }

    public static void collar_on_own_familiar_does_nothing(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);
        var generation = ServicesPMX.CAPABILITIES.binding(wolf).orElseThrow().generation();
        assertTrue(helper, useCollar(player, wolf), "Interaction was not consumed");
        assertValueEqual(helper, generation, ServicesPMX.CAPABILITIES.binding(wolf).orElseThrow().generation(), "Binding changed");
        assertValueEqual(helper, 1, collar.getCount(), "Collar consumed");
        helper.succeed();
    }

    public static void collar_on_another_players_familiar_does_nothing(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        RecordingServerPlayer player = holdingCollar(helper, RecordingServerPlayer.create(helper, true), collar);
        ServerPlayer other = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), new Vec3(3.5D, 1.0D, 3.5D));
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, other, PET_POS);
        assertTrue(helper, useCollar(player, wolf), "Interaction was not consumed");
        assertTrue(helper, player.hasSystemMessage("message.primalmagickextended.familiar.not_yours"), "Player not told the familiar is not theirs");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).orElseThrow().isOwnedBy(other.getUUID()), "Binding stolen");
        assertValueEqual(helper, 1, collar.getCount(), "Collar consumed");
        helper.succeed();
    }

    public static void collar_converts_ocelot_into_ocelot_cat_familiar(GameTestHelper helper) {
        ItemStack collar = FamiliarTestUtils.collar();
        ServerPlayer player = holdingCollar(helper, collar);
        Ocelot ocelot = helper.spawnWithNoFreeWill(EntityType.OCELOT, PET_POS);
        assertTrue(helper, useCollar(player, ocelot), "Interaction was not consumed");
        assertTrue(helper, ocelot.isRemoved(), "Ocelot still present");
        assertTrue(helper, collar.isEmpty(), "Collar not consumed");
        Cat cat = convertedCat(helper);
        assertTrue(helper, cat.isTame() && cat.getOwnerReference() != null && cat.getOwnerReference().getUUID().equals(player.getUUID()), "Cat is not the player's pet");
        assertValueEqual(helper, CatVariantsPMX.OCELOT, cat.getVariant().unwrapKey().orElseThrow(), "Cat variant");
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(cat).map(b -> b.isOwnedBy(player.getUUID())).orElse(false), "Cat not bound as familiar");
        assertTrue(helper, FamiliarManager.findActive(player).map(m -> m == cat).orElse(false), "Cat is not the active familiar");
        helper.succeed();
    }

    public static void collar_names_unnamed_ocelot_cat_ocelot(GameTestHelper helper) {
        ServerPlayer player = holdingCollar(helper, FamiliarTestUtils.collar());
        Ocelot ocelot = helper.spawnWithNoFreeWill(EntityType.OCELOT, PET_POS);
        assertTrue(helper, useCollar(player, ocelot), "Interaction was not consumed");
        Cat cat = convertedCat(helper);
        assertTrue(helper, cat.getCustomName() != null, "Unnamed ocelot's cat has no name");
        assertValueEqual(helper, EntityType.OCELOT.getDescription().getString(), cat.getCustomName().getString(), "Unnamed ocelot's cat name");
        assertValueEqual(helper, EntityType.OCELOT.getDescription().getString(), cat.getDisplayName().getString(), "Unnamed ocelot's cat display name");
        assertFalse(helper, cat.isCustomNameVisible(), "Default name shows a floating nameplate");
        helper.succeed();
    }

    public static void collar_keeps_name_tag_name_when_converting_ocelot(GameTestHelper helper) {
        ServerPlayer player = holdingCollar(helper, FamiliarTestUtils.collar());
        Ocelot ocelot = helper.spawnWithNoFreeWill(EntityType.OCELOT, PET_POS);
        ocelot.setCustomName(net.minecraft.network.chat.Component.literal("Spots"));
        assertTrue(helper, useCollar(player, ocelot), "Interaction was not consumed");
        Cat cat = convertedCat(helper);
        assertValueEqual(helper, "Spots", cat.getCustomName().getString(), "Name-tagged ocelot's cat name");
        helper.succeed();
    }

    public static void collar_converts_leashed_ocelot_keeping_leash(GameTestHelper helper) {
        ServerPlayer player = holdingCollar(helper, FamiliarTestUtils.collar());
        Ocelot ocelot = helper.spawnWithNoFreeWill(EntityType.OCELOT, PET_POS);
        ocelot.setLeashedTo(player, true);
        assertTrue(helper, useCollar(player, ocelot), "Interaction was not consumed");
        Cat cat = convertedCat(helper);
        assertTrue(helper, cat.getLeashHolder() == player, "Cat is not leashed to the player");
        helper.succeed();
    }

    public static void client_side_collar_use_is_predicted_for_owned_pet(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        ServerPlayer other = makeMockServerPlayer(helper);
        Wolf wolf = FamiliarTestUtils.tamedWolf(helper, player, PET_POS);
        assertTrue(helper, FamiliarCandidates.looksOwnedOnClient(wolf, player), "Own tamed wolf not predicted as owned");
        assertFalse(helper, FamiliarCandidates.looksOwnedOnClient(wolf, other), "Another player's wolf predicted as owned");
        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, PET_POS);
        horse.setTamed(true);
        horse.setOwner(other);
        assertTrue(helper, FamiliarCandidates.looksOwnedOnClient(horse, player), "Tamed horse not predicted as owned, though its owner is not synced");
        Fox fox = helper.spawnWithNoFreeWill(EntityType.FOX, PET_POS);
        assertFalse(helper, FamiliarCandidates.looksOwnedOnClient(fox, player), "Untrusting fox predicted as owned");
        helper.succeed();
    }

    /** The single cat standing where the test ocelot was. */
    private static Cat convertedCat(GameTestHelper helper) {
        var cats = helper.getEntities(EntityType.CAT, new BlockPos(1, 1, 1), 2.0D);
        assertValueEqual(helper, 1, cats.size(), "Cats where the ocelot was");
        return cats.get(0);
    }

    public static void ocelot_cat_variant_is_registered(GameTestHelper helper) {
        var variant = helper.getLevel().registryAccess().lookupOrThrow(Registries.CAT_VARIANT).getOrThrow(CatVariantsPMX.OCELOT).value();
        assertValueEqual(helper, Identifier.withDefaultNamespace("textures/entity/cat/ocelot.png"), variant.adultAssetInfo().texturePath(), "Ocelot variant texture");
        assertTrue(helper, variant.spawnConditions().selectors().isEmpty(), "Ocelot variant can spawn naturally");
        helper.succeed();
    }
}
