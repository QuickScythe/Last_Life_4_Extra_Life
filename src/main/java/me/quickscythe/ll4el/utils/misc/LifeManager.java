package me.quickscythe.ll4el.utils.misc;

import me.quickscythe.ll4el.utils.CoreUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;
import org.json2.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LifeManager {

    private static final File file = new File(CoreUtils.getPlugin().getDataFolder() + "/lives.json");
    private static JSONObject lives;

    public static void start() {

        setupTeams();

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
        lives = data.toString().isEmpty() ? new JSONObject() : new JSONObject(data.toString());


    }

    private static void setupTeams() {
        try {
            Team red = CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("red");
            red.setColor(ChatColor.RED);

            Team yellow = CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("yellow");
            yellow.setColor(ChatColor.YELLOW);

            Team lime = CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("lime");
            lime.setColor(ChatColor.GREEN);

            Team green = CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("green");
            green.setColor(ChatColor.DARK_GREEN);

            Team gray = CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("gray");
            gray.setColor(ChatColor.GRAY);
        } catch (IllegalArgumentException ignored){}
    }

    public static void end() {
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(lives.toString());
            f2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void checkLives(OfflinePlayer player) {
        if (!lives.has(String.valueOf(player.getUniqueId())))
            lives.put(String.valueOf(player.getUniqueId()), 3);
    }

    public static int getLives(OfflinePlayer player) {
        checkLives(player);
        return lives.getInt(String.valueOf(player.getUniqueId()));
    }

    public static void removeLife(OfflinePlayer player) {
        editLife(player, -1);
    }


    public static void addLife(OfflinePlayer player) {
        editLife(player, 1);
    }

    public static void editLife(OfflinePlayer player, int i) {
        checkLives(player);
        int l = getLives(player) + i;
        lives.put(String.valueOf(player.getUniqueId()), l);
        if(l == 0){
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").addEntry(player.getName());
        }
        if(l == 1){
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
        if(l == 2){
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
        if(l == 3){
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
        if(l > 3){
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("yellow").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("lime").removeEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("green").addEntry(player.getName());
            CoreUtils.getPlugin().getServer().getScoreboardManager().getMainScoreboard().getTeam("gray").removeEntry(player.getName());
        }
    }
}
