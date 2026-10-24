package com.claviger.primalmagickextended.common.spells.payloads;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.familiars.FamiliarManager;
import com.claviger.primalmagickextended.common.research.ResearchEntriesPMX;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.research.requirements.AbstractRequirement;
import com.verdantartifice.primalmagick.common.research.requirements.ResearchRequirement;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.Sources;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;
import com.verdantartifice.primalmagick.common.spells.SpellPropertyConfiguration;
import com.verdantartifice.primalmagick.common.spells.mods.SpellModsPM;
import com.verdantartifice.primalmagick.common.spells.payloads.AbstractSpellPayload;
import com.verdantartifice.primalmagick.common.spells.payloads.SpellPayloadType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Spell payload which calls the caster's bound familiar to their side, or dismisses it if it is
 * already present. The target is ignored: the bond belongs to the caster. Burst casts do nothing,
 * and toggles within {@link FamiliarManager#TOGGLE_DEBOUNCE_TICKS} ticks of each other are
 * collapsed, so Fork casts toggle once even with projectiles.
 * Mines do nothing either: a mine fires when any creature walks onto it, which must not toggle the
 * caster's familiar. Primal Magick passes no projectile entity when a mine executes its payload, so a
 * mine trigger is recognised by the spell carrying the Mine mod (the initial cast of such a spell only
 * places the mine and never reaches the payload).
 */
public class FindFamiliarSpellPayload extends AbstractSpellPayload<FindFamiliarSpellPayload> {
    public static final FindFamiliarSpellPayload INSTANCE = new FindFamiliarSpellPayload();

    public static final MapCodec<FindFamiliarSpellPayload> CODEC = MapCodec.unit(FindFamiliarSpellPayload.INSTANCE);
    public static final StreamCodec<ByteBuf, FindFamiliarSpellPayload> STREAM_CODEC = StreamCodec.unit(FindFamiliarSpellPayload.INSTANCE);

    public static final String TYPE = "find_familiar";
    protected static final AbstractRequirement<?> REQUIREMENT = loadedResearch(ResearchEntriesPMX.FIND_FAMILIAR);
    protected static final int BASE_MANA_COST = 20;

    public static AbstractRequirement<?> getRequirement() {
        return REQUIREMENT;
    }

    /**
     * A requirement met by players who know the given research while its entry is loaded. A player who researched
     * Find Familiar before familiars were turned off still knows its key, so knowing the research alone would keep
     * offering the payload once the entry is gone.
     */
    @VisibleForTesting
    public static AbstractRequirement<?> loadedResearch(ResourceKey<ResearchEntry> entry) {
        return new ResearchRequirement(new ResearchEntryKey(entry)) {
            @Override
            public boolean isMetBy(Player player) {
                return super.isMetBy(player) && player.registryAccess().lookupOrThrow(RegistryKeysPM.RESEARCH_ENTRIES).containsKey(entry);
            }
        };
    }

    public static FindFamiliarSpellPayload getInstance() {
        return INSTANCE;
    }

    @Override
    public SpellPayloadType<FindFamiliarSpellPayload> getType() {
        return SpellPayloadsPMX.FIND_FAMILIAR.get();
    }

    @Override
    protected List<SpellProperty> getPropertiesInner() {
        return ImmutableList.of();
    }

    @Override
    public void execute(HitResult target, Vec3 burstPoint, @NotNull SpellPackage spell, @NotNull Level world, @NotNull LivingEntity caster, ItemStack spellSource, Entity projectileEntity) {
        if (burstPoint != null || world.isClientSide() || spell.getMod(SpellModsPM.MINE.get()).isPresent()) {
            return;
        }
        if (caster instanceof ServerPlayer player) {
            FamiliarManager.toggle(player);
        }
    }

    @Override
    @NotNull
    public Source getSource() {
        return Sources.BLOOD;
    }

    @Override
    public int getBaseManaCost(SpellPropertyConfiguration properties) {
        return BASE_MANA_COST;
    }

    @Override
    public void playSounds(@NotNull Level world, @NotNull BlockPos origin) {
        world.playSound(null, origin, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0F, 1.2F + (float)(world.getRandom().nextGaussian() * 0.05D));
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }

    @Override
    @NotNull
    public Component getTypeName() {
        // Primal Magick's default hardcodes its own namespace
        return Component.translatable("spells." + ConstantsPMX.MOD_ID + ".payload." + TYPE + ".type");
    }

    @Override
    @NotNull
    public Component getDefaultNamePiece() {
        return Component.translatable("spells." + ConstantsPMX.MOD_ID + ".payload." + TYPE + ".default_name");
    }
}
