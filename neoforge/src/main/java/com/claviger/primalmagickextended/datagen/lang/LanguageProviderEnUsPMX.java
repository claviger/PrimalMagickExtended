package com.claviger.primalmagickextended.datagen.lang;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.items.familiars.FamiliarCollarItem;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.spells.payloads.FindFamiliarSpellPayload;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * English (US) localization for the mod. This is the only source of en_us.json; other locales are
 * hand-maintained translations of its output.
 */
public class LanguageProviderEnUsPMX extends LanguageProvider {
    public LanguageProviderEnUsPMX(PackOutput output) {
        super(output, ConstantsPMX.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.addItems();
        this.addPortalBlocks();
        this.addPortalItems();
        this.addTooltips();
        this.addResearch();
        this.addSpells();
        this.addMessages();
        this.addConfig();
    }

    private void addItems() {
        this.add(ItemsPMX.FAMILIAR_COLLAR.get(), "Familiar Collar");
    }

    private void addPortalBlocks() {
        for (PortalColor color : PortalColor.values()) {
            this.add(BlocksPMX.portal(color), colorName(color) + " Portal");
        }
        this.add(BlocksPMX.HASTE_RUNE_BLOCK.get(), "Haste Rune");
        this.add(BlocksPMX.GATE_RUNE_BLOCK.get(), "Gate Rune");
        this.add(BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(), "Enhancer Rune");
        this.add(BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(), "Strong Enhancer Rune");
        this.add(BlocksPMX.INFINITY_RUNE_BLOCK.get(), "Infinity Rune");
    }

    private void addPortalItems() {
        for (PortalColor color : PortalColor.values()) {
            this.add(ItemsPMX.catalyst(color), "Portal Catalyst (" + colorName(color) + ")");
        }
        this.add(ItemsPMX.HASTE_RUNE.get(), "Haste Rune");
        this.add(ItemsPMX.GATE_RUNE.get(), "Gate Rune");
        this.add(ItemsPMX.WEAK_ENHANCER_RUNE.get(), "Enhancer Rune");
        this.add(ItemsPMX.STRONG_ENHANCER_RUNE.get(), "Strong Enhancer Rune");
        this.add(ItemsPMX.INFINITY_RUNE.get(), "Infinity Rune");
    }

    private void addTooltips() {
        this.add(FamiliarCollarItem.TOOLTIP_KEY, "Binds a tamed companion to you as a familiar");
        this.tooltip(ItemsPMX.HASTE_RUNE.get(), "Allows for instantaneous portal travel");
        this.tooltip(ItemsPMX.GATE_RUNE.get(), "Allows portals to link across dimensions");
        this.tooltip(ItemsPMX.WEAK_ENHANCER_RUNE.get(), "Expands portal range to %d blocks");
        this.tooltip(ItemsPMX.STRONG_ENHANCER_RUNE.get(), "Expands portal range to %d blocks");
        this.tooltip(ItemsPMX.INFINITY_RUNE.get(), "Expands portal range indefinitely");
    }

    private void addResearch() {
        this.researchEntry("familiar_collar", "Familiar Collar",
                "I have learned to summon or reshape creatures, and built a portal to another realm. I've formed bonds with animals, but I believe I can make a deeper bond, a magickal bond. It would take a ritual to craft it into a physical form.",
                "I have figured out the ritual. A few runes of power, and a few items attractive to beasts, to lure their spirits close and link them to mine. The ritual will produce a collar, which I can give to any animal that's already tamed and loyal to me. Bound to my spirit, it would be protected - anything that harmed it would instead have it slip away into another realm, where I should be able to summon it back to me.<BR>In addition, such a beast present nearby could bear some of the cost of spellcasting for me, contributing its mana to mine and making spellcasting easier.<BR>I can keep only one familiar; binding another would release the first back to ordinary life, though still loyal and unharmed. To call or dismiss a familiar at will, however, I will need a spell.");
        this.researchEntry("find_familiar", "Find Familiar",
                "With a familiar bound, its presence lingers at the edge of my thoughts even when it is far away. Surely that thread can be pulled upon with a spell.",
                "Find Familiar is a spell that calls my bound familiar to my side if it is absent, or dismisses it if it is present. The spell's target is unimportant, as it travels along the familiar bond and not through space.<BR>A familiar that was dismissed, lost, or seemingly destroyed returns whole, still bearing its name and collar.");
        this.researchEntry("portal_travel", "Portal Travel",
                "Obsidian and flame can naturally create a portal to the Nether, but what if I applied magick to a portal? Might I be able to connect other places?",
                "With a bit of infernal magick and the natural teleportation abilities of Ender Pearls and Eyes of Ender, I can create portals anywhere I desire! I use the Ender Pearls and Eyes of Ender to create a portal catalyst, adding a bit of dye to give it a color, and then toss it into a frame made of any single material. If another frame made of the same material with the same color catalyst is nearby, they link up directly, creating a portal between them.<BR>Unfortunately, the range is fairly limited, and it cannot cross dimensions. Perhaps more research is required...");
        this.researchEntry("weak_enhancer_rune", "Portal Enhancer Runes",
                "While the portals are very useful, their range is inconveniently short. With my knowledge of runecrafting, could I draw a rune on the portal frame to extend its range?",
                "I have devised a rune that will extend the range of a portal, allowing me to connect ten times further than before, up to 1000 meters. The rune is a bit too large to scribe on a stone tablet at the runescribing table, but I should be able to put one together at my Arcane Workbench. If I apply the rune to the frame of a portal, it should allow it to link up to a greater distance. It shouldn't matter which end of the portal I apply the rune to, either should work.");
        this.researchEntry("strong_enhancer_rune", "Strong Enhancer Runes",
                "With my greater knowledge of runecrafting, I could invent a better portal enhancer rune.",
                "Using prismarine inks, I have devised a superior rune that will extend the range of a portal another tenfold.  With this rune, I can reach ten kilometers away, traversing great distances in a single jump.  Once again, it shouldn't matter which end of the portal I apply the rune to, either should work.");
        this.researchEntry("infinity_rune", "Infinite Enhancer Runes",
                "Now that I have access to the energies of the Void, there should be no limit to the distance a portal can cross.",
                "The materials are quite costly - only a rune forged of netherite can cross such distances. But using one, a portal should be able to link across any distance, traversing the entire world, though still limited to travel within the same dimension.");
        this.researchEntry("gate_rune", "Gate Runes",
                "Now that I have access to the energies of the Void, it should be possible for a portal to bore across dimensions.",
                "By combining materials from the Nether and the End, I can now create a rune that will cross the void and connect dimensions. It may need to be combined with an appropriate Enhancer Rune, depending on where in the dimensions the portals are found.");
        this.researchEntry("haste_rune", "Haste Runes",
                "Using the power of the Void, I should be able to quicken my portals, linking locations instantly.",
                "With this rune, a portal will work instantaneously, like the Endermen's teleportation rather than the slow transit of a Nether Portal.");
        this.expertiseGroup("portal_catalysts", "Portal Catalysts");
    }

    private void addSpells() {
        this.spellPayload(FindFamiliarSpellPayload.TYPE, "Find Familiar", "Familiar");
    }

    private void addMessages() {
        this.message("familiar.bound", "%s is now your familiar.");
        this.message("familiar.released", "%s is no longer your familiar, but remains your loyal companion.");
        this.message("familiar.already", "%s is already your familiar.");
        this.message("familiar.none", "You have no familiar to call.");
        this.message("familiar.not_yours", "That creature is bound to someone else.");
        this.message("familiar.summon_failed", "Your familiar could not answer your call.");
        this.message("command.affinities.explain.generated", "Affinities for item %1$s are generated by Primal Magick Extended (as %2$s); a data pack entry would replace them");
        this.message("command.familiar.summon.ok", "Summoned %s's familiar.");
        this.message("command.familiar.summon.none", "%s has no familiar to summon.");
        this.message("command.familiar.summon.present", "%s's familiar is already in play.");
        this.message("command.familiar.summon.failed", "%s's familiar could not be summoned.");
        this.message("command.familiar.dismiss.ok", "Dismissed %s's familiar.");
        this.message("command.familiar.dismiss.none", "%s has no familiar in play.");
        this.message("command.familiar.clear.ok", "Cleared %s's familiar bond.");
        this.message("command.familiar.clear.none", "%s has no familiar bond.");
        this.message("command.familiar.clear.failed", "%s's familiar could not be restored, so its bond was kept.");
        this.message("familiar.lost", "The bond to your familiar has faded; it could not be restored.");
        this.message("portal_spawn_set", "Set portal's spawn position to (%s, %s, %s)");
    }

    private void addConfig() {
        this.config("title", "Primal Magick Extended");
        this.config("features", "Features");
        this.config("features.tooltip", "Turn whole features of Primal Magick Extended on or off. A disabled feature's Grimoire entries and recipes are not loaded. Changes take effect after restarting the game or server.");
        this.config("features.familiars", "Familiars");
        this.config("features.familiars.tooltip", "Familiar Collar ritual and Find Familiar spell.");
        this.config("features.portals", "Portals");
        this.config("features.portals.tooltip", "Portal catalysts and portal runes.");
        this.config("features.generated_affinities", "Generated Affinities");
        this.config("features.generated_affinities.tooltip", "Affinities worked out for natural blocks (such as those of biome mods) that have none.");
        this.config("portals", "Portals");
        this.config("unlimitedRange", "Portals Always Have Unlimited Range");
        this.config("alwaysInterdim", "Portals Always Link Across Dimensions");
        this.config("alwaysHaste", "Portals Always Teleport Instantly");
        this.config("privatePortals", "Private Portals");
        this.config("privatePortals.tooltip", "Only allows portals to link if they were each constructed by the same player. Useful for servers.");
        this.config("redstone", "Redstone...");
        this.config("redstone.tooltip", "Note: Setting this to ON means your portals will be off without redstone power!");
        this.config("muteAmbientSounds", "Mute Ambient Portal Sound Effects");
        this.config("muteTeleportSounds", "Mute Player Teleport Sound Effects");
        this.config("defaultRange", "Default Portal Range (In Blocks)");
        this.config("rangeWithEnhancer", "Portal Range With Enhancer Rune");
        this.config("rangeWithStrongEnhancer", "Portal Range With Strong Enhancer Rune");
        this.config("isWhitelist", "Whitelist or Blacklist");
        this.config("isWhitelist.tooltip", "Whether the filtered block list is a whitelist (true) or a blacklist (false)");
        this.config("filteredBlocks", "Filtered Blocks");
        this.config("filteredBlocks.tooltip", "Allows customizing which blocks may be used as portal frames. Takes the block's id, e.g. \"minecraft:stone\" for stone block.");
    }

    /**
     * Adds a research entry title and its numbered stage texts, using Primal Magick's key pattern.
     * Entries must be built with {@code new ResearchEntry.Builder(ConstantsPMX.MOD_ID, key)}; PM's default
     * builder puts stage text keys in the primalmagick namespace and these keys would not be found.
     */
    protected void researchEntry(String entryPath, String title, String... stageTexts) {
        String base = String.join(".", "research", ConstantsPMX.MOD_ID, entryPath);
        this.add(base + ".title", title);
        for (int index = 0; index < stageTexts.length; index++) {
            this.add(base + ".text.stage." + (index + 1), stageTexts[index]);
        }
    }

    /**
     * Adds the name of an arcane recipe expertise group, using the key Primal Magick derives from the group id:
     * {@code expertise_group.primalmagickextended.<group>}.
     */
    protected void expertiseGroup(String group, String name) {
        this.add(String.join(".", "expertise_group", ConstantsPMX.MOD_ID, group), name);
    }

    /** Adds a spell payload's type name and default name piece, using Primal Magick's key pattern. */
    protected void spellPayload(String payloadType, String typeName, String defaultName) {
        String base = String.join(".", "spells", ConstantsPMX.MOD_ID, "payload", payloadType);
        this.add(base + ".type", typeName);
        this.add(base + ".default_name", defaultName);
    }

    /** Adds an item's tooltip line under {@code <item description id>.tooltip}. */
    protected void tooltip(Item item, String text) {
        this.add(item.getDescriptionId() + ".tooltip", text);
    }

    /** Adds a chat message under {@code message.primalmagickextended.<path>}. */
    protected void message(String path, String text) {
        this.add(String.join(".", "message", ConstantsPMX.MOD_ID, path), text);
    }

    /**
     * Adds a configuration screen string under {@code primalmagickextended.configuration.<path>}. Option keys must
     * match the {@code .translation(...)} keys in {@code ConfigPMXNeoforge}; NeoForge's configuration screen also
     * reads {@code <key>.tooltip} for an option's tooltip and uses the bare key of a config section as its label.
     */
    protected void config(String path, String text) {
        this.add(String.join(".", ConstantsPMX.MOD_ID, "configuration", path), text);
    }

    /** A colour's display name as Custom Portals spells it, e.g. "Light Blue" for {@link PortalColor#LIGHT_BLUE}. */
    private static String colorName(PortalColor color) {
        return Arrays.stream(color.getSerializedName().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
