package me.quickscythe.ll4el.listeners;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.misc.BoogieManager;
import me.quickscythe.ll4el.utils.misc.LifeManager;
import org.bukkit.Bukkit;
import org.bukkit.ExplosionResult;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerListener implements Listener {
    public PlayerListener(LastLife plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (player.getHealth() - e.getFinalDamage() <= 0) {
                if (!player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING) && !player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
                    if (LifeManager.getLives(player) == 1) {
                        e.setCancelled(true);
                        player.setGameMode(GameMode.SPECTATOR);
                        player.getWorld().strikeLightningEffect(player.getLocation());
                        Bukkit.broadcastMessage(player.getName() + MessageUtils.colorize("&f has been eliminated" + (player.hasMetadata("last_damager") ? " by " + ((Player) player.getMetadata("last_damager").get(0).value()).getName() : "") + "!"));
                        for (ItemStack item : player.getInventory().getContents())
                            if (item != null) player.getWorld().dropItem(player.getLocation(), item);
                        player.getInventory().clear();
                    }
                    LifeManager.removeLife(player);
                    if(player.hasMetadata("last_damager")){
                        Player killer = (Player) player.getMetadata("last_damager").get(0).value();
                        if(BoogieManager.isBoogie(killer))
                            BoogieManager.removeBoogie(killer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if(damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Player)
            damager = (Player) ((Projectile) damager).getShooter();
        if (e.getEntity() instanceof Player player && damager instanceof Player) {

            player.setMetadata("last_damager", new FixedMetadataValue(CoreUtils.getPlugin(), damager));
            Entity finalDamager = damager;
            Bukkit.getScheduler().runTaskLater(CoreUtils.getPlugin(), () -> {
                if (player.getMetadata("last_damager").get(0).value().equals(finalDamager)) {
                    player.removeMetadata("last_damager", CoreUtils.getPlugin());
                }
            }, 20 * 5);
        }

    }
}
