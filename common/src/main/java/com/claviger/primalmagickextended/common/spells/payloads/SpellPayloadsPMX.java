package com.claviger.primalmagickextended.common.spells.payloads;

import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.common.registries.IRegistryItem;
import com.verdantartifice.primalmagick.common.research.requirements.AbstractRequirement;
import com.verdantartifice.primalmagick.common.spells.payloads.AbstractSpellPayload;
import com.verdantartifice.primalmagick.common.spells.payloads.SpellPayloadType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

/**
 * Spell payload types added by the mod. Sort orders start above Primal Magick's highest (2400).
 */
public class SpellPayloadsPMX {
    public static void init() {
        // Pass the service initialization through this class so it gets class loaded and fields registered
        ServicesPMX.SPELL_PAYLOAD_TYPES_REGISTRY.init();
    }

    public static final IRegistryItem<SpellPayloadType<?>, SpellPayloadType<FindFamiliarSpellPayload>> FIND_FAMILIAR = register(FindFamiliarSpellPayload.TYPE, 2500,
            FindFamiliarSpellPayload::getInstance, FindFamiliarSpellPayload::getRequirement, FindFamiliarSpellPayload.CODEC, FindFamiliarSpellPayload.STREAM_CODEC);

    protected static <T extends AbstractSpellPayload<T>> IRegistryItem<SpellPayloadType<?>, SpellPayloadType<T>> register(String id, int sortOrder, Supplier<T> instanceSupplier,
            Supplier<AbstractRequirement<?>> requirementSupplier, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return ServicesPMX.SPELL_PAYLOAD_TYPES_REGISTRY.register(id, () -> new SpellPayloadType<>(ResourceUtilsPMX.loc(id), sortOrder, instanceSupplier, requirementSupplier, codec, streamCodec));
    }
}
