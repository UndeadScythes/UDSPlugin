package com.undeadscythes.udsplugin.commands;

import com.undeadscythes.udsplugin.*;
import org.bukkit.entity.*;

/**
 * Accept a request sent by another player.
 * @author UndeadScythes
 */
public class YCmd extends PlayerCommandExecutor {
    /**
     * @inheritDocs
     */
    @Override
    public void playerExecute(SaveablePlayer player, String[] args) {
        Request request;
        SaveablePlayer sender;
        if((request = getRequest()) != null && (sender = request.getSender()) != null && sender.isOnline()) {
            int price;
            switch (request.getType()) {
                case TP:    sender.teleport(player);
                            break;

                case CLAN:  Clan clan = UDSPlugin.getClans().get(request.getData());
                            clan.addMember(player);
                            clan.sendMessage(player.getDisplayName() + " has joined the clan.");
                            Region base;
                            if((base = UDSPlugin.getBases().get(clan.getName() + "base")) != null) {
                                base.addMember(player);
                            }
                            break;

                case HOME:  if(canAfford(price = Integer.parseInt(request.getData())) && noHome()) {
                                Region home = UDSPlugin.getHomes().get(sender.getName() + "home");
                                home.clearMembers();
                                home.changeOwner(player);
                                player.debit(price);
                                sender.credit(price);
                            }
                            break;

                case PET:   if(canAfford(price = Integer.parseInt(request.getData()))) {
                                for(Entity entity : sender.getWorld().getEntities()) {
                                    if(entity.getUniqueId().equals(sender.getSelectedPet())) {
                                        player.debit(price);
                                        sender.credit(price);
                                        ((Tameable)entity).setOwner(player);
                                        entity.teleport(player);
                                        player.sendMessage(Color.MESSAGE + "Your bought " + sender.getDisplayName() + "'s pet.");
                                        sender.sendMessage(Color.MESSAGE + player.getDisplayName() + " bought your pet.");
                                    }
                                }
                            }
                            break;

                case PVP:   if(canAfford(price = Integer.parseInt(request.getData()))) {
                                sender.sendMessage(Color.MESSAGE + player.getDisplayName() + " accepted your duel, may the best player win.");
                                player.sendMessage(Color.MESSAGE + "Duel accepted, may the best player win.");
                                player.setChallenger(sender);
                                sender.setChallenger(sender);
                                player.setWager(price);
                                sender.setWager(price);
                            }
                            break;

                case SHOP:  if(canAfford(price = Integer.parseInt(request.getData())) && noShop()) {
                                Region shop = UDSPlugin.getShops().get(sender.getName() + "shop");
                                shop.clearMembers();
                                shop.changeOwner(player);
                                player.debit(price);
                                sender.credit(price);
                            }
                            break;

                default:    sender.sendMessage(Color.MESSAGE + player.getDisplayName() + " was unable to accept your request.");
            }
        }
        UDSPlugin.getRequests().remove(player.getName());
    }
}