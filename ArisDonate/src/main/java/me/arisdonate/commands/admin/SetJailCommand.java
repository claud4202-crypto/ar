package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetJailCommand extends BaseCommand {
    public SetJailCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.setjail")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        plugin.getJailManager().setJail(p.getLocation());
        p.sendMessage(Msg.parse("&aЛокация джейла установлена."));
    }
}
