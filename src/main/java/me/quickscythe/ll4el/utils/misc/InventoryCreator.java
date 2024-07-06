package me.quickscythe.ll4el.utils.misc;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryCreator {

	String name;
	Player holder;
	Inventory inv;
	List<ItemStack> items = new LinkedList<>();
	Map<Character, ItemStack> identifier = new LinkedHashMap<>();

	public InventoryCreator(String name, Player holder, int size) {
		this.name = name;
		this.holder = holder;
		inv = Bukkit.createInventory(holder, size, MessageUtils.colorize(name));
	}

	public void addItem(ItemStack item, String name, char identifier, List<String> lore) {

		addItem(item, name, identifier, lore.toArray(new String[lore.size()]), false, true);
	}

	public void addItem(ItemStack item, String name, char identifier, String[] lore) {
		addItem(item, name, identifier, lore, false, true);
	}

	public void addItem(ItemStack item, String name, char identifier, List<String> lore, boolean showValues) {
		addItem(item, name, identifier, lore.toArray(new String[lore.size()]), false, showValues);
	}

	public void addItem(ItemStack item, String name, char identifier, String[] lore, boolean showValues) {
		addItem(item, name, identifier, lore, false, showValues);
	}

	public void addItem(ItemStack item, String name, char identifier, List<String> lore, boolean unbreakable,
			boolean showValues) {
		addItem(item, name, identifier, lore.toArray(new String[lore.size()]), unbreakable, showValues);
	}

	public void addItem(ItemStack item, String name, char identifier, String[] lore, boolean unbreakable,
			boolean showValues) {
		addItem(item, name, identifier, lore, unbreakable, showValues, (short) 0);
	}

	public void addItem(ItemStack item, String name, char identifier, List<String> lore, boolean unbreakable,
			boolean showValues, short data) {
		addItem(item, name, identifier, lore.toArray(new String[lore.size()]), unbreakable, showValues, data);
	}

	@SuppressWarnings("deprecation")
	public void addItem(ItemStack item, String name, char identifier, String[] lore, boolean unbreakable,
			boolean showValues, short data) {

		if (item != null && (item.getType() != Material.AIR)) {
			ItemMeta im = item.getItemMeta();
			if (data != (short) 0)
				item.setDurability(data);

			im.setDisplayName(MessageUtils.colorize(name));
			if (lore != null) {

				im.setLore(CoreUtils.colorizeStringList(lore));
			} else {
				im.setLore(null);
			}
			im.setUnbreakable(unbreakable);
			if (!showValues) {
				for (ItemFlag flag : ItemFlag.values()) {
					im.addItemFlags(flag);
				}
//				im.addItemFlags(ItemFlag.values());
			}
			item.setItemMeta(im);
		}
		this.identifier.put(identifier, item);
	}

	public void addItem(ItemStack item, String name, char identifier) {
		addItem(item, name, identifier, (String[]) null);
	}

	public void addItem(ItemStack item, char identifier) {
		this.identifier.put(identifier, item);
	}

	public void editItem(char identifier, ItemStack item, String name, List<String> lore, short value) {

		if (item != null && (item.getType() != Material.AIR)) {
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(MessageUtils.colorize(name));
			if (lore != null) {
				im.setLore(CoreUtils.colorizeStringList(lore));
			}
			item.setItemMeta(im);

		}

		this.identifier.put(identifier, item);
	}

	public void editItem(char identifier, ItemStack item, String name, String[] lore, short value) {

		if (item != null && (item.getType() != Material.AIR)) {
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(MessageUtils.colorize(name));
			if (lore != null) {
				im.setLore(CoreUtils.colorizeStringList(lore));
			}
			item.setItemMeta(im);

		}

		this.identifier.put(identifier, item);
	}

	public void editItem(char identifier, String name) {
		ItemStack item = this.identifier.get(identifier);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageUtils.colorize(name));
		item.setItemMeta(meta);
		this.identifier.put(identifier, item);
	}

	public void editItem(char identifier, ItemStack item) {
		ItemMeta meta = this.identifier.get(identifier).getItemMeta();
		item.setItemMeta(meta);
		this.identifier.put(identifier, item);
	}

	public void setConfiguration(char[] c) {
		for (Character ch : c)
			items.add(identifier.get(ch));
	}

	public Inventory getInventory() {
		int a = 0;
		for (ItemStack i : items) {
			if (a < inv.getSize())
				inv.setItem(a, i);
			a = a + 1;
		}
		return inv;
	}

	public String getName() {
		return name;
	}

	public Player getHolder() {
		return holder;
	}

	public void setConfiguration(ArrayList<Character> ids) {
		for (Character ch : ids) {
			items.add(identifier.get(ch));
		}
	}

}