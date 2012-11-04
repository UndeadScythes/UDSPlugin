package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;

/**
 * Spy a players inventory.
 * @author UndeadScythes
 */
public class InvSeeCmd extends PlayerCommandExecutor {
    /**
     * @inheritDocs
     */
    @Override
    public void playerExecute(SaveablePlayer player, String[] args) {
        SaveablePlayer target;
        if(argsLessEq(1)) {
            if(args.length == 0) {
                if(player.getInventoryCopy() != null) {
                    player.loadInventory();
                    player.loadArmor();
                    player.sendMessage(Color.MESSAGE + "Your inventory has been restored.");
                } else {
                    player.sendMessage(Color.ERROR + "You have no saved inventory.");
                }
            } else if((target = matchesPlayer(args[0])) != null && isOnline(target) && notSelf(target)) {
                if(player.getInventoryCopy() == null) {
                    player.saveInventory();
                    player.saveArmor();
                    player.getInventory().setContents(target.getInventory().getContents());
                    player.getInventory().setArmorContents(target.getInventory().getArmorContents());
                } else {
                    player.getInventory().setContents(target.getInventory().getContents());
                    player.getInventory().setArmorContents(target.getInventory().getArmorContents());
                }
                player.sendMessage(Color.MESSAGE + "You now have a copy of " + target.getDisplayName() + "'s inventory.");
            }
        }
    }
}
