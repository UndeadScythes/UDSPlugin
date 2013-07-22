package com.undeadscythes.udsplugin.eventhandlers;

import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.commands.JailCmd;
import java.io.*;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

/**
 * When a player chats in game.
 * @author UndeadScythes
 */
public class AsyncPlayerChat implements Listener {
    @EventHandler
    public void onEvent(final AsyncPlayerChatEvent event) throws IOException {
        event.setCancelled(true);
        final SaveablePlayer player = UDSPlugin.getOnlinePlayers().get(event.getPlayer().getName());
        final String logMessage = player.getNick() + ": " + event.getMessage();
        Bukkit.getLogger().info(logMessage);
        if(!player.newChat()) {
            player.sendMessage(Color.ERROR + "You have been jailed for spamming chat.");
            Bukkit.broadcastMessage(Color.BROADCAST + player.getNick() + " gets jail time for spamming chat.");
            JailCmd.jail(player, 5, 1000);
        } else if(player.getChannel() == ChatChannel.PUBLIC) {
            String message = event.getMessage();
            if(Censor.noCensor(message)) {
                message = (player.getRankColor() + player.getNick() + ": " + Color.TEXT).concat(message);
            } else {
                player.sendMessage(Color.ERROR + "Please do not use bad language.");
                message = player.getRankColor() + player.getNick() + ": " + Color.TEXT + Censor.fix(message);
                if(player.canAfford(1)) {
                    player.debit(1);
                    Bukkit.broadcastMessage(Color.BROADCAST + player.getNick() + " put 1 " + UDSPlugin.getConfigString(ConfigRef.CURRENCY) + " in the swear jar.");
                } else {
                    player.sendMessage(Color.ERROR + "You have no money to put in the swear jar.");
                    Bukkit.broadcastMessage(Color.BROADCAST + player.getNick() + " gets jail time for using bad language.");
                    JailCmd.jail(player, 1, 1);
                }
            }
            for(SaveablePlayer target : UDSPlugin.getOnlinePlayers().values()) {
                if(!target.isIgnoringPlayer(player)) {
                    target.sendMessage(message);
                }
            }
        } else if(player.getChannel() == ChatChannel.ADMIN) {
            final String message = PlayerRank.ADMIN.getColor() + "[ADMIN] " + player.getNick() + ": " + event.getMessage();
            for(SaveablePlayer target : UDSPlugin.getOnlinePlayers().values()) {
                if(target.hasRank(PlayerRank.MOD)) {
                    target.sendMessage(message);
                }
            }
        } else if(player.getChannel() == ChatChannel.CLAN) {
            final Clan clan = player.getClan();
            final String message = Color.CLAN + "[" + clan.getName() + "] " + player.getNick() + ": " + event.getMessage();
            for(SaveablePlayer target : clan.getOnlineMembers()) {
                target.sendMessage(message);
            }
        } else if(player.getChannel() == ChatChannel.PRIVATE) {
            final ChatRoom chatRoom = player.getChatRoom();
            final String message = Color.PRIVATE + "[" + chatRoom.getName() + "] " + player.getNick() + ": " + event.getMessage();
            for(SaveablePlayer target : chatRoom.getOnlineMembers()) {
                target.sendMessage(message);
            }
        }
    }
}
