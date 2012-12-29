package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;

/**
 * Allows players to use /admin help.
 * @author UndeadScythes
 */
public class AdminCmd extends CommandWrapper {
    @Override
    public void playerExecute() {
        if(args.length == 0) {
            sendHelp(1);
        } else {
            subCmdHelp();
        }
    }
}
