package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllCommand extends BaseCommand {
    public TpAllCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.tpall")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        int count = 0;
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(p)) continue;
            plugin.getBackManager().store(other);
            other.teleportAsync(p.getLocation());
            count++;
        }
        sender.sendMessage(Msg.parse("&aТелепорт всех игроков (&e" + count + "&a) к вам."));
    }
}
