package com.claviger.primalmagickextended.platform.registries;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.platform.services.registries.ISpellPayloadTypeRegistryServicePMX;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.spells.payloads.SpellPayloadType;
import com.verdantartifice.primalmagick.platform.registries.SpellPayloadTypeRegistryServiceNeoforge;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * NeoForge implementation of the spell payload type registry service. The registry itself is
 * created by Primal Magick; this mod only registers into it under its own namespace.
 */
public class SpellPayloadTypeRegistryServicePMXNeoforge extends AbstractRegistryServicePMXNeoforge<SpellPayloadType<?>> implements ISpellPayloadTypeRegistryServicePMX {
    private static final DeferredRegister<SpellPayloadType<?>> DEFERRED_TYPES = DeferredRegister.create(RegistryKeysPM.SPELL_PAYLOAD_TYPES, ConstantsPMX.MOD_ID);

    @Override
    protected Supplier<DeferredRegister<SpellPayloadType<?>>> getDeferredRegisterSupplier() {
        return () -> DEFERRED_TYPES;
    }

    @Override
    protected Registry<SpellPayloadType<?>> getRegistry() {
        return SpellPayloadTypeRegistryServiceNeoforge.TYPES;
    }
}
