package com.undeadscythes.udsplugin.eventhandlers;

import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.Region.RegionFlag;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;

/**
 * Description.
 * @author UndeadScythes
 */
public class EntityInteract extends ListenerWrapper implements Listener {
    @EventHandler
    public void onEvent(EntityInteractEvent event) {
        Block block = event.getBlock();
        event.setCancelled(hasFlag(block.getLocation(), RegionFlag.PROTECTION) && block.getType() == Material.SOIL);
    }
}