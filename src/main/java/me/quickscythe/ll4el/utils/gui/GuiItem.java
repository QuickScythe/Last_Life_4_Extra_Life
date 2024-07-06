package me.quickscythe.ll4el.utils.gui;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.chat.placeholder.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json2.JSONArray;
import org.json2.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GuiItem {
    String id;
    String display_name = "default_name";
    Material mat = Material.GRASS_BLOCK;
    List<String> lore = null;
    JSONArray actions = new JSONArray();
    int amount = 1;

    public GuiItem(String id) {
        this.id = id;
    }

    public GuiItem setDisplayName(String display_name) {
        this.display_name = display_name;
        return this;
    }

    public GuiItem setMaterial(Material mat) {
        this.mat = mat;
        return this;
    }

    public GuiItem setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public GuiItem addAction(JSONObject action) {
        this.actions.put(action);
        return this;
    }

    public GuiItem setAmount(int i) {
        this.amount = i;
        return this;
    }

    public int getAmount() {
        return amount;
    }


    public GuiItem setActions(JSONArray actions) {
        this.actions = actions;
        return this;
    }


    public JSONArray getActions() {
        return actions;
    }

    public String getIdentifier() {
        return id;
    }

    public ItemStack getItem(Player player) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (lore != null) {
            List<String> tmp = new ArrayList<>();
            if (meta.hasLore()) for (String a : meta.getLore())
                tmp.add(a);
            for (String a : lore) {
                tmp.add(MessageUtils.colorize(PlaceholderUtils.replace(player, a)));
            }
            meta.setLore(tmp);
        }
        if (meta != null) {

            meta.addItemFlags(ItemFlag.values());
            meta.setDisplayName(MessageUtils.colorize(PlaceholderUtils.replace(player, display_name)));
            item.setItemMeta(meta);

        }
        item.setAmount(amount);

        return item;
    }

    public boolean hasAction() {
        return actions.length() != 0;
    }

    /**
     * @param player Online Player entity
     * @param type Type of Click
     * @return returns true if all processes run
     */
    public boolean processActions(Player player, ClickType type) {
        for (int i = 0; i < actions.length(); i++) {
            if (!processAction(player, actions.getJSONObject(i), type)) return false;
        }

        return true;
    }

    private boolean processAction(Player player, JSONObject action, ClickType click) {
        if ((action.has("click") && ClickType.valueOf(action.getString("click")).equals(click)) || !action.has("click")) {
            switch (action.getString("action").toLowerCase()) {
                case "sound":
                    player.playSound(player.getLocation(), Sound.valueOf(action.getString("sound")), 10F, 1F);
                    return true;
//            case "sell":
//                ItemStack t = getItem(player);
//                if (action.has("amount"))
//                    t.setAmount(Integer.parseInt(action.getString("amount")));
//                if (player.getInventory().contains(t)) {
//                    player.getInventory().remove(t);
//                    Utils.getEconomy().depositPlayer(player, item.getSellPrice());
//                    return true;
//                } else
//                    return false;
                case "buy":
                    int price = action.has("price") ? action.getInt("price") : 1;
                    if (action.getString("buy_type").equalsIgnoreCase("inventory")) {
                        return CoreUtils.consumeItem(player, price, Material.valueOf(action.getString("item")));
                    }
//                if (action.getString("buy_type").equalsIgnoreCase("economy")) {
//                    if (Utils.getEconomy().has(player, price)) {
//                        return Utils.getEconomy().withdrawPlayer(player, price);
//
//                    }
//                }
                    return false;
                case "send_message":
                    player.sendMessage(MessageUtils.colorize(action.getString("message")));
                    return true;
                case "open_gui":
                    try {
                        GuiManager.openGui(player, GuiManager.getGui(action.getString("gui")));
                    } catch (NullPointerException ex) {
                        player.sendMessage(MessageUtils.prefixes("gui") + "There was an error opening that GUI. Does it exist?");
                    }
                    return true;
                case "join_server":
                    MessageUtils.sendPluginMessage(player, "BungeeCord", "Connect", action.getString("server"));
                    return true;

                case "command":
                    String sender = action.has("sender") ? action.getString("sender") : "player";
                    String cmd = PlaceholderUtils.replace(player, action.getString("command"));
                    Bukkit.dispatchCommand(sender.equalsIgnoreCase("CONSOLE") ? Bukkit.getConsoleSender() : player, cmd);
                    return true;
                case "close_gui":
                    player.closeInventory();
                    return true;
            }

        }
        if (action.has("error_message"))
            player.sendMessage(PlaceholderUtils.replace(player, action.getString("error_message")));

        return false;

    }



    public class Action {

    }

    public enum ActionType {
        SOUND, SEND_MESSAGE, OPEN_GUI,
    }
}
