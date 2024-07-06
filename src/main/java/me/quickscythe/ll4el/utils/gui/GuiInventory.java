package me.quickscythe.ll4el.utils.gui;

import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.chat.placeholder.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GuiInventory {

    String id;
    String display_name = "";
    String config = "xxxxxx";
    int size = 9;
    Map<String, GuiItem> items = new HashMap<>();
    private Map<Player, Map<ItemStack, GuiItem>> storedItems = new HashMap<>();

    @Deprecated
    public GuiInventory(String id) {
        this.id = id;
    }

    @Deprecated
    public GuiInventory(String id, String display_name) {
        this(id);
        this.display_name = MessageUtils.colorize(display_name);
    }

    @Deprecated
    public GuiInventory(String id, String display_name, int size) {
        this(id, display_name);
        this.size = size;
    }

    public GuiInventory(String id, String display_name, int size, String config) {
        this(id, display_name, size);
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public void addItem(String identifier, GuiItem item) {
        items.put(identifier, item);
    }

    public Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, size, PlaceholderUtils.replace(player, display_name));
        for (int i = 0; i != config.length(); i++) {
            String key = config.substring(i, i + 1);
            inv.setItem(i, getGuiItem(key).getItem(player));
        }
        return inv;
    }

    public GuiItem getGuiItem(String key) {
        return items.get(key);
    }

    public boolean hasItem(ItemStack item, Player player) {
        return getItem(item, player) != null;
    }

    public GuiItem getItem(ItemStack item, Player player) {
        if (storedItems.containsKey(player)) {
            for (Entry<ItemStack, GuiItem> entry : storedItems.get(player).entrySet())
                if (entry.getKey().equals(item)) return entry.getValue();

        }
        return null;
    }

    public String getConfig() {
        return config;
    }

    public Map<String, GuiItem> getItemMap() {
        return items;
    }

    protected void open(Player player) {
        player.openInventory(getInventory(player));
        storedItems.put(player, new HashMap<ItemStack, GuiItem>());
        for (GuiItem item : items.values()) {
            storedItems.get(player).put(item.getItem(player), item);
        }
    }

    protected void close(Player player) {
        if (player.getOpenInventory() != null) player.closeInventory();
        storedItems.remove(player);
    }
}