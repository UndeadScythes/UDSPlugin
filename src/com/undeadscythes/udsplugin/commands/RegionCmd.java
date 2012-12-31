package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;

/**
 * Region related commands.
 * @author UndeadScythes
 */
public class RegionCmd extends CommandWrapper {
    @Override
    public final void playerExecute() {
        if(args.length == 1) {
            if(args[0].equals("vert")) {
                vert();
            } else if(args[0].equals("list")) {
                list(RegionType.NORMAL);
            } else if(args[0].equals("type")) {
                showTypes();
            } else if(args[0].equals("flag")) {
                flagList();
            } else {
                subCmdHelp();
            }
        } else if(args.length == 2) {
            if(args[0].equals("del")) {
                del();
            } else if(args[0].equals("list")) {
                final RegionType type = getRegionType(args[1]);
                if(type != null) {
                    list(type);
                }
            } else if(args[0].equals("tp")) {
                tp();
            } else if(args[0].equals("info")) {
                info();
            } else if(args[0].equals("reset")) {
                reset();
            } else if(args[0].equals("select")) {
                select();
            } else if(args[0].equals("set")) {
                set(RegionType.NORMAL);
            } else {
                subCmdHelp();
            }
        } else if(args.length == 3) {
            if(args[0].equals("addmember")) {
                addMember();
            } else if(args[0].equals("delmember")) {
                delMember();
            } else if(args[0].equals("owner")) {
                owner();
            } else if(args[0].equals("flag")) {
                flag();
            } else if(args[0].equals("rename")) {
                rename();
            } else if(args[0].equals("set")) {
                final RegionType type = getRegionType(args[2]);
                if(type != null) {
                    set(type);
                }
            } else if(args[0].equals("type")) {
                changeType();
            } else {
                subCmdHelp();
            }
        } else if(numArgsHelp(4)) {
            if(args[0].equals("expand")) {
                expand();
            } else if(args[0].equals("contract")) {
                contract();
            } else {
                subCmdHelp();
            }
        }
    }

    private void changeType() {
        final Region region;
        final RegionType type;
        if((region = getRegion(args[1])) != null && ((type = getRegionType(args[2]))) != null) {
            UDSPlugin.getRegions(region.getType()).remove(region.getName());
            region.setType(type);
            UDSPlugin.getRegions(region.getType()).put(region.getName(), region);
            player.sendMessage(Color.MESSAGE + "Region " + region.getName() + " set to type " + type.toString() + ".");
        }
    }

    private void showTypes() {
        player.sendMessage(Color.MESSAGE + "Available region types:");
        String types = "";
        for(RegionType type : RegionType.values()) {
            types = types.concat(type.toString() + ", ");
        }
        player.sendMessage(Color.TEXT + types.substring(0, types.length() - 2));
    }

    private void contract() {
        Direction direction;
        Region region;
        int distance;
        if((region = getRegion(args[1])) != null && (direction = getCardinalDirection(args[3])) != null && (distance = parseInt(args[2])) > -1) {
            region.contract(direction, distance);
            player.sendMessage(Color.MESSAGE + "Region has been contracted.");
        }
    }

    private void expand() {
        Direction direction;
        Region region;
        int distance;
        if((region = getRegion(args[1])) != null && (direction = getCardinalDirection(args[3])) != null && (distance = parseInt(args[2])) > -1) {
            region.expand(direction, distance);
            player.sendMessage(Color.MESSAGE + "Region has been expanded.");
        }
    }

    private void vert() {
        final Session session = getSession();
        if(session != null && hasTwoPoints(session)) {
            session.vert();
            player.sendMessage(Color.MESSAGE + "Region extended from bedrock to build limit.");
        }
    }

    private void list(final RegionType type) {
        String list = "";
        for(Region test : UDSPlugin.getRegions(RegionType.NORMAL).values()) {
            if(test.getType().equals(type)) {
                list = list.concat(test.getName() + ", ");
            }
        }
        if(list.isEmpty()) {
            player.sendMessage(Color.MESSAGE + "There are no " + type.name().toLowerCase() + " regions.");
        } else {
            player.sendMessage(Color.MESSAGE + type.name().toLowerCase() + " Regions:");
            player.sendMessage(Color.TEXT + list.substring(0, list.length() - 2));
        }
    }

    private void flagList() {
        player.sendMessage(Color.MESSAGE + "Available region flags:");
        String message = "";
        for(RegionFlag test : RegionFlag.values()) {
            message = message.concat(test.name() + ", ");
        }
        player.sendMessage(Color.TEXT + message.substring(0, message.length() - 2));
    }

    private void flag() {
        final Region region = getRegion(args[1]);
        final RegionFlag flag = getFlag(args[2]);
        if(region != null && flag != null) {
            player.sendMessage(Color.MESSAGE + region.getName() + " flag " + flag.toString() + " now set to " + region.toggleFlag(flag) + ".");
        }
    }

    private void del() {
        final Region region = getRegion(args[1]);
        if(region != null) {
            UDSPlugin.getRegions(RegionType.NORMAL).remove(region.getName());
            player.sendMessage(Color.MESSAGE + "Region deleted.");
        }
    }

    private void tp() {
        final Region region = getRegion(args[1]);
        if(region != null) {
            player.teleport(region.getWarp());
        }
    }

    private void info() {
        final Region region = getRegion(args[1]);
        if(region != null) {
            region.sendInfo(player);
        }
    }

    private void reset() {
        final Session session = getSession();
        final Region region = getRegion(args[1]);
        if(session != null && hasTwoPoints(session) && region != null) {
            region.changeV(session.getV1(), session.getV2());
            player.sendMessage(Color.MESSAGE + "Region reset with new points.");
        }
    }

    private void select() {
        final Region region = getRegion(args[1]);
        if(region != null) {
            final Session session = getSession();
            session.setVPair(region.getV1(), region.getV2(), region.getWorld());
            player.sendMessage(Color.MESSAGE + "Points set. " + session.getVolume() + " blocks selected.");
        }
    }

    private void set(final RegionType type) {
        final Session session = getSession();
        if(session != null && hasTwoPoints(session) && notRegion(args[1]) && noCensor(args[1])) {
            final Region region = new Region(args[1], session.getV1(), session.getV2(), player.getLocation(), null, "", type);
            UDSPlugin.getRegions(RegionType.NORMAL).put(region.getName(), region);
            if(!region.getType().equals(RegionType.NORMAL)) {
                UDSPlugin.getRegions(region.getType()).put(region.getName(), region);
            }
            player.sendMessage(Color.MESSAGE + "Region " + region.getName() + " set.");
        }
    }

    private void addMember() {
        final Region region = getRegion(args[1]);
        final SaveablePlayer target = getMatchingPlayer(args[2]);
        if(region != null && target != null) {
            region.addMember(target);
            player.sendMessage(Color.MESSAGE + target.getNick() + " add to region " + region.getName() + ".");
        }
    }

    private void delMember() {
        final Region region = getRegion(args[1]);
        final SaveablePlayer target = getMatchingPlayer(args[2]);
        if(region != null && target != null) {
            region.delMember(target);
            player.sendMessage(Color.MESSAGE + target.getNick() + " removed from region " + region.getName() + ".");
        }
    }

    private void owner() {
        final Region region = getRegion(args[1]);
        final SaveablePlayer target = getMatchingPlayer(args[2]);
        if(region != null && target != null) {
            region.changeOwner(target);
            player.sendMessage(Color.MESSAGE + target.getNick() + " made owner of region " + region.getName() + ".");
        }
    }

    private void rename() {
        final Region region = getRegion(args[1]);
        if(region != null && noCensor(args[1]) && notRegion(args[2])) {
            final String oldName = region.getName();
            UDSPlugin.getRegions(RegionType.NORMAL).remove(oldName);
            region.changeName(args[2]);
            UDSPlugin.getRegions(RegionType.NORMAL).put(region.getName(), region);
            if(!region.getType().equals(RegionType.NORMAL)) {
                UDSPlugin.getRegions(region.getType()).replace(oldName, region.getName(), region);
            }
            player.sendMessage(Color.MESSAGE + "Region " + oldName + " renamed to " + region.getName());
        }
    }
}
