package com.claviger.primalmagickextended.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * NeoForge attachment form of the player familiar data. Persistence uses the common codec, through the common
 * {@code serializeNBT}/{@code deserializeNBT} pair.
 */
public class PlayerFamiliarNeoforge extends PlayerFamiliar implements INBTSerializable<CompoundTag> {
}
