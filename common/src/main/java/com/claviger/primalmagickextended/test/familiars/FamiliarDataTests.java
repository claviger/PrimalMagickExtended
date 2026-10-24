package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.capabilities.PlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import com.claviger.primalmagickextended.common.network.packets.familiars.SyncFamiliarPacket;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import net.minecraft.core.RegistryAccess;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Tests for the familiar data model: player attachment persistence and entity bindings.
 */
public class FamiliarDataTests extends AbstractBaseTestPMX {
    public static void player_familiar_round_trips_through_codec(GameTestHelper helper) {
        var wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, new Vec3(1.5D, 2.0D, 1.5D));
        CompoundTag data = FamiliarTestUtils.snapshot(wolf);
        UUID generation = UUID.randomUUID();
        UUID active = UUID.randomUUID();
        PlayerFamiliar original = new PlayerFamiliar();
        original.setStored(Optional.of(new StoredFamiliar(EntityType.WOLF, data, generation, Optional.of(active))));
        original.setFamiliarNearby(true);

        // Round trip through the same NBT path the attachment uses
        RegistryAccess registryAccess = helper.getLevel().registryAccess();
        CompoundTag tag = original.serializeNBT(registryAccess);
        PlayerFamiliar decoded = new PlayerFamiliar();
        decoded.deserializeNBT(registryAccess, tag);

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
        var wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, new Vec3(1.5D, 2.0D, 1.5D));
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Fresh wolf has a binding");
        FamiliarBinding binding = new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID());
        ServicesPMX.CAPABILITIES.setBinding(wolf, binding);
        assertValueEqual(helper, Optional.of(binding), ServicesPMX.CAPABILITIES.binding(wolf), "Binding after set");
        ServicesPMX.CAPABILITIES.clearBinding(wolf);
        assertTrue(helper, ServicesPMX.CAPABILITIES.binding(wolf).isEmpty(), "Binding after clear");
        helper.succeed();
    }

    public static void entity_binding_survives_entity_save_and_load(GameTestHelper helper) {
        var wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, new Vec3(1.5D, 2.0D, 1.5D));
        FamiliarBinding binding = new FamiliarBinding(UUID.randomUUID(), UUID.randomUUID());
        ServicesPMX.CAPABILITIES.setBinding(wolf, binding);
        CompoundTag tag = FamiliarTestUtils.snapshot(wolf);
        // The copy is never added to the level, so it cannot clash with the original's UUID
        var copy = EntityType.WOLF.create(helper.getLevel());
        if (copy == null) {
            helper.fail("Could not create the wolf copy");
            return;
        }
        FamiliarTestUtils.load(copy, tag);
        assertValueEqual(helper, Optional.of(binding), ServicesPMX.CAPABILITIES.binding(copy), "Binding after NBT round trip");
        copy.discard();
        helper.succeed();
    }

    /**
     * The client view of the familiar data travels in the mod's own packet on 1.21.1, which has no attachment sync;
     * it must carry both booleans, and applying it must set them on the client copy.
     */
    public static void sync_familiar_packet_round_trips_through_stream_codec(GameTestHelper helper) {
        RegistryFriendlyByteBuf buf = registryBuffer(Unpooled.buffer(), helper.getLevel().registryAccess());
        SyncFamiliarPacket decoded;
        try {
            SyncFamiliarPacket.STREAM_CODEC.encode(buf, new SyncFamiliarPacket(true, false));
            decoded = SyncFamiliarPacket.STREAM_CODEC.decode(buf);
        } finally {
            buf.release();
        }
        assertTrue(helper, decoded.hasFamiliar(), "Decoded has-familiar flag");
        assertFalse(helper, decoded.isFamiliarNearby(), "Decoded nearby flag");
        PlayerFamiliar client = new PlayerFamiliar();
        client.applyClientView(decoded.hasFamiliar(), decoded.isFamiliarNearby());
        assertTrue(helper, client.hasFamiliar(), "Client view has no familiar after the sync");
        assertFalse(helper, client.isFamiliarNearby(), "Client view has a nearby familiar after the sync");
        helper.succeed();
    }
}
