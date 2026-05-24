package me.arisdonate.commands.staff;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.StaffRank;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArisStaffAdminCommand extends BaseCommand {

    public ArisStaffAdminCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.admin")) return;
        if (args.length == 0) {
            sender.sendMessage(Msg.parse("&7Использование: &e/arisstaff &7<set|remove|list|reload> <ник> [ранг]"));
            return;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list" -> {
                sender.sendMessage(Msg.parse("&6Стаф-ранги (от высокого к низкому):"));
                List<StaffRank> sorted = new ArrayList<>(plugin.getStaffManager().ranks().values());
                sorted.sort(Comparator.comparingInt(StaffRank::weight).reversed());
                for (StaffRank r : sorted) {
                    sender.sendMessage(Msg.parse(" &f• " + r.gradientName() + " &7(id: &f" + r.id() + "&7)"));
                }
            }
            case "reload" -> {
                plugin.reloadConfig();
                plugin.getStaffManager().reload();
                sender.sendMessage(Msg.parse("&aСтаф-ранги перечитаны."));
            }
            case "remove" -> {
                if (args.length < 2) { sender.sendMessage(Msg.parse("&7Использование: &e/arisstaff remove <ник>")); return; }
                String nick = args[1];
                plugin.getStaffManager().clearPlayer(nick);
                Player p = Players.online(nick);
                if (p != null) {
                    plugin.getChatFormatter().applyTabPrefix(p);
                    plugin.getPermissionService().apply(p);
                }
                sender.sendMessage(Msg.parse("&aСтаф-ранг у &e" + nick + " &aснят."));
            }
            case "set", "give" -> {
                if (args.length < 3) { sender.sendMessage(Msg.parse("&7Использование: &e/arisstaff " + sub + " <ник> <ранг>")); return; }
                String nick = args[1];
                String rankId = args[2];
                StaffRank rank = plugin.getStaffManager().getRank(rankId);
                if (rank == null) { sender.sendMessage(Msg.parse("&cНеизвестный ранг: &e" + rankId)); return; }
                plugin.getStaffManager().setPlayer(nick, rank.id());
                Player p = Players.online(nick);
                if (p != null) {
                    plugin.getChatFormatter().applyTabPrefix(p);
                    plugin.getPermissionService().apply(p);
                    p.sendMessage(Msg.parse("&aВам выдан ранг " + rank.gradientName() + " &a!"));
                }
                sender.sendMessage(Msg.parse("&aРанг " + rank.gradientName() + " &aвыдан &e" + nick));
            }
            default -> sender.sendMessage(Msg.parse("&cНеизвестная подкоманда: &e" + sub));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("set", "give", "remove", "list", "reload");
        if (args.length == 2 && !args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("reload")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give"))) {
            return new ArrayList<>(plugin.getStaffManager().ranks().keySet());
        }
        return List.of();
    }
}
