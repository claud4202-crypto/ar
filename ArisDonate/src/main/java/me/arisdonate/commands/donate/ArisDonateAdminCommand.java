package me.arisdonate.commands.donate;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArisDonateAdminCommand extends BaseCommand {

    public ArisDonateAdminCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.admin")) return;
        if (args.length == 0) {
            sender.sendMessage(Msg.parse("&7Использование: &e/arisdonate &7<give|set|remove|list|reload> <ник> [донат]"));
            return;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list":
                sender.sendMessage(Msg.parse("&6Доступные донаты:"));
                for (DonateRank r : plugin.getDonateManager().getOrderedRanks()) {
                    sender.sendMessage(Msg.parse(" &f• " + r.gradientName() + " &7(id: &f" + r.id() + "&7)"));
                }
                return;
            case "reload":
                plugin.reloadConfig();
                if (plugin.getPermissionService() != null) plugin.getPermissionService().refreshAll();
                sender.sendMessage(Msg.parse("&aКонфигурация перечитана."));
                return;
            case "remove": {
                if (args.length < 2) { sender.sendMessage(Msg.parse("&7Использование: &e/arisdonate remove <ник>")); return; }
                String nick = args[1];
                plugin.getDonateManager().setPlayerRank(nick, null);
                Player p = Players.online(nick);
                if (p != null) {
                    plugin.getChatFormatter().applyTabPrefix(p);
                    if (plugin.getPermissionService() != null) plugin.getPermissionService().apply(p);
                }
                sender.sendMessage(Msg.parse("&aДонат у &e" + nick + " &aудалён."));
                return;
            }
            case "give":
            case "set": {
                if (args.length < 3) { sender.sendMessage(Msg.parse("&7Использование: &e/arisdonate " + sub + " <ник> <донат>")); return; }
                String nick = args[1];
                String rankId = args[2];
                DonateRank rank = plugin.getDonateManager().getRank(rankId);
                if (rank == null) {
                    sender.sendMessage(Msg.parse("&cНеизвестный донат: &e" + rankId));
                    return;
                }
                plugin.getDonateManager().setPlayerRank(nick, rank.id());
                Player p = Players.online(nick);
                if (p != null) {
                    plugin.getChatFormatter().applyTabPrefix(p);
                    if (plugin.getPermissionService() != null) plugin.getPermissionService().apply(p);
                    p.sendMessage(Msg.parse("&aВам выдан донат " + rank.gradientName() + " &a!"));
                }
                sender.sendMessage(Msg.parse("&aДонат " + rank.gradientName() + " &aвыдан игроку &e" + nick));
                return;
            }
            default:
                sender.sendMessage(Msg.parse("&cНеизвестный подкоманд: &e" + sub));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("give", "set", "remove", "list", "reload");
        if (args.length == 2 && !args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("reload")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("set"))) {
            return new ArrayList<>(plugin.getDonateManager().getRanks().keySet());
        }
        return List.of();
    }
}
