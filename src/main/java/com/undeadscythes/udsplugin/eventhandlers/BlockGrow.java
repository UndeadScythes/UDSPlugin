package com.undeadscythes.udsplugin.eventhandlers;

import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.regions.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

/**
 * Fired when a block grows.
 * 
 * @author UndeadScythes
 */
public class BlockGrow extends ListenerWrapper implements Listener {
    @EventHandler
    public void onEvent(final BlockGrowEvent event) {
        if(!hasFlag(event.getNewState().getLocation(), RegionFlag.VINES)) {
            event.setCancelled(true);
        }
    }
}