package me.quickscythe.ll4el.utils.chat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.quickscythe.ll4el.utils.CoreUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessageUtils {

    private static Map<String, String> prefixes = new HashMap<>();

    public static void log(String message) {
        CoreUtils.getPlugin().getLogger().log(Level.ALL, colorize(message));
    }


    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String prefixes(String key) {
        if (prefixes.get(key) == null)
            prefixes.put(key, colorize("&e&l" + key.toUpperCase().substring(0, 1) + key.toLowerCase().substring(1, key.length()) + " &7>&f "));
        return prefixes.get(key);
    }

    public static void prefixes(String key, String prefix) {
        prefixes.put(key, colorize(prefix));
    }

    public static String fade(String fromHex, String toHex, String string) {
        int[] start = getRGB(fromHex);
        int[] last = getRGB(toHex);

        StringBuilder sb = new StringBuilder();

        Integer dR = numberFade(start[0], last[0], string.length());
        Integer dG = numberFade(start[1], last[1], string.length());
        Integer dB = numberFade(start[2], last[2], string.length());

        for (int i = 0; i < string.length(); i++) {
            Color c = new Color(start[0] + dR * i, start[1] + dG * i, start[2] + dB * i);

            sb.append(net.md_5.bungee.api.ChatColor.of(c) + "" + string.charAt(i));
        }
        return sb.toString();
    }

    private static int[] getRGB(String rgb) {
        int[] ret = new int[3];
        for (int i = 0; i < 3; i++) {
            ret[i] = hexToInt(rgb.charAt(i * 2), rgb.charAt(i * 2 + 1));
        }
        return ret;
    }

    private static int hexToInt(char a, char b) {
        int x = a < 65 ? a - 48 : a - 55;
        int y = b < 65 ? b - 48 : b - 55;
        return x * 16 + y;
    }

    private static Integer numberFade(int i, int f, int n) {
        int d = (f - i) / (n - 1);
        return d;
    }

    public static String formatDate(long ms, String tcolor, String ncolor) {

        int l = (int) (ms / 1000);
        int sec = l % 60;
        int min = (l / 60) % 60;
        int hours = ((l / 60) / 60) % 24;
        int days = (((l / 60) / 60) / 24) % 7;
        int weeks = (((l / 60) / 60) / 24) / 7;

        if (weeks > 0) {
            return ncolor + weeks + tcolor + " weeks" + (days > 0 ? ", " + ncolor + days + tcolor + " days" : "") + (hours > 0 ? ", " + ncolor + hours + tcolor + " hours" : "") + (min > 0 ? ", " + ncolor + min + tcolor + " minutes" : "") + (sec > 0 ? ", and " + ncolor + sec + tcolor + " " + (sec == 1 ? "second" : "seconds") : "");
        }
        if (days > 0) {
            return ncolor + days + tcolor + " days" + (hours > 0 ? ", " + ncolor + hours + tcolor + " hours" : "") + (min > 0 ? ", " + ncolor + min + tcolor + " minutes" : "") + (sec > 0 ? ", and " + ncolor + sec + tcolor + " " + (sec == 1 ? "second" : "seconds") : "");
        }
        if (hours > 0) {
            return ncolor + hours + tcolor + " hours" + (min > 0 ? ", " + ncolor + min + tcolor + " minutes" : "") + (sec > 0 ? ", and " + ncolor + sec + tcolor + " " + (sec == 1 ? "second" : "seconds") : "");
        }
        if (min > 0) {
            return ncolor + min + tcolor + " minutes" + (sec > 0 ? ", and " + ncolor + sec + tcolor + " " + (sec == 1 ? "second" : "seconds") : "");
        }
        if (sec > 0) {
            return ncolor + sec + tcolor + " " + (sec == 1 ? "second" : "seconds");
        }

        return ncolor + "less than a second" + tcolor + "";
    }

    public static String formatDateRaw(long ms) {
        return formatDate(ms, "", "");
    }

    public static String formatTime(long ms, String ncolor, String tcolor) {
        int l = (int) (ms / 1000);

        int sec = l % 60;
        int min = (l / 60) % 60;
        int hours = ((l / 60) / 60) % 24;
        int days = (((l / 60) / 60) / 24) % 7;
        int weeks = (((l / 60) / 60) / 24) / 7;

        DecimalFormat format = new DecimalFormat("00");

        if (weeks > 0) {
            return ncolor + format.format(weeks) + tcolor + ":" + ncolor + format.format(days) + tcolor + ":" + ncolor + format.format(hours) + tcolor + ":" + ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor;

        }
        if (days > 0) {
            return ncolor + format.format(days) + tcolor + ":" + ncolor + format.format(hours) + tcolor + ":" + ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor;
        }
        if (hours > 0) {
            return ncolor + format.format(hours) + tcolor + ":" + ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor;
        }
        if (min > 0) {
            return ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor;
        }
        if (sec > 0) {
            return ncolor + "00" + tcolor + ":" + ncolor + format.format(sec) + tcolor;
        }

        return ncolor + "00" + tcolor + ":" + ncolor + "00" + tcolor;
    }

    public static String formatTimeRaw(long ms) {
        return formatTime(ms, "", "");
    }


    public static void sendPluginMessage(Player player, String channel, String... arguments) {
        if (arguments == null | arguments.length == 0) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String s : arguments) {
            out.writeUTF(s);
        }
        player.sendPluginMessage(CoreUtils.getPlugin(), channel, out.toByteArray());
    }
}
