package me.quickscythe.ll4el;

import me.quickscythe.ll4el.commands.AdminCommands;
import me.quickscythe.ll4el.commands.PlayerCommands;
import me.quickscythe.ll4el.listeners.ChatListener;
import me.quickscythe.ll4el.listeners.GuiListener;
import me.quickscythe.ll4el.listeners.PlayerListener;
import me.quickscythe.ll4el.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LastLife extends JavaPlugin {

    @Override
    public void onEnable() {

        CoreUtils.start(this);
        new ChatListener(this);
        new PlayerListener(this);
        new GuiListener(this);

        new PlayerCommands(this,"settings", "party");
        new AdminCommands(this, "inventory", "lastlife", "status");

    }

    @Override
    public void onDisable() {

        CoreUtils.end();
        // Plugin shutdown logic
    }
}
