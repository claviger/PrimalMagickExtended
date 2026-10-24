package com.claviger.primalmagickextended.common.portals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import net.minecraft.core.BlockPos;

/**
 * The set of known portals, indexed by dimension and portal block position. Registering a portal links it with the
 * first compatible portal in registration order; unregistering one relinks its former partner. Ported from Custom
 * Portals.
 */
public class PortalRegistry {

    private List<CustomPortal> portals;
    private Map<String, Map<BlockPos, CustomPortal>> portalPositions;

    public PortalRegistry() {
        portals = new ArrayList<CustomPortal>();
        portalPositions = new HashMap<String, Map<BlockPos, CustomPortal>>();
    }
    
    public void register(CustomPortal portal) {
        tryWithAll(portal);
        Map<BlockPos, CustomPortal> positions = portalPositions.computeIfAbsent(portal.getDimensionId(), k -> new HashMap<BlockPos, CustomPortal>());
        for (BlockPos blockPos : portal.getPortalBlocks()) {
            positions.put(blockPos, portal);
        }
        portals.add(portal);
    }

    public void unregister(CustomPortal portal) {
        portals.remove(portal);
        if (portal.hasLinked()) {
            tryWithAll(portal.getLinked());
        }
        Map<BlockPos, CustomPortal> positions = portalPositions.get(portal.getDimensionId());
        if (positions != null) {
            for (BlockPos blockPos : portal.getPortalBlocks()) {
                positions.remove(blockPos);
            }
            if (positions.isEmpty())
                portalPositions.remove(portal.getDimensionId());
        }
    }

    public void tryWithAll(CustomPortal portal) {
        portal.setLinked(null);
        for (CustomPortal p : portals) {
            portal.tryLink(p);
        }
    }

    public void refreshPortals() {
        for (CustomPortal portal : portals) {
            tryWithAll(portal);
        }
    }

    public List<CustomPortal> getPortals() { return portals; }

    public CustomPortal getPortalFromPos(String dimensionId, BlockPos pos) {
        Map<BlockPos, CustomPortal> positions = portalPositions.get(dimensionId);
        return positions == null ? null : positions.get(pos);
    }
}
