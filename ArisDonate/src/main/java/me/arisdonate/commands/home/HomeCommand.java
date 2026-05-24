package me.arisdonate.commands.home;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends BaseCommand {
    public HomeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        String name = args.length > 0 ? args[0] : "home";
        Location loc = plugin.getHomeManager().getHome(p.getUniqueId(), name);
        if (loc == null) {
            p.sendMessage(Msg.parse("&cДом &e" + name + " &cне найден."));
            return;
        }
        plugin.getBackManager().store(p);
        p.teleportAsync(loc);
        p.sendMessage(Msg.parse("&aТелепорт к дому &e" + name));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p && args.length == 1) {
            return new ArrayList<>(plugin.getHomeManager().listHomes(p.getUniqueId()));
        }
        return List.of();
    }
}
