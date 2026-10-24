package com.claviger.primalmagickextended.test;

import com.mojang.authlib.GameProfile;
import com.verdantartifice.primalmagick.platform.Services;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class of the mod's game tests, providing static assertion helpers and mock server players. Primal Magick
 * 6.0.7's own base class only has instance helpers, while this mod's test functions are static, so the mod keeps its
 * own copy, which sets up mock players the way Primal Magick does.
 */
public abstract class AbstractBaseTestPMX {
    protected static ServerPlayer makeMockServerPlayer(GameTestHelper helper) {
        return makeMockServerPlayer(helper, false);
    }

    protected static ServerPlayer makeMockServerPlayer(GameTestHelper helper, boolean joinLevel) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation()) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
        attachMockConnection(helper, player, cookie, joinLevel);
        return player;
    }

    /**
     * Gives a mock server player a working network connection, optionally adding it to the test level. Tests that
     * need a custom {@link ServerPlayer} subclass can construct it themselves and pass it here.
     */
    protected static void attachMockConnection(GameTestHelper helper, ServerPlayer player, CommonListenerCookie cookie, boolean joinLevel) {
        attachMockConnection(helper, player, cookie, joinLevel, new Connection(PacketFlow.SERVERBOUND));
    }

    /**
     * As {@link #attachMockConnection(GameTestHelper, ServerPlayer, CommonListenerCookie, boolean)}, over a given
     * serverbound connection, such as one that records what is sent through it.
     */
    @SuppressWarnings("deprecation") // The decorator without a connection type; see registryBuffer
    protected static void attachMockConnection(GameTestHelper helper, ServerPlayer player, CommonListenerCookie cookie, boolean joinLevel, Connection connection) {
        MinecraftServer server = helper.getLevel().getServer();
        new EmbeddedChannel(new ChannelHandler[]{connection});
        ServerGamePacketListenerImpl listener = new ServerGamePacketListenerImpl(server, connection, player, cookie);
        ProtocolInfo<ServerGamePacketListener> info = GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(server.registryAccess()));
        connection.setupInboundProtocol(info, listener);
        Services.TEST.configureMockConnection(connection);
        if (joinLevel) {
            server.getPlayerList().placeNewPlayer(connection, player, cookie);
        }
    }

    /**
     * Wraps a buffer, with registry access, for round-tripping stream codecs. Common code cannot name NeoForge's
     * connection type, so this uses the constructor without one, which NeoForge deprecates; it gives the buffer the
     * connection type {@code OTHER}.
     */
    @SuppressWarnings("deprecation")
    protected static RegistryFriendlyByteBuf registryBuffer(ByteBuf source, RegistryAccess registryAccess) {
        return new RegistryFriendlyByteBuf(source, registryAccess);
    }

    protected static void assertTrue(GameTestHelper helper, boolean condition, String failureMessage) {
        helper.assertTrue(condition, failureMessage);
    }

    protected static void assertFalse(GameTestHelper helper, boolean condition, String failureMessage) {
        helper.assertFalse(condition, failureMessage);
    }

    /**
     * Asserts that two values are equal, expected value first. Unlike {@link GameTestHelper#assertValueEqual}, which
     * takes the actual value first, this also accepts a null on either side.
     */
    protected static <T> void assertValueEqual(GameTestHelper helper, T expected, T actual, String failureMessage) {
        if (!Objects.equals(expected, actual)) {
            throw new GameTestAssertException("Expected " + failureMessage + " to be " + expected + ", but was " + actual);
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T assertInstanceOf(GameTestHelper helper, Object obj, Class<T> clazz, String failureMessage) {
        assertTrue(helper, clazz.isInstance(obj), failureMessage);
        return (T)obj;
    }
}
