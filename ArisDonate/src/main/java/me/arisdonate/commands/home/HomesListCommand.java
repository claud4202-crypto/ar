package me.arisdonate.commands.home;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class HomesListCommand extends BaseCommand {
    public HomesListCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        Set<String> list = plugin.getHomeManager().listHomes(p.getUniqueId());
        if (list.isEmpty()) {
            p.sendMessage(Msg.parse("&7У вас нет домов. Создайте: &e/sethome <имя>"));
            return;
        }
        p.sendMessage(Msg.parse("&6Ваши дома (&f" + list.size() + "&6):"));
        p.sendMessage(Msg.parse("&7" + list.stream().collect(Collectors.joining(", "))));
    }
}
