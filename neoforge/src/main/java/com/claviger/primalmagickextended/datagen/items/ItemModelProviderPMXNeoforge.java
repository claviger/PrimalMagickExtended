package com.claviger.primalmagickextended.datagen.items;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Generates item model files for the mod.
 */
public class ItemModelProviderPMXNeoforge extends ItemModelProvider {
    public ItemModelProviderPMXNeoforge(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ConstantsPMX.MOD_ID, existingFileHelper);
    }

    @Override
    @NotNull
    public String getName() {
        return ConstantsPMX.MOD_NAME + " Item Models";
    }

    @Override
    protected void registerModels() {
        this.registerFamiliarModels();
        this.registerPortalModels();
    }

    private void registerFamiliarModels() {
        this.basicItem(ItemsPMX.FAMILIAR_COLLAR.get());
    }

    private void registerPortalModels() {
        // Portal blocks have no item form; the rune items are flat items with their own textures, apart from their blocks
        ItemsPMX.catalysts().forEach(this::basicItem);
        ItemsPMX.runes().forEach(this::basicItem);
    }
}
