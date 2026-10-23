package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.mojang.logging.LogUtils;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.List;

/**
 * Helpers shared by the familiar tests.
 */
public class FamiliarTestUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** Saves an entity without its id, the same way the familiar manager snapshots one. */
    public static CompoundTag snapshot(Entity entity) {
        try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(LOGGER)) {
            TagValueOutput out = TagValueOutput.createWithContext(problems, entity.registryAccess());
            entity.saveWithoutId(out);
            return out.buildResult();
        }
    }

    /** Loads saved data into an existing entity. */
    public static void load(Entity entity, CompoundTag tag) {
        try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(LOGGER)) {
            ValueInput in = TagValueInput.create(problems, entity.registryAccess(), tag);
            entity.load(in);
        }
    }

    /** Makes a fox trust the player by loading its "Trusted" list, since the vanilla mutator is private. */
    public static void trust(Fox fox, ServerPlayer player) {
        CompoundTag tag = snapshot(fox);
        tag.store("Trusted", EntityReference.<LivingEntity>codec().listOf(), List.of(EntityReference.of(player)));
        load(fox, tag);
    }

    /** Places a mock player at a position relative to the test structure, facing south. */
    public static ServerPlayer placePlayer(GameTestHelper helper, ServerPlayer player, Vec3 relativePos) {
        Vec3 abs = helper.absoluteVec(relativePos);
        player.absSnapTo(abs.x, abs.y, abs.z, 0.0F, 0.0F);
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
}
