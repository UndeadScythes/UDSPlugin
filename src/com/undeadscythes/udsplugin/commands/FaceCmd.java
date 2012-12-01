package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;
import org.bukkit.*;

/**
 * Face a player in a certain direction or get the direction a player is facing.
 * @author UndeadScythes
 */
public class FaceCmd extends PlayerCommandExecutor {
    /**
     * @inheritDocs
     */
    @Override
    public void playerExecute(SaveablePlayer player, String[] args) {
        Bearing.Direction direction;
        if(args.length == 0) {
            player.sendMessage(Color.MESSAGE + "You are facing " + Bearing.Direction.valueOf(player.getLocation()).toString() + ".");
        } else if(numArgsHelp(1) && (direction = getDirection(args[0])) != null) {
            Location location = player.getLocation();
            location.setYaw(direction.getYaw());
            player.teleport(location);
        }
    }
}