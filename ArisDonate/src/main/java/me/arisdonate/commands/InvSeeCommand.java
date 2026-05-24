package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvSeeCommand extends BaseCommand {
    public InvSeeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.invsee")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/invsee <ник>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { p.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        p.openInventory(t.getInventory());
    }
}
