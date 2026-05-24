package me.regionblocks.commands;

import me.regionblocks.RegionBlocks;
import me.regionblocks.managers.ArisManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * /a give <ник> <кол>
 * /a set  <ник> <кол>
 * /a reset <ник>
 * /a giveall <кол>
 */
public class AdminArisCommand implements CommandExecutor {

    private final RegionBlocks plugin;

    public AdminArisCommand(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("regionblocks.admin")) {
            sender.sendMessage(Component.text("✗ Нет прав.").color(TextColor.color(0xFF4444)));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        ArisManager am = plugin.getArisManager();
        String sub = args[0].toLowerCase();

        switch (sub) {

            // /a giveall <кол>
            case "giveall" -> {
                if (args.length < 2) { sendUsage(sender); return true; }
                long amount = parseLong(sender, args[1]);
                if (amount < 0) return true;

                int count = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    am.give(p.getName(), amount);
                    // Уведомить игрока
                    p.sendMessage(buildGiveMsg(amount, am.getBalance(p.getName())));
                    count++;
                }
                sender.sendMessage(
                    Component.text("✓ Начислено ").color(TextColor.color(0x55FF55))
                    .append(gold(format(amount) + " ✦"))
                    .append(Component.text(" всем онлайн-игрокам (" + count + " чел.)").color(TextColor.color(0x55FF55)))
                );
            }

            // /a give <ник> <кол>
            case "give" -> {
                if (args.length < 3) { sendUsage(sender); return true; }
                long amount = parseLong(sender, args[2]);
                if (amount < 0) return true;
                String nick = args[1];
                am.give(nick, amount);
                notifyPlayer(nick, buildGiveMsg(amount, am.getBalance(nick)));
                sender.sendMessage(
                    Component.text("✓ Игроку ").color(TextColor.color(0x55FF55))
                    .append(white(nick))
                    .append(Component.text(" начислено ").color(TextColor.color(0x55FF55)))
                    .append(gold(format(amount) + " ✦"))
                    .append(Component.text("  (баланс: ").color(TextColor.color(0xAAAAAA)))
                    .append(gold(format(am.getBalance(nick)) + " ✦"))
                    .append(Component.text(")").color(TextColor.color(0xAAAAAA)))
                );
            }

            // /a set <ник> <кол>
            case "set" -> {
                if (args.length < 3) { sendUsage(sender); return true; }
                long amount = parseLong(sender, args[2]);
                if (amount < 0) return true;
                String nick = args[1];
                am.set(nick, amount);
                notifyPlayer(nick, buildSetMsg(amount));
                sender.sendMessage(
                    Component.text("✓ Баланс игрока ").color(TextColor.color(0x55FF55))
                    .append(white(nick))
                    .append(Component.text(" установлен: ").color(TextColor.color(0x55FF55)))
                    .append(gold(format(amount) + " ✦"))
                );
            }

            // /a reset <ник>
            case "reset" -> {
                if (args.length < 2) { sendUsage(sender); return true; }
                String nick = args[1];
                am.reset(nick);
                notifyPlayer(nick, Component.text("⚠ Ваш баланс Арисов сброшен до 0.")
                    .color(TextColor.color(0xFF8800)));
                sender.sendMessage(
                    Component.text("✓ Баланс игрока ").color(TextColor.color(0x55FF55))
                    .append(white(nick))
                    .append(Component.text(" сброшен до 0.").color(TextColor.color(0x55FF55)))
                );
            }

            default -> sendUsage(sender);
        }

        return true;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Component buildGiveMsg(long amount, long newBal) {
        return Component.text("")
            .appendNewline()
            .append(Component.text("  ✦ Начислено ").color(TextColor.color(0xFFBB33)))
            .append(gold(format(amount) + " Арисов ✦"))
            .appendNewline()
            .append(Component.text("  Баланс: ").color(TextColor.color(0xAAAAAA)))
            .append(gold(format(newBal) + " ✦"))
            .appendNewline();
    }

    private Component buildSetMsg(long newBal) {
        return Component.text("")
            .appendNewline()
            .append(Component.text("  ✦ Ваш баланс установлен: ").color(TextColor.color(0xFFBB33)))
            .append(gold(format(newBal) + " Арисов ✦"))
            .appendNewline();
    }

    private void notifyPlayer(String nick, Component msg) {
        Player p = Bukkit.getPlayer(nick);
        if (p != null) p.sendMessage(msg);
    }

    private long parseLong(CommandSender s, String str) {
        try { return Long.parseLong(str); }
        catch (NumberFormatException e) {
            s.sendMessage(Component.text("✗ Неверное число: " + str).color(TextColor.color(0xFF4444)));
            return -1;
        }
    }

    private Component gold(String text) {
        return Component.text(text).color(TextColor.color(0xFF8000))
            .decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, true);
    }

    private Component white(String text) {
        return Component.text(text).color(TextColor.color(0xFFFFFF));
    }

    private String format(long n) {
        return String.format("%,d", n).replace(',', ' ');
    }

    private void sendUsage(CommandSender s) {
        s.sendMessage(Component.text("Использование:").color(TextColor.color(0xAAAAAA)));
        s.sendMessage(Component.text("  /a give <ник> <кол>").color(TextColor.color(0xFFFF55)));
        s.sendMessage(Component.text("  /a set <ник> <кол>").color(TextColor.color(0xFFFF55)));
        s.sendMessage(Component.text("  /a reset <ник>").color(TextColor.color(0xFFFF55)));
        s.sendMessage(Component.text("  /a giveall <кол>  (только онлайн)").color(TextColor.color(0xFFFF55)));
    }
}
