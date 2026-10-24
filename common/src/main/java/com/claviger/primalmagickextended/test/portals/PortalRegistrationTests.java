package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.client.fx.particles.ParticleTypesPMX;
import com.claviger.primalmagickextended.common.blocks.BlocksPMX;
import com.claviger.primalmagickextended.common.blocks.portals.AbstractRuneBlock;
import com.claviger.primalmagickextended.common.blocks.portals.PortalBlock;
import com.claviger.primalmagickextended.common.items.ItemsPMX;
import com.claviger.primalmagickextended.common.items.portals.PortalCatalystItem;
import com.claviger.primalmagickextended.common.items.portals.RuneBlockItem;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.tiles.BlockEntityTypesPMX;
import com.claviger.primalmagickextended.common.util.ResourceUtilsPMX;
import com.claviger.primalmagickextended.test.AbstractBaseTestPMX;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

/**
 * Tests that the portal blocks, rune blocks, their items, the portal block entity type and the portal particle types
 * are registered under the expected ids with Custom Portals' properties.
 */
public class PortalRegistrationTests extends AbstractBaseTestPMX {
    public static void portal_blocks_and_items_are_registered(GameTestHelper helper) {
        for (PortalColor color : PortalColor.values()) {
            String name = color.getSerializedName();
            PortalBlock block = BlocksPMX.portal(color);
            assertTrue(helper, block != null, "Missing portal block for " + name);
            assertValueEqual(helper, ResourceUtilsPMX.loc(name + "_portal"), BuiltInRegistries.BLOCK.getKey(block), "portal block id for " + name);
            assertTrue(helper, block.defaultMapColor() == color.mapColor(), "Portal block map colour for " + name);

            Item catalyst = ItemsPMX.catalyst(color);
            assertInstanceOf(helper, catalyst, PortalCatalystItem.class, "Catalyst for " + name + " should be a portal catalyst");
            assertValueEqual(helper, ResourceUtilsPMX.loc(name + "_portal_catalyst"), BuiltInRegistries.ITEM.getKey(catalyst), "catalyst id for " + name);
            assertValueEqual(helper, 16, catalyst.getDefaultMaxStackSize(), "catalyst max stack size for " + name);
        }
        assertValueEqual(helper, PortalColor.values().length, BlocksPMX.portals().size(), "portal block count");
        helper.succeed();
    }

    public static void rune_blocks_and_items_are_registered(GameTestHelper helper) {
        Map<String, Block> blocks = Map.of(
                "haste_rune", BlocksPMX.HASTE_RUNE_BLOCK.get(),
                "gate_rune", BlocksPMX.GATE_RUNE_BLOCK.get(),
                "weak_enhancer_rune", BlocksPMX.WEAK_ENHANCER_RUNE_BLOCK.get(),
                "strong_enhancer_rune", BlocksPMX.STRONG_ENHANCER_RUNE_BLOCK.get(),
                "infinity_rune", BlocksPMX.INFINITY_RUNE_BLOCK.get());
        Map<String, Item> items = Map.of(
                "haste_rune", ItemsPMX.HASTE_RUNE.get(),
                "gate_rune", ItemsPMX.GATE_RUNE.get(),
                "weak_enhancer_rune", ItemsPMX.WEAK_ENHANCER_RUNE.get(),
                "strong_enhancer_rune", ItemsPMX.STRONG_ENHANCER_RUNE.get(),
                "infinity_rune", ItemsPMX.INFINITY_RUNE.get());
        for (String name : List.of("haste_rune", "gate_rune", "weak_enhancer_rune", "strong_enhancer_rune", "infinity_rune")) {
            Block block = blocks.get(name);
            assertInstanceOf(helper, block, AbstractRuneBlock.class, name + " block should be a rune block");
            assertValueEqual(helper, ResourceUtilsPMX.loc(name + "_block"), BuiltInRegistries.BLOCK.getKey(block), name + " block id");

            Item item = items.get(name);
            assertValueEqual(helper, ResourceUtilsPMX.loc(name), BuiltInRegistries.ITEM.getKey(item), name + " item id");
            assertValueEqual(helper, 1, item.getDefaultMaxStackSize(), name + " max stack size");
            BlockItem blockItem = assertInstanceOf(helper, item, RuneBlockItem.class, name + " item should be a rune block item");
            assertTrue(helper, blockItem.getBlock() == block, name + " item should place its rune block");
            // The rune items are named apart from their blocks, so the item keeps its own key
            assertValueEqual(helper, "item.primalmagickextended." + name, item.getDescriptionId(), name + " item description id");
            assertFalse(helper, item.getDescriptionId().equals(block.getDescriptionId()), name + " item description id should differ from its block's");
        }
        helper.succeed();
    }

    public static void portal_block_entity_and_particles_are_registered(GameTestHelper helper) {
        assertValueEqual(helper, ResourceUtilsPMX.loc("portal_block_entity"), BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(BlockEntityTypesPMX.PORTAL.get()), "portal block entity id");
        for (PortalColor color : PortalColor.values()) {
            String name = color.getSerializedName();
            PortalBlock block = BlocksPMX.portal(color);
            assertTrue(helper, BlockEntityTypesPMX.PORTAL.get().isValid(block.defaultBlockState()), "Portal block entity should be valid for " + name);

            ResourceLocation particleId = ResourceUtilsPMX.loc(name + "_portal_particle");
            ParticleOptions particle = ParticleTypesPMX.particle(color);
            if (color == PortalColor.PURPLE) {
                assertFalse(helper, BuiltInRegistries.PARTICLE_TYPE.containsKey(particleId), "Purple should have no particle type of its own");
                assertTrue(helper, particle == ParticleTypes.PORTAL, "Purple portals should use the vanilla portal particle");
            } else {
                assertTrue(helper, BuiltInRegistries.PARTICLE_TYPE.containsKey(particleId), "Missing particle type " + particleId);
                assertTrue(helper, particle.getType() == BuiltInRegistries.PARTICLE_TYPE.get(particleId), "Particle for " + name + " should be " + particleId);
            }
        }
        helper.succeed();
    }
}
