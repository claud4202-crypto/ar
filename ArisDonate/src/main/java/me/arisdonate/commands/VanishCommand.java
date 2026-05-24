package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {
    public VanishCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.vanish")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        boolean v = !plugin.getVanishManager().isVanished(p.getUniqueId());
        plugin.getVanishManager().setVanished(p, v);
        p.sendMessage(Msg.parse(v ? "&7Вы стали невидимы." : "&aВы снова видимы."));
    }
}
