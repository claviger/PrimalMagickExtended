package com.claviger.primalmagickextended.datagen.models;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.AbstractRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;

import java.util.Optional;

/**
 * Generates block state, block model and item model files for the mod.
 */
public class ModelProviderPMXNeoforge extends ModelProvider {
    private static final TextureSlot PORTAL_SLOT = TextureSlot.create("portal");

    /** Portal pane along the X axis (Custom Portals' "north-south" model). */
    private static final ModelTemplate PORTAL_NS = portalTemplate("_ns", PORTAL_SLOT);

    /** Portal pane along the Z axis (Custom Portals' "east-west" model). */
    private static final ModelTemplate PORTAL_EW = portalTemplate("_ew", PORTAL_SLOT);

    /** Horizontal portal pane (Custom Portals' "up-down" model). */
    private static final ModelTemplate PORTAL_UD = portalTemplate("_ud", PORTAL_SLOT);

    /** Thin rune lying flat on its attachment face; used by the gate, haste and weak enhancer runes. */
    private static final ModelTemplate PORTAL_RUNE = new ModelTemplate(Optional.of(ResourceUtilsPMX.loc("block/template_portal_rune")), Optional.empty(), TextureSlot.TEXTURE);

    /** Rune covering the full face of its frame block; used by the strong enhancer and infinity runes. */
    private static final ModelTemplate PORTAL_RUNE_FULL = new ModelTemplate(Optional.of(ResourceUtilsPMX.loc("block/template_portal_rune_full")), Optional.empty(), TextureSlot.TEXTURE);

    public ModelProviderPMXNeoforge(PackOutput output) {
        super(output, ConstantsPMX.MOD_ID);
    }

    /**
     * Creates a portal pane template from the parent model {@code block/template_custom_portal<suffix>}. Models made
     * from it take the same suffix, and the template's single texture is bound to the given slot.
     */
    private static ModelTemplate portalTemplate(String suffix, TextureSlot slot) {
        return new ModelTemplate(Optional.of(ResourceUtilsPMX.loc("block/template_custom_portal" + suffix)), Optional.of(suffix), slot);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ItemsPMX.FAMILIAR_COLLAR.get(), ModelTemplates.FLAT_ITEM);

        // Portals; portal blocks have no item form, so no item models are generated for them
        BlocksPMX.portals().forEach(portal -> this.createPortalBlock(portal.get(), blockModels));
        ItemsPMX.catalysts().forEach(catalyst -> itemModels.generateFlatItem(catalyst, ModelTemplates.FLAT_ITEM));

        // Runes; the rune items have their own ids and textures, separate from their blocks
        this.createRuneBlock(BlocksPMX.HASTE_RUNE_BLOCK.get(), PORTAL_RUNE, blockModels);
        this.createRuneBlock(BlocksPMX.GATE_RUNE_BLOCK.get(), PORTAL_RUNE, blockModels);
        this.createRuneBlock(BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(), PORTAL_RUNE, blockModels);
        this.createRuneBlock(BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(), PORTAL_RUNE_FULL, blockModels);
        this.createRuneBlock(BlocksPMX.INFINITY_RUNE_BLOCK.get(), PORTAL_RUNE_FULL, blockModels);
        ItemsPMX.runes().forEach(rune -> itemModels.generateFlatItem(rune, ModelTemplates.FLAT_ITEM));
    }

    /**
     * Generates the six models of a portal block (lit and inactive, for each of the three axes) and a block state
     * that picks one by {@link PortalBlock#AXIS} and {@link PortalBlock#LIT}, matching Custom Portals' files.
     */
    private void createPortalBlock(Block block, BlockModelGenerators blockModels) {
        TextureMapping activeMapping = new TextureMapping().put(PORTAL_SLOT, TextureMapping.getBlockTexture(block));
        TextureMapping inactiveMapping = new TextureMapping().put(PORTAL_SLOT, TextureMapping.getBlockTexture(block, "_inactive"));
        PropertyDispatch.C2<MultiVariant, Direction.Axis, Boolean> dispatch = PropertyDispatch.initial(PortalBlock.AXIS, PortalBlock.LIT);
        for (Direction.Axis axis : Direction.Axis.values()) {
            ModelTemplate template = switch (axis) {
                case X -> PORTAL_NS;
                case Z -> PORTAL_EW;
                case Y -> PORTAL_UD;
            };
            dispatch.select(axis, true, BlockModelGenerators.plainVariant(template.create(block, activeMapping, blockModels.modelOutput)));
            dispatch.select(axis, false, BlockModelGenerators.plainVariant(template.createWithSuffix(block, "_inactive", inactiveMapping, blockModels.modelOutput)));
        }
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(dispatch));
    }

    /**
     * Generates a rune block's model and a block state reproducing Custom Portals' rotations for each attachment
     * face and facing. The rune models lie on the floor, so wall runes are tipped over by 90 degrees
     * and ceiling runes flipped by 180.
     */
    private void createRuneBlock(Block block, ModelTemplate template, BlockModelGenerators blockModels) {
        MultiVariant variant = BlockModelGenerators.plainVariant(template.create(block, TextureMapping.defaultTexture(block), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(AbstractRuneBlock.FACE, AbstractRuneBlock.FACING)
                        .select(AttachFace.CEILING, Direction.EAST, variant.with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_270))
                        .select(AttachFace.CEILING, Direction.NORTH, variant.with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_180))
                        .select(AttachFace.CEILING, Direction.SOUTH, variant.with(BlockModelGenerators.X_ROT_180))
                        .select(AttachFace.CEILING, Direction.WEST, variant.with(BlockModelGenerators.X_ROT_180).with(BlockModelGenerators.Y_ROT_90))
                        .select(AttachFace.FLOOR, Direction.EAST, variant.with(BlockModelGenerators.Y_ROT_270))
                        .select(AttachFace.FLOOR, Direction.NORTH, variant.with(BlockModelGenerators.Y_ROT_180))
                        .select(AttachFace.FLOOR, Direction.SOUTH, variant)
                        .select(AttachFace.FLOOR, Direction.WEST, variant.with(BlockModelGenerators.Y_ROT_90))
                        .select(AttachFace.WALL, Direction.EAST, variant.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_90))
                        .select(AttachFace.WALL, Direction.NORTH, variant.with(BlockModelGenerators.X_ROT_90))
                        .select(AttachFace.WALL, Direction.SOUTH, variant.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_180))
                        .select(AttachFace.WALL, Direction.WEST, variant.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_270))));
    }
}
