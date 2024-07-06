package me.quickscythe.ll4el.utils.misc.runnables;

import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.misc.BoogieManager;
import me.quickscythe.ll4el.utils.misc.LifeManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BoogieParticles implements Runnable {
    protected Particle.DustOptions dustoptions = new Particle.DustOptions(Color.RED, 1);

    public BoogieParticles(){

    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
             if(BoogieManager.isBoogie(player)){
                 player.spawnParticle(Particle.DUST,player.getLocation(),1,dustoptions);
                 if(LifeManager.getLives(player)<1)
                     BoogieManager.removeBoogie(player);
             }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreUtils.getPlugin(),this,0);
    }
}
