package com.claviger.primalmagickextended.datagen.models;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

/**
 * Generates block state, block model and item model files for the mod.
 */
public class ModelProviderPMXNeoforge extends ModelProvider {
    public ModelProviderPMXNeoforge(PackOutput output) {
        super(output, ConstantsPMX.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ItemsPMX.FAMILIAR_COLLAR.get(), ModelTemplates.FLAT_ITEM);
    }
}
