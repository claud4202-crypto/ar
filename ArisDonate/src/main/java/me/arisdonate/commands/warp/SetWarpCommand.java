package me.arisdonate.commands.warp;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends BaseCommand {
    public SetWarpCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.setwarp")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/setwarp <имя>")); return; }
        plugin.getWarpManager().setWarp(args[0], p.getLocation());
        p.sendMessage(Msg.parse("&aВарп &e" + args[0] + " &aустановлен."));
    }
}
