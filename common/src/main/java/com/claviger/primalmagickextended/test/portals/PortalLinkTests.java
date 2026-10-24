package com.claviger.primalmagickextended.test.portals;

import com.claviger.primalmagickextended.common.portals.CustomPortal;
import com.claviger.primalmagickextended.common.portals.PortalColor;
import com.claviger.primalmagickextended.common.portals.PortalRegistry;
import com.verdantartifice.primalmagick.test.AbstractBaseTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tests the portal link rules (colour, frame, range per rune tier, gate rule, Nether scaling), the haste delay and
 * registry relinking. These are pure logic tests on {@link CustomPortal} objects and rely on the default server
 * config.
 */
public class PortalLinkTests extends AbstractBaseTest {
    private static final String OVERWORLD = "minecraft:overworld";
    private static final String NETHER = "minecraft:the_nether";
    private static final String END = "minecraft:the_end";
    private static final String STONE = "minecraft:stone";
    private static final String COBBLESTONE = "minecraft:cobblestone";

    private static CustomPortal portal(String dim, int x, int z, PortalColor color, String frame, int haste, int gate, int weak, int strong, int infinity) {
        return new CustomPortal(frame, dim, color.mapColor(), new BlockPos(x, 64, z), new ArrayList<>(List.of(new BlockPos(x, 64, z))),
                0.5F, 0.5F, UUID.randomUUID(), haste, gate, weak, strong, infinity);
    }

    private static CustomPortal plain(String dim, int x, int z) {
        return portal(dim, x, z, PortalColor.RED, STONE, 0, 0, 0, 0, 0);
    }

    /** Links {@code a} with {@code b} and returns whether they ended up linked to each other both ways. */
    private static boolean link(CustomPortal a, CustomPortal b) {
        a.tryLink(b);
        return a.getLinked() == b && b.getLinked() == a;
    }

    private static boolean unlinked(CustomPortal a, CustomPortal b) {
        return a.getLinked() == null && b.getLinked() == null;
    }

    public static void same_colour_and_frame_within_default_range_link(GameTestHelper helper) {
        CustomPortal a = plain(OVERWORLD, 0, 0);
        CustomPortal b = plain(OVERWORLD, 99, 0);
        assertTrue(helper, link(a, b), "Portals 99 blocks apart should link");

        CustomPortal c = plain(OVERWORLD, 0, 0);
        CustomPortal d = plain(OVERWORLD, 100, 0);
        assertTrue(helper, link(c, d), "Portals exactly 100 blocks apart should link");
        helper.succeed();
    }

    public static void beyond_default_range_does_not_link(GameTestHelper helper) {
        CustomPortal a = plain(OVERWORLD, 0, 0);
        CustomPortal b = plain(OVERWORLD, 101, 0);
        a.tryLink(b);
        assertTrue(helper, unlinked(a, b), "Portals 101 blocks apart should not link");
        helper.succeed();
    }

    public static void weak_enhancer_extends_range_to_one_thousand(GameTestHelper helper) {
        CustomPortal a1 = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 0, 1, 0, 0);
        CustomPortal b1 = plain(OVERWORLD, 999, 0);
        assertTrue(helper, link(a1, b1), "Enhanced portals 999 blocks apart should link");

        CustomPortal a3 = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 0, 1, 0, 0);
        CustomPortal b3 = plain(OVERWORLD, 1000, 0);
        assertTrue(helper, link(a3, b3), "Enhanced portals exactly 1000 blocks apart should link");

        CustomPortal a2 = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 0, 1, 0, 0);
        CustomPortal b2 = plain(OVERWORLD, 1001, 0);
        a2.tryLink(b2);
        assertTrue(helper, unlinked(a2, b2), "Enhanced portals 1001 blocks apart should not link");
        helper.succeed();
    }

    public static void strong_enhancer_extends_range_to_ten_thousand(GameTestHelper helper) {
        CustomPortal a1 = plain(OVERWORLD, 0, 0);
        CustomPortal b1 = portal(OVERWORLD, 9999, 0, PortalColor.RED, STONE, 0, 0, 0, 1, 0);
        assertTrue(helper, link(a1, b1), "Strongly enhanced portals 9999 blocks apart should link");

        CustomPortal a3 = plain(OVERWORLD, 0, 0);
        CustomPortal b3 = portal(OVERWORLD, 10000, 0, PortalColor.RED, STONE, 0, 0, 0, 1, 0);
        assertTrue(helper, link(a3, b3), "Strongly enhanced portals exactly 10000 blocks apart should link");

        CustomPortal a2 = plain(OVERWORLD, 0, 0);
        CustomPortal b2 = portal(OVERWORLD, 10001, 0, PortalColor.RED, STONE, 0, 0, 0, 1, 0);
        a2.tryLink(b2);
        assertTrue(helper, unlinked(a2, b2), "Strongly enhanced portals 10001 blocks apart should not link");
        helper.succeed();
    }

    public static void infinity_rune_has_unlimited_range(GameTestHelper helper) {
        CustomPortal a = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 0, 0, 0, 1);
        CustomPortal b = plain(OVERWORLD, 5000000, 0);
        assertTrue(helper, link(a, b), "Infinity rune portals 5,000,000 blocks apart should link");
        helper.succeed();
    }

    public static void different_colour_does_not_link(GameTestHelper helper) {
        CustomPortal a = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 0, 0, 0, 0);
        CustomPortal b = portal(OVERWORLD, 10, 0, PortalColor.BLUE, STONE, 0, 0, 0, 0, 0);
        a.tryLink(b);
        assertTrue(helper, unlinked(a, b), "Portals of different colours should not link");
        helper.succeed();
    }

    public static void different_frame_does_not_link(GameTestHelper helper) {
        CustomPortal a = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 0, 0, 0, 0);
        CustomPortal b = portal(OVERWORLD, 10, 0, PortalColor.RED, COBBLESTONE, 0, 0, 0, 0, 0);
        a.tryLink(b);
        assertTrue(helper, unlinked(a, b), "Portals of different frames should not link");
        helper.succeed();
    }

    public static void cross_dimension_needs_gate_rune(GameTestHelper helper) {
        CustomPortal a1 = plain(OVERWORLD, 0, 0);
        CustomPortal b1 = plain(END, 0, 0);
        a1.tryLink(b1);
        assertTrue(helper, unlinked(a1, b1), "Portals in different dimensions without a gate rune should not link");

        CustomPortal a2 = plain(OVERWORLD, 0, 0);
        CustomPortal b2 = portal(END, 0, 0, PortalColor.RED, STONE, 0, 1, 0, 0, 0);
        assertTrue(helper, link(a2, b2), "Portals in different dimensions with a gate rune should link");
        helper.succeed();
    }

    public static void nether_coordinates_scale_by_eight(GameTestHelper helper) {
        CustomPortal a1 = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 1, 0, 0, 0);
        CustomPortal b1 = plain(NETHER, 12, 0);
        assertTrue(helper, link(a1, b1), "Nether portal at x=12 (96 scaled) should link");

        CustomPortal a2 = portal(OVERWORLD, 0, 0, PortalColor.RED, STONE, 0, 1, 0, 0, 0);
        CustomPortal b2 = plain(NETHER, 13, 0);
        a2.tryLink(b2);
        assertTrue(helper, unlinked(a2, b2), "Nether portal at x=13 (104 scaled) should not link");

        // The other branch: the Nether portal is the one calling tryLink.
        CustomPortal n3 = portal(NETHER, 12, 0, PortalColor.RED, STONE, 0, 1, 0, 0, 0);
        CustomPortal o3 = plain(OVERWORLD, 0, 0);
        assertTrue(helper, link(n3, o3), "Nether portal at x=12 (96 scaled) linking to the overworld should link");

        CustomPortal n4 = portal(NETHER, 13, 0, PortalColor.RED, STONE, 0, 1, 0, 0, 0);
        CustomPortal o4 = plain(OVERWORLD, 0, 0);
        n4.tryLink(o4);
        assertTrue(helper, unlinked(n4, o4), "Nether portal at x=13 (104 scaled) linking to the overworld should not link");
        helper.succeed();
    }

    public static void portal_linked_elsewhere_is_not_stolen(GameTestHelper helper) {
        CustomPortal a = plain(OVERWORLD, 0, 0);
        CustomPortal b = plain(OVERWORLD, 10, 0);
        CustomPortal c = plain(OVERWORLD, 20, 0);
        assertTrue(helper, link(a, b), "First pair should link");
        c.tryLink(b);
        assertTrue(helper, c.getLinked() == null, "Third portal should stay unlinked");
        assertTrue(helper, b.getLinked() == a, "Linked portal should keep its partner");
        assertTrue(helper, a.getLinked() == b, "Original portal should keep its partner");
        helper.succeed();
    }

    public static void haste_rune_sets_one_tick_delay(GameTestHelper helper) {
        CustomPortal a1 = plain(OVERWORLD, 0, 0);
        CustomPortal b1 = portal(OVERWORLD, 10, 0, PortalColor.RED, STONE, 1, 0, 0, 0, 0);
        assertTrue(helper, link(a1, b1), "Hasted pair should link");
        assertValueEqual(helper, 1, a1.getPlayerTeleportDelay(), "Delay with a haste rune on the partner");

        CustomPortal a2 = plain(OVERWORLD, 0, 0);
        CustomPortal b2 = plain(OVERWORLD, 10, 0);
        assertTrue(helper, link(a2, b2), "Plain pair should link");
        assertValueEqual(helper, 80, a2.getPlayerTeleportDelay(), "Delay without a haste rune");
        helper.succeed();
    }

    public static void registry_unregister_relinks_partner(GameTestHelper helper) {
        PortalRegistry registry = new PortalRegistry();
        CustomPortal a = plain(OVERWORLD, 0, 0);
        CustomPortal b = plain(OVERWORLD, 10, 0);
        CustomPortal c = plain(OVERWORLD, 20, 0);
        registry.register(a);
        registry.register(b);
        registry.register(c);
        assertTrue(helper, a.getLinked() == b && b.getLinked() == a, "First two registered portals should link");
        assertTrue(helper, c.getLinked() == null, "Third registered portal should stay unlinked");

        registry.unregister(a);
        assertTrue(helper, b.getLinked() == c && c.getLinked() == b, "Unregistering a portal should relink its partner");
        assertTrue(helper, registry.getPortalFromPos(OVERWORLD, new BlockPos(0, 64, 0)) == null, "Unregistered portal's blocks should be forgotten");
        assertTrue(helper, registry.getPortalFromPos(OVERWORLD, new BlockPos(10, 64, 0)) == b, "Remaining portal's blocks should still resolve");
        helper.succeed();
    }
}
