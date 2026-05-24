package me.arisdonate.commands.staff;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.models.StaffRank;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * /check <ник> — подробная информация об игроке для стаффа/донатеров.
 */
public class CheckCommand extends BaseCommand {

    public CheckCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.check")) return;
        if (args.length == 0) {
            sender.sendMessage(Msg.parse("&7Использование: &e/check <ник>"));
            return;
        }
        Player target = Players.online(args[0]);
        if (target == null) {
            sender.sendMessage(Msg.parse("&cИгрок &e" + args[0] + " &cне в сети."));
            return;
        }

        DonateRank donateRank = plugin.getDonateManager().getPlayerRank(target.getName());
        StaffRank staffRank = plugin.getStaffManager().getPlayerRank(target.getName());

        sender.sendMessage(Msg.parse("&8&m─────────────────────────────"));
        sender.sendMessage(Msg.parse("&6Информация об игроке: &f" + target.getName()));
        sender.sendMessage(Msg.parse("&8&m─────────────────────────────"));
        sender.sendMessage(Msg.parse("&7Здоровье: &f" + String.format("%.1f", target.getHealth()) + " &7/ &f" + String.format("%.1f", target.getMaxHealth())));
        sender.sendMessage(Msg.parse("&7Голод: &f" + target.getFoodLevel() + " &7/ &f20"));
        sender.sendMessage(Msg.parse("&7Уровень: &f" + target.getLevel()));
        sender.sendMessage(Msg.parse("&7Режим: &f" + formatGamemode(target.getGameMode())));
        sender.sendMessage(Msg.parse("&7Мир: &f" + target.getWorld().getName()));
        sender.sendMessage(Msg.parse("&7Координаты: &f" + target.getLocation().getBlockX()
                + ", " + target.getLocation().getBlockY()
                + ", " + target.getLocation().getBlockZ()));
        sender.sendMessage(Msg.parse("&7Пинг: &f" + target.getPing() + " мс"));
        sender.sendMessage(Msg.parse("&7OP: " + (target.isOp() ? "&aДа" : "&cНет")));
        sender.sendMessage(Msg.parse("&7Донат: " + (donateRank != null ? donateRank.gradientName() : "&8Нет")));
        sender.sendMessage(Msg.parse("&7Стаф: " + (staffRank != null ? staffRank.gradientName() : "&8Нет")));
        sender.sendMessage(Msg.parse("&7Полёт: " + (target.isFlying() ? "&aДа" : "&cНет")));

        if (plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage(Msg.parse("&7Мьют: &cДа"));
        }
        if (plugin.getFreezeManager().isFrozen(target.getUniqueId())) {
            sender.sendMessage(Msg.parse("&7Заморожен: &cДа"));
        }

        sender.sendMessage(Msg.parse("&8&m─────────────────────────────"));
    }

    private String formatGamemode(GameMode gm) {
        return switch (gm) {
            case SURVIVAL -> "Выживание";
            case CREATIVE -> "Креатив";
            case ADVENTURE -> "Приключение";
            case SPECTATOR -> "Наблюдатель";
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return Players.onlineNames(args[0]);
        return List.of();
    }
}
