package me.quickscythe.ll4el.utils.misc.managers;

import me.quickscythe.ll4el.utils.chat.ChatManager;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.ll4el.utils.misc.managers.config.ConfigFile;
import me.quickscythe.ll4el.utils.misc.managers.config.ConfigFileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.common.value.qual.ArrayLenRange;
import org.json2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyManager {

    static ConfigFile parties;
    static List<UUID> in_chat = new ArrayList();

    public static void start() {
        parties = ConfigFileManager.getFile("parties");
    }

    public static void end(){
        parties.save();
    }

    public static JSONObject createParty(String name) {
        JSONObject party = new JSONObject();
        parties.getData().put(name, party);
        return party;
    }

    public static JSONObject getParty(String name) {
        try {
            return parties.getData().getJSONObject(name);
        } catch (NullPointerException ex) {
            throw new NullPointerException("Couldn't find party: " + name);
        }
    }

    public static void removeParty(String name) {
        parties.getData().remove(name);
    }

    public static boolean inPartyChat(OfflinePlayer player) {
        return in_chat.contains(player.getUniqueId());
    }

    public static void handleChat(Player player, String message) {
        String format = PlaceholderUtils.replace(player, ChatManager.getFormat("party") + ChatManager.getFormat("player"));
        String party = PlayerManager.getParty(player);
        for (Player r : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.getParty(r).equalsIgnoreCase(party) || r.hasPermission("lastlife.admin.see_chat"))
                r.sendMessage(MessageUtils.colorize((r.hasPermission("lastlife.admin.see_chat") ? "&7&o(" + party + ")" : "") + format) + message);
        }
    }

    public static void joinChat(Player player) {
        in_chat.add(player.getUniqueId());
        player.sendMessage(MessageUtils.getMessage("party.chat.join"));
    }

    public static void leaveChat(Player player) {
        in_chat.remove(player.getUniqueId());
        player.sendMessage(MessageUtils.getMessage("party.chat.leave"));
    }

    public static void toggleChat(Player player) {
        if (in_chat.contains(player.getUniqueId())) leaveChat(player);
        else joinChat(player);
    }

    public static List<String> getParties() {
        return new ArrayList<>(PartyManager.parties.getData().keySet());
    }

    public static List<UUID> getPlayers(String party) {
        List<UUID> uids = new ArrayList<>();
        for(UUID uid : PlayerManager.getPlayers())
            if(PlayerManager.getParty(Bukkit.getOfflinePlayer(uid)).equalsIgnoreCase(party))
                uids.add(uid);
        return uids;
    }
}
