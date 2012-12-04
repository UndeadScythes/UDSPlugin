package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;

/**
 * Switches the current world to sunset.
 * @author UndeadScythes
 */
public class DayCmd extends AbstractPlayerCommand {
    @Override
    public void playerExecute(final SaveablePlayer player, final String[] args) {
        player.getWorld().setTime(0);
        player.sendMessage(Color.MESSAGE + "Summoning the sun.");
    }
}
