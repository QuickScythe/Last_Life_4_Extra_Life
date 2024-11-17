package me.quickscythe.ll4el.commands;

import me.quickscythe.ll4el.LastLife;
import me.quickscythe.ll4el.utils.chat.MessageUtils;
import me.quickscythe.ll4el.utils.gui.GuiManager;
import me.quickscythe.ll4el.utils.misc.managers.PartyManager;
import me.quickscythe.ll4el.utils.misc.managers.PlayerManager;
import me.quickscythe.ll4el.utils.misc.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements CommandExecutor {
    public PlayerCommands(LastLife plugin, String... cmds) {
        for (String cmd : cmds)
            plugin.getCommand(cmd).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("party")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(MessageUtils.colorize("&a/" + label + " [help] &7- Displays this list."));
                if (sender.hasPermission("lastlife.party.create"))
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " create <party> &7- Creates a party."));
                if (sender.hasPermission("lastlife.party.join"))
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " join <party> " + (sender.hasPermission("lastlife.party.join.other") ? "[player] " : "") + "&7- " + (sender.hasPermission("lastlife.party.join.other") ? "Joins you or another player to a party" : "Joins you to a party") + "."));
                if (sender.hasPermission("lastlife.party.leave"))
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " leave " + (sender.hasPermission("lastlife.party.leave.other") ? "[player] " : "") + "&7- " + (sender.hasPermission("lastlife.party.leave.other") ? "Pull you or another player out of a party" : "Pulls out out of your party") + "."));
                if (sender.hasPermission("lastlife.party.chat"))
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " chat [message] &7- Either sends a single chat to the party, or toggles party chat mode."));
                return true;
            }
            if (args[0].equalsIgnoreCase("chat")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.player_only"));
                    return true;
                }
                if (!sender.hasPermission("lastlife.party.chat")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }
                if (PlayerManager.getParty(((Player) sender)).equalsIgnoreCase("none")) {
                    sender.sendMessage(MessageUtils.getMessage("party.chat.no_party"));
                    return true;
                }
                if (args.length == 1) {
                    PartyManager.toggleChat(((Player) sender));
                    return true;
                }
                StringBuilder s = new StringBuilder(args[1]);
                for (int i = 2; i != args.length; i++)
                    s.append(" ").append(args[i]);
                PartyManager.handleChat(((Player) sender), s.toString());
                return true;
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.player_only"));
                    return true;
                }
                if (!sender.hasPermission("lastlife.party.create")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }
                if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " create <party> &7- Creates a party."));
                    return true;
                }
                PartyManager.createParty(args[1]);
                sender.sendMessage(MessageUtils.getMessage("cmd.party.create", args[1]));
                return true;
            }
            if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.player_only"));
                    return true;
                }
                if (!sender.hasPermission("lastlife.party.join")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }

                if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    sender.sendMessage(MessageUtils.colorize("&a/" + label + " join <party> " + (sender.hasPermission("lastlife.party.join.other") ? "[player] " : "") + "&7- " + (sender.hasPermission("lastlife.party.join.other") ? "Joins you or another player to a party" : "Joins you to a party") + "."));
                    return true;
                }
                if (args.length == 3) if (Bukkit.getPlayer(args[2]) == null && PlayerManager.getUUID(args[2]) == null) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_player"));
                    return true;
                }
                OfflinePlayer target = args.length == 3 ? (Bukkit.getPlayer(args[2]) == null ? Bukkit.getOfflinePlayer(PlayerManager.getUUID(args[2])) : Bukkit.getPlayer(args[2])) : player;

                if (PartyManager.getParty(args[1]) == null) {
                    sender.sendMessage(MessageUtils.getMessage("error.party.no_party"));
                    return true;
                }
                PlayerManager.setParty(target, args[1]);
                if (!player.getUniqueId().equals(target.getUniqueId()))
                    sender.sendMessage(MessageUtils.getMessage("cmd.party.join.other", player.getName(), args[1]));
                return true;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                if (!sender.hasPermission("lastlife.party.leave")) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_perm"));
                    return true;
                }
                if (args.length == 2) if (Bukkit.getPlayer(args[1]) == null && PlayerManager.getUUID(args[1]) == null) {
                    sender.sendMessage(MessageUtils.getMessage("cmd.error.no_player"));
                    return true;
                }
                OfflinePlayer target = args.length == 2 ? (Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(PlayerManager.getUUID(args[1])) : Bukkit.getPlayer(args[1])) : (Player) sender;
                PlayerManager.setParty(target, "none");
                return true;
            }

        }
        if (cmd.getName().equalsIgnoreCase("settings")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtils.getMessage("cmd.error.player_only"));
                return true;
            }
            if (args.length == 0) {
                GuiManager.openGui(player, GuiManager.getGui("settings"));
                return true;
            }
            if (args[0].equalsIgnoreCase("particle")) {
                SettingsManager.Settings set = SettingsManager.getSettings(player);
                set.particles(args.length == 2 ? Boolean.parseBoolean(args[1]) : !set.particles());
                SettingsManager.setSettings(player, set);
                return true;
            }
            if (args[0].equalsIgnoreCase("icon")) {
                SettingsManager.Settings set = SettingsManager.getSettings(player);
                set.icon(args.length == 2 ? Boolean.parseBoolean(args[1]) : !set.icon());
                SettingsManager.setSettings(player, set);
                return true;
            }
            if (args[0].equalsIgnoreCase("chat")) {
                SettingsManager.Settings set = SettingsManager.getSettings(player);
                set.chat(args.length == 2 ? Boolean.parseBoolean(args[1]) : !set.chat());
                SettingsManager.setSettings(player, set);
                return true;
            }


//            GuiManager.openGui(((Player)sender), );
        }
        sender.sendMessage(MessageUtils.getMessage("cmd.error.no_command", "/" + label + " " + args[0]));
        return true;
    }
}
