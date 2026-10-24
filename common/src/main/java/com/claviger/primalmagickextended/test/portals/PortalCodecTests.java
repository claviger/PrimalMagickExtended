package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.network.packets.portals.SyncPortalsPacket;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Tests that a {@link CustomPortal} survives its persistence codec and its network stream codec unchanged.
 */
public class PortalCodecTests extends AbstractBaseTestPMX {
    /** The persisted field names, which saved worlds depend on. */
    private static final Set<String> PERSISTED_KEYS = Set.of("frameId", "dimensionId", "color", "spawnPos", "portalBlocks",
            "offsetX", "offsetZ", "creatorId", "hasteRunes", "gateRunes", "weakEnhancerRunes", "strongEnhancerRunes",
            "infinityRunes");

    private static CustomPortal sample() {
        List<BlockPos> blocks = new ArrayList<>(List.of(new BlockPos(1, 65, 3), new BlockPos(1, 66, 3), new BlockPos(2, 65, 3)));
        return new CustomPortal("minecraft:stone", "minecraft:the_nether", PortalColor.CYAN.mapColor(), new BlockPos(1, 65, 3),
                blocks, 0.25F, 0.75F, UUID.randomUUID(), 1, 2, 3, 4, 5);
    }

    /** Asserts that every persisted and synced field of {@code actual} matches {@code expected}. */
    static void assertSame(GameTestHelper helper, CustomPortal expected, CustomPortal actual) {
        assertValueEqual(helper, expected.getFrameId(), actual.getFrameId(), "frameId");
        assertValueEqual(helper, expected.getDimensionId(), actual.getDimensionId(), "dimensionId");
        assertTrue(helper, expected.getColor() == actual.getColor(), "color");
        assertValueEqual(helper, expected.getColorId(), actual.getColorId(), "colorId");
        assertValueEqual(helper, expected.getSpawnPos(), actual.getSpawnPos(), "spawnPos");
        assertValueEqual(helper, expected.getPortalBlocks(), actual.getPortalBlocks(), "portalBlocks");
        assertValueEqual(helper, expected.getOffsetX(), actual.getOffsetX(), "offsetX");
        assertValueEqual(helper, expected.getOffsetZ(), actual.getOffsetZ(), "offsetZ");
        assertValueEqual(helper, expected.getCreatorId(), actual.getCreatorId(), "creatorId");
        assertValueEqual(helper, expected.getHasteRunes(), actual.getHasteRunes(), "hasteRunes");
        assertValueEqual(helper, expected.getGateRunes(), actual.getGateRunes(), "gateRunes");
        assertValueEqual(helper, expected.getWeakEnhancerRunes(), actual.getWeakEnhancerRunes(), "weakEnhancerRunes");
        assertValueEqual(helper, expected.getStrongEnhancerRunes(), actual.getStrongEnhancerRunes(), "strongEnhancerRunes");
        assertValueEqual(helper, expected.getInfinityRunes(), actual.getInfinityRunes(), "infinityRunes");
    }

    public static void custom_portal_round_trips_through_codec(GameTestHelper helper) {
        CustomPortal original = sample();
        Tag tag = CustomPortal.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
        CompoundTag compound = assertInstanceOf(helper, tag, CompoundTag.class, "Encoded portal is not a compound tag");
        assertValueEqual(helper, PERSISTED_KEYS, compound.getAllKeys(), "persisted keys");
        CustomPortal decoded = CustomPortal.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        assertSame(helper, original, decoded);
        helper.succeed();
    }

    public static void custom_portal_round_trips_through_stream_codec(GameTestHelper helper) {
        CustomPortal original = sample();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        try {
            CustomPortal.STREAM_CODEC.encode(buf, original);
            CustomPortal decoded = CustomPortal.STREAM_CODEC.decode(buf);
            assertSame(helper, original, decoded);
            assertValueEqual(helper, 0, buf.readableBytes(), "unread bytes");
        } finally {
            buf.release();
        }
        helper.succeed();
    }

    /**
     * A portal-block count or portal count the buffer cannot hold, or a negative one, must fail with a decoder
     * exception before anything is allocated, since a client can send a sync packet to the server.
     */
    public static void stream_decoding_rejects_impossible_counts(GameTestHelper helper) {
        for (int count : new int[] { Integer.MAX_VALUE, 1_000_000, -1 }) {
            assertDecodeFails(helper, "portal blocks " + count, buf -> {
                buf.writeUtf("minecraft:stone");
                buf.writeUtf("minecraft:overworld");
                buf.writeVarInt(PortalColor.CYAN.mapColor().id);
                buf.writeBlockPos(BlockPos.ZERO);
                buf.writeVarInt(count);
                buf.writeLong(0L);
            }, CustomPortal.STREAM_CODEC::decode);
            assertDecodeFails(helper, "portals " + count, buf -> {
                buf.writeVarInt(count);
                buf.writeLong(0L);
            }, buf -> SyncPortalsPacket.STREAM_CODEC.decode(registryBuffer(buf, helper.getLevel().registryAccess())));
        }
        helper.succeed();
    }

    private static void assertDecodeFails(GameTestHelper helper, String label, Consumer<FriendlyByteBuf> writer, Consumer<FriendlyByteBuf> decoder) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        try {
            writer.accept(buf);
            boolean rejected = false;
            try {
                decoder.accept(buf);
            } catch (DecoderException e) {
                rejected = true;
            }
            assertTrue(helper, rejected, "Decoding did not reject the count for " + label);
        } finally {
            buf.release();
        }
    }
}
