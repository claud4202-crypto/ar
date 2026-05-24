package me.regionblocks.commands;

import me.regionblocks.managers.ArisItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveArisCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {

        // /givearis <ник>
        if (args.length < 1) {
            sender.sendMessage(
                Component.text("Использование: /givearis <ник>")
                    .color(TextColor.color(0xFFFF55))
            );
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(
                Component.text("✗ Игрок «" + args[0] + "» не найден или оффлайн.")
                    .color(TextColor.color(0xFF4444))
            );
            return true;
        }

        ItemStack aris = ArisItemManager.createArisBlock();
        target.getInventory().addItem(aris);

        // Сообщение получателю
        target.sendMessage(Component.text(""));
        target.sendMessage(
            Component.text("✦ Вы получили блок ")
                .color(TextColor.color(0xFFCC55))
            .append(Component.text("Арис")
                .color(TextColor.color(0xFF8000))
                .decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, true))
            .append(Component.text("!").color(TextColor.color(0xFFCC55)))
        );
        target.sendMessage(Component.text(""));

        // Сообщение выдающему
        if (!sender.getName().equals(target.getName())) {
            sender.sendMessage(
                Component.text("✓ Блок Арис выдан игроку ")
                    .color(TextColor.color(0x55FF55))
                .append(Component.text(target.getName())
                    .color(TextColor.color(0xFFFFFF)))
                .append(Component.text(".").color(TextColor.color(0x55FF55)))
            );
        }

        return true;
    }
}
