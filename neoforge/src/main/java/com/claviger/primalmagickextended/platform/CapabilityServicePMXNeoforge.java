package com.claviger.primalmagickextended.platform;

import com.claviger.primalmagickextended.common.capabilities.CapabilitiesPMXNeoforge;
import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.common.network.packets.familiars.SyncFamiliarPacket;
import com.claviger.primalmagickextended.platform.services.ICapabilityServicePMX;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * NeoForge implementation of the capability service, backed by data attachments. Attachments cannot sync
 * themselves on this version, so the player familiar data is synced with the mod's own packet.
 */
public class CapabilityServicePMXNeoforge implements ICapabilityServicePMX {
    @Override
    public Optional<IPlayerFamiliar> familiar(Player player) {
        return player == null ? Optional.empty() : Optional.of(player.getData(CapabilitiesPMXNeoforge.FAMILIAR));
    }

    @Override
    public void syncFamiliar(ServerPlayer player) {
        IPlayerFamiliar familiar = player.getData(CapabilitiesPMXNeoforge.FAMILIAR);
        PacketHandler.sendToPlayer(new SyncFamiliarPacket(familiar.hasFamiliar(), familiar.isFamiliarNearby()), player);
    }

    @Override
    public Optional<FamiliarBinding> binding(Entity entity) {
        if (entity == null || !entity.hasData(CapabilitiesPMXNeoforge.FAMILIAR_BINDING)) {
            return Optional.empty();
        }
        FamiliarBinding binding = entity.getData(CapabilitiesPMXNeoforge.FAMILIAR_BINDING);
        return binding.isPresent() ? Optional.of(binding) : Optional.empty();
    }

    @Override
    public void setBinding(Entity entity, FamiliarBinding binding) {
        entity.setData(CapabilitiesPMXNeoforge.FAMILIAR_BINDING, binding);
    }

    @Override
    public void clearBinding(Entity entity) {
        entity.removeData(CapabilitiesPMXNeoforge.FAMILIAR_BINDING);
    }
}
