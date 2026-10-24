package com.claviger.primalmagickextended.common.research;

import com.google.common.annotations.VisibleForTesting;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.topics.AbstractResearchTopic;
import com.verdantartifice.primalmagick.common.research.topics.EntryResearchTopic;
import com.verdantartifice.primalmagick.common.research.topics.MainIndexResearchTopic;
import com.verdantartifice.primalmagick.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Predicate;

/**
 * Clears Grimoire bookmarks that point at research entries which are no longer loaded, as happens to a player who
 * read a feature's entries before that feature was turned off. Primal Magick would otherwise reopen the Grimoire on
 * a blank page, and its Back button would lead to one.
 */
public final class StaleResearchTopicsPMX {
    private StaleResearchTopicsPMX() {}

    /**
     * Clears the given player's stale Grimoire bookmarks, and schedules a knowledge sync so the change reaches their
     * client. Called when a player logs in.
     */
    public static void clearFor(ServerPlayer player) {
        Services.CAPABILITIES.knowledge(player).ifPresent(knowledge -> {
            if (clear(knowledge, player.registryAccess())) {
                ResearchManager.scheduleSync(player);
            }
        });
    }

    /**
     * Sends the Grimoire back to its main index if it was last open at a missing entry, and drops missing entries
     * from its Back history.
     *
     * @return whether anything was cleared
     */
    @VisibleForTesting
    public static boolean clear(IPlayerKnowledge knowledge, RegistryAccess registryAccess) {
        Registry<ResearchEntry> entries = registryAccess.lookupOrThrow(RegistryKeysPM.RESEARCH_ENTRIES);
        Predicate<AbstractResearchTopic<?>> stale = topic -> topic instanceof EntryResearchTopic entryTopic && !entries.containsKey(entryTopic.getEntry().getRootKey());
        boolean cleared = false;
        if (stale.test(knowledge.getLastResearchTopic())) {
            knowledge.setLastResearchTopic(MainIndexResearchTopic.INSTANCE);
            cleared = true;
        }
        List<AbstractResearchTopic<?>> history = knowledge.getResearchTopicHistory();
        if (history.stream().anyMatch(stale)) {
            knowledge.setResearchTopicHistory(history.stream().filter(stale.negate()).toList());
            cleared = true;
        }
        return cleared;
    }
}
