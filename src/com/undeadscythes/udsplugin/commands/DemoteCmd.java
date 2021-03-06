package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;

/**
 * Demote a player by a single rank.
 * 
 * @author UndeadScythes
 */
public class DemoteCmd extends CommandHandler {
    @Override
    public final void playerExecute() {
        final SaveablePlayer target;
        if(numArgsHelp(1) && (target = matchPlayer(arg(0))) != null && notSelf(target)) {
            final PlayerRank rank;
            if(player().outRanks(target) && (rank = target.demote()) != null) {
                player().sendNormal(target.getNick() + " has been demoted to " + rank.toString() + ".");
                target.sendNormal("You have been demoted to " + rank.toString() + ".");
            } else {
                player().sendError("You can't demote this player any further.");
            }
        }
    }
}
