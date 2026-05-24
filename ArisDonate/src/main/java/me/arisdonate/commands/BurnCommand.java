package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BurnCommand extends BaseCommand {
    public BurnCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.burn")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/burn <ник> [секунд]")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        int sec = args.length > 1 ? safe(args[1], 5) : 5;
        t.setFireTicks(sec * 20);
        sender.sendMessage(Msg.parse("&aПодожжён &e" + t.getName() + " &aна " + sec + "с."));
    }
    private int safe(String s, int d) { try { return Integer.parseInt(s); } catch (Exception e) { return d; } }
}
