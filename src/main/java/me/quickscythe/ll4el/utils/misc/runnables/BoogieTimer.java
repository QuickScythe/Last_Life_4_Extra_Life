package me.quickscythe.ll4el.utils.misc.runnables;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.BoogieManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

public class BoogieTimer implements Runnable {

    private final long started = new Date().getTime();
    private int stage = 0;
    private int amount;


    public BoogieTimer(int amount) {
        this.amount = amount;
    }

    @Override
    public void run() {
        if (stage == 5) BoogieManager.selectBoogies(amount);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (stage == 0) player.sendTitle("", MessageUtils.colorize("&c&lBoogie Selection in..."), 1, 20, 1);
            if (stage == 1) player.sendTitle("", MessageUtils.colorize("&c&l3..."), 1, 20, 1);
            if (stage == 2) player.sendTitle("", MessageUtils.colorize("&c&l2..."), 1, 20, 1);
            if (stage == 3) player.sendTitle("", MessageUtils.colorize("&c&l1..."), 1, 20, 1);
            if (stage == 4) player.sendTitle("", MessageUtils.colorize("&c&lYou are..."), 1, 20, 1);
            if (stage == 5)
                player.sendTitle("", MessageUtils.colorize(BoogieManager.getBoogieInfo(player).getBoolean("boogie") ? "&c&la Boogie!" : "&a&lNOT a Boogie!"), 1, 20, 1);
        }
        stage = stage + 1;
        if (stage <= 5) Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(), this, 22);
    }
}
