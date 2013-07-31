package com.undeadscythes.udsplugin.commands;

import Comparators.*;
import com.undeadscythes.udsplugin.*;
import com.undeadscythes.udsplugin.utilities.*;
import java.text.*;
import java.util.*;
import org.bukkit.util.Vector;

/**
 * Various clan related commands.
 * @author UndeadScythes
 */
public class ClanCmd extends CommandValidator {
    @Override
    public void playerExecute() {
        Clan clan;
        Region base;
        SaveablePlayer target;
        if(args.length == 1) {
            if(subCmd.equals("base")) {
                if((clan = getClan()) != null && (base = getBase(clan)) != null && notJailed() && notPinned()) {
                    player.teleport(base.getWarp());
                }
            } else if(subCmd.equals("leave")) {
                if((clan = getClan()) != null) {
                    if(clan.delMember(player)) {
                        if((base = RegionUtils.getRegion(RegionType.BASE, clan.getName() + "base")) != null) {
                            base.delMember(player);
                        }
                        clan.sendMessage(player.getNick() + " has left the clan.");
                    } else {
                        ClanUtils.removeClan(clan);
                        RegionUtils.removeRegion(RegionType.BASE, clan.getName() + "base");
                        UDSPlugin.sendBroadcast(clan.getName() + " no longer exists as all members have left.");
                    }
                    player.setClan(null);
                    player.sendNormal("You have left " + clan.getName());
                }
            } else if(subCmd.equals("members")) {
                if((clan = getClan()) != null) {
                    if(!clan.getLeader().equals(player)) {
                        player.sendNormal("Your clan leader is " + clan.getLeader() + ".");
                        player.sendNormal("Your rank is " + clan.getRankOf(player));
                    }
                    boolean empty = true;
                    for(final ClanRank rank : ClanRank.values()) {
                        String members = "";
                        for(SaveablePlayer member : clan.getRankMembers(rank)) {
                            if(!member.equals(player)) {
                                members = members.concat(member.getNick() + ", ");
                            }
                        }
                        if(!members.isEmpty()) {
                            empty = false;
                            player.sendNormal(clan.getName() + "s " + rank.toString() + "s are:");
                            player.sendText(members.substring(0, members.length() - 2));
                        }
                    }
                    if(empty) {
                        player.sendNormal("Your clan has no other members.");
                    }
                }
            } else if(subCmd.equals("list")) {
                sendPage(1, player);
            } else if(subCmd.equals("disband")) {
                if((clan = getClan()) != null && isLeader(clan)) {
                    UDSPlugin.sendBroadcast(clan.getName() + " has been disbanded.");
                    ClanUtils.removeClan(clan);
                    clan.sendMessage("Your clan has been disbanded.");
                    for(SaveablePlayer member : clan.getMembers()) {
                        member.setClan(null);
                    }
                    RegionUtils.removeRegion(RegionType.BASE, clan.getName() + "base");
                }
            } else if(subCmd.equals("stats")) {
                if((clan = getClan()) != null) {
                    final DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    player.sendNormal(clan.getName() + "'s stats:");
                    player.sendListItem("Members: ", "" + clan.getMembers().size());
                    player.sendListItem("Kills: ", "" + clan.getKills());
                    player.sendListItem("Deaths: ", "" + clan.getDeaths());
                    player.sendListItem("KDR: ", decimalFormat.format(clan.getRatio()));
                }
            } else {
                subCmdHelp();
            }
        } else if(numArgsHelp(2)) {
            int page;
            if(subCmd.equals("new")) {
                if(isClanless() && canAfford(UDSPlugin.getConfigInt(ConfigRef.CLAN_COST)) && noCensor(args[1]) && notClan(args[1])) {
                    player.debit(UDSPlugin.getConfigInt(ConfigRef.CLAN_COST));
                    clan = new Clan(args[1], player);
                    player.setClan(clan);
                    ClanUtils.addClan(clan);
                    UDSPlugin.sendBroadcast(player.getNick() + " just created " + args[1] + ".");
                }
            } else if(subCmd.equals("invite")) {
                if((target = getMatchingPlayer(args[1])) != null && isOnline(target) && canRequest(target) && (clan = getClan()) != null && hasClanRank(clan, player) && notSelf(target)) {
                    UDSPlugin.addRequest(target.getName(), new Request(player, RequestType.CLAN, clan.getName(), target));
                    player.sendMessage(Message.REQUEST_SENT);
                    target.sendNormal(player.getNick() + " has invited you to join " + clan.getName() + ".");
                    target.sendMessage(Message.REQUEST_Y_N);
                }
            } else if(subCmd.equals("kick")) {
                if((target = getMatchingPlayer(args[1])) != null && (clan = getClan()) != null && isInClan(target, clan) && hasClanRank(clan, player) && notSelf(target)) {
                    clan.delMember(target);
                    target.setClan(null);
                    if((base = RegionUtils.getRegion(RegionType.BASE, clan.getName() + "base")) != null) {
                        base.delMember(target);
                    }
                    target.sendNormal(player.getNick() + " has kicked you out of " + clan.getName() + ".");
                    player.sendNormal(target.getNick() + " has been kicked out of your clan.");
                    clan.sendMessage(target.getNick() + " has left the clan.");
                }
            } else if(subCmd.equals("promote")) {
                if((target = getMatchingPlayer(args[1])) != null && (clan = getClan()) != null && isInClan(target, clan) && hasClanRank(clan, player) && notSelf(target)) {
                    if(clan.promote(target)) {
                        target.sendNormal("You have been promoted in clan " + clan.getName() + ".");
                        player.sendNormal(target.getNick() + " has been promoted.");
                        clan.sendMessage(target.getNick() + " has been promoted.");
                    } else {
                        player.sendError("That player cannot be promoted any further.");
                    }
                }
            } else if(subCmd.equals("demote")) {
                if((target = getMatchingPlayer(args[1])) != null && (clan = getClan()) != null && isInClan(target, clan) && hasClanRank(clan, player) && notSelf(target)) {
                    if(clan.demote(target)) {
                        target.sendNormal("You have been demoted in clan " + clan.getName() + ".");
                        player.sendNormal(target.getNick() + " has been promoted.");
                        clan.sendMessage(target.getNick() + " has been demoted.");
                    } else {
                        player.sendError("That player cannot be demoted any further.");
                    }
                }
            } else if(subCmd.equals("members")) {
                if((clan = getClan(args[1])) != null) {
                    String members = "";
                    for(SaveablePlayer member : clan.getMembers()) {
                        members = members.concat(member.getNick() + ", ");
                    }
                    player.sendNormal(clan.getName() + "'s leader is " + clan.getLeader().getNick() + ".");
                    player.sendNormal(clan.getName() + "'s members are:");
                    player.sendText(members.substring(0, members.length() - 2));
                }
            } else if(subCmd.equals("list")) {
                if((page = parseInt(args[1])) != -1) {
                    sendPage(page, player);
                }
            } else if(subCmd.equals("stats")) {
                if((clan = getClan(args[1])) != null) {
                    final DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    player.sendNormal(clan.getName() + "'s stats:");
                    player.sendListItem("Members: ", "" + clan.getMembers().size());
                    player.sendListItem("Kills: ", "" +  clan.getKills());
                    player.sendListItem("Deaths: ", "" + clan.getDeaths());
                    player.sendListItem("KDR: ", "" + decimalFormat.format(clan.getRatio()));
                }
            } else if(subCmd.equals("rename")) {
                if((clan = getClan()) != null && isLeader(clan) && noCensor(args[1]) && notClan(args[1]) && canAfford(UDSPlugin.getConfigInt(ConfigRef.CLAN_COST))) {
                    player.debit(UDSPlugin.getConfigInt(ConfigRef.CLAN_COST));
                    ClanUtils.removeClan(clan);
                    if((base = RegionUtils.getRegion(RegionType.BASE, clan.getName() + "base")) != null) {
                        RegionUtils.renameRegion(base, args[1] + "base");
                    }
                    clan.rename(args[1]);
                    ClanUtils.addClan(clan);
                    clan.sendMessage("Your clan has been renamed " + args[1] + ".");
                    UDSPlugin.sendBroadcast(player.getNick() + " has rebranded their clan as " + args[1] + ".");
                }
            } else if(subCmd.equals("owner")) {
                if((clan = getClan()) != null && isLeader(clan) && (target = getMatchingPlayer(args[1])) != null && isInClan(target, clan)) {
                    clan.changeLeader(target);
                    clan.sendMessage("Your new leader is " + target.getNick());
                    if(target.isOnline()) {
                        target.sendNormal("You are the new leader of " + clan.getName());
                    }
                    player.sendNormal("You have resigned as leader of " + clan.getName());
                }
            } else if(subCmd.equals("base")) {
                if((clan = getClan()) != null && isLeader(clan)) {
                    if(args[1].equals("make")) {
                        if(noBase(clan) && canAfford(UDSPlugin.getConfigInt(ConfigRef.BASE_COST))) {
                            final Vector min = player.getLocation().add(-25, 0, -25).toVector().setY(20);
                            final Vector max = player.getLocation().add(25, 0, 25).toVector().setY(220);
                            base = new Region(clan.getName() + "base", min, max, player.getLocation(), null, "", RegionType.BASE);
                            if(noOverlaps(base)) {
                                player.debit(UDSPlugin.getConfigInt(ConfigRef.BASE_COST));
                                RegionUtils.addRegion(base);
                                base.placeMoreMarkers();
                                base.placeTowers();
                                clan.sendMessage("Your clan base has just been set up. Use /clan base to teleport to it.");
                                for(SaveablePlayer member : clan.getMembers()) {
                                    base.addMember(member);
                                }
                            }
                        }
                    } else if(args[1].equals("clear")) {
                        if((base = getBase(clan)) != null) {
                            RegionUtils.removeRegion(base);
                            clan.sendMessage("Your clan base has been removed.");
                        }
                    } else if(args[1].equals("set")) {
                        if((base = getBase(clan)) != null) {
                            base.setWarp(player.getLocation());
                            player.sendNormal("Clan base warp point set.");
                        }
                    } else {
                        subCmdHelp();
                    }
                }
            } else {
                subCmdHelp();
            }
        }
    }

    private void sendPage(int page, SaveablePlayer player) {
        final List<Clan> clans = ClanUtils.getSortedClans(new SortByKDR());
        final int pages = (clans.size() + 8) / 9;
        if(pages == 0) {
            player.sendNormal("There are no clans on the server.");
        } else if(page > pages) {
            player.sendMessage(Message.NO_PAGE);
        } else {
            player.sendNormal("--- Current Clans " + (pages > 1 ? "Page " + page + "/" + pages + " " : "") + "---");
            int posted = 0;
            int skipped = 1;
            final DecimalFormat decimalFormat = new DecimalFormat("#.##");
            for(Clan clan : clans) {
                if(skipped > (page - 1) * 9 && posted < 9) {
                    player.sendListItem(clan.getName() + " - ", clan.getLeader().getNick() + " KDR: " + decimalFormat.format(clan.getRatio()));
                    posted++;
                } else {
                    skipped++;
                }
            }
        }
    }
    
    private boolean hasClanRank(final Clan clan, final SaveablePlayer player) {
        if(!clan.hasClanRank(player)) {
            player.sendError("You are not high enough rank in the clan to do this.");
            return false;
        }
        return true;
    }
}
