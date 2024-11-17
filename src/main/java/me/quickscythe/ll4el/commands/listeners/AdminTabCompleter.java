package me.quickscythe.ll4el.commands.listeners;

import java.util.ArrayList;
import java.util.List;

import me.quickscythe.ll4el.utils.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;


public class AdminTabCompleter implements TabCompleter {

    private List<String> invs = new ArrayList<>();
    private List<String> ll_sub_commands = new ArrayList<>();
    private List<String> life_sub_commands = new ArrayList<>();
    private List<String> boogie_sub_commands = new ArrayList<>();
    private List<String> loot_sub_commands = new ArrayList<>();

    public AdminTabCompleter() {
        for (String key : GuiManager.getGuis().keySet()) {
            invs.add(key);
        }
        ll_sub_commands.add("life");
        ll_sub_commands.add("loot");
        ll_sub_commands.add("boogie");
        life_sub_commands.add("add");
        life_sub_commands.add("set");
        boogie_sub_commands.add("roll");
        boogie_sub_commands.add("set");
        boogie_sub_commands.add("remove");
        loot_sub_commands.add("drop");
        loot_sub_commands.add("create");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("inventory")) {
            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], invs, completions);
            }
            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], getOnlinePlayers(), completions);
            }
        }
        if(cmd.getName().equalsIgnoreCase("lastlife")){
            if(args.length == 1){
                StringUtil.copyPartialMatches(args[0], ll_sub_commands, completions);
            }
            if(args.length >= 2){
                if(args[0].equalsIgnoreCase("life") && args.length == 2){
                    StringUtil.copyPartialMatches(args[1], life_sub_commands, completions);
                }
                if(args[0].equalsIgnoreCase("boogie") && args.length == 2){
                    StringUtil.copyPartialMatches(args[1], boogie_sub_commands, completions);
                }
                if(args[0].equalsIgnoreCase("loot") && args.length == 2){
                    StringUtil.copyPartialMatches(args[1], loot_sub_commands, completions);
                }
                if(args.length >=3 && !args[1].equalsIgnoreCase("roll") && !args[0].equalsIgnoreCase("loot")){
                    StringUtil.copyPartialMatches(args[2], getOnlinePlayers(), completions);
                }
                if(args[0].equalsIgnoreCase("loot")){
                    if(args.length >=3){
                        if(args.length == 3){

                        }
                    }
                }
            }
        }

        return completions;

    }

    public List<String> getOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

}