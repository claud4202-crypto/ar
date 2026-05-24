package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.TeleportManager;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand extends BaseCommand {
    public TpDenyCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        TeleportManager.Request req = plugin.getTeleportManager().takeIncoming(p.getUniqueId());
        if (req == null) { p.sendMessage(Msg.parse("&cНет входящих запросов.")); return; }
        Player from = Bukkit.getPlayer(req.requester);
        if (from != null) from.sendMessage(Msg.parse("&cЗапрос отклонён игроком &e" + p.getName()));
        p.sendMessage(Msg.parse("&aЗапрос отклонён."));
    }
}
