package com.claviger.primalmagickextended.datagen.blocks;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.AbstractRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Generates block state and block model files for the mod. Render types come from the hand-written parent
 * templates: the portal templates are translucent and the rune templates cutout.
 */
public class BlockStateProviderPMXNeoforge extends BlockStateProvider {
    /** Thin rune lying flat on its attachment face; used by the gate, haste and weak enhancer runes. */
    private static final String PORTAL_RUNE = "block/template_portal_rune";

    /** Rune covering the full face of its frame block; used by the strong enhancer and infinity runes. */
    private static final String PORTAL_RUNE_FULL = "block/template_portal_rune_full";

    public BlockStateProviderPMXNeoforge(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ConstantsPMX.MOD_ID, exFileHelper);
    }

    @Override
    @NotNull
    public String getName() {
        return ConstantsPMX.MOD_NAME + " Block States";
    }

    @Override
    protected void registerStatesAndModels() {
        this.registerPortalStatesAndModels();
    }

    private void registerPortalStatesAndModels() {
        // Portals; portal blocks have no item form, so no item models are generated for them
        BlocksPMX.portals().forEach(portal -> this.portalBlock(portal.get()));

        // Runes; the rune items have their own ids and textures, separate from their blocks
        this.runeBlock(BlocksPMX.HASTE_RUNE_BLOCK.get(), PORTAL_RUNE);
        this.runeBlock(BlocksPMX.GATE_RUNE_BLOCK.get(), PORTAL_RUNE);
        this.runeBlock(BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(), PORTAL_RUNE);
        this.runeBlock(BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(), PORTAL_RUNE_FULL);
        this.runeBlock(BlocksPMX.INFINITY_RUNE_BLOCK.get(), PORTAL_RUNE_FULL);
    }

    private String name(Block block) {
        return this.blockTexture(block).getPath().substring("block/".length());
    }

    /**
     * Generates the six models of a portal block (lit and inactive, for each of the three axes) and a block state
     * that picks one by {@link PortalBlock#AXIS} and {@link PortalBlock#LIT}, matching Custom Portals' files.
     * Waterlogging does not change the model.
     */
    private void portalBlock(Block block) {
        String name = this.name(block);
        ResourceLocation activeTexture = this.blockTexture(block);
        ResourceLocation inactiveTexture = activeTexture.withSuffix("_inactive");
        this.getVariantBuilder(block).forAllStatesExcept(state -> {
            // Custom Portals' "north-south" pane lies along the X axis, "east-west" along Z, and "up-down" is horizontal
            String suffix = switch (state.getValue(PortalBlock.AXIS)) {
                case X -> "_ns";
                case Z -> "_ew";
                case Y -> "_ud";
            };
            boolean lit = state.getValue(PortalBlock.LIT);
            ModelFile model = this.models().withExistingParent(name + (lit ? "" : "_inactive") + suffix, this.modLoc("block/template_custom_portal" + suffix))
                    .texture("portal", lit ? activeTexture : inactiveTexture);
            return ConfiguredModel.builder().modelFile(model).build();
        }, PortalBlock.WATERLOGGED);
    }

    /**
     * Generates a rune block's model and a block state reproducing Custom Portals' rotations for each attachment
     * face and facing. The rune models lie on the floor, so wall runes are tipped over by 90 degrees and ceiling
     * runes flipped by 180.
     */
    private void runeBlock(Block block, String template) {
        ModelFile model = this.models().withExistingParent(this.name(block), this.modLoc(template))
                .texture("texture", this.blockTexture(block));
        this.getVariantBuilder(block).forAllStates(state -> {
            AttachFace face = state.getValue(AbstractRuneBlock.FACE);
            Direction facing = state.getValue(AbstractRuneBlock.FACING);
            int x = switch (face) {
                case FLOOR -> 0;
                case WALL -> 90;
                case CEILING -> 180;
            };
            int y = switch (face) {
                case FLOOR, CEILING -> floorRotationY(facing);
                case WALL -> wallRotationY(facing);
            };
            return ConfiguredModel.builder().modelFile(model).rotationX(x).rotationY(y).build();
        });
    }

    /** Y rotation of a floor or ceiling rune facing the given way; the model faces south unrotated. */
    private static int floorRotationY(Direction facing) {
        return switch (facing) {
            case NORTH -> 180;
            case EAST -> 270;
            case WEST -> 90;
            default -> 0;
        };
    }

    /** Y rotation of a wall rune facing the given way; tipped over, the model faces north unrotated. */
    private static int wallRotationY(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
