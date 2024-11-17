package me.quickscythe.ll4el.commands;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.commands.listeners.AdminTabCompleter;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.gui.GuiInventory;
import me.quickscythe.ll4el.utils.gui.GuiItem;
import me.quickscythe.ll4el.utils.gui.GuiManager;
import me.quickscythe.ll4el.utils.misc.Pagifier;
import me.quickscythe.ll4el.utils.misc.managers.BoogieManager;
import me.quickscythe.ll4el.utils.misc.managers.PartyManager;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
import me.quickscythe.ll4el.utils.misc.managers.loot.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.json2.JSONArray;
import org.json2.JSONObject;

import java.util.*;

public class AdminCommands implements CommandExecutor {

    public AdminCommands(LastLife plugin, String... commands) {
        for (String s : commands) {
            PluginCommand cmd = plugin.getCommand(s);
            cmd.setExecutor(this);
            cmd.setTabCompleter(new AdminTabCompleter());
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("status")) {
            if (!sender.hasPermission("lastlife.admin.status")) {
                sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                return true;
            }
            if (sender instanceof Player player) {
                if (args.length == 0 || args[0].equalsIgnoreCase("admin")) {
                    GuiInventory inv = generateStatusMenu();
                    GuiManager.openGui(player, inv);
                    return true;
                }
                if (args[0].equalsIgnoreCase("party")) {
                    GuiManager.openGui(player, generatePartyList(player, args));
                }
                if (args[0].equalsIgnoreCase("boogie")) {
                    GuiManager.openGui(player, generateBoogieList(args));
                }
                if (args[0].equalsIgnoreCase("player")) {
                    if (args.length == 1) {
                        GuiManager.openGui(player, generatePlayerList(args));
                    } else {
                        if (Bukkit.getOfflinePlayer(PlayerManager.getUUID(args[1])) == null) {
                            sender.sendMessage(MessageUtils.colorize("&cSorry that player couldn't be found. If the player is offline their username must be typed exactly."));
                        } else
                            GuiManager.openGui(player, generatePlayerStatus(Bukkit.getOfflinePlayer(Objects.requireNonNull(PlayerManager.getUUID(args[1])))));
                    }
                }

            } else {
                sender.sendMessage(MessageUtils.getMessage("cmd.error.player_only"));
            }
        }
        if (cmd.getName().equalsIgnoreCase("lastlife")) {
            if (!sender.hasPermission("lastlife.admin")) {
                sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                return true;
            }
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(MessageUtils.colorize("&a/" + label + " [help] &7- Displays this list."));
                sender.sendMessage(MessageUtils.colorize("&a/" + label + " loot &7- Displays loot commands."));
                sender.sendMessage(MessageUtils.colorize("&a/" + label + " life &7- Displays life commands."));
                return true;
            }
            if (args[0].equalsIgnoreCase("boogie")) {
                if (!sender.hasPermission("lastlife.admin.boogie")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }
                if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " boogie [help] &7- Displays this list."));
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " boogie roll &7- Rolls for random boogie. Amount of boogies can be selected."));
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " boogie set <player> &7- Sets a player as a boogie."));
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " boogie remove <player> &7- Removes a player's boogie status"));
                    return true;
                }
                if (args[1].equalsIgnoreCase("roll")) {
                    if (!sender.hasPermission("lastlife.admin.boogie.roll")) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                        return true;
                    }
                    int amount = args.length == 3 ? Integer.parseInt(args[2]) : 1;
                    BoogieManager.rollBoogies(amount, true);
                    sender.sendMessage(MessageUtils.getMessage("cmd.boogie.roll", amount));
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (!sender.hasPermission("lastlife.admin.boogie.remove")) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                        return true;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(MessageUtils.colorize("&a/" + label + " boogie remove <player> &7- Removes a player's boogie status"));
                        return true;
                    }
                    if (Bukkit.getPlayer(args[2]) == null && PlayerManager.getUUID(args[2]) == null) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_player"));
                        return true;
                    }
                    OfflinePlayer player = Bukkit.getPlayer(args[2]) == null ? Bukkit.getOfflinePlayer(PlayerManager.getUUID(args[2])) : Bukkit.getPlayer(args[2]);
                    PlayerManager.removeBoogie(player);
                    sender.sendMessage(MessageUtils.getMessage("cmd.boogie.remove.success", player.getName()));
                }
                if (args[1].equalsIgnoreCase("set")) {
                    if (!sender.hasPermission("lastlife.admin.boogie.set")) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                        return true;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(MessageUtils.colorize("&a/" + label + " boogie set <player> &7- Sets a player as a boogie."));
                        return true;
                    }
                    if (Bukkit.getPlayer(args[2]) == null && PlayerManager.getUUID(args[2]) == null) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_player", args[2]));
                        return true;
                    }
                    OfflinePlayer player = Bukkit.getPlayer(args[2]) == null ? Bukkit.getOfflinePlayer(PlayerManager.getUUID(args[2])) : Bukkit.getPlayer(args[2]);
                    PlayerManager.setBoogie(player);
                    sender.sendMessage(MessageUtils.getMessage("cmd.boogie.set.success", player.getName()));
                }

            }
            if (args[0].equalsIgnoreCase("loot")) {
                if (!sender.hasPermission("lastlife.admin.loot")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }
                if (sender instanceof Player player) {
                    if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
                        sender.sendMessage(MessageUtils.colorize("&a/" + label + " loot [help] &7- Displays this list."));
                        sender.sendMessage(MessageUtils.colorize("&a/" + label + " loot create <name> &7- Creates a loot drop location."));
                        sender.sendMessage(MessageUtils.colorize("&a/" + label + " loot drop <name> <type> &7- Drops loot in a location."));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("drop")) {
                        if (!sender.hasPermission("lastlife.admin.loot.drop")) {
                            sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                            return true;
                        }
                        LootManager.dropLoot(new Random().nextBoolean() ? LootManager.DropType.SHULKER : LootManager.DropType.OTHER);
                    }
                    if (args[1].equalsIgnoreCase("create")) {
                        if (!sender.hasPermission("lastlife.admin.loot.drop")) {
                            sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                            return true;
                        }
                        if (args.length == 2) {
                            sender.sendMessage(MessageUtils.colorize("&a/" + label + " loot create <name> &7- Creates a loot drop location."));
                            return true;
                        }
                        String name = args[2];
                        Location loc = player.getTargetBlock(null, 5).getLocation();
                        LootManager.createDrop(name, loc);
                        player.sendMessage(MessageUtils.getMessage("cmd.loot.create.success", name, "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")"));
                    }
                }

            }
            if (args[0].equalsIgnoreCase("life")) {
                if (!sender.hasPermission("lastlife.admin.life")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }
                if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " life [help] &7- Displays this list."));
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " life add <player> [amount] &7- Edits a player's lives. Amount can be negative. Default amount=1."));
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " life set <player> <amount> &7- Sets a player's lives."));
                    return true;
                }

                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("set")) {
                    if (!sender.hasPermission("lastlife.admin.life.edit")) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                        return true;
                    }
                    boolean animation = args.length != 5 || Boolean.parseBoolean(args[4]);
                    if (args.length < 3) {
                        sender.sendMessage(MessageUtils.colorize("&a/" + label + " life add|set <player> [amount] &7- Edits a player's lives. Amount can be negative. Default amount=1."));
                        return true;
                    }
                    if (Bukkit.getPlayer(args[2]) == null && PlayerManager.getUUID(args[2]) == null) {
                        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_player"));
                        return true;
                    }
                    OfflinePlayer player = Bukkit.getPlayer(args[2]) == null ? Bukkit.getOfflinePlayer(PlayerManager.getUUID(args[2])) : Bukkit.getPlayer(args[2]);
                    int amount = args.length == 3 ? 1 : Integer.parseInt(args[3]);
                    if (args[1].equalsIgnoreCase("add")) PlayerManager.editLife(player, amount, animation);
                    if (args[1].equalsIgnoreCase("set")) PlayerManager.setLife(player, amount);
                    sender.sendMessage(MessageUtils.getMessage("cmd.life.edit.success", player.getName()));
                }
            }
        }
        return true;
    }

    private GuiInventory generateBoogieList(String[] args) {
        //todo:
        // Parse args to find page. Args will only be page
        // Pagify and display current boogies
        int page = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
        StringBuilder config = new StringBuilder();
        List<GuiItem> items = new ArrayList<>();
        Pagifier pagifier = new Pagifier(BoogieManager.getBoogies(), 27);
        int page_size;
        try {
            page_size = pagifier.getPage(page).size();
            for (int i = 0; i < page_size; i++) {

                OfflinePlayer target = Bukkit.getOfflinePlayer((UUID) pagifier.getPage(page).get(i));
                GuiItem item = new GuiItem(((char) i) + "");
                setupPlayerInfo(item, target);
                items.add(item);
                config.append(item.getIdentifier());
            }
        } catch (NullPointerException ex) {
            page_size=0;
        }


        GuiItem search = new GuiItem("W");
        search.setMaterial(Material.STICK);
        search.setCustomModelData(107);
        search.setDisplayName("&bSearch");

        config.append("X".repeat(Math.max(0, 27 - page_size)));
        if (page < pagifier.getPages() && page > 1) config.append("XXXYXZXXX");
        if (page < pagifier.getPages() && page == 1) config.append("XXXXXZXXX");
        if (page >= pagifier.getPages() && pagifier.getPages() != 1) config.append("XXXYXXXXX");
        if (page == pagifier.getPages() && page == 1) config.append("XXXXXXXXX");
        GuiInventory inv = new GuiInventory(new Date().getTime() + "", "&a&lParty List", 36, config.toString());
        for (GuiItem item : items)
            inv.addItem(item);
        GuiItem air = new GuiItem("X");
        air.setMaterial(Material.AIR);
        inv.addItem(air);

        GuiItem next = new GuiItem("Z");
        next.setMaterial(Material.STICK);
        next.setCustomModelData(103);
        next.setDisplayName("&aNext Page (" + (page + 1) + "/" + pagifier.getPages() + ")");
        JSONArray nextArray = new JSONArray();
        JSONObject nextAction1 = new JSONObject();
        nextAction1.put("action", "command");
        nextAction1.put("command", "status boogie " + (page + 1));
        nextArray.put(nextAction1);
        next.setActions(nextArray);
        inv.addItem(next);

        GuiItem prev = new GuiItem("Y");
        prev.setMaterial(Material.STICK);
        prev.setCustomModelData(104);
        prev.setDisplayName("&aPrevious Page (" + (page - 1) + "/" + pagifier.getPages() + ")");
        JSONArray prevArray = new JSONArray();
        JSONObject prevAction1 = new JSONObject();
        prevAction1.put("action", "command");
        prevAction1.put("command", "status boogie " + (page - 1));
        prevArray.put(prevAction1);
        prev.setActions(prevArray);
        inv.addItem(prev);
        return inv;


    }

    private GuiInventory generatePartyList(Player sender, String[] args) {
        //If party == "none" list parties, otherwise list all players in named party
        GuiInventory inv;
        String party = args.length >= 2 ? args[1] : "none";
        StringBuilder config = new StringBuilder();
        List<GuiItem> items = new ArrayList<>();
        Pagifier pagifier;
        int page;
        int page_size;
        int pages;
        if (party.equalsIgnoreCase("none") || party.matches("-?\\d+")) {
            page = party.equalsIgnoreCase("none") ? 1 : Integer.parseInt(party);
            pagifier = new Pagifier(PartyManager.getParties(), 27);
            page_size = pagifier.getPage(page).size();
            pages = pagifier.getPages();
            for (int i = 0; i < page_size; i++) {

                String pname = (String) pagifier.getPage(page).get(i);
                GuiItem item = new GuiItem(((char) i) + "");
                item.setMaterial(Material.RED_WOOL);
                item.setDisplayName("&a" + pname);

                JSONArray array = new JSONArray();
                JSONObject action1 = new JSONObject();
                action1.put("action", "command");
                action1.put("command", "status party " + pname);
                array.put(action1);
                item.setActions(array);
                items.add(item);
                config.append(item.getIdentifier());
            }


            GuiItem search = new GuiItem("W");
            search.setMaterial(Material.STICK);
            search.setCustomModelData(107);
            search.setDisplayName("&bSearch");


        } else {
            page = args.length == 3 ? Integer.parseInt(args[2]) : 1;

            try {
                pagifier = new Pagifier(PartyManager.getPlayers(party), 27);
                page_size = pagifier.getPage(page).size();
                pages = pagifier.getPages();
                for (int i = 0; i < page_size; i++) {

                    OfflinePlayer target = Bukkit.getOfflinePlayer((UUID) pagifier.getPage(page).get(i));
                    GuiItem item = new GuiItem(((char) i) + "");
                    setupPlayerInfo(item, target);


                    items.add(item);
                    config.append(item.getIdentifier());
                }
            } catch (NullPointerException ex) {
                sender.sendMessage(MessageUtils.getMessage("error.party.no_party", party));
                page_size=0;
                pages = 1;
            }

        }
        config.append("X".repeat(Math.max(0, 27 - page_size)));
        if (page < pages && page > 1) config.append("XXXYXZXXX");
        if (page < pages && page == 1) config.append("XXXXXZXXX");
        if (page >= pages && pages != 1) config.append("XXXYXXXXX");
        if (page == pages && page == 1) config.append("XXXXXXXXX");
        inv = new GuiInventory(new Date().getTime() + "", "&a&lParty List", 36, config.toString());
        for (GuiItem item : items)
            inv.addItem(item);
        GuiItem air = new GuiItem("X");
        air.setMaterial(Material.AIR);
        inv.addItem(air);

        GuiItem next = new GuiItem("Z");
        next.setMaterial(Material.STICK);
        next.setCustomModelData(103);
        next.setDisplayName("&aNext Page (" + (page + 1) + "/" + pages + ")");
        JSONArray nextArray = new JSONArray();
        JSONObject nextAction1 = new JSONObject();
        nextAction1.put("action", "command");
        String pstr = party.equalsIgnoreCase("none") ? "" : party + " ";
        nextAction1.put("command", "status party " + pstr + (page + 1));
        nextArray.put(nextAction1);
        next.setActions(nextArray);
        inv.addItem(next);

        GuiItem prev = new GuiItem("Y");
        prev.setMaterial(Material.STICK);
        prev.setCustomModelData(104);
        prev.setDisplayName("&aPrevious Page (" + (page - 1) + "/" + pages + ")");
        JSONArray prevArray = new JSONArray();
        JSONObject prevAction1 = new JSONObject();
        prevAction1.put("action", "command");
        prevAction1.put("command", "status party " + pstr + (page - 1));
        prevArray.put(prevAction1);
        prev.setActions(prevArray);
        inv.addItem(prev);
        return inv;
    }

    private GuiInventory generateStatusMenu() {
        GuiInventory inv = new GuiInventory(new Date().getTime() + "", "&e&lStatus Menu", 27, "XXXXXXXXX" + "XAXXBXXCX" + "XXXXXXXXX");
        GuiItem party = new GuiItem("A");
        party.setMaterial(Material.STICK).setCustomModelData(109).setDisplayName("&a&lParty").setLore("&7Search by party");
        party.addAction(new JSONObject().put("action", "command").put("command", "status party"));
        inv.addItem(party);

        GuiItem boogie = new GuiItem("B");
        boogie.setMaterial(Material.STICK).setCustomModelData(110).setDisplayName("&c&lBoogie").setLore("&7Search for boogies");
        boogie.addAction(new JSONObject().put("action", "command").put("command", "status boogie"));
        inv.addItem(boogie);

        GuiItem player = new GuiItem("C");
        player.setMaterial(Material.STICK).setCustomModelData(110).setDisplayName("&e&lPlayer").setLore("&7List all players");
        player.addAction(new JSONObject().put("action", "command").put("command", "status player"));
        inv.addItem(player);

        inv.addItem(new GuiItem("X").setMaterial(Material.AIR));

        return inv;

    }

    private GuiInventory generatePlayerStatus(OfflinePlayer target) {
        GuiInventory inv = new GuiInventory(target.getName(), "&e&lStatus&7: " + target.getName(), 36, "AXXXXXXXX" + "XXXXXXDXX" + "XXBXXXCXX" + "XXXXXXEXX");
        GuiItem head = new GuiItem("A");
        head.setMaterial(Material.PLAYER_HEAD);
        head.setSkullData(new GuiItem.SkullData(target.getUniqueId()));
        head.setDisplayName("&7" + target.getName());
        head.addAction(new JSONObject().put("action", "command").put("command", "status"));
        inv.addItem(head);

        GuiItem boogie = new GuiItem("B");
        boogie.setDisplayName("&cBoogie Status");
        boogie.setLore("&7Click to toggle.", "&7Current status: " + (PlayerManager.isBoogie(target) ? "&ayes" : "&cno"));
        boogie.setMaterial(Material.STICK).setCustomModelData(101);
        boogie.addAction(new JSONObject().put("action", "command").put("command", PlayerManager.isBoogie(target) ? "lastlife boogie remove " + target.getName() : "lastlife boogie set " + target.getName()));
        boogie.addAction(new JSONObject().put("action", "command").put("command", "status player " + target.getName()));
        inv.addItem(boogie);

        GuiItem heart = new GuiItem("C");
        heart.setDisplayName("&aLives&7: " + PlayerManager.getLives(target)).setMaterial(Material.TOTEM_OF_UNDYING).setCustomModelData(1000);
        inv.addItem(heart);

        GuiItem up = new GuiItem("D");
        up.setDisplayName("&aIncrease").setMaterial(Material.STICK).setCustomModelData(105);
        up.addAction(new JSONObject().put("action", "command").put("command", "lastlife life add " + target.getName() + " 1 false"));
        up.addAction(new JSONObject().put("action", "command").put("command", "status player " + target.getName()));
        inv.addItem(up);

        GuiItem down = new GuiItem("E");
        down.setDisplayName("&cDecrease").setMaterial(Material.STICK).setCustomModelData(106);
        down.addAction(new JSONObject().put("action", "command").put("command", "lastlife life add " + target.getName() + " -1"));
        down.addAction(new JSONObject().put("action", "command").put("command", "status player " + target.getName()));
        inv.addItem(down);

        inv.addItem(new GuiItem("X").setMaterial(Material.AIR));
        return inv;
    }

    private GuiInventory generatePlayerList(String[] args) {
        int page = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
        StringBuilder config = new StringBuilder();
        List<GuiItem> items = new ArrayList<>();
        Pagifier pagifier = new Pagifier(PlayerManager.getPlayers(), 27);
        for (int i = 0; i < pagifier.getPage(page).size(); i++) {

            UUID uid = (UUID) pagifier.getPage(page).get(i);
            OfflinePlayer target = Bukkit.getOfflinePlayer(uid);
            GuiItem item = new GuiItem(((char) i) + "");
            setupPlayerInfo(item, target);


            items.add(item);
            config.append(item.getIdentifier());


        }
        for (int i = pagifier.getPage(page).size(); i < 27; i++) {
            config.append("X");
        }
        if (page < pagifier.getPages() && page > 1) config.append("XXXYXZXXX");
        if (page < pagifier.getPages() && page == 1) config.append("XXXXXZXXX");
        if (page >= pagifier.getPages() && pagifier.getPages() != 1) config.append("XXXYXXXXX");
        if (page == pagifier.getPages() && page == 1) config.append("XXXXXXXXX");
        GuiInventory inv = new GuiInventory(new Date().getTime() + "", "&e&lStatus", 36, config.toString());
        for (GuiItem item : items)
            inv.addItem(item);
        GuiItem air = new GuiItem("X");
        air.setMaterial(Material.AIR);
        inv.addItem(air);

        GuiItem next = new GuiItem("Z");
        next.setMaterial(Material.STICK);
        next.setCustomModelData(103);
        next.setDisplayName("&aNext Page (" + (page + 1) + "/" + pagifier.getPages() + ")");
        JSONArray nextArray = new JSONArray();
        JSONObject nextAction1 = new JSONObject();
        nextAction1.put("action", "command");
        nextAction1.put("command", "status admin " + (page + 1));
        nextArray.put(nextAction1);
        next.setActions(nextArray);
        inv.addItem(next);

        GuiItem prev = new GuiItem("Y");
        prev.setMaterial(Material.STICK);
        prev.setCustomModelData(104);
        prev.setDisplayName("&aPrevious Page (" + (page - 1) + "/" + pagifier.getPages() + ")");
        JSONArray prevArray = new JSONArray();
        JSONObject prevAction1 = new JSONObject();
        prevAction1.put("action", "command");
        prevAction1.put("command", "status admin " + (page - 1));
        prevArray.put(prevAction1);
        prev.setActions(prevArray);
        inv.addItem(prev);

        GuiItem search = new GuiItem("W");
        search.setMaterial(Material.STICK);
        search.setCustomModelData(107);
        search.setDisplayName("&bSearch");

        return inv;
    }

    private void setupPlayerInfo(GuiItem item, OfflinePlayer target) {
        item.setMaterial(Material.PLAYER_HEAD);
        item.setSkullData(new GuiItem.SkullData(target.getUniqueId()));

        item.setDisplayName("&a" + target.getName());
        int lives = PlayerManager.getLives(target);
        item.setLore("&7Online: " + (target.isOnline() ? "&ayes" : "&cno"), "&7Boogie: " + (PlayerManager.isBoogie(target) ? "&ayes" : "&cno"), "&7Lives: " + (lives > 3 ? "&2" : lives == 3 ? "&a" : lives == 2 ? "&e" : lives == 1 ? "&c" : "&7") + lives);

        JSONArray array = new JSONArray();
        JSONObject action1 = new JSONObject();
        action1.put("action", "command");
        action1.put("command", "status player " + target.getName());
        array.put(action1);
        item.setActions(array);
    }
}
