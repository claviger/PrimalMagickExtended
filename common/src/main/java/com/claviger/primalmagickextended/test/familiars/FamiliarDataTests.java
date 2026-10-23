package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.capabilities.PlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.mojang.logging.LogUtils;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

/**
 * Tests for the familiar data model: player attachment persistence and entity bindings.
 */
public class FamiliarDataTests extends AbstractBaseTest {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void player_familiar_round_trips_through_codec(GameTestHelper helper) {
        var wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, new Vec3(1.5D, 1.0D, 1.5D));
        CompoundTag data = FamiliarTestUtils.snapshot(wolf);
        UUID generation = UUID.randomUUID();
        UUID active = UUID.randomUUID();
        PlayerFamiliar original = new PlayerFamiliar();
        original.setStored(Optional.of(new StoredFamiliar(EntityType.WOLF, data, generation, Optional.of(active))));
        original.setFamiliarNearby(true);

        // Round trip through the same value input/output path the attachment uses
        RegistryAccess registryAccess = helper.getLevel().registryAccess();
        CompoundTag tag;
        try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(LOGGER)) {
            TagValueOutput out = TagValueOutput.createWithContext(problems, registryAccess);
            original.serialize(out);
            tag = out.buildResult();
        }
        PlayerFamiliar decoded = new PlayerFamiliar();
        try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(LOGGER)) {
            decoded.deserialize(TagValueInput.create(problems, registryAccess, tag));
        }

        assertFalse(helper, decoded.isFamiliarNearby(), "Transient nearby flag was persisted");
        StoredFamiliar stored = decoded.getStored().orElseThrow();
        assertValueEqual(helper, EntityType.WOLF, stored.type(), "Decoded entity type");
        assertValueEqual(helper, data, stored.entityData(), "Decoded entity data");
        assertValueEqual(helper, generation, stored.generation(), "Decoded generation");
        assertValueEqual(helper, Optional.of(active), stored.activeEntityId(), "Decoded active entity id");
        helper.succeed();
    }

    public static void player_familiar_attachment_is_available_and_empty_by_default(GameTestHelper helper) {
        var player = makeMockServerPlayer(helper);
        IPlayerFamiliar familiar = ServicesPMX.CAPABILITIES.familiar(player).orElseThrow();
        assertFalse(helper, familiar.hasFamiliar(), "New player already has a familiar");
        assertFalse(helper, familiar.isFamiliarNearby(), "New player already has a nearby familiar");
        helper.succeed();
    }

    public static void entity_binding_can_be_set_read_and_cleared(GameTestHelper helper) {
        var wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, new Vec3(1.5D, 1.0D, 1.5D));
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Fresh wolf has a binding");
        FamiliarBinding binding = new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID());
        ServicesPMX.CAPABILITIES.setBinding(wolf, binding);
        assertValueEqual(helper, Optional.of(binding), ServicesPMX.CAPABILITIES.binding(wolf), "Binding after set");
        ServicesPMX.CAPABILITIES.clearBinding(wolf);
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Binding after clear");
        helper.succeed();
    }

    public static void entity_binding_survives_entity_save_and_load(GameTestHelper helper) {
        var wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, new Vec3(1.5D, 1.0D, 1.5D));
        FamiliarBinding binding = new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID());
        ServicesPMX.CAPABILITIES.setBinding(wolf, binding);
        CompoundTag tag = FamiliarTestUtils.snapshot(wolf);
        // The copy is never added to the level, so it cannot clash with the original's UUID
        var copy = EntityType.WOLF.create(helper.getLevel(), EntitySpawnReason.LOAD);
        if (copy == null) {
            helper.fail("Could not create the wolf copy");
            return;
        }
        FamiliarTestUtils.load(copy, tag);
        assertValueEqual(helper, Optional.of(binding), ServicesPMX.CAPABILITIES.binding(copy), "Binding after NBT round trip");
        copy.discard();
        helper.succeed();
    }
}
