package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;
import org.bukkit.util.*;

/**
 * Home region related commands.
 * @author UndeadScythes
 */
public class HomeCmd extends CommandWrapper {
    @Override
    public void playerExecute() {
        Region home;
        SaveablePlayer target;
        int price;
        final String subCmd = args[0].toLowerCase();
        if(args.length == 0) {
            if((home = getHome()) != null && notJailed() && notPinned()) {
                player.teleport(home.getWarp());
            }
        } else if(args.length == 1) {
            if(subCmd.equals("make")) {
                if(canAfford(UDSPlugin.getConfigInt(ConfigRef.HOME_COST)) && noHome()) {
                    final Vector min = player.getLocation().add(-10, 28, -10).toVector();
                    final Vector max = player.getLocation().add(10, 12, 10).toVector();
                    home = new Region(player.getName() + "home", min, max, player.getLocation(), player, "", RegionType.HOME);
                    if(noOverlaps(home)) {
                        player.debit(UDSPlugin.getConfigInt(ConfigRef.HOME_COST));
                        UDSPlugin.getRegions(RegionType.GENERIC).put(home.getName(), home);
                        UDSPlugin.getRegions(RegionType.HOME).put(home.getName(), home);
                        home.placeCornerMarkers();
                        player.sendMessage(Color.MESSAGE + "Home area protected.");
                    }
                }
            } else if(subCmd.equals("clear")) {
                if((home = getHome()) != null) {
                    UDSPlugin.getRegions(RegionType.GENERIC).remove(home.getName());
                    UDSPlugin.getRegions(RegionType.HOME).remove(home.getName());
                    player.sendMessage(Color.MESSAGE + "Home protection removed.");
                }
            } else if(subCmd.equals("set")) {
                if((home = getHome()) != null) {
                    home.setWarp(player.getLocation());
                    player.sendMessage(Color.MESSAGE + "Home warp point set.");
                }
            } else if(subCmd.equals("roomies")) {
                String message = "";
                for(Region otherHome : UDSPlugin.getRegions(RegionType.HOME).values()) {
                    if(otherHome.hasMember(player)) {
                        message = message.concat(otherHome.getOwner().getNick() + ", ");
                    }
                    if(!message.isEmpty()) {
                        player.sendMessage(Color.MESSAGE + "You are room mates with:");
                        player.sendMessage(Color.TEXT + message.substring(0, message.length() - 2));
                    }
                    message = "";
                    if((home = UDSPlugin.getRegions(RegionType.HOME).get(player.getName() + "home")) != null) {
                        for(SaveablePlayer member : home.getMembers()) {
                            message = message.concat(member.getNick() + ", ");
                        }
                    }
                    if(message.equals("")) {
                        player.sendMessage(Color.MESSAGE + "You have no room mates.");
                    } else {
                        player.sendMessage(Color.MESSAGE + "Your room mates are:");
                        player.sendMessage(Color.TEXT + message.substring(0, message.length() - 2));
                    }
                }
            } else if(subCmd.equals("lock")) {
                if((home = getHome()) != null) {
                    home.setFlag(RegionFlag.LOCK);
                    player.sendMessage(Color.MESSAGE + "Your home is now locked.");
                }
            } else if(subCmd.equals("unlock")) {
                if((home = getHome()) != null) {
                    home.setFlag(RegionFlag.LOCK);
                    home.toggleFlag(RegionFlag.LOCK);
                    player.sendMessage(Color.MESSAGE + "Your home is now unlocked.");
                }
            } else if(subCmd.equals("help")) {
                sendHelp(1);
            } else if((target = getMatchingPlayer(args[0])) != null && (home = getHome(target)) != null && (isRoomie(home) || hasPerm(Perm.HOME_OTHER)) && notJailed() && notPinned()) {
                player.teleport(home.getWarp());
            }
        } else if(args.length == 2) {
            Direction direction;
            if(subCmd.equals("expand")) {
                if((home = getHome()) != null && canAfford(UDSPlugin.getConfigInt(ConfigRef.EXPAND_COST)) && (direction = getCardinalDirection(args[1])) != null) {
                    home.expand(direction, 1);
                    if(noOverlaps(home)) {
                        player.debit(UDSPlugin.getConfigInt(ConfigRef.EXPAND_COST));
                        player.sendMessage(Color.MESSAGE + "Your home has been expanded.");
                    } else {
                        home.expand(direction, -1);
                    }
                }
            } else if(subCmd.equals("boot")) {
                if((home = getHome()) != null && (target = getMatchingPlayer(args[1])) != null && isOnline(target) && isInHome(target, home)) {
                    target.teleport(player.getWorld().getSpawnLocation());
                    target.sendMessage(Color.MESSAGE + player.getNick() + " has booted you from their home.");
                    player.sendMessage(Color.MESSAGE + target.getNick() + " has been booted.");
                }
            } else if(subCmd.equals("add")) {
                if((target = getMatchingPlayer(args[1])) != null && (home = getHome()) != null) {
                    home.addMember(target);
                    player.sendMessage(Color.MESSAGE + target.getNick() + " has been added as your room mate.");
                    if(target.isOnline()) {
                        target.sendMessage(Color.MESSAGE + "You have been added as " + player.getNick() + "'s room mate.");
                    }
                }
            } else if(subCmd.equals("kick")) {
                if((target = getMatchingPlayer(args[1])) != null && (home = getHome()) != null && isRoomie(target, home)) {
                    home.delMember(target);
                    player.sendMessage(Color.MESSAGE + target.getNick() + " is no longer your room mate.");
                    if(target.isOnline()) {
                        target.sendMessage(Color.MESSAGE + "You are no longer " + player.getNick() + "'s room mate.");
                    }
                }
            } else {
                subCmdHelp();
            }
        } else if(numArgsHelp(3)) {
            if(subCmd.equals("sell")) {
                if((getHome()) != null && (target = getMatchingPlayer(args[1])) != null && canRequest(target) && isOnline(target) && (price = parseInt(args[2])) != -1) {
                    player.sendMessage(Message.REQUEST_SENT);
                    target.sendMessage(Color.MESSAGE + player.getNick() + " wants to sell you their house for " + price + " " + UDSPlugin.getConfigInt(ConfigRef.CURRENCIES) + ".");
                    target.sendMessage(Message.REQUEST_Y_N);
                    UDSPlugin.getRequests().put(target.getName(), new Request(player, RequestType.HOME, price, target));
                }
            } else {
                subCmdHelp();
            }
        }
    }
}
