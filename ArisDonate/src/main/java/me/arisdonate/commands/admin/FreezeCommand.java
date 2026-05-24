package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends BaseCommand {
    public FreezeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.freeze")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/freeze <ник>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        boolean on = plugin.getFreezeManager().toggle(t);
        t.sendMessage(Msg.parse(on ? "&cВы заморожены." : "&aВы разморожены."));
        sender.sendMessage(Msg.parse("&a" + t.getName() + ": frozen=" + on));
    }
}
