package me.quickscythe.ll4el.utils.misc.managers.loot;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.misc.managers.config.ConfigFile;
import me.quickscythe.ll4el.utils.misc.managers.config.ConfigFileManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LootManager {
    static Map<String, LootTable> tables_map = new HashMap<>();
    private static ConfigFile drops;

    public static void start() {
        drops = ConfigFileManager.getFile("loot_drops");
        ConfigFile tables = ConfigFileManager.getFile("loot_tables", CoreUtils.getPlugin().getResource("loot_tables.json"));
        for (String s : tables.getData().keySet()) {
            tables_map.put(s, new LootTable(tables.getData().getJSONObject(s)));
        }
    }

    public static void end() {
        drops.save();
    }

    public static LootTable getLootTable(String name) {
        return tables_map.get(name);
    }

    public static void createDrop(String name, Location location) {
        drops.getData().put(name, CoreUtils.encryptLocation(location));
    }

    public static void dropLoot(String name, DropType type) {
        Location drop = CoreUtils.decryptLocation(drops.getData().getString(name));
        switch (type) {
            case OTHER -> drop.getBlock().setType(new Random().nextBoolean() ? Material.CHEST : Material.BARREL);
            case SHULKER -> drop.getBlock().setType(Material.SHULKER_BOX);
        }
        Container block = (Container) drop.getBlock().getState();
        Inventory inv = block.getInventory();
        generateLoot(inv, tables_map.get(tables_map.keySet().stream().skip(new Random().nextInt(tables_map.size())).findFirst().get()));

    }

    public static void dropLoot(DropType type) {
        dropLoot(drops.getData().keySet().stream().skip(new Random().nextInt(drops.getData().length())).findFirst().get(), type);

    }

    private static void generateLoot(Inventory inv, LootTable table) {
        for (LootItem loot : table.generateItems()) {
            inv.setItem(new Random().nextInt(inv.getSize()), loot.generateItem());
        }
    }

    public enum DropType {

        SHULKER, OTHER;

        DropType() {

        }
    }
}
