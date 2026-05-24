package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.TeleportManager;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAcceptCommand extends BaseCommand {
    public TpAcceptCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        TeleportManager.Request req = plugin.getTeleportManager().takeIncoming(p.getUniqueId());
        if (req == null) {
            p.sendMessage(Msg.parse("&cНет входящих запросов."));
            return;
        }
        Player from = Bukkit.getPlayer(req.requester);
        if (from == null) { p.sendMessage(Msg.parse("&cИгрок оффлайн.")); return; }
        if (req.type == TeleportManager.Type.TPA) {
            plugin.getBackManager().store(from);
            from.teleportAsync(p.getLocation());
            p.sendMessage(Msg.parse("&aЗапрос принят, " + from.getName() + " телепортирован."));
            from.sendMessage(Msg.parse("&aЗапрос принят, телепортирую..."));
        } else {
            plugin.getBackManager().store(p);
            p.teleportAsync(from.getLocation());
            p.sendMessage(Msg.parse("&aЗапрос принят, телепортирую к " + from.getName()));
            from.sendMessage(Msg.parse("&aЗапрос принят, " + p.getName() + " телепортирован к вам."));
        }
    }
}
