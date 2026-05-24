package me.arischat.commands;

import me.arischat.ArisChatPlugin;
import me.arischat.managers.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IgnoreCommand implements CommandExecutor, TabCompleter {

    private final ArisChatPlugin plugin;

    public IgnoreCommand(ArisChatPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }
        if (args.length == 0) { p.sendMessage(Msg.parse("&7/ignore <ник>")); return true; }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { p.sendMessage(Msg.parse("&cИгрок не найден.")); return true; }
        if (target.equals(p)) { p.sendMessage(Msg.parse("&cНельзя игнорировать себя.")); return true; }

        boolean added = plugin.getChatManager().toggleIgnore(p, target.getUniqueId());
        if (added) p.sendMessage(Msg.parse("&aВы игнорируете &e" + target.getName()));
        else p.sendMessage(Msg.parse("&aВы больше не игнорируете &e" + target.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return List.of();
    }
}
