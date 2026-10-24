package com.claviger.primalmagickextended.test.util;

import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A mock server player, set up like the ones made by {@link AbstractBaseTestPMX}, that records every system and
 * overlay message sent to it so tests can confirm which feedback the player was given. It also records the
 * type of every custom payload packet sent to it, such as the mod's own packets.
 */
public class RecordingServerPlayerPMX extends ServerPlayer {
    private final List<Component> systemMessages = new ArrayList<>();
    private final List<Component> overlayMessages = new ArrayList<>();
    private final List<CustomPacketPayload.Type<?>> sentPayloadTypes = new ArrayList<>();

    private RecordingServerPlayerPMX(ServerLevel level, CommonListenerCookie cookie) {
        super(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());
    }

    /**
     * Creates a recording mock player with a working mock connection, optionally adding it to the test level.
     */
    public static RecordingServerPlayerPMX create(GameTestHelper helper, boolean joinLevel) {
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        RecordingServerPlayerPMX player = new RecordingServerPlayerPMX(helper.getLevel(), cookie);
        Connector.connect(helper, player, cookie, joinLevel, new RecordingConnection(player));
        return player;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public void sendSystemMessage(Component message, boolean overlay) {
        (overlay ? this.overlayMessages : this.systemMessages).add(message);
        super.sendSystemMessage(message, overlay);
    }

    public boolean hasSystemMessage(String translationKey) {
        return hasKey(this.systemMessages, translationKey);
    }

    public boolean hasOverlayMessage(String translationKey) {
        return hasKey(this.overlayMessages, translationKey);
    }

    /** How many custom payload packets of the given type have been sent to this player. */
    public long sentPayloadCount(CustomPacketPayload.Type<?> type) {
        return this.sentPayloadTypes.stream().filter(type::equals).count();
    }

    public void clearSentPayloads() {
        this.sentPayloadTypes.clear();
    }

    private static boolean hasKey(List<Component> messages, String translationKey) {
        return messages.stream().anyMatch(m -> m.getContents() instanceof TranslatableContents contents && contents.getKey().equals(translationKey));
    }

    /**
     * Gives access to the protected mock connection setup in the base test class.
     */
    private static class Connector extends AbstractBaseTestPMX {
        static void connect(GameTestHelper helper, ServerPlayer player, CommonListenerCookie cookie, boolean joinLevel, Connection connection) {
            attachMockConnection(helper, player, cookie, joinLevel, connection);
        }
    }

    /** A mock connection that records the type of each custom payload packet sent through it before sending it on. */
    private static class RecordingConnection extends Connection {
        private final RecordingServerPlayerPMX player;

        RecordingConnection(RecordingServerPlayerPMX player) {
            super(PacketFlow.SERVERBOUND);
            this.player = player;
        }

        @Override
        public void send(Packet<?> packet, @Nullable PacketSendListener listener, boolean flush) {
            if (packet instanceof ClientboundCustomPayloadPacket customPacket) {
                this.player.sentPayloadTypes.add(customPacket.payload().type());
            }
            super.send(packet, listener, flush);
        }
    }
}
