package com.undeadscythes.udsplugin.commands;


import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.*;

/**
 * @author UndeadScythes
 */
public class ACmd extends CommandHandler {
    @Override
    public void playerExecute() {
        if(args.length == 0) {
            if(player.toggleChannel(ChatChannel.ADMIN)) {
                player.sendNormal("You are now talking in admin chat.");
            } else {
                player.sendMessage(Message.PUBLIC_CHAT);
            }
        } else {
            player.chat(ChatChannel.ADMIN, argsToMessage());
        }
    }
}
