package com.undeadscythes.udsplugin.eventhandlers;

import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.Region.RegionFlag;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

/**
 * A block is destroyed by fire.
 * @author UndeadScythes
 */
public class BlockBurn extends ListenerWrapper implements Listener {
    @EventHandler
    public void onEvent(BlockBurnEvent event) {
        if(!hasFlag(event.getBlock().getLocation(), RegionFlag.FIRE)) {
            event.setCancelled(true);
        }
    }
}
