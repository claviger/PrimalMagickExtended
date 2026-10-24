package com.claviger.primalmagickextended.common.familiars;

import com.claviger.primalmagickextended.common.entities.CatVariantsPMX;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.item.ItemStack;

/**
 * Undocumented Easter egg: a Familiar Collar used on an ocelot turns it into a cat that keeps the
 * ocelot's appearance, tamed by the player, and binds that cat as the player's familiar.
 */
public class OcelotConversion {
    /**
     * Converts the ocelot into an ocelot-variant cat tamed by the player and binds it. Any current
     * familiar is released first; if that fails nothing happens. Vanilla's single conversion carries
     * over the leash, vehicle, passengers, effects, age, name, tags, team and flags. If the level
     * refuses the converted cat (another mod vetoed the join), the ocelot is lost as in any vanilla
     * conversion, the previously released familiar stays an ordinary pet, no collar is consumed and
     * nothing is bound.
     *
     * @return true if the cat is now the player's familiar
     */
    public static boolean convertAndBind(ServerPlayer player, Ocelot ocelot, ItemStack collarStack) {
        if (FamiliarManager.data(player).flatMap(familiar -> familiar.getStored()).isPresent() && !FamiliarManager.release(player)) {
            return false;
        }
        ServerLevel level = player.level();
        Holder<CatVariant> variant = level.registryAccess().lookupOrThrow(Registries.CAT_VARIANT).getOrThrow(CatVariantsPMX.OCELOT);
        // Ocelots carry no equipment worth keeping, and the cat keeps its own loot-pickup default
        Cat cat = ocelot.convertTo(EntityType.CAT, ConversionParams.single(ocelot, false, false), EntitySpawnReason.CONVERSION, converted -> {
            converted.setComponent(DataComponents.CAT_VARIANT, variant);
            converted.tame(player);
            // The Ocelot variant alone keeps an unnamed cat called an ocelot (see OcelotCatNameMixin), without the nameplate a custom name would show
        });
        // convertTo ignores whether the level accepted the new entity
        if (cat == null || level.getEntity(cat.getUUID()) != cat) {
            return false;
        }
        return FamiliarManager.bind(player, cat, collarStack);
    }
}
