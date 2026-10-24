package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.wands.IHasWandComponents;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.wands.WandCap;
import com.verdantartifice.primalmagick.common.wands.WandCore;
import com.verdantartifice.primalmagick.common.wands.WandGem;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Helpers shared by the familiar tests.
 */
public class FamiliarTestUtils {
    /** Saves an entity without its id, the same way the familiar manager snapshots one. */
    public static CompoundTag snapshot(Entity entity) {
        return entity.saveWithoutId(new CompoundTag());
    }

    /** Loads saved data into an existing entity. */
    public static void load(Entity entity, CompoundTag tag) {
        entity.load(tag);
    }

    /** Makes a fox trust the player by loading its "Trusted" list, since the vanilla mutator is not public. */
    public static void trust(Fox fox, ServerPlayer player) {
        CompoundTag tag = snapshot(fox);
        ListTag trusted = new ListTag();
        trusted.add(NbtUtils.createUUID(player.getUUID()));
        tag.put("Trusted", trusted);
        load(fox, tag);
    }

    /** Sets a wolf's collar colour by loading its "CollarColor" byte, since the vanilla mutator is private. */
    public static void setCollarColor(Wolf wolf, DyeColor color) {
        CompoundTag tag = snapshot(wolf);
        tag.putByte("CollarColor", (byte)color.getId());
        load(wolf, tag);
    }

    /** Places a mock player at a position relative to the test structure, facing south. */
    public static ServerPlayer placePlayer(GameTestHelper helper, ServerPlayer player, Vec3 relativePos) {
        Vec3 abs = helper.absoluteVec(relativePos);
        player.absMoveTo(abs.x, abs.y, abs.z, 0.0F, 0.0F);
        return player;
    }

    /** Spawns a wolf at the given relative position, tamed by and owned by the player. */
    public static Wolf tamedWolf(GameTestHelper helper, ServerPlayer player, Vec3 relativePos) {
        Wolf wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, relativePos);
        wolf.tame(player);
        return wolf;
    }

    /** A single Familiar Collar. */
    public static ItemStack collar() {
        return new ItemStack(ItemsPMX.FAMILIAR_COLLAR.get());
    }

    /** Binds a freshly tamed wolf to the player through the manager and returns it. */
    public static Wolf bindWolf(GameTestHelper helper, ServerPlayer player, Vec3 relativePos) {
        Wolf wolf = tamedWolf(helper, player, relativePos);
        if (!FamiliarManager.bind(player, wolf, collar())) {
            helper.fail("Failed to bind test wolf");
        }
        return wolf;
    }

    /** A modular wand or staff with the given components. */
    public static ItemStack makeModularCaster(Item item, WandCore core, WandCap cap, WandGem gem) {
        return IHasWandComponents.setWandComponents(item.getDefaultInstance(), core, cap, gem);
    }

    /**
     * A stack to stand in for the item a spell is cast from. It must not be empty, because payloads that enchant a
     * copy of the source stack would otherwise modify the shared empty stack.
     */
    public static ItemStack scrollSource() {
        return new ItemStack(ItemsPM.SPELL_SCROLL_FILLED.get());
    }

    /** Runs a spell's payload directly against a hit, as if cast from a scroll. */
    public static void executePayload(GameTestHelper helper, SpellPackage spell, HitResult hit, LivingEntity caster) {
        spell.payload().getComponent().execute(hit, null, spell, helper.getLevel(), caster, scrollSource(), null);
    }
}
