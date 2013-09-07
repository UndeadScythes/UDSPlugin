package com.undeadscythes.udsplugin.eventhandlers;

import com.undeadscythes.udsplugin.members.*;
import com.undeadscythes.udsplugin.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

/**
 * @author UndeadScythes
 */
public class BlockPlace extends ListenerWrapper implements Listener {
    @EventHandler
    public void onEvent(final BlockPlaceEvent event) {
        final Member player = MemberUtils.getOnlineMember(event.getPlayer());
        if(!player.canBuildHere(event.getBlock().getLocation())) {
            player.sendMessage(Message.CANT_BUILD_HERE);
            event.setCancelled(true);
        } else if(event.getBlock().getType().equals(Material.ENDER_CHEST) && UDSPlugin.getWorldMode(player.getWorld()).equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }
}