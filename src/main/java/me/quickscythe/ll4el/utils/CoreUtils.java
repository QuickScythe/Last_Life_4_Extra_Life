package me.quickscythe.ll4el.utils;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.utils.chat.ChatManager;
import me.quickscythe.ll4el.utils.chat.DebugUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.ll4el.utils.gui.GuiManager;
import me.quickscythe.ll4el.utils.misc.PageResult;
import me.quickscythe.ll4el.utils.misc.managers.*;
import me.quickscythe.ll4el.utils.misc.managers.loot.LootManager;
import me.quickscythe.ll4el.utils.misc.runnables.HeartbeatRunnable;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class CoreUtils {


    private static LastLife plugin;


    public static void start(LastLife main) {
        Bukkit.broadcastMessage("0-1");
        plugin = main;
        Bukkit.broadcastMessage("0-2");
        plugin.saveConfig();
        Bukkit.broadcastMessage("0-3");
        GuiManager.init();
        Bukkit.broadcastMessage("0-4");
        LifeManager.start();
        Bukkit.broadcastMessage("0-5");
        PlaceholderUtils.registerPlaceholders();
        Bukkit.broadcastMessage("0-6");
        MessageUtils.start();
        Bukkit.broadcastMessage("0-7");
        PlayerManager.start();
        Bukkit.broadcastMessage("0-8");
        LootManager.start();
        Bukkit.broadcastMessage("0-9");
        PartyManager.start();
        Bukkit.broadcastMessage("0-10");
        ChatManager.start();
        Bukkit.broadcastMessage("0-11");

        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(), new HeartbeatRunnable(), 0);


    }

    public static LastLife getPlugin() {
        return plugin;
    }


    public static void end() {
        PlayerManager.end();
        LootManager.end();
        PartyManager.end();
        saveConfig();
    }

    public static void saveConfig() {

        getPlugin().saveConfig();

    }

    public static List<String> colorizeStringList(List<String> stringList) {
        return colorizeStringList((String[]) stringList.toArray());
    }

    public static List<String> colorizeStringList(String[] stringList) {
        List<String> ret = new ArrayList<>();
        for (String s : stringList) {
            ret.add(MessageUtils.colorize(s));
        }
        return ret;
    }

    public static void playTotemAnimation(Player player, int customModelData) {
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = totem.getItemMeta();
        if (meta == null) return;
        meta.setCustomModelData(customModelData);
        totem.setItemMeta(meta);
        ItemStack hand = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(totem);
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        player.getInventory().setItemInMainHand(hand);
    }


    public static String encryptLocation(Location loc) {
        String r = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getPitch() + ":" + loc.getYaw();
        r = r.replaceAll("\\.", ",");
        r = "location:" + r;
        return r;
    }

    public static Location decryptLocation(String s) {
        debug("Decrypting Location: " + s);
        if (s.startsWith("location:")) s = s.replaceAll("location:", "");

        if (s.contains(",")) s = s.replaceAll(",", ".");
        String[] args = s.split(":");
        Location r = new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        if (args.length >= 5) {
            r.setPitch(Float.parseFloat(args[4]));
            r.setYaw(Float.parseFloat(args[5]));
        }
        return r;
    }


    public static Color generateColor(double seed, double frequency) {
        return generateColor(seed, frequency, 100);
    }

    public static Color generateColor(double seed, double frequency, int amp) {

        if (amp > 127) amp = 127;
        int peak = 255 - amp;
        int red = (int) (Math.sin(frequency * (seed) + 0) * amp + peak);
        int green = (int) (Math.sin(frequency * (seed) + 2 * Math.PI / 3) * amp + peak);
        int blue = (int) (Math.sin(frequency * (seed) + 4 * Math.PI / 3) * amp + peak);
        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;

        return new Color(red, green, blue);
    }


    public static void debug(Object obj) {
        debug(obj + "");
    }

    public static void debug(String message) {

        DebugUtils.debug(message);

    }

    public static PageResult pagify(List<Object> things, int items) {
        return pagify(things, items, 1);
    }

    public static PageResult pagify(List<Object> things, int items, int page) {

        List<Object> rtn = new ArrayList<>();
        int totalposts = 0;
        int pagetracker = 0;

        for (Object s : things) {
            // REFERENCE for (int i = (page - 1) * pageResult; i < page * pageResult; i++)
            totalposts = totalposts + 1;
            pagetracker = pagetracker + 1;
            if (pagetracker < page * items && pagetracker > ((page - 1) * items) - 1) {
                rtn.add(s);
            }

        }
        int pages = ((int) (totalposts / items));
//		if (pages > 1) {
//			if (page == 1) {
//				// can't go back
//			} else {
//				// can go back
//			}
//			// page / pages
//			if (page == pages) {
//				// can't go forward
//			} else {
//				// can go forward
//			}
//
//		}

        return new PageResult(rtn, page, pages);

    }

    public static String formatMessage(String message, String... values) {
        int i = 0;
        String string = message;
        while (string.contains("@")) {
            string = string.replaceFirst("@", values[i]);
            i = i + 1;
        }
        return string;
    }


    public static List<String> getPageResults(List<String> rules, int page, int pageResult) {
        List<String> rturn = new ArrayList<String>();
        for (int i = (page - 1) * pageResult; i < page * pageResult; i++) {
            if (i < rules.size()) rturn.add(rules.get(i));
        }
        return rturn;
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

    public static String formatDateTime(long ms, String ncolor, String tcolor) {

        int sec60 = (int) (ms % 1000) / 10;

        int l = (int) (ms / 1000);

        int sec = l % 60;
        int min = (l / 60) % 60;
        int hours = ((l / 60) / 60) % 24;
        int days = (((l / 60) / 60) / 24) % 7;
        int weeks = (((l / 60) / 60) / 24) / 7;

        DecimalFormat format = new DecimalFormat("00");

        if (weeks > 0) {
            return ncolor + format.format(weeks) + tcolor + ":" + ncolor + format.format(days) + tcolor + ":" + ncolor + format.format(hours) + tcolor + ":" + ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor + ":" + ncolor + format.format(sec60) + tcolor;

        }
        if (days > 0) {
            return ncolor + format.format(days) + tcolor + ":" + ncolor + format.format(hours) + tcolor + ":" + ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor + ":" + ncolor + format.format(sec60) + tcolor;
        }
        if (hours > 0) {
            return ncolor + format.format(hours) + tcolor + ":" + ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor + ":" + ncolor + format.format(sec60) + tcolor;
        }
        if (min > 0) {
            return ncolor + format.format(min) + tcolor + ":" + ncolor + format.format(sec) + tcolor + ":" + ncolor + format.format(sec60) + tcolor;
        }
        if (sec > 0) {
            return ncolor + format.format(sec) + tcolor + ":" + ncolor + format.format(sec60) + tcolor;
        }
        if (sec60 > 0) {
            return ncolor + "00" + tcolor + ":" + ncolor + format.format(sec60) + tcolor;
        }

        return ncolor + "less than a millisecond" + tcolor + "";
    }

    public static String formatDateTimeRaw(long ms) {
        return formatDateTime(ms, "", "");
    }

    public static String getSimpleTimeFormat(long ms) {
        return formatDate(ms, "&c", "&4");
    }


    public static boolean consumeItem(Player player, int count, Material mat) {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

        int found = 0;
        for (ItemStack stack : ammo.values())
            found += stack.getAmount();
        if (count > found) return false;

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed) player.getInventory().setItem(index, null);
            else stack.setAmount(stack.getAmount() - removed);

            if (count <= 0) break;
        }

        player.updateInventory();
        return true;
    }


    public static boolean downloadFile(String url, String filename, String... auth) {

        boolean success = true;
        InputStream in = null;
        FileOutputStream out = null;

        try {

            URL myUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            conn.setDoOutput(true);
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestMethod("GET");

            if (auth != null && auth.length >= 2) {
                String userCredentials = auth[0].trim() + ":" + auth[1].trim();
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
                conn.setRequestProperty("Authorization", basicAuth);
            }
            in = conn.getInputStream();
            out = new FileOutputStream(filename);
            int c;
            byte[] b = new byte[1024];
            while ((c = in.read(b)) != -1) out.write(b, 0, c);

        } catch (Exception ex) {
            MessageUtils.log(("There was an error downloading " + filename + ". Check console for details."));
            ex.printStackTrace();
            success = false;
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                MessageUtils.log(("There was an error downloading " + filename + ". Check console for details."));
                e.printStackTrace();
            }
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                MessageUtils.log(("There was an error downloading " + filename + ". Check console for details."));
                e.printStackTrace();
            }
        }
        return success;
    }

}
