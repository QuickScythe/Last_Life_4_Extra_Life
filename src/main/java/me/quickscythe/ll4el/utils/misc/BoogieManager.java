package me.quickscythe.ll4el.utils.misc;

import com.google.gson.JsonDeserializationContext;
import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.runnables.BoogieParticles;
import me.quickscythe.ll4el.utils.misc.runnables.BoogieTimer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json2.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BoogieManager {
    private static final File file = new File(CoreUtils.getPlugin().getDataFolder() + "/boogies.json");
    private static JSONObject boogies = new JSONObject();

    public static void start() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        StringBuilder data = new StringBuilder();
        try {
            if (!file.exists())
                file.createNewFile();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        boogies = data.toString().isEmpty() ? new JSONObject() : new JSONObject(data.toString());

        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(), new BoogieParticles(), 0);
    }

    private static void checkData(OfflinePlayer player) {
        if (!boogies.has(String.valueOf(player.getUniqueId()))) {
            JSONObject pd = new JSONObject();
            pd.put("last_selected", 0);
            pd.put("boogie", false);
            boogies.put(String.valueOf(player.getUniqueId()), pd);
        }
    }

    public static void end() {
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(boogies.toString());
            f2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean isBoogie(OfflinePlayer player) {
        checkData(player);
        return boogies.getJSONObject(String.valueOf(player.getUniqueId())).getBoolean("boogie");
    }

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
            checkData(pot);
            JSONObject potd = getBoogieInfo(pot);
            if (!potd.getBoolean("boogie") && new Date().getTime() - potd.getLong("last_selected") > 10000) {
                selected = selected + 1;
                potd.put("boogie", true);
                potd.put("last_selected", new Date().getTime());
                setBoogieInfo(pot, potd);
                pot.sendMessage(MessageUtils.colorize("&cYou are a Boogie! Kill someone quick to get rid of this effect"));
            }
        }
    }

    private static void setBoogieInfo(Player player, JSONObject pd) {
        checkData(player);
        boogies.put(String.valueOf(player.getUniqueId()), pd);
    }

    public static JSONObject getBoogieInfo(OfflinePlayer player) {
        checkData(player);
        return boogies.getJSONObject(String.valueOf(player.getUniqueId()));
    }


    public static void removeBoogie(Player player) {
        JSONObject bi = getBoogieInfo(player);
        bi.put("boogie", false);
        bi.put("last_selected", new Date().getTime());
        setBoogieInfo(player,bi);
    }
}
