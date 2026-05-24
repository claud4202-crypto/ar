package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand extends BaseCommand {
    public SocialSpyCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.socialspy")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        plugin.getMessageManager().toggleSocialSpy(p.getUniqueId());
        boolean on = plugin.getMessageManager().inSocialSpy(p.getUniqueId());
        p.sendMessage(Msg.parse(on ? "&aSocialSpy включён." : "&7SocialSpy выключен."));
    }
}
