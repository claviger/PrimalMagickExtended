package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.portals.PortalHasteMode;
import com.claviger.primalmagickextended.common.portals.PortalRedstoneMode;
import com.claviger.primalmagickextended.platform.ServicesPMX;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.List;

/**
 * Tests that the portal configuration service is available and reports Custom Portals' defaults.
 */
public class PortalConfigTests extends AbstractBaseTestPMX {
    public static void config_service_reports_portal_defaults(GameTestHelper helper) {
        var config = ServicesPMX.CONFIG;
        assertFalse(helper, config.portalsUnlimitedRange(), "unlimitedRange default");
        assertFalse(helper, config.portalsAlwaysInterdimensional(), "alwaysInterdim default");
        assertValueEqual(helper, PortalHasteMode.CREATIVE, config.portalsAlwaysHaste(), "alwaysHaste default");
        assertFalse(helper, config.portalsPrivate(), "privatePortals default");
        assertValueEqual(helper, PortalRedstoneMode.OFF, config.portalsRedstone(), "redstone default");
        assertValueEqual(helper, 100, config.portalsDefaultRange(), "defaultRange default");
        assertValueEqual(helper, 1000, config.portalsRangeWithEnhancer(), "rangeWithEnhancer default");
        assertValueEqual(helper, 10000, config.portalsRangeWithStrongEnhancer(), "rangeWithStrongEnhancer default");
        assertFalse(helper, config.portalsFrameFilterIsWhitelist(), "isWhitelist default");
        assertValueEqual(helper, List.of(), List.copyOf(config.portalsFilteredFrameBlocks()), "filteredBlocks default");
        assertFalse(helper, config.portalsMuteAmbientSounds(), "muteAmbientSounds default");
        assertFalse(helper, config.portalsMuteTeleportSounds(), "muteTeleportSounds default");
        helper.succeed();
    }
}
