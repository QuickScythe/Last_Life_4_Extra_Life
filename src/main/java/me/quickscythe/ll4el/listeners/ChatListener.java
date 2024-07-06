package me.quickscythe.ll4el.listeners;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.utils.CoreUtils;
import me.quickscythe.ll4el.utils.misc.BoogieManager;
import me.quickscythe.ll4el.utils.misc.LifeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    public ChatListener(LastLife plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        if(e.getMessage().startsWith("!test"))
            LifeManager.addLife(e.getPlayer());
        if(e.getMessage().startsWith("!run"))
            BoogieManager.rollBoogies(1,true);
    }
}
