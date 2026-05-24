package me.arischat.commands;

import me.arischat.ArisChatPlugin;
import me.arischat.managers.ChatManager;
import me.arischat.managers.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatModeCommand implements CommandExecutor, TabCompleter {

    private final ArisChatPlugin plugin;

    public ChatModeCommand(ArisChatPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }

        ChatManager cm = plugin.getChatManager();
        if (args.length == 0) {
            cm.toggleMode(p);
        } else {
            String mode = args[0].toLowerCase();
            if ("local".equals(mode) || "l".equals(mode)) cm.setMode(p, ChatManager.ChatMode.LOCAL);
            else if ("global".equals(mode) || "g".equals(mode)) cm.setMode(p, ChatManager.ChatMode.GLOBAL);
            else { p.sendMessage(Msg.parse("&7/chatmode [local|global]")); return true; }
        }

        ChatManager.ChatMode current = cm.getMode(p);
        p.sendMessage(Msg.parse("&aРежим чата: " + (current == ChatManager.ChatMode.LOCAL ? "&eЛокальный" : "&fГлобальный")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("local", "global");
        return List.of();
    }
}
