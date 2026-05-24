package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JailCommand extends BaseCommand {
    public JailCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.jail")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/jail <ник>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        if (plugin.getJailManager().getJail() == null) { sender.sendMessage(Msg.parse("&cЛокация джейла не задана (&e/setjail&c).")); return; }
        plugin.getJailManager().jail(t.getUniqueId());
        plugin.getBackManager().store(t);
        t.teleportAsync(plugin.getJailManager().getJail());
        t.sendMessage(Msg.parse("&cВы отправлены в джейл."));
        sender.sendMessage(Msg.parse("&aДжейл: &e" + t.getName()));
    }
}
