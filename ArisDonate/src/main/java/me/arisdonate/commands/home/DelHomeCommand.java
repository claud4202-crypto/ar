package me.arisdonate.commands.home;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand extends BaseCommand {
    public DelHomeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/delhome <имя>")); return; }
        boolean ok = plugin.getHomeManager().delHome(p.getUniqueId(), args[0]);
        p.sendMessage(Msg.parse(ok ? "&aДом &e" + args[0] + " &aудалён." : "&cДом не найден."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p && args.length == 1) {
            return new ArrayList<>(plugin.getHomeManager().listHomes(p.getUniqueId()));
        }
        return List.of();
    }
}
