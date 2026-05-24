package me.arisdonate.commands.home;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameHomeCommand extends BaseCommand {
    public RenameHomeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length < 2) { p.sendMessage(Msg.parse("&7Использование: &e/renamehome <старое> <новое>")); return; }
        boolean ok = plugin.getHomeManager().renameHome(p.getUniqueId(), args[0], args[1]);
        p.sendMessage(Msg.parse(ok ? "&aДом переименован: &e" + args[0] + " &7→ &e" + args[1]
                : "&cДом &e" + args[0] + " &cне найден."));
    }
}
