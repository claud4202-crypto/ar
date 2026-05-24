package me.arisdonate.commands.warp;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand extends BaseCommand {
    public DelWarpCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.delwarp")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/delwarp <имя>")); return; }
        boolean ok = plugin.getWarpManager().delWarp(args[0]);
        sender.sendMessage(Msg.parse(ok ? "&aВарп удалён." : "&cВарп не найден."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(plugin.getWarpManager().listWarps());
        return List.of();
    }
}
