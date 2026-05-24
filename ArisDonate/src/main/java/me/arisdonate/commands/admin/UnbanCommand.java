package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnbanCommand extends BaseCommand {
    public UnbanCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.ban")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/unban <ник>")); return; }
        Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
        sender.sendMessage(Msg.parse("&aРазбан: &e" + args[0]));
    }
}
