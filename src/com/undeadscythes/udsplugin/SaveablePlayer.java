package com.undeadscythes.udsplugin;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

/**
 * An extension of Minecraft players adding various fields and methods.
 * @author UndeadScythes
 */
public class SaveablePlayer implements Saveable {
    /**
     * File name of player file.
     */
    public final static String PATH = "players.csv";

    private String name; // Non-transient.
    private Player base; // Non-transient.
    private String nick; // Non-transient.

    private boolean godMode = false;
    private boolean lockdownPass = false;
    private boolean afk = false;
    private boolean loadItems = false;
    private boolean isShopping = false;
    private boolean isBuying = false;
    private int wager = 0;
    private int powertoolID = 0;
    private int bounty = 0;
    private int money = 0;
    private int vipSpawns = 0;
    private int bail = 0;
    private long vipTime = 0;
    private long jailTime = 0;
    private long jailSentence = 0;
    private long timeLogged = 0;
    private long lastDamageCaused = 0;
    private long prizeClaim = 0;
    private String powertoolCmd = "";
    private Location back = null;
    private Location checkPoint = null;
    private SaveablePlayer challenger = null;
    private SaveablePlayer whisperer = null;
    private ChatRoom chatRoom = null; // Is this field never modified?
    private ChatChannel channel = ChatChannel.PUBLIC;
    private UUID selectedPet = null;
    private Clan clan = null;
    private PlayerRank rank = PlayerRank.DEFAULT;
    private ItemStack[] inventoryCopy = null;
    private ItemStack[] armorCopy = null;
    private final Inventory shoppingList = Bukkit.createInventory(base, 36);
    private final LinkedList<Long> lastChats = new LinkedList<Long>();
    private final Set<SaveablePlayer> ignoredPlayers = new HashSet<SaveablePlayer>();

    /**
     * Initialise a brand new player extension.
     * @param player Player to connect to this extension.
     */
    public SaveablePlayer(final Player player) {
        base = player;
        nick = player.getName();
        name = player.getName();
    }

    /**
     * Initialise an extended player from a string record.
     * @param record A line from a save file.
     */
    public SaveablePlayer(final String record) {
        final String[] recordSplit = record.split("\t");
        name = recordSplit[0];
        bounty = Integer.parseInt(recordSplit[1]);
        money = Integer.parseInt(recordSplit[2]);
        rank = PlayerRank.getByName(recordSplit[3]);
        vipTime = Long.parseLong(recordSplit[4]);
        vipSpawns = Integer.parseInt(recordSplit[5]);
        jailTime = Long.parseLong(recordSplit[6]);
        jailSentence = Long.parseLong(recordSplit[7]);
        bail = Integer.parseInt(recordSplit[8]);
        nick = recordSplit[9];
        timeLogged = Long.parseLong(recordSplit[10]);
    }

    @Override
    public String getRecord() {
        final List<String> record = new ArrayList<String>();
        record.add(name);
        record.add(Integer.toString(bounty));
        record.add(Integer.toString(money));
        record.add(rank.toString());
        record.add(Long.toString(vipTime));
        record.add(Integer.toString(vipSpawns));
        record.add(Long.toString(jailTime));
        record.add(Long.toString(jailSentence));
        record.add(Integer.toString(bail));
        record.add(nick);
        record.add(Long.toString(timeLogged));
        return StringUtils.join(record.toArray(), "\t");
    }

    @Override
    public String toString() {
        Bukkit.getLogger().info("Implicit Player.toString()."); // Implicit .toString()
        return name;
    }

    /**
     * Warp an existing player with these extensions.
     * @param player Player to wrap.
     */
    public void wrapPlayer(final Player player) {
        base = player;
        player.setDisplayName(nick);
    }

    /**
     * Increment logged time.
     * @param time Time to add.
     */
    public void addTime(final long time) {
        timeLogged += time;
    }

    /**
     * Toggle this players lockdown pass.
     */
    public void toggleLockdownPass() {
        lockdownPass ^= true;
    }

    /**
     * Get this players selected pet.
     * @return Pet UUID.
     */
    public UUID getSelectedPet() {
        return selectedPet;
    }

    /**
     * Set this players selected pet.
     * @param id Pet UUID
     */
    public void selectPet(final UUID id) {
        selectedPet = id;
    }

    /**
     * Check if this player currently shopping.
     * @return
     */
    public final boolean isShopping() {
        return isShopping;
    }

    /**
     * Set if this player is currently shopping.
     * @param isShopping
     */
    public final void setShopping(final boolean isShopping) {
        this.isShopping = isShopping;
    }

    /**
     * Set the player that this player is whispering with.
     * @param player Player whispering.
     */
    public void setWhisperer(final SaveablePlayer player) {
        whisperer = player;
    }

    /**
     * Get this players duel wager.
     * @return Duel wager.
     */
    public int getWager() {
        return wager;
    }

    /**
     * Check if this player is buying an item.
     * @return
     */
    public final boolean isBuying() {
        return isBuying;
    }

    /**
     * Set if this player is buying an item.
     * @param isBuying
     */
    public final void setBuying(final boolean isBuying) {
        this.isBuying = isBuying;
    }

    /**
     * Check if this player is wearing scuba gear.
     * @return <code>true</code> if the player is wearing scuba gear, <code>false</code> otherwise.
     */
    public final boolean hasScuba() {
        if(base != null) {
            ItemStack helmet = base.getInventory().getHelmet();
            if(helmet != null && helmet.getType().equals(Material.GLASS)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the player that this player is whispering with.
     * @return Player whispering.
     */
    public SaveablePlayer getWhisperer() {
        return whisperer;
    }

    /**
     *
     * @param search
     * @return
     */
    public int countItems(final ItemStack search) {
        if(base == null) {
            return 0;
        } else {
            final ItemStack[] inventory = base.getInventory().getContents();
            int count = 0;
            for(int i = 0; i < inventory.length; i++) {
                final ItemStack item = inventory[i];
                if(item != null && item.getType() == search.getType() && item.getData().getData() == search.getData().getData()) {
                    count += item.getAmount();
                }
            }
            return count;
        }
    }

    /**
     *
     * @return
     */
    public int getPowertoolID() {
        return powertoolID;
    }

    /**
     *
     * @return
     */
    public String getPowertool() {
        return powertoolCmd;
    }

    /**
     *
     */
    public void claimPrize() {
        prizeClaim = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public boolean hasClaimedPrize() {
        return (prizeClaim + Timer.DAY > System.currentTimeMillis());
    }

    /**
     *
     * @return
     */
    public Session forceSession() {
        Session session = UDSPlugin.getSessions().get(getName());
        if(session == null) {
            session = new Session();
            UDSPlugin.getSessions().put(getName(), session);
        }
        return session;
    }

    /**
     *
     * @param ID
     */
    public void setPowertoolID(final int ID) {
        powertoolID = ID;
    }

    /**
     * Get this players current shopping list.
     * @return
     */
    public Inventory getShoppingList() {
        return shoppingList;
    }

    /**
     *
     * @param cmd
     */
    public void setPowertool(final String cmd) {
        powertoolCmd = cmd;
    }

    /**
     *
     * @return
     */
    public Player getBase() {
        return base;
    }

    /**
     * Sets the base player reference to <code>null</code>.
     */
    public void nullBase() {
        base = null;
    }

    /**
     * Get the players saved inventory.
     * @return The players saved inventory, <code>null</code> if none exists.
     */
    public ItemStack[] getInventoryCopy() {
        return inventoryCopy == null ? null : inventoryCopy.clone();
    }

    /**
     *
     * @param location
     * @return
     */
    public boolean isInShop(final Location location) {
        for(Region region : UDSPlugin.getRegions(RegionType.SHOP).values()) {
            if(location.toVector().isInAABB(region.getV1(), region.getV2())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param clan
     */
    public void setClan(final Clan clan) {
        this.clan = clan;
    }

    /**
     * Save this players inventory for later retrieval.
     */
    public void saveInventory() {
        if(base != null) {
            inventoryCopy = base.getInventory().getContents();
        }
    }

    /**
     *
     */
    public void saveItems() {
        saveInventory();
        saveArmor();
    }

    /**
     * Load this players armor.
     */
    public void loadArmor() {
        if(base != null) {
            base.getInventory().setArmorContents(armorCopy);
            armorCopy = new ItemStack[0];
        }
    }

    /**
     *
     */
    public void endChallenge() {
        wager = 0;
        challenger = null;
    }

    /**
     * Load this players inventory.
     */
    public void loadInventory() {
        if(base != null) {
            base.getInventory().setContents(inventoryCopy);
            inventoryCopy = new ItemStack[0];
        }
    }

    /**
     * Save this players armor for later retrieval.
     */
    public void saveArmor() {
        if(base != null) {
            armorCopy = base.getInventory().getArmorContents();
        }
    }

    /**
     * Get this players bail.
     * @return This players bail.
     */
    public int getBail() {
        return bail;
    }

    /**
     *
     */
    public void loadItems() {
        loadInventory();
        loadArmor();
    }

    /**
     * Update the chat times with a new value.
     * @return <code>true</code> if chat events are not occurring too frequently, <code>false</code> otherwise.
     */
    public boolean newChat() {
        if(lastChats.size() > 5) {
            lastChats.removeFirst();
        }
        lastChats.offerLast(System.currentTimeMillis());
        if(lastChats.size() == 5 && lastChats.getLast() - lastChats.getFirst() < 3000) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public String getTimeLogged() {
        return timeToString(timeLogged);
    }

    /**
     *
     * @return
     */
    public String getLastSeen() {
        if(base == null) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if(offlinePlayer == null) {
                return "Unknown";
            } else {
                return timeToString(System.currentTimeMillis() - Bukkit.getOfflinePlayer(name).getLastPlayed());
            }
        } else {
            return timeToString(System.currentTimeMillis() - base.getLastPlayed());
        }
    }

    /**
     * Construct an English reading string of the remaining VIP time.
     * @return English reading string.
     */
    public String getVIPTimeString() {
        return timeToString(UDSPlugin.getConfigLong(ConfigRef.VIP_TIME) - System.currentTimeMillis() - getVIPTime());
    }

    /**
     *
     * @param time
     * @return
     */
    private String timeToString(final long time) {
        long timeRemaining = time;
        String timeString = "";
        if(timeRemaining >= Timer.DAY) {
            final int days = (int)(timeRemaining / Timer.DAY);
            timeString = timeString.concat(days + (days == 1 ? " day " : " days "));
            timeRemaining -= days * Timer.DAY;
        }
        if(timeRemaining >= Timer.HOUR) {
            final int hours = (int)(timeRemaining / Timer.HOUR);
            timeString = timeString.concat(hours + (hours == 1 ? " hour " : " hours "));
            timeRemaining -= hours * Timer.HOUR;
        }
        if(timeRemaining >= Timer.MINUTE) {
            final int minutes = (int)(timeRemaining / Timer.MINUTE);
            timeString = timeString.concat(minutes + (minutes == 1 ? " minute " : " minutes "));
            timeRemaining -= minutes * Timer.MINUTE;
        }
        if(timeRemaining >= Timer.SECOND) {
            final int seconds = (int)(timeRemaining / Timer.SECOND);
            timeString = timeString.concat(seconds + (seconds == 1 ? " second " : " seconds "));
            timeRemaining -= seconds * Timer.SECOND;
        }
        return timeString;
    }

    /**
     * Use up some the players VIP daily item spawns.
     * @param amount The amount of spawns to use up.
     * @return The number of spawns remaining.
     */
    public int useVIPSpawns(final int amount) {
        vipSpawns -= amount;
        return vipSpawns;
    }

    /**
     * Set the players back warp point to their current location.
     */
    public void setBackPoint() {
        if(base != null) {
            back = base.getLocation();
        }
    }

    /**
     *
     * @param location
     */
    public void setBackPoint(final Location location) {
        back = location;
    }

    /**
     * Toggle this players game mode.
     * @return <code>true</code> if the players game mode is now set to creative, <code>false</code> otherwise.
     */
    public boolean toggleGameMode() {
        if(base == null) {
            return false;
        } else {
            if(base.getGameMode().equals(GameMode.SURVIVAL)) {
                base.setGameMode(GameMode.CREATIVE);
                return true;
            } else {
                base.setGameMode(GameMode.SURVIVAL);
                return false;
            }
        }
    }

    /**
     * Get a players monetary value.
     * @return A players monetary value.
     */
    public int getMoney() {
        return money;
    }

    /**
     * Get the chat room this player is currently in.
     * @return The players chat room.
     */
    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    /**
     * Take a player out of jail and perform all the necessary operations.
     */
    public void release() {
        jailTime = 0;
        jailSentence = 0;
        quietTeleport(UDSPlugin.getWarps().get("jailout"));
    }

    /**
     * Put a player in jail and perform all the necessary operations.
     * @param sentence Time in minutes to jail the player.
     * @param bail Bail to set.
     * @throws IOException
     */
    public void jail(final long sentence, final int bail) {
        if(base != null) {
            base.getWorld().strikeLightningEffect(base.getLocation());
            quietTeleport(UDSPlugin.getWarps().get("jailin"));
            jailTime = System.currentTimeMillis();
            jailSentence = sentence * Timer.MINUTE;
            this.bail = bail;
            base.sendMessage(Color.MESSAGE + "You have been jailed for " + sentence + " minutes.");
            if(bail != 0) {
                base.sendMessage(Color.MESSAGE + "If you can afford it, use /paybail to get out early for " + bail + " " + UDSPlugin.getConfigString(ConfigRef.CURRENCIES) + ".");
            }
        }
    }

    /**
     * Get the first region that the player is currently inside.
     * @param type Optional type of region to check, <code>null</code> to search all regions.
     * @return The first region the player is in, <code>null</code> otherwise.
     */
    public Region getCurrentRegion(final RegionType type) {
        if(base != null) {
            if(type == RegionType.CITY) {
                for(Region region : UDSPlugin.getRegions(RegionType.CITY).values()) {
                    if(base.getLocation().toVector().isInAABB(region.getV1(), region.getV2())) {
                        return region;
                    }
                }
            } else if(type == RegionType.SHOP) {
                for(Region region : UDSPlugin.getRegions(RegionType.CITY).values()) {
                    if(base.getLocation().toVector().isInAABB(region.getV1(), region.getV2())) {
                        return region;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Demote a player.
     * @return Players new rank, <code>null</code> if no change.
     */
    public PlayerRank demote() {
        final PlayerRank newRank = PlayerRank.getBelow(rank);
        if(newRank != null) {
            rank = newRank;
        }
        return newRank;
    }

    /**
     * Promote a player.
     * @return Players new rank, <code>null</code> if no change.
     */
    public PlayerRank promote() {
        final PlayerRank newRank = PlayerRank.getAbove(rank);
        if(newRank != null) {
            rank = newRank;
        }
        return newRank;
    }

    /**
     * Get the players current chat channel.
     * @return The players chat channel.
     */
    public ChatChannel getChannel() {
        return channel;
    }

    /**
     * Get this players saved checkpoint.
     * @return This players checkpoint.
     */
    public Location getCheckPoint() {
        return checkPoint;
    }

    /**
     *
     * @param location
     */
    public void setCheckPoint(final Location location) {
        checkPoint = location;
    }

    /**
     * Check if a player is engaged in a duel with another player.
     * @return <code>true</code> if the player is engaged in a duel, <code>false</code> otherwise.
     */
    public boolean isDuelling() {
        return challenger != null;
    }

    /**
     *
     * @param challenger
     */
    public void setChallenger(final SaveablePlayer challenger) {
        this.challenger = challenger;
    }

    /**
     *
     * @return
     */
    public SaveablePlayer getChallenger() {
        return challenger;
    }

    /**
     * Check if a player can build in a given location.
     * @param location
     * @return
     */
    public boolean canBuildHere(final Location location) {
        boolean contained = false;
        for(Region region : UDSPlugin.getRegions(RegionType.GENERIC).values()) {
            if(location.toVector().isInAABB(region.getV1(), region.getV2())) {
                if(((region.getRank() != null && rank.compareTo(region.getRank()) >= 0)) || region.isOwner(this) || region.hasMember(this)) {
                    return true;
                }
                contained = true;
            }
        }
        return !contained;
    }

    /**
     * Give a player an item stack, any items that don't fit in the inventory are dropped at the players feet.
     * @param item Item to give the player.
     */
    public void giveAndDrop(final ItemStack item) {
        if(base != null) {
            final Map<Integer, ItemStack> drops = base.getInventory().addItem(item);
            for(ItemStack drop : drops.values()) {
                base.getWorld().dropItemNaturally(base.getLocation(), drop);
            }
        }
    }

    /**
     * Add a player to a list of players this player is ignoring.
     * @param player Player to ignore.
     * @return <code>true</code> if this player was not already being ignored, <code>false</code> otherwise.
     */
    public boolean ignorePlayer(final SaveablePlayer player) {
        return ignoredPlayers.add(player);
    }

    /**
     * Remove a player from a list of players this player is ignoring.
     * @param player Player to stop ignoring.
     * @return <code>true</code> if this player was being ignored, <code>false</code> otherwise.
     */
    public boolean unignorePlayer(final SaveablePlayer player) {
        return ignoredPlayers.remove(player);
    }

    /**
     * Check to see if this player is ignoring a player.
     * @param player Player to check.
     * @return <code>true</code> if this player is ignoring that player, <code>false</code> otherwise.
     */
    public boolean isIgnoringPlayer(final SaveablePlayer player) {
        return ignoredPlayers.contains(player);
    }

    /**
     * Get the last time this player caused damage to another player.
     * @return The last time this player did damage to another player.
     */
    public long getLastDamageCaused() {
        return lastDamageCaused;
    }

    /**
     * Get this players total bounty.
     * @return Players bounty.
     */
    public int getBounty() {
        return bounty;
    }

    /**
     * Set the bounty on a player.
     * @param bounty Bounty to set.
     */
    public void setBounty(final int bounty) {
        this.bounty = bounty;
    }

    /**
     * Add a bounty to this player.
     * @param bounty New bounty.
     */
    public void addBounty(final int bounty) {
        this.bounty += bounty;
    }

    /**
     * Get the last recorded location of the player.
     * @return The last recorded location of the player.
     */
    public Location getBack() {
        return back;
    }

    /**
     * Get a player current rank.
     * @return Player rank.
     */
    public PlayerRank getRank() {
        return rank;
    }

    /**
     * Set the player rank.
     * @param rank The rank to set.
     */
    public void setRank(final PlayerRank rank) {
        this.rank = rank;
    }

    /**
     * Check to see if a player belongs to a clan.
     * @return <code>true</code> if a player is in a clan, <code>false</code> otherwise.
     */
    public boolean isInClan() {
        return clan != null;
    }

    /**
     * Get the name of the clan the player is a member of.
     * @return Clan name.
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Storage of nick name kept in extension for offline access.
     * @return Player nick name.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Toggle the players chat channel.
     * @param channel Channel to toggle.
     * @return <code>true</code> if channel was toggled on, <code>false</code> if channel switched back to public.
     */
    public boolean toggleChannel(final ChatChannel channel) {
        if(this.channel.equals(channel)) {
            this.channel = ChatChannel.PUBLIC;
            return false;
        } else {
            this.channel = channel;
            return true;
        }
    }

    /**
     * Check whether this player has a lockdown pass.
     * @return Has player got lockdown pass.
     */
    public boolean hasLockdownPass() {
        return lockdownPass;
    }

    /**
     *
     * @param loadItems
     */
    public void setLoadItems(final boolean loadItems) {
        this.loadItems = loadItems;
    }

    /**
     *
     * @return
     */
    public boolean hasLoadItems() {
        return loadItems;
    }

    /**
     * Get the time when this player rented VIP status.
     * @return When VIP was rented.
     */
    public long getVIPTime() {
        return vipTime;
    }

    /**
     * Set when a player rented VIP status.
     * @param time Time to set.
     */
    public void setVIPTime(final long time) {
        vipTime = time;
    }

    /**
     * Get the number of free item spawns this player has left.
     * @return Number of spawns left.
     */
    public int getVIPSpawns() {
        return vipSpawns;
    }

    /**
     * Set the number of free VIP item spawns a player has remaining.
     * @param spawns Number of spawns to set.
     */
    public void setVIPSpawns(final int spawns) {
        vipSpawns = spawns;
    }

    /**
     * Get the time that this player was put in jail.
     * @return Players jail time.
     */
    public long getJailTime() {
        return jailTime;
    }

    /**
     * Set when a player was put in jail.
     * @param time Jail time.
     */
    public void setJailTime(final long time) {
        jailTime = time;
    }

    /**
     * Get how long this player was sentenced to jail for.
     * @return Players sentence.
     */
    public long getJailSentence() {
        return jailSentence;
    }

    /**
     * Set the length of a players jail sentence.
     * @param sentence Length of sentence.
     */
    public void setJailSentence(final long sentence) {
        jailSentence = sentence;
    }

    /**
     * Check if a player is currently in jail.
     * @return <code>true</code> if a player is in jail, <code>false</code> otherwise.
     */
    public boolean isJailed() {
        return (jailTime > 0);
    }

    /**
     * Check if a player currently has god mode enabled.
     * @return God mode setting.
     */
    public boolean hasGodMode() {
        return godMode;
    }

    /**
     * Toggle a players god mode.
     * @return Players current god mode setting.
     */
    public boolean toggleGodMode() {
        godMode ^= true;
        return godMode;
    }

    /**
     * Set a players god mode.
     * @param mode God mode setting.
     */
    public void setGodMode(final boolean mode) {
        godMode = mode;
    }

    /**
     * Send a message in a particular channel.
     * @param channel Channel to send message in.
     * @param message Message to send.
     */
    public void chat(final ChatChannel channel, final String message) {
        if(base != null) {
            final ChatChannel temp = this.channel;
            this.channel = channel;
            base.chat(message);
            this.channel = temp;
        }
    }

    /**
     * Check if a player can afford to pay some value.
     * @param price Price to pay.
     * @return <code>true</code> if player has enough money, <code>false</code> otherwise.
     */
    public boolean canAfford(final int price) {
        return (money >= price);
    }

    /**
     *
     * @param wager
     */
    public void setWager(final int wager) {
        this.wager = wager;
    }

    /**
     * Debit the players account the amount passed.
     * @param amount Amount to debit.
     */
    public void debit(final int amount) {
        money -= amount;
    }

    /**
     * Credit the players account the amount passed.
     * @param amount Amount to credit.
     */
    public void credit(final int amount) {
        money += amount;
    }

    /**
     * Set a players account.
     * @param amount Amount to set.
     */
    public void setMoney(final int amount) {
        money = amount;
    }

    /**
     * Check if the player is marked as AFK.
     * @return Player is AFK.
     */
    public boolean isAfk() {
        return afk;
    }

    /**
     * Toggle whether this player is marked as AFK.
     * @return Whether this player is now marked as AFK.
     */
    public boolean toggleAfk() {
        afk ^= true;
        return afk;
    }

    /**
     * Teleport a player but reserve pitch and yaw of player.
     * @param location Location to teleport to.
     */
    public void move(final Location location) {
        if(base != null) {
            final Location destination = location;
            destination.setPitch(base.getLocation().getPitch());
            destination.setYaw(base.getLocation().getYaw());
            base.teleport(destination);
        }
    }

    /**
     * Teleport a player but fail quietly if location is <code>null</code>.
     * @param location Location to teleport player to.
     * @return <code>true</code> if location is not <code>null</code>, <code>false</code> otherwise.
     */
    public boolean quietTeleport(final Location location) {
        if(location == null) {
            return false;
        } else {
            if(base != null) {
                base.teleport(location);
            }
            return true;
        }
    }

    /**
     * Teleport a player but fail quietly if warp or location are <code>null</code>.
     * @param warp Warp to teleport player to.
     * @return <code>true</code> if warp and location are not <code>null</code>, <code>false</code> otherwise.
     */
    public boolean quietTeleport(final Warp warp) {
        if(warp == null) {
            return false;
        } else {
            if(base == null) {
                return warp.getLocation() != null;
            } else {
                return base.teleport(warp.getLocation());
            }
        }
    }

    /**
     * Check if this player has a permission.
     * @param perm The permission to check.
     * @return <code>true</code> if the player has the permission, <code>false</code> otherwise.
     */
    public boolean hasPermission(final Perm perm) {
        if(perm.isHereditary()) {
            return perm.getRank().compareTo(rank) <= 0;
        } else {
            return perm.getRank().equals(rank);
        }
    }

    /**
     *
     * @param name
     */
    public void setDisplayName(final String name) {
        nick = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return base == null ? name : base.getName();
    }

    /**
     *
     * @param message
     */
    public void sendMessage(final String message) {
        if(base != null) {
            base.sendMessage(message);
        }
    }

    /**
     *
     * @return
     */
    public boolean isOnline() {
        return base == null ? false : base.isOnline();
    }

    /**
     *
     * @param level
     */
    public void setFoodLevel(final int level) {
        if(base != null) {
            base.setFoodLevel(level);
        }
    }

    /**
     *
     * @return
     */
    public Location getLocation() {
        return base == null ? null : base.getLocation();
    }

    /**
     *
     * @return
     */
    public World getWorld() {
        return base == null ? null : base.getWorld();
    }

    /**
     *
     * @return
     */
    public long getLastPlayed() {
        return base == null ? Bukkit.getOfflinePlayer(name).getLastPlayed() : base.getLastPlayed();
    }

    /**
     *
     * @param message
     */
    public void kickPlayer(final String message) {
        if(base != null) {
            base.kickPlayer(message);
        }
    }

    /**
     *
     * @param banned
     */
    public void setBanned(final boolean banned) {
        if(base == null) {
            Bukkit.getOfflinePlayer(name).setBanned(banned);
        } else {
            base.setBanned(banned);
        }
    }

    /**
     *
     * @return
     */
    public boolean isBanned() {
        return base == null ? Bukkit.getOfflinePlayer(name).isBanned() : base.isBanned();
    }

    /**
     *
     * @return
     */
    public PlayerInventory getInventory() {
        return base == null ? null : base.getInventory();
    }

    /**
     *
     * @param location
     * @return
     */
    public boolean teleport(final Location location) {
        return base == null? false : base.teleport(location);
    }

    /**
     *
     * @return
     */
    public ItemStack getItemInHand() {
        return base == null ? null : base.getItemInHand();
    }

    /**
     *
     * @param command
     * @return
     */
    public boolean performCommand(final String command) {
        return base == null ? false : base.performCommand(command);
    }

    /**
     *
     * @return
     */
    public boolean isOp() {
        return base == null ? false : base.isOp();
    }

    /**
     *
     * @return
     */
    public boolean isSneaking() {
        return base == null ? false : base.isSneaking();
    }

    /**
     *
     * @param item
     */
    public void setItemInHand(final ItemStack item) {
        if(base != null) {
            base.setItemInHand(item);
        }
    }

    /**
     *
     * @param transparent
     * @param range
     * @return
     */
    public Block getTargetBlock(final HashSet<Byte> transparent, final int range) {
        return base == null ? null : base.getTargetBlock(transparent, range);
    }

    /**
     *
     * @param transparent
     * @param range
     * @return
     */
    public List<Block> getLastTwoTargetBlocks(final HashSet<Byte> transparent, final int range) {
        return base == null ? null : base.getLastTwoTargetBlocks(transparent, range);
    }

    /**
     *
     * @return
     */
    public Player getKiller() {
        return base == null ? null : base.getKiller();
    }

    /**
     *
     * @return
     */
    public GameMode getGameMode() {
        return base == null ? null : base.getGameMode();
    }

    /**
     *
     */
    @SuppressWarnings("deprecation")
    public void updateInventory() {
        if(base != null) {
            base.updateInventory();
        }
    }

    /**
     *
     * @param player
     */
    public void teleport(final SaveablePlayer player) {
        if(base != null) {
            base.teleport(player.getBase());
        }
    }

    /**
     *
     * @param entity
     */
    public void teleportHere(final Entity entity) {
        if(base != null) {
            entity.teleport(base);
        }
    }

    /**
     *
     * @param pet
     */
    public void setPet(final Tameable pet) {
        if(base != null) {
            pet.setOwner(base);
        }
    }

    /**
     *
     * @param levels
     */
    public void giveExpLevels(final int levels) {
        if(base != null) {
            base.giveExpLevels(levels);
        }
    }

    /**
     *
     * @param exp
     */
    public void setExp(final int exp) {
        if(base != null) {
            base.setExp(exp);
        }
    }

    /**
     *
     * @param level
     */
    public void setLevel(final int level) {
        if(base != null) {
            base.setLevel(level);
        }
    }

    /**
     *
     * @return
     */
    public int getLevel() {
        return base == null ? 0 : base.getLevel();
    }

    /**
     *
     * @return
     */
    public int getMaxHealth() {
        return base == null ? 0 : base.getMaxHealth();
    }

    /**
     *
     * @param health
     */
    public void setHealth(final int health) {
        if(base != null) {
            base.setHealth(health);
        }
    }

    /**
     *
     * @return
     */
    public Location getBedSpawnLocation() {
        return base == null ? null : base.getBedSpawnLocation();
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public List<Entity> getNearbyEntities(final double x, final double y, final double z) {
        return base == null ? null : base.getNearbyEntities(x, y, z);
    }

    /**
     *
     * @return
     */
    public boolean isInsideVehicle() {
        return base == null ? false : base.isInsideVehicle();
    }

    /**
     *
     * @return
     */
    public Entity getVehicle() {
        return base == null ? null : base.getVehicle();
    }
}
