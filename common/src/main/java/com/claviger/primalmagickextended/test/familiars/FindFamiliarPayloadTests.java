package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.claviger.primalmagickextended.common.spells.payloads.FindFamiliarSpellPayload;
import com.claviger.primalmagickextended.common.spells.payloads.SpellPayloadsPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.entities.EntityTypesPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.wands.AbstractWandItem;
import com.verdantartifice.primalmagick.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellPropertiesPM;
import com.verdantartifice.primalmagick.common.spells.mods.MineSpellMod;
import com.verdantartifice.primalmagick.common.spells.vehicles.SelfSpellVehicle;
import com.verdantartifice.primalmagick.common.stats.ExpertiseManager;
import com.verdantartifice.primalmagick.common.wands.WandCap;
import com.verdantartifice.primalmagick.common.wands.WandCore;
import com.verdantartifice.primalmagick.common.wands.WandGem;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Tests for the Find Familiar spell payload.
 */
public class FindFamiliarPayloadTests extends AbstractBaseTestPMX {
    private static final Vec3 PLAYER_POS = new Vec3(2.5D, 2.0D, 2.5D);
    private static final Vec3 PET_POS = new Vec3(1.5D, 2.0D, 1.5D);

    private static ServerPlayer caster(GameTestHelper helper) {
        ServerPlayer player = FamiliarTestUtils.placePlayer(helper, makeMockServerPlayer(helper, true), PLAYER_POS);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static SpellPackage selfSpell() {
        return SpellPackage.builder().name("Find Familiar")
                .vehicle().type(SelfSpellVehicle.INSTANCE).end()
                .payload().type(FindFamiliarSpellPayload.INSTANCE).end()
                .build();
    }

    private static void castOnSelf(GameTestHelper helper, ServerPlayer caster) {
        FamiliarTestUtils.executePayload(helper, selfSpell(), new EntityHitResult(caster, caster.getEyePosition()), caster);
    }

    public static void find_familiar_payload_is_registered(GameTestHelper helper) {
        assertValueEqual(helper, ResourceUtilsPMX.loc("find_familiar"), SpellPayloadsPMX.FIND_FAMILIAR.get().id(), "Payload type id");
        assertValueEqual(helper, Sources.BLOOD, FindFamiliarSpellPayload.INSTANCE.getSource(), "Payload source");
        var cost = selfSpell().getManaCost();
        // 20 Blood mana in centimana; the Self vehicle adds nothing
        assertValueEqual(helper, 2000, cost.getAmount(Sources.BLOOD), "Spell cost in Blood centimana");
        assertValueEqual(helper, 1, cost.getSources().size(), "Spell cost sources (should be Blood only)");
        helper.succeed();
    }

    public static void find_familiar_payload_toggles_for_caster(GameTestHelper helper) {
        ServerPlayer caster = caster(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, caster, PET_POS);
        castOnSelf(helper, caster);
        assertTrue(helper, wolf.isRemoved(), "Cast did not dismiss the familiar");
        helper.runAfterDelay(42, () -> {
            castOnSelf(helper, caster);
            helper.succeedWhen(() -> assertValueEqual(helper, 1, helper.getEntities(EntityType.WOLF).size(), "Wolves after second cast"));
        });
    }

    public static void find_familiar_payload_ignores_burst_points(GameTestHelper helper) {
        ServerPlayer caster = caster(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, caster, PET_POS);
        FindFamiliarSpellPayload.INSTANCE.execute(new EntityHitResult(caster, caster.getEyePosition()), caster.position(), selfSpell(), helper.getLevel(), caster, null, null);
        assertFalse(helper, wolf.isRemoved(), "Burst execution toggled the familiar");
        helper.succeed();
    }

    public static void find_familiar_payload_with_no_familiar_does_nothing(GameTestHelper helper) {
        ServerPlayer caster = caster(helper);
        castOnSelf(helper, caster);
        assertTrue(helper, FamiliarManager.findActive(caster).isEmpty(), "Something was summoned");
        helper.succeed();
    }

    public static void find_familiar_payload_hidden_without_research_and_shown_with_it(GameTestHelper helper) {
        ServerPlayer player = makeMockServerPlayer(helper);
        assertFalse(helper, SpellManager.getPayloadTypes(player).contains(SpellPayloadsPMX.FIND_FAMILIAR.get()), "Payload offered without research");
        ResearchManager.forceGrantWithAllParents(player, ResearchEntriesPMX.FIND_FAMILIAR);
        assertTrue(helper, SpellManager.getPayloadTypes(player).contains(SpellPayloadsPMX.FIND_FAMILIAR.get()), "Payload not offered with research");
        helper.succeed();
    }

    public static void find_familiar_wand_cast_deducts_mana_and_dismisses(GameTestHelper helper) {
        ServerPlayer player = caster(helper);
        ResearchManager.forceGrantWithAllParents(player, ResearchEntriesPMX.FIND_FAMILIAR);
        int expertiseBefore = ExpertiseManager.getValue(player, ResearchDisciplines.SORCERY).orElseThrow();
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, player, PET_POS);

        ItemStack wandStack = FamiliarTestUtils.makeModularCaster(ItemsPM.MODULAR_WAND.get(), WandCore.HEARTWOOD, WandCap.IRON, WandGem.APPRENTICE);
        AbstractWandItem wand = assertInstanceOf(helper, wandStack.getItem(), AbstractWandItem.class, "Wand stack is not a wand");
        Sources.getAll().forEach(s -> wand.addMana(wandStack, s, wand.getMaxMana(wandStack, s)));
        player.setItemInHand(InteractionHand.MAIN_HAND, wandStack);
        assertTrue(helper, wand.addSpell(wandStack, selfSpell()), "Spell was not added to the wand");
        assertTrue(helper, wand.setActiveSpellIndex(wandStack, 0), "Spell was not set to active");

        int maxBlood = wand.getMaxMana(wandStack, Sources.BLOOD);
        var result = wand.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertTrue(helper, result.getResult().consumesAction(), "Wand use result was not a success");
        int expectedBlood = maxBlood - wand.getModifiedCost(wandStack, player, Sources.BLOOD, 2000, helper.getLevel().registryAccess());
        assertValueEqual(helper, expectedBlood, wand.getMana(wandStack, Sources.BLOOD), "Blood mana after cast");
        assertTrue(helper, wolf.isRemoved(), "Wand cast did not dismiss the familiar");
        int expertiseAfter = ExpertiseManager.getValue(player, ResearchDisciplines.SORCERY).orElseThrow();
        assertValueEqual(helper, 20, expertiseAfter - expertiseBefore, "Sorcery expertise awarded by the cast");
        helper.succeed();
    }

    public static void find_familiar_mine_does_not_toggle(GameTestHelper helper) {
        ServerPlayer caster = caster(helper);
        Wolf wolf = FamiliarTestUtils.bindWolf(helper, caster, PET_POS);
        SpellPackage mineSpell = SpellPackage.builder().name("Find Familiar Mine")
                .vehicle().type(SelfSpellVehicle.INSTANCE).end()
                .payload().type(FindFamiliarSpellPayload.INSTANCE).end()
                .primaryMod().type(MineSpellMod.INSTANCE).with(SpellPropertiesPM.NON_ZERO_DURATION.get(), 1).end()
                .build();
        // A pig on a mine away from the caster and the familiar triggers it once it arms
        Vec3 minePoint = helper.absoluteVec(new Vec3(3.5D, 2.0D, 0.5D));
        helper.spawnWithNoFreeWill(EntityType.PIG, new Vec3(3.5D, 2.0D, 0.5D));
        BlockHitResult hit = new BlockHitResult(minePoint, Direction.UP, BlockPos.containing(minePoint), false);
        SpellManager.executeSpellPayload(mineSpell, hit, helper.getLevel(), caster, FamiliarTestUtils.scrollSource(), true, null);
        BlockPos minePos = new BlockPos(3, 2, 0);
        assertValueEqual(helper, 1, helper.getEntities(EntityTypesPM.SPELL_MINE.get(), minePos, 1.0D).size(), "Mines after cast");
        assertFalse(helper, wolf.isRemoved(), "Placing the mine toggled the familiar");
        helper.succeedWhen(() -> {
            assertValueEqual(helper, 0, helper.getEntities(EntityTypesPM.SPELL_MINE.get(), minePos, 1.0D).size(), "Mines after the pig stepped on it");
            assertFalse(helper, wolf.isRemoved(), "Triggered mine toggled the familiar");
        });
    }

    public static void find_familiar_spell_package_round_trips_through_codecs(GameTestHelper helper) {
        RegistryAccess registryAccess = helper.getLevel().registryAccess();
        SpellPackage spell = selfSpell();

        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
        Tag encoded = SpellPackage.codec().encodeStart(ops, spell).getOrThrow();
        SpellPackage decoded = SpellPackage.codec().parse(ops, encoded).getOrThrow();
        assertTrue(helper, decoded.payload().getComponent() == FindFamiliarSpellPayload.INSTANCE, "Codec round trip lost the payload instance");

        RegistryFriendlyByteBuf buf = registryBuffer(Unpooled.buffer(), registryAccess);
        try {
            SpellPackage.streamCodec().encode(buf, spell);
            SpellPackage streamed = SpellPackage.streamCodec().decode(buf);
            assertTrue(helper, streamed.payload().getComponent() == FindFamiliarSpellPayload.INSTANCE, "Stream codec round trip lost the payload instance");
        } finally {
            buf.release();
        }
        helper.succeed();
    }
}
