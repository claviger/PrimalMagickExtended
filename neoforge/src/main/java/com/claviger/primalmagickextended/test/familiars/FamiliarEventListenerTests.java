package com.claviger.primalmagickextended.test.familiars;

import com.claviger.primalmagickextended.common.events.FamiliarEventListeners;
import com.claviger.primalmagickextended.common.network.packets.familiars.SyncFamiliarPacket;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import com.claviger.primalmagickextended.test.util.RecordingServerPlayerPMX;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;

/**
 * Tests for the NeoForge familiar listeners that resend the player's familiar data when the client gets a new player
 * entity.
 */
public class FamiliarEventListenerTests extends AbstractBaseTestPMX {
    public static void familiar_data_is_resent_on_login_respawn_and_dimension_change(GameTestHelper helper) {
        RecordingServerPlayerPMX player = RecordingServerPlayerPMX.create(helper, false);
        Map<String, Runnable> resends = Map.of(
                "login", () -> FamiliarEventListeners.onPlayerLogin(new PlayerEvent.PlayerLoggedInEvent(player)),
                "respawn", () -> FamiliarEventListeners.onPlayerRespawn(new PlayerEvent.PlayerRespawnEvent(player, false)),
                "dimension change", () -> FamiliarEventListeners.onPlayerChangedDimension(new PlayerEvent.PlayerChangedDimensionEvent(player, Level.OVERWORLD, Level.NETHER)));
        resends.forEach((name, resend) -> {
            player.clearSentPayloads();
            resend.run();
            assertValueEqual(helper, 1L, player.sentPayloadCount(SyncFamiliarPacket.type()), "familiar sync packets sent on " + name);
        });
        helper.succeed();
    }
}
