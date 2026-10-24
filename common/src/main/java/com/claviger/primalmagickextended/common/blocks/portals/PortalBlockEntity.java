package com.claviger.primalmagickextended.common.blocks.portals;

import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalData;
import com.claviger.primalmagickextended.common.portals.PortalHelper;
import com.claviger.primalmagickextended.common.portals.PortalRedstoneMode;
import com.claviger.primalmagickextended.common.tiles.BlockEntityTypesPMX;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of every portal block. Each server tick it sets its block's lit state from whether the portal is
 * linked and, depending on the redstone mode, whether it is powered. Ported from Custom Portals.
 */
public class PortalBlockEntity extends BlockEntity {
    public PortalBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesPMX.PORTAL.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T be) {
        if (world.isClientSide())
            return;
        CustomPortal portal = PortalData.getPortalFromPos(world, pos);
        if (portal != null) {
            PortalRedstoneMode redstone = ServicesPMX.CONFIG.portalsRedstone();
            // The signal is not saved, so re-derive it once after a load rather than waiting for a neighbour change
            if (redstone != PortalRedstoneMode.NONE && !portal.isRedstoneChecked()) {
                portal.setHasRedstoneSignal(PortalHelper.checkRedstoneSignal(world, portal));
            }
            if (redstone == PortalRedstoneMode.OFF) {
                if (portal.hasLinked() && !state.getValue(PortalBlock.LIT) && !portal.hasRedstoneSignal()) {
                    world.setBlock(pos, state.setValue(PortalBlock.LIT, true), Block.UPDATE_ALL);
                } else if ((!portal.hasLinked() || portal.hasRedstoneSignal()) && state.getValue(PortalBlock.LIT)) {
                    world.setBlock(pos, state.setValue(PortalBlock.LIT, false), Block.UPDATE_ALL);
                }
            } else if (redstone == PortalRedstoneMode.ON) {
                if (portal.hasLinked() && !state.getValue(PortalBlock.LIT) && portal.hasRedstoneSignal()) {
                    world.setBlock(pos, state.setValue(PortalBlock.LIT, true), Block.UPDATE_ALL);
                } else if ((!portal.hasLinked() || !portal.hasRedstoneSignal()) && state.getValue(PortalBlock.LIT)) {
                    world.setBlock(pos, state.setValue(PortalBlock.LIT, false), Block.UPDATE_ALL);
                }
            } else {
                if (portal.hasLinked() && !state.getValue(PortalBlock.LIT))
                    world.setBlock(pos, state.setValue(PortalBlock.LIT, true), Block.UPDATE_ALL);
                if (!portal.hasLinked() && state.getValue(PortalBlock.LIT))
                    world.setBlock(pos, state.setValue(PortalBlock.LIT, false), Block.UPDATE_ALL);
            }
        }
    }
}
