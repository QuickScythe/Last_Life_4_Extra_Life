package me.quickscythe.ll4el.listeners;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.managers.BoogieManager;
import me.quickscythe.ll4el.utils.misc.managers.LifeManager;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    public PlayerListener(LastLife plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        PlayerManager.checkData(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (player.getKiller() != null && PlayerManager.isBoogie(player.getKiller()) && !player.equals(player.getKiller()))
            PlayerManager.removeBoogie(player.getKiller());
        if (PlayerManager.getLives(player) == 1) {
            player.setGameMode(GameMode.SPECTATOR);
            player.getWorld().strikeLightningEffect(player.getLocation());
            String msg = MessageUtils.getMessage("action.elimination",player.getName(),(player.hasMetadata("last_damager") ? " by " + ((Player) player.getMetadata("last_damager").get(0).value()).getName() : ""));
            Bukkit.broadcastMessage(msg);
        }
        PlayerManager.removeLife(player);

    }
}
