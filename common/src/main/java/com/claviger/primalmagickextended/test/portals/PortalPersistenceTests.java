package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.network.packets.portals.SyncPortalsPacket;
import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.common.portals.PortalRegistry;
import com.claviger.primalmagickextended.common.portals.PortalSavedData;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tests that the portal registry survives a save and reload with its links recomputed, that the client sync packet
 * carries portals unchanged, and that the server-side registry lookup resolves to the saved data.
 */
public class PortalPersistenceTests extends AbstractBaseTest {
    private static final String OVERWORLD = "minecraft:overworld";

    private static CustomPortal portal(int x, int z) {
        BlockPos pos = new BlockPos(x, 64, z);
        return new CustomPortal("minecraft:stone", OVERWORLD, PortalColor.GREEN.mapColor(), pos,
                new ArrayList<>(List.of(pos, pos.above())), 0.5F, 0.5F, UUID.randomUUID(), 0, 0, 1, 0, 0);
    }

    public static void saved_data_relinks_portals_on_load(GameTestHelper helper) {
        CustomPortal a = portal(0, 0);
        CustomPortal b = portal(10, 0);
        PortalSavedData original = new PortalSavedData();
        original.getRegistry().register(a);
        original.getRegistry().register(b);

        Tag tag = PortalSavedData.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
        PortalSavedData loaded = PortalSavedData.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        PortalRegistry registry = loaded.getRegistry();

        assertValueEqual(helper, 2, registry.getPortals().size(), "loaded portal count");
        CustomPortal loadedA = registry.getPortals().get(0);
        CustomPortal loadedB = registry.getPortals().get(1);
        assertTrue(helper, loadedA != a && loadedB != b, "Loaded portals should be new instances");
        assertTrue(helper, loadedA.getLinked() == loadedB, "First loaded portal should link to the second");
        assertTrue(helper, loadedB.getLinked() == loadedA, "Second loaded portal should link to the first");
        for (BlockPos pos : a.getPortalBlocks()) {
            assertTrue(helper, registry.getPortalFromPos(OVERWORLD, pos) == loadedA, "Position lookup for the first portal at " + pos);
        }
        for (BlockPos pos : b.getPortalBlocks()) {
            assertTrue(helper, registry.getPortalFromPos(OVERWORLD, pos) == loadedB, "Position lookup for the second portal at " + pos);
        }
        helper.succeed();
    }

    public static void sync_packet_round_trips_portals(GameTestHelper helper) {
        CustomPortal a = portal(0, 0);
        CustomPortal b = new CustomPortal("minecraft:obsidian", "minecraft:the_nether", PortalColor.ORANGE.mapColor(),
                new BlockPos(-5, 70, 12), new ArrayList<>(List.of(new BlockPos(-5, 70, 12))), 0.25F, 0.75F,
                UUID.randomUUID(), 1, 2, 3, 4, 5);
        SyncPortalsPacket packet = new SyncPortalsPacket(List.of(a, b));

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            SyncPortalsPacket.STREAM_CODEC.encode(buf, packet);
            SyncPortalsPacket decoded = SyncPortalsPacket.STREAM_CODEC.decode(buf);
            assertValueEqual(helper, 0, buf.readableBytes(), "unread bytes");
            assertValueEqual(helper, 2, decoded.getPortals().size(), "decoded portal count");
            PortalCodecTests.assertSame(helper, a, decoded.getPortals().get(0));
            PortalCodecTests.assertSame(helper, b, decoded.getPortals().get(1));
        } finally {
            buf.release();
        }
        helper.succeed();
    }

    public static void portal_data_resolves_server_registry(GameTestHelper helper) {
        PortalRegistry fromLevel = PortalData.get(helper.getLevel());
        PortalRegistry fromSavedData = PortalSavedData.get(helper.getLevel().getServer()).getRegistry();
        assertTrue(helper, fromLevel == fromSavedData, "PortalData should resolve to the saved server registry");
        assertValueEqual(helper, ResourceUtilsPMX.loc("portals"), PortalSavedData.TYPE.id(), "saved data id");
        assertTrue(helper, PortalSavedData.TYPE.codec() == PortalSavedData.CODEC, "Saved data type should use the portal saved data codec");
        helper.succeed();
    }
}
