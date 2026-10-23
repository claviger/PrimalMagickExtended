package com.claviger.primalmagickextended.common.familiars;

import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.fx.WandPoofPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;

/**
 * Visual and audio effects for familiars. Reuses Primal Magick's wand poof packet, which is what the
 * Polymorph entity swap plays, so summoning and dismissal look and sound exactly like a polymorph.
 */
public class FamiliarFx {
    private static final double RANGE = 32.0D;

    /** Plays the poof effect at the centre of the entity; does nothing on the client. */
    public static void poof(Level level, Entity entity) {
        if (level instanceof ServerLevel serverLevel) {
            poof(serverLevel, entity.position().add(0.0D, entity.getBbHeight() / 2.0D, 0.0D));
        }
    }

    /** Sends the poof effect at the given position to all players within range. */
    public static void poof(ServerLevel level, Vec3 pos) {
        PacketHandler.sendToAllAround(new WandPoofPacket(pos.x, pos.y, pos.z, Color.WHITE.getRGB(), true, Direction.UP), level, BlockPos.containing(pos), RANGE);
    }
}
