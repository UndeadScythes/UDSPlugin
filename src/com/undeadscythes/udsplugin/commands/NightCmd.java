package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.CommandHandler;

/**
 * Set the world the players is in to night.
 * 
 * @author UndeadScythes
 */
public class NightCmd extends CommandHandler {
    @Override
    public final void playerExecute() {
        player().getWorld().setTime(14000);
        player().sendNormal("Summoning the moon.");
    }

}
