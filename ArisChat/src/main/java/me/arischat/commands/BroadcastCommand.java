package me.arischat.commands;

import me.arischat.ArisChatPlugin;
import me.arischat.managers.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

    private final ArisChatPlugin plugin;

    public BroadcastCommand(ArisChatPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("arischat.broadcast")) { sender.sendMessage(Msg.parse("&cНет доступа.")); return true; }
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7/broadcast <сообщение>")); return true; }

        String msg = String.join(" ", args);
        String fmt = plugin.getConfig().getString("broadcast-format", "&c&l[!] &e{message}").replace("{message}", msg);
        Bukkit.broadcast(Msg.parse(fmt));
        return true;
    }
}
