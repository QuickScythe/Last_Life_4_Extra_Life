package me.quickscythe.ll4el.utils.chat.placeholder;

import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.managers.LifeManager;
import me.quickscythe.ll4el.utils.misc.managers.PartyManager;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
import me.quickscythe.ll4el.utils.misc.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PlaceholderUtils {

    static Map<String, PlaceholderWorker> placeholders = new HashMap<>();

    public static void registerPlaceholders() {
        registerPlaceholder("name", Player::getName);
        registerPlaceholder("player", Player::getName);
        registerPlaceholder("online", (player) -> Bukkit.getOnlinePlayers().size() + "");
        registerPlaceholder("lives_color", LifeManager::getLifeColor);

        registerPlaceholder("setting_particles", (player)-> SettingsManager.getSettings(player).particles() ? "&aon" : "&coff");
        registerPlaceholder("setting_icon", (player)-> SettingsManager.getSettings(player).icon() ? "&aon" : "&coff");
        registerPlaceholder("setting_chat", (player)-> SettingsManager.getSettings(player).chat() ? "&aon" : "&coff");
        registerPlaceholder("setting_chat", (player)-> SettingsManager.getSettings(player).chat() ? "&aon" : "&coff");
        registerPlaceholder("party", (player)-> PlayerManager.getParty(player).equalsIgnoreCase("none") ? "" : PlayerManager.getParty(player));
        registerPlaceholder("party_tag", (player)-> PlayerManager.getParty(player).equalsIgnoreCase("none") ? "&f" : "[" + PlayerManager.getParty(player) + "]");

    }

    public static void registerPlaceholder(String key, PlaceholderWorker worker) {
        placeholders.put("%" + key + "%", worker);
    }

    public static String replace(Player player, String string) {

        for (Entry<String, PlaceholderWorker> e : placeholders.entrySet()) {
            if (string.contains(e.getKey())) {
                string = string.replaceAll(e.getKey(), e.getValue().run(player));
            }
        }

        string = emotify(string);
        return string;
    }

    public static String emotify(String string) {
        String tag = string;
        while (tag.contains("%symbol:")) {
            String icon = tag.split("ymbol:")[1].split("%")[0];
            if (Symbols.valueOf(icon.toUpperCase()) == null) {
                tag = tag.replaceAll("%symbol:" + icon + "%", Symbols.UNKNOWN.toString());
            } else {
                tag = tag.replaceAll("%symbol:" + icon + "%", Symbols.valueOf(icon.toUpperCase()).toString());
            }
        }
        return tag;
    }

    public static String markup(Player player, String string) {
        String tag = string;
        while (tag.contains("%bold:")) {
            String icon = tag.split("old:")[1].split("%")[0];
            tag = tag.replaceAll("%bold:" + icon + "%", ChatColor.BOLD + icon + ChatColor.getLastColors(tag.split("%bold")[0]));
        }
        while (tag.contains("%upper:")) {
            String icon = tag.split("pper:")[1].split("%")[0];
            tag = tag.replaceAll("%upper:" + icon + "%", icon.contains("%") ? replace(player, icon).toUpperCase() : icon.toUpperCase());
        }
        while (tag.contains("%fade:")) {
            String from = tag.split(":")[1];
            String to = tag.split(":")[2];
            String s = tag.split(":")[3].split("%")[0];

            tag = tag.replaceFirst("%fade:" + from + ":" + to + ":" + s + "%", MessageUtils.fade(from, to, s));
        }
        return tag;
    }

    @FunctionalInterface
    public interface PlaceholderWorker {

        public abstract String run(Player player);

    }

}