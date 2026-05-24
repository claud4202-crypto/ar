package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class NearCommand extends BaseCommand {
    public NearCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        double radius = args.length > 0 ? safeDouble(args[0], 50) : 50;
        String list = p.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e instanceof Player && !e.equals(p))
                .map(e -> e.getName() + " (" + (int) p.getLocation().distance(e.getLocation()) + "м)")
                .collect(Collectors.joining(", "));
        p.sendMessage(Msg.parse("&6Рядом (&e" + (int) radius + "&6): &7" + (list.isEmpty() ? "никого" : list)));
    }

    private double safeDouble(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
}
