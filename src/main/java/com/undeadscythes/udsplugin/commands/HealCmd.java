package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.members.*;
import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.exceptions.*;

/**
 * @author UndeadScythes
 */
public class HealCmd extends CommandHandler {
    @Override
    public void playerExecute() throws PlayerNotOnlineException {
        Member target;
        final String message = "You have been healed.";
        if(args.length == 0) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.sendNormal(message);
        } else if(numArgsHelp(1) && (target = matchOnlinePlayer(args[0])) != null) {
            target.setHealth(target.getMaxHealth());
            target.setFoodLevel(20);
            target.sendNormal(message);
            if(!player.equals(target)) {
                player.sendNormal(target.getNick() + " has been healed.");
            }
        }
    }

}