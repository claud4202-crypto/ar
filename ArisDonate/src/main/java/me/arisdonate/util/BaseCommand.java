package me.arisdonate.util;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/** Базовый класс с удобными методами. */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final ArisDonatePlugin plugin;

    public BaseCommand(ArisDonatePlugin plugin) { this.plugin = plugin; }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            execute(sender, command, label, args);
        } catch (Exception e) {
            sender.sendMessage(Msg.parse("&cОшибка выполнения: " + e.getMessage()));
            plugin.getLogger().warning("Ошибка в /" + label + ": " + e);
            e.printStackTrace();
        }
        return true;
    }

    protected abstract void execute(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }

    protected Player requirePlayer(CommandSender s) {
        if (s instanceof Player p) return p;
        s.sendMessage(Msg.parse("&cЭта команда только для игроков."));
        return null;
    }

    protected boolean check(CommandSender s, String perm) {
        if (s.hasPermission(perm)) return true;
        s.sendMessage(Msg.parse("&cНет права &7" + perm));
        return false;
    }
}
