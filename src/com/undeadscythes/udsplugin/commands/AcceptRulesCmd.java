package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.SaveablePlayer.PlayerRank;
import org.bukkit.*;

/**
 * Lets a player get build rights and promotes them to member. Ignores extra arguments.
 * @author UndeadScythes
 */
public class AcceptRulesCmd extends PlayerCommandExecutor {
    /**
     * @inheritDocs
     */
    @Override
    public void playerExecute(SaveablePlayer player, String[] args) {
        if(canAfford(Config.BUILD_COST)) {
            player.setRank(PlayerRank.MEMBER);
            player.debit(Config.BUILD_COST);
            Bukkit.broadcastMessage(Color.BROADCAST + player.getDisplayName() + " has accepted the rules.");
            player.sendMessage(Color.MESSAGE + "Thanks for accepting the rules, enjoy your stay on " + Bukkit.getServerName() + ".");
        }
    }

}