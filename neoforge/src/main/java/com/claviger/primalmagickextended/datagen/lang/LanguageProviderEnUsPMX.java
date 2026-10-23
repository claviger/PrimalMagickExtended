package com.claviger.primalmagickextended.datagen.lang;

import com.claviger.primalmagickextended.ConstantsPMX;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.items.familiars.FamiliarCollarItem;
import com.claviger.primalmagickextended.common.spells.payloads.FindFamiliarSpellPayload;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

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
        this.addTooltips();
        this.addResearch();
        this.addSpells();
        this.addMessages();
    }

    private void addItems() {
        this.add(ItemsPMX.FAMILIAR_COLLAR.get(), "Familiar Collar");
    }

    private void addTooltips() {
        this.add(FamiliarCollarItem.TOOLTIP_KEY, "Binds a tamed companion to you as a familiar");
    }

    private void addResearch() {
        this.researchEntry("familiar_collar", "Familiar Collar",
                "I have learned to summon or reshape creatures, and built a portal to another realm. I've formed bonds with animals, but I believe I can make a deeper bond, a magickal bond. It would take a ritual to craft it into a physical form.",
                "I have figured out the ritual. A few runes of power, and a few items attractive to beasts, to lure their spirits close and link them to mine. The ritual will produce a collar, which I can give to any animal that's already tamed and loyal to me. Bound to my spirit, it would be protected - anything that harmed it would instead have it slip away into another realm, where I should be able to summon it back to me.<BR>In addition, such a beast present nearby could bear some of the cost of spellcasting for me, contributing its mana to mine and making spellcasting easier.<BR>I can keep only one familiar; binding another would release the first back to ordinary life, though still loyal and unharmed. To call or dismiss a familiar at will, however, I will need a spell.");
        this.researchEntry("find_familiar", "Find Familiar",
                "With a familiar bound, its presence lingers at the edge of my thoughts even when it is far away. Surely that thread can be pulled upon with a spell.",
                "Find Familiar is a spell that calls my bound familiar to my side if it is absent, or dismisses it if it is present. The spell's target is unimportant, as it travels along the familiar bond and not through space.<BR>A familiar that was dismissed, lost, or seemingly destroyed returns whole, still bearing its name and collar.");
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

    /** Adds a spell payload's type name and default name piece, using Primal Magick's key pattern. */
    protected void spellPayload(String payloadType, String typeName, String defaultName) {
        String base = String.join(".", "spells", ConstantsPMX.MOD_ID, "payload", payloadType);
        this.add(base + ".type", typeName);
        this.add(base + ".default_name", defaultName);
    }

    /** Adds a chat message under {@code message.primalmagickextended.<path>}. */
    protected void message(String path, String text) {
        this.add(String.join(".", "message", ConstantsPMX.MOD_ID, path), text);
    }
}
