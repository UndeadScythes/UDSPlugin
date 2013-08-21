package com.undeadscythes.udsplugin.eventhandlers;

import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.utilities.*;
import org.bukkit.event.*;
import org.bukkit.event.hanging.*;

/**
 * @author UndeadScythes
 */
public class HangingPlace extends ListenerWrapper implements Listener {
    @EventHandler
    public void onEvent(final HangingPlaceEvent event) {
        final Member player = PlayerUtils.getOnlinePlayer(event.getPlayer());
        if(!player.canBuildHere(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendNormal(Message.CANT_BUILD_HERE);
        }
    }
}
