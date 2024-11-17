package me.quickscythe.ll4el.utils.misc.runnables;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.managers.BoogieManager;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
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

            if (stage == 0) player.sendTitle("", MessageUtils.getMessage("message.boogie.countdown.4"), 1, 20, 1);
            if (stage == 1) player.sendTitle("", MessageUtils.getMessage("message.boogie.countdown.3"), 1, 20, 1);
            if (stage == 2) player.sendTitle("", MessageUtils.getMessage("message.boogie.countdown.2"), 1, 20, 1);
            if (stage == 3) player.sendTitle("", MessageUtils.getMessage("message.boogie.countdown.1"), 1, 20, 1);
            if (stage == 4) player.sendTitle("", MessageUtils.getMessage("message.boogie.countdown.0"), 1, 20, 1);
            if (stage == 5)
                player.sendTitle("", PlayerManager.getPlayerData(player).getBoolean("boogie") ? MessageUtils.getMessage("message.boogie.countdown.boogie") : MessageUtils.getMessage("message.boogie.countdown.not"), 1, 20, 1);
        }
        stage = stage + 1;
        if (stage <= 5) Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(), this, 22);
    }
}
