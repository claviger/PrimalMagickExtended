package com.claviger.primalmagickextended.test.features;

import com.claviger.primalmagickextended.common.config.ConfigPMXNeoforge;
import com.claviger.primalmagickextended.common.features.FeaturePMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.verdantartifice.primalmagick.common.capabilities.PlayerKnowledge;
import com.verdantartifice.primalmagick.common.registries.RegistryKeysPM;
import com.verdantartifice.primalmagick.common.research.keys.ResearchEntryKey;
import com.verdantartifice.primalmagick.common.research.topics.EntryResearchTopic;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.connection.ConnectionType;

/**
 * NeoForge-side tests for the feature switches: the defaults declared in the config spec, and the knowledge sync
 * packet carrying research keys left behind by a disabled feature.
 */
public class FeatureToggleNeoforgeTests extends AbstractBaseTest {
    public static void feature_switches_default_to_enabled(GameTestHelper helper) {
        assertValueEqual(helper, FeaturePMX.values().length, ConfigPMXNeoforge.FEATURES.size(), "Feature switch count");
        for (FeaturePMX feature : FeaturePMX.values()) {
            String name = feature.getSerializedName();
            assertTrue(helper, ConfigPMXNeoforge.FEATURES.get(feature).getDefault(), name + " does not default to enabled");
            assertValueEqual(helper, name, ConfigPMXNeoforge.FEATURES.get(feature).getPath().getLast(), name + " option name");
        }
        helper.succeed();
    }

    public static void stale_research_key_survives_sync_packet(GameTestHelper helper) {
        ResearchEntryKey staleKey = new ResearchEntryKey(ResourceKey.create(RegistryKeysPM.RESEARCH_ENTRIES, ResourceUtilsPMX.loc("no_such_entry")));
        PlayerKnowledge knowledge = new PlayerKnowledge();
        knowledge.addResearch(staleKey);
        knowledge.setLastResearchTopic(new EntryResearchTopic(staleKey, 1));

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
        PlayerKnowledge.STREAM_CODEC.encode(buf, knowledge);
        PlayerKnowledge synced = PlayerKnowledge.STREAM_CODEC.decode(buf);
        assertTrue(helper, synced.isResearchKnown(staleKey), "Stale key lost in the sync packet");
        assertValueEqual(helper, knowledge.getLastResearchTopic(), synced.getLastResearchTopic(), "Last topic in the sync packet");
        helper.succeed();
    }
}
