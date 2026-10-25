package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.ConstantsPMX;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Registers every game test of the mod with NeoForge. The tests themselves are static methods listed in per-feature
 * tables; to add a test, add a row to its feature's table. NeoForge only keeps the tests whose structure template is
 * in a namespace listed in the {@code neoforge.enabledGameTestNamespaces} system property, which the run configs set
 * to this mod's id, so every template must be in this mod's namespace.
 */
@GameTestHolder(ConstantsPMX.MOD_ID)
public class GameTestsPMXNeoforge {
    @GameTestGenerator
    public static Collection<TestFunction> allTests() {
        List<TestEntryPMX> entries = Stream.of(
                        CoreTestsPMX.entries(),
                        FamiliarTestsPMX.entries(),
                        FamiliarTestsPMXNeoforge.entries(),
                        PortalTestsPMX.entries(),
                        FeatureTestsPMX.entries(),
                        FeatureTestsPMXNeoforge.entries(),
                        AffinityTestsPMX.entries())
                .flatMap(List::stream)
                .toList();

        // A duplicate name would make one test silently replace another
        Set<String> names = new HashSet<>();
        for (TestEntryPMX entry : entries) {
            if (!names.add(entry.name())) {
                throw new IllegalStateException("Duplicate game test name " + entry.name());
            }
        }
        return entries.stream().map(TestEntryPMX::toTestFunction).toList();
    }
}
