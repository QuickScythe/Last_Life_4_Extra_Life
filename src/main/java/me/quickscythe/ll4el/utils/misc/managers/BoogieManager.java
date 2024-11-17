package me.quickscythe.ll4el.utils.misc.managers;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.misc.runnables.BoogieTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json2.JSONObject;

import java.util.*;

public class BoogieManager {

    public static void rollBoogies(int amount, boolean timer) {
        if (timer) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(), new BoogieTimer(amount), 0);

        } else selectBoogies(amount);

    }

    public static void selectBoogies(int amount) {
        int selected = 0;
        int check = 0;
        while (selected < amount && check < 2) {
            check = check + 1;
            Player pot = (Player) ((List) Bukkit.getOnlinePlayers()).get(new Random().nextInt(Bukkit.getOnlinePlayers().size()));
            JSONObject potd = PlayerManager.getPlayerData(pot);
            if (!potd.getBoolean("boogie") && new Date().getTime() - potd.getLong("last_selected") > 10000) {
                selected = selected + 1;
                PlayerManager.setBoogie(pot);
            }
        }
    }


    public static List<?> getBoogies() {
        List<UUID> uids = new ArrayList<>();
        for (UUID uid : PlayerManager.getPlayers())
            if (PlayerManager.isBoogie(Bukkit.getOfflinePlayer(uid))) uids.add(uid);
        return uids;

    }
}
