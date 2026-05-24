package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand extends BaseCommand {
    public NickCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.nick")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/nick <ник>")); return; }
        String nick = String.join(" ", args);
        p.displayName(Msg.parse(nick));
        p.playerListName(Msg.parse(nick));
        p.sendMessage(Msg.parse("&aНик: " + nick));
    }
}
