package com.claviger.primalmagickextended.common.capabilities;

import com.claviger.primalmagickextended.common.familiars.StoredFamiliar;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

/**
 * Per-player familiar data. Persistent state is the stored familiar; the nearby flag and toggle
 * time are transient, and only the booleans are synced. Delta updates are sent to the owning player
 * only; NeoForge's initial pairing sync also sends the two booleans to players that start tracking
 * the owner (engine limitation), so client code must only read the local player's data.
 */
public interface IPlayerFamiliar {
    /** The player's bound familiar, whether in play or dismissed; always empty on the client. */
    Optional<StoredFamiliar> getStored();

    /**
     * Replaces the player's bound familiar. On the server, callers must call
     * {@code ServicesPMX.CAPABILITIES.syncFamiliar} afterwards so the client view stays current.
     */
    void setStored(Optional<StoredFamiliar> stored);

    /** True if the player has a bound familiar, whether in play or dismissed. Works on both sides. */
    boolean hasFamiliar();

    /** True while the familiar is in play within 100 blocks of the player. Works on both sides. */
    boolean isFamiliarNearby();

    /**
     * Sets the nearby flag. On the server, callers must call {@code ServicesPMX.CAPABILITIES.syncFamiliar}
     * afterwards so the client view stays current.
     */
    void setFamiliarNearby(boolean nearby);

    /** The game time of the player's last summon/dismiss toggle, or -1 if they have never toggled. */
    long getLastToggleGameTime();

    /** Records the game time of a summon/dismiss toggle; -1 means never. Transient, not synced. */
    void setLastToggleGameTime(long gameTime);

    void serialize(ValueOutput output);
    void deserialize(ValueInput input);
}
