package com.claviger.primalmagickextended.common.capabilities;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.PrimalMagickExtended;
import com.claviger.primalmagickextended.common.familiars.FamiliarBinding;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Data attachment registrations for the mod. Named after Primal Magick's CapabilitiesNeoforge.
 */
public class CapabilitiesPMXNeoforge {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ConstantsPMX.MOD_ID);

    public static void init() {
        ATTACHMENTS.register(PrimalMagickExtended.getEventBus());
    }

    /**
     * Per-player familiar data; copied on death. Delta updates are sent to the owning player only;
     * NeoForge's initial pairing sync also sends the two booleans to players that start tracking the
     * owner (engine limitation), so client code must only read the local player's data.
     */
    public static final Supplier<AttachmentType<PlayerFamiliarNeoforge>> FAMILIAR = ATTACHMENTS.register("familiar",
            () -> AttachmentType.serializable(PlayerFamiliarNeoforge::new)
                    .copyOnDeath()
                    .sync((holder, player) -> holder == player, PlayerFamiliarNeoforge.SYNC_STREAM_CODEC)
                    .build());

    /** Per-entity familiar marker; persisted only when present. */
    public static final Supplier<AttachmentType<FamiliarBinding>> FAMILIAR_BINDING = ATTACHMENTS.register("familiar_binding",
            () -> AttachmentType.builder(() -> FamiliarBinding.NONE)
                    .serialize(FamiliarBinding.MAP_CODEC, FamiliarBinding::isPresent)
                    .build());
}
