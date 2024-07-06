package me.quickscythe.ll4el.utils.gui;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.json2.JSONArray;
import org.json2.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class GuiManager {

    private static final Map<UUID, GuiInventory> invTracker = new HashMap<>();
    private static final Map<String, GuiInventory> guis = new HashMap<>();
    private static File guiFolder = null;

    public static void init() {
        guiFolder = new File(CoreUtils.getPlugin().getDataFolder().getPath() + "/guis");
        registerGuis();

    }

    private static void registerGuis() {
        guis.clear();
        GuiInventory gui = new GuiInventory("waiting", "&7Waiting...", 9, "XXXXXXXXX");
        GuiItem item = new GuiItem("X");
        item.setDisplayName("&7Waiting...");
        item.setMaterial(Material.GRAY_STAINED_GLASS_PANE);
        gui.addItem("X", item);
        guis.put("waiting", gui);
        try {

            if (!guiFolder.exists()) guiFolder.mkdir();

            for (File file : guiFolder.listFiles())
                if (file.getName().toLowerCase().endsWith(".yml")) loadGuis(file);

        } catch (Exception e) {
            MessageUtils.log("There was an error registering guis.");
            e.printStackTrace();
        }
    }

    public static void openGui(Player player, GuiInventory gui) {
        if (gui == null) return;
        if (invTracker.containsKey(player.getUniqueId())) {
            switchGui(player, gui);
            return;
        }
        gui.open(player);
//        player.openInventory(gui.getInventory(player));
        invTracker.put(player.getUniqueId(), gui);
    }

    public static Map<String, GuiInventory> getGuis() {
        return guis;
    }

    public static GuiInventory getGui(String name) {
        return guis.getOrDefault(name, null);
    }


    public static GuiInventory getOpenGui(Player player) {
        return invTracker.getOrDefault(player.getUniqueId(), null);
    }

    public static void switchGui(Player player, final GuiInventory gui) {
        if (gui == null) return;
        closeGui(player);
        player.setMetadata("switchinv", new FixedMetadataValue(CoreUtils.getPlugin(), "yup"));
//        player.openInventory(getGuis().get(invTracker.get(player.getUniqueId())).getInventory(player));
        invTracker.put(player.getUniqueId(), getGui("waiting"));
        player.openInventory(getGui("waiting").getInventory(player));
        Bukkit.getScheduler().runTaskLater(CoreUtils.getPlugin(), new Runnable() {

            @Override
            public void run() {
                invTracker.remove(player.getUniqueId());
                player.removeMetadata("switchinv", CoreUtils.getPlugin());
                openGui(player, gui);
            }

        }, 5);
    }

    public static void closeGui(Player player) {
        if (invTracker.containsKey(player.getUniqueId())) {
            player.setMetadata("switchinv", new FixedMetadataValue(CoreUtils.getPlugin(), true));
            invTracker.get(player.getUniqueId()).close(player);
            invTracker.remove(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(CoreUtils.getPlugin(),()->{player.removeMetadata("switchinv",CoreUtils.getPlugin());}, 2);

        } else {
            try {
            } catch (Exception ex) {
            }
            invTracker.remove(player.getUniqueId());
        }

    }

    private static void loadGuis(File file) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

        MessageUtils.log("Loading GUIs... (" + file.getName() + ")");
        int x = 0;
        for (String name : fc.getConfigurationSection("guis").getKeys(false)) {
            MessageUtils.log(" - Loading " + name + "...");
            int size = fc.getInt("guis." + name + ".size", 9);
            String sname = MessageUtils.colorize(fc.getString("guis." + name + ".name", "Custom GUI"));
            String array = "";
            for (String s : fc.getStringList("guis." + name + ".config")) {
                array = array + s;
            }
            GuiInventory gui = new GuiInventory(name, sname, size, array);
            for (String iid : fc.getConfigurationSection("guis." + name + ".items").getKeys(false)) {
                MessageUtils.log("  - Adding item: " + iid);
                GuiItem item = new GuiItem(iid);

                if (fc.isSet("guis." + name + ".items." + iid + ".name"))
                    item.setDisplayName(fc.getString("guis." + name + ".items." + iid + ".name"));
                if (fc.isSet("guis." + name + ".items." + iid + ".type")) {
                    String type = fc.getString("guis." + name + ".items." + iid + ".type");

                    item.setMaterial(Material.valueOf(fc.getString("guis." + name + ".items." + iid + ".type").toUpperCase()));
                }


                if (fc.isSet("guis." + name + ".items." + iid + ".lore"))
                    item.setLore(fc.getStringList("guis." + name + ".items." + iid + ".lore"));

                if (fc.isSet("guis." + name + ".items." + iid + ".action")) {
                    JSONObject json = new JSONObject("{}");
                    if (fc.isSet("guis." + name + ".items." + iid + ".action.action"))
                        json.put("action", fc.getString("guis." + name + ".items." + iid + ".action.action"));
                    if (fc.isSet("guis." + name + ".items." + iid + ".action.server"))
                        json.put("server", fc.getString("guis." + name + ".items." + iid + ".action.server"));
                    if (fc.isSet("guis." + name + ".items." + iid + ".action.item"))
                        json.put("item", fc.getString("guis." + name + ".items." + iid + ".action.item"));
                    if (fc.isSet("guis." + name + ".items." + iid + ".action.amount"))
                        json.put("amount", fc.getString("guis." + name + ".items." + iid + ".action.amount"));
                    if (fc.isSet("guis." + name + ".items." + iid + ".action.message"))
                        json.put("message", fc.getString("guis." + name + ".items." + iid + ".action.message"));
                    if (fc.isSet("guis." + name + ".items." + iid + ".action.command"))
                        json.put("command", fc.getString("guis." + name + ".items." + iid + ".action.command"));
                    item.addAction(json);
                }

                if (fc.isSet("guis." + name + ".items." + iid + ".actions")) {
                    JSONArray actions = new JSONArray();

                    for (String clickAction : fc.getConfigurationSection("guis." + name + ".items." + iid + ".actions").getKeys(false)) {
                        for (String a : fc.getConfigurationSection("guis." + name + ".items." + iid + ".actions." + clickAction).getKeys(false)) {
                            String key = "guis." + name + ".items." + iid + ".actions." + clickAction;
                            JSONObject action = new JSONObject("{}");
                            action.put("click", clickAction);
                            if (fc.isSet(key + "." + a + ".action"))
                                action.put("action", fc.getString(key + "." + a + ".action"));
                            if (fc.isSet(key + "." + a + ".server"))
                                action.put("server", fc.getString(key + "." + a + ".server"));
                            if (fc.isSet(key + "." + a + ".item"))
                                action.put("item", fc.getString(key + "." + a + ".item"));
                            if (fc.isSet(key + "." + a + ".amount"))
                                action.put("amount", fc.getString(key + "." + a + ".amount"));
                            if (fc.isSet(key + "." + a + ".message"))
                                action.put("message", fc.getString(key + "." + a + ".message"));
                            if (fc.isSet(key + "." + a + ".command"))
                                action.put("command", fc.getString(key + "." + a + ".command"));
                            actions.put(action);
                        }
                    }
                    item.setActions(actions);
                }

                gui.addItem(item.getIdentifier(), item);

            }

            guis.put(name, gui);
            MessageUtils.log("Successfully loaded " + name);
            x = x + 1;
        }
    }

}