package me.quickscythe.ll4el.listeners;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.utils.chat.ChatManager;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.ll4el.utils.misc.managers.BoogieManager;
import me.quickscythe.ll4el.utils.misc.managers.LifeManager;
import me.quickscythe.ll4el.utils.misc.managers.PartyManager;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    public ChatListener(LastLife plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){

        if(PartyManager.inPartyChat(e.getPlayer())){
            PartyManager.handleChat(e.getPlayer(), e.getMessage());
            e.setCancelled(true);
            return;
        }
        e.setFormat(MessageUtils.colorize(PlaceholderUtils.replace(e.getPlayer(), ChatManager.getFormat("player"))) + MessageUtils.colorize(ChatManager.getFormat("chat")).replaceAll("%message%", e.getMessage()));

    }
}
