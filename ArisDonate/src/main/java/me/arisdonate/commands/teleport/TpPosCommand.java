package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpPosCommand extends BaseCommand {
    public TpPosCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.tppos")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length < 3) { p.sendMessage(Msg.parse("&7Использование: &e/tppos <x> <y> <z>")); return; }
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            plugin.getBackManager().store(p);
            p.teleportAsync(new Location(p.getWorld(), x, y, z));
            p.sendMessage(Msg.parse("&aТелепорт."));
        } catch (NumberFormatException e) {
            p.sendMessage(Msg.parse("&cНеверные координаты."));
        }
    }
}
