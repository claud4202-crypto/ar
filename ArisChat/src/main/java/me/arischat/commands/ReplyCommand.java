package me.arischat.commands;

import me.arischat.ArisChatPlugin;
import me.arischat.managers.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final ArisChatPlugin plugin;

    public ReplyCommand(ArisChatPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }
        if (args.length == 0) { p.sendMessage(Msg.parse("&7/reply <сообщение>")); return true; }

        UUID targetId = plugin.getChatManager().getReplyTarget(p);
        if (targetId == null) { p.sendMessage(Msg.parse("&cНекому отвечать.")); return true; }
        Player target = Bukkit.getPlayer(targetId);
        if (target == null || !target.isOnline()) { p.sendMessage(Msg.parse("&cИгрок не в сети.")); return true; }

        String msg = String.join(" ", args);
        // Delegate to /msg
        p.performCommand("msg " + target.getName() + " " + msg);
        return true;
    }
}
