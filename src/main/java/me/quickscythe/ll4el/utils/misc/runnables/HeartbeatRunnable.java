package me.quickscythe.ll4el.utils.misc.runnables;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
import me.quickscythe.ll4el.utils.misc.managers.SettingsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class HeartbeatRunnable implements Runnable {
    protected Particle.DustOptions dustoptions = new Particle.DustOptions(Color.RED, 1);

    public HeartbeatRunnable() {

    }

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.isBoogie(player)) {
                if (SettingsManager.getSettings(player).particles())
                    player.spawnParticle(Particle.DUST, player.getLocation(), 1, dustoptions);
                if (SettingsManager.getSettings(player).icon())
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("                                                                                          \ue001").create());
                if (PlayerManager.getLives(player) < 1) PlayerManager.removeBoogie(player);
            }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(), this, 0);
    }
}
