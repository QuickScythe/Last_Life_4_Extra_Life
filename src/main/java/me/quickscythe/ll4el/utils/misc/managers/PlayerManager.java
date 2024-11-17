package me.quickscythe.ll4el.utils.misc.managers;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.managers.config.ConfigFile;
import me.quickscythe.ll4el.utils.misc.managers.config.ConfigFileManager;
import org.bukkit.OfflinePlayer;
import org.json2.JSONObject;

import java.util.*;

public class PlayerManager {
//    private static final File file = new File(CoreUtils.getPlugin().getDataFolder() + "/players.json");
//    private static JSONObject players = new JSONObject();

    private static ConfigFile config;

    public static void start() {
        config = ConfigFileManager.getFile("players");

    }

    public static void checkData(OfflinePlayer player) {
        if (!config.getData().has(String.valueOf(player.getUniqueId()))) {
            JSONObject pd = new JSONObject();
            pd.put("last_selected", 0);
            pd.put("boogie", false);
            pd.put("lives", 3);
            pd.put("name", player.getName());
            pd.put("settings", SettingsManager.Settings.defaultSettings());
            pd.put("party", "none");
            config.getData().put(String.valueOf(player.getUniqueId()), pd);

        }
        JSONObject pd = getPlayerData(player);
        pd.put("name", player.getName());
        setPlayerData(player, pd);
        setScoreboardInfo(player);
    }

    public static void end() {
        config.save();
    }


    @Deprecated
    public static UUID getUUID(String lastUsername) {
        for (String sid : config.getData().keySet()) {
            if (config.getData().getJSONObject(sid).get("name").equals(lastUsername)) return UUID.fromString(sid);
        }
        return null;
    }

    public static JSONObject getPlayerData(OfflinePlayer player) {
        return config.getData().getJSONObject(String.valueOf(player.getUniqueId()));
    }

    public static void setPlayerData(OfflinePlayer player, JSONObject json) {
        config.getData().put(String.valueOf(player.getUniqueId()), json);
    }

    public static String getParty(OfflinePlayer player){
        return config.getData().getJSONObject(String.valueOf(player.getUniqueId())).getString("party");
    }

    public static void setParty(OfflinePlayer player, String party){
        setPlayerData(player, getPlayerData(player).put("party",party));
        if(player.isOnline())
            player.getPlayer().sendMessage(MessageUtils.getMessage("party.join.success", party));
    }

    public static boolean isBoogie(OfflinePlayer player) {
        return getPlayerData(player).getBoolean("boogie");
    }


    public static void removeBoogie(OfflinePlayer player) {
        JSONObject pd = getPlayerData(player);
        pd.put("boogie", false);
        pd.put("last_selected", new Date().getTime());
        if (player.isOnline()) player.getPlayer().sendMessage(MessageUtils.getMessage("message.boogie.cured"));
        setPlayerData(player, pd);
    }


    public static int getLives(OfflinePlayer player) {
        return getPlayerData(player).getInt("lives");
    }

    public static void removeLife(OfflinePlayer player) {
        editLife(player, -1);
    }


    public static void addLife(OfflinePlayer player) {
        editLife(player, 1);
    }

    public static void setLife(OfflinePlayer player, int l) {
        JSONObject pd = getPlayerData(player);
        pd.put("lives", l);
        setPlayerData(player, pd);
        setScoreboardInfo(player);
    }

    public static void editLife(OfflinePlayer player, int i) {
        editLife(player, i, true);
    }

    public static void editLife(OfflinePlayer player, int i, boolean animation) {
        int l = getLives(player) + i;
        setLife(player, l);
        if (i > 0 && player.isOnline()) {
            int cmd = 1000 + i;
            if (animation) CoreUtils.playTotemAnimation(player.getPlayer(), cmd);
            player.getPlayer().sendMessage(MessageUtils.getMessage("message.lives.more", i +""));
        }
    }

    private static void setScoreboardInfo(OfflinePlayer player) {
        int l = getLives(player);
        if (l == 0) {
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").addEntry(player.getName());
        }
        if (l == 1) {
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
        if (l == 2) {
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
        if (l == 3) {
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
        if (l > 3) {
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
    }

    public static void setBoogie(OfflinePlayer player) {
        JSONObject pd = getPlayerData(player);
        pd.put("boogie", true);
        pd.put("last_selected", new Date().getTime());
        setPlayerData(player, pd);
        if (SettingsManager.getSettings(player).chat() && player.isOnline())
            player.getPlayer().sendMessage(MessageUtils.getMessage("message.boogie.chat"));

    }

    public static List<UUID> getPlayers() {
        List<UUID> uids = new ArrayList<>();
        for (String sid : config.getData().keySet()) {
            uids.add(UUID.fromString(sid));
        }
        return uids;

    }
}
