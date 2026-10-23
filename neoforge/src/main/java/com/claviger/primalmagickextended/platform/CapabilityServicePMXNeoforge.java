package com.claviger.primalmagickextended.platform;

import com.claviger.primalmagickextended.common.capabilities.CapabilitiesPMXNeoforge;
import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import com.claviger.primalmagickextended.platform.services.ICapabilityServicePMX;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * NeoForge implementation of the capability service, backed by data attachments.
 */
public class CapabilityServicePMXNeoforge implements ICapabilityServicePMX {
    @Override
    public Optional<IPlayerFamiliar> familiar(Player player) {
        return player == null ? Optional.empty() : Optional.of(player.getData(CapabilitiesPMXNeoforge.FAMILIAR));
    }

    @Override
    public void syncFamiliar(ServerPlayer player) {
        player.syncData(CapabilitiesPMXNeoforge.FAMILIAR.get());
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
