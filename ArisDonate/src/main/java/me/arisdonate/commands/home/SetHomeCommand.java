package me.arisdonate.commands.home;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends BaseCommand {
    public SetHomeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        String name = args.length > 0 ? args[0] : "home";
        boolean ok = plugin.getHomeManager().setHome(p, name);
        if (!ok) {
            int limit = plugin.getDonateManager().getPlayerRank(p.getName()) != null
                    ? plugin.getDonateManager().getPlayerRank(p.getName()).homeLimit()
                    : plugin.getConfig().getInt("default-home-limit", 1);
            p.sendMessage(Msg.parse("&cДостигнут лимит /sethome: &e" + limit + "&c. Поднимите свой донат, чтобы получить больше регионов."));
            return;
        }
        p.sendMessage(Msg.parse("&aДом &e" + name + " &aустановлен."));
    }
}
