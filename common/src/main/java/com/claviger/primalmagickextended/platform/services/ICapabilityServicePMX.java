package com.claviger.primalmagickextended.platform.services;

import com.claviger.primalmagickextended.common.capabilities.IPlayerFamiliar;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Platform access to the mod's attached data.
 */
public interface ICapabilityServicePMX {
    Optional<IPlayerFamiliar> familiar(@Nullable Player player);

    /** Sends the player's familiar data to that player's client. */
    void syncFamiliar(ServerPlayer player);

    /** The binding on an in-world familiar entity, or empty if the entity is not a familiar. */
    Optional<FamiliarBinding> binding(@Nullable Entity entity);

    /**
     * Marks the entity as a familiar with the given owner and generation, replacing any existing binding.
     * Server side; the binding is persisted with the entity but not synced to clients.
     */
    void setBinding(Entity entity, FamiliarBinding binding);

    /**
     * Removes the familiar binding from the entity, turning it back into an ordinary entity.
     * Server side; the binding is persisted with the entity but not synced to clients.
     */
    void clearBinding(Entity entity);
}
