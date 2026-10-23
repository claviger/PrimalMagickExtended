package com.claviger.primalmagickextended.platform.services.registries;

import com.verdantartifice.primalmagick.platform.services.registries.IRegistryService;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.Consumer;

/**
 * Registry service for this mod's game test functions.
 */
public interface ITestFunctionRegistryServicePMX extends IRegistryService<Consumer<GameTestHelper>> {
}
