package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;

/**
 * Promote a player by a single rank.
 * @author UndeadScythes
 */
public class PromoteCmd extends AbstractPlayerCommand {
    @Override
    public void playerExecute() {
        SaveablePlayer target;
        if(numArgsHelp(1) && (target = getMatchingPlayer(args[0])) != null && notSelf(target)) {
            PlayerRank rank;
            if(player.getRank().compareTo(target.getRank()) > 0 && (rank = target.promote()) != null) {
                player.sendMessage(Color.MESSAGE + target.getNick() + " has been promoted to " + rank.toString() + ".");
                target.sendMessage(Color.MESSAGE + "You have been promoted to " + rank.toString() + ".");
            } else {
                player.sendMessage(Color.ERROR + "You can't promote this player any further.");
            }
        }
    }
}
