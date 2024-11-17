package me.quickscythe.ll4el.utils.misc.managers.loot;

import org.json2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootTable {

    LootManager.DropType type;
    List<LootItem> items;
    String amount = "5-10";

    public LootTable(JSONObject data) {
        type = LootManager.DropType.valueOf(data.getString("type"));
        items = new ArrayList<>();
        amount = data.has("amount") ? data.getString("amount") : amount;
        for (int i = 0; i != data.getJSONArray("items").length(); i++) {
            items.add(new LootItem(data.getJSONArray("items").getJSONObject(i)));
        }
    }

    public List<LootItem> getItems() {
        return items;
    }

    public List<LootItem> generateItems() {
        List<LootItem> loot = new ArrayList<>();
        int min = 0;
        int max = 0;
        if (amount.contains("-")) {
            min = Integer.parseInt(amount.split("-")[0]);
            max = Integer.parseInt(amount.split("-")[1]);
        } else {
            min = Integer.parseInt(amount);
            max = min;
        }
        int goal = new Random().nextInt(max - min) + min;
        for (int i = 0; i != goal; i++) {
            loot.add(checkRarity());
        }
        return loot;
    }

    private LootItem checkRarity() {
        LootItem item = getItems().get(new Random().nextInt(getItems().size()));
        if (new Random().nextDouble() <= (item.getData().has("rarity") ? item.getData().getDouble("rarity") : 1D))
            return item;
        else return checkRarity();
    }
}
