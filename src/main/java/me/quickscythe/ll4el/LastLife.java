package me.quickscythe.ll4el;

import me.quickscythe.ll4el.listeners.ChatListener;
import me.quickscythe.ll4el.listeners.PlayerListener;
import me.quickscythe.ll4el.utils.CoreUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class LastLife extends JavaPlugin {

    @Override
    public void onEnable() {
        CoreUtils.start(this);
        new ChatListener(this);
        new PlayerListener(this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {

        CoreUtils.end();
        // Plugin shutdown logic
    }
}
