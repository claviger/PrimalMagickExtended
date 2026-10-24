package com.claviger.primalmagickextended.common.init;

import com.claviger.primalmagickextended.client.fx.particles.ParticleTypesPMX;
import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.entities.CatVariantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.spells.payloads.SpellPayloadsPMX;
import com.claviger.primalmagickextended.common.tiles.BlockEntityTypesPMX;

/**
 * Point of initialization for the mod's cross-platform deferred registries. Each holder's init()
 * call class-loads it so that its static registration fields are populated.
 */
public class InitRegistriesPMX {
    public static void initDeferredRegistries() {
        // Blocks must be registered before the items and block entity types that refer to them
        BlocksPMX.init();
        ItemsPMX.init();
        BlockEntityTypesPMX.init();
        ParticleTypesPMX.init();
        SpellPayloadsPMX.init();
        CatVariantsPMX.init();
    }
}
