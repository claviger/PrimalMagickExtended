package com.claviger.primalmagickextended.test;

import com.claviger.primalmagickextended.test.familiars.FamiliarEventListenerTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Game test table for the familiars' NeoForge-only code: the event listeners.
 */
public class FamiliarTestsPMXNeoforge {
    public static List<TestEntryPMX> entries() {
        List<TestEntryPMX> entries = new ArrayList<>();
        entries.add(TestEntryPMX.of("familiar_data_is_resent_on_login_respawn_and_dimension_change", FamiliarEventListenerTests::familiar_data_is_resent_on_login_respawn_and_dimension_change));

        return entries;
    }
}
