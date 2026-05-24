package me.arischat.commands;

import me.arischat.ArisChatPlugin;
import me.arischat.managers.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpyCommand implements CommandExecutor {

    private final ArisChatPlugin plugin;

    public SpyCommand(ArisChatPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }
        if (!p.hasPermission("arischat.spy")) { p.sendMessage(Msg.parse("&cНет доступа.")); return true; }

        boolean on = plugin.getChatManager().toggleSpy(p);
        p.sendMessage(Msg.parse(on ? "&aСлежка за ЛС: &eВКЛ" : "&aСлежка за ЛС: &cВЫКЛ"));
        return true;
    }
}
