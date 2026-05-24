package me.regionblocks.commands;

import me.regionblocks.RegionBlocks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArisCommand implements CommandExecutor {

    private final RegionBlocks plugin;

    public ArisCommand(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }

        long bal = plugin.getArisManager().getBalance(player.getName());

        player.sendMessage(Component.text(""));
        player.sendMessage(
            Component.text("  ✦ Ваш баланс Арисов: ")
                .color(TextColor.color(0xFFBB33))
                .decoration(TextDecoration.BOLD, false)
            .append(
                Component.text(String.format("%,d", bal) + " ✦")
                    .color(TextColor.color(0xFF8000))
                    .decoration(TextDecoration.BOLD, true)
            )
        );
        player.sendMessage(Component.text(""));

        return true;
    }
}
