package me.regionblocks.commands;

import me.regionblocks.RegionBlocks;
import me.regionblocks.models.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RgCommand implements CommandExecutor {

    private final RegionBlocks plugin;

    public RgCommand(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("i") || args[0].equalsIgnoreCase("info")) {
            showInfo(player);
            return true;
        }

        player.sendMessage(Component.text("Использование: /rg i  или  /rg info").color(TextColor.color(0xAAAAAA)));
        return true;
    }

    private void showInfo(Player player) {
        List<Region> regions = plugin.getRegionManager().getPlayerRegions(player.getName());

        player.sendMessage(Component.text(""));
        player.sendMessage(
            Component.text("══════ Ваши регионы ══════")
                .color(TextColor.color(0xFFAA00)).decoration(TextDecoration.BOLD, true)
        );

        if (regions.isEmpty()) {
            player.sendMessage(Component.text("  У вас нет регионов.").color(TextColor.color(0x888888)));
        } else {
            for (Region r : regions) {
                int s = r.getTier().getSize();

                player.sendMessage(Component.text("  ✦ ").color(TextColor.color(0xFFAA00))
                    .append(Component.text(r.getName()).color(TextColor.color(0xFFFFFF)).decoration(TextDecoration.BOLD, true)));
                player.sendMessage(Component.text("     Тир: ").color(TextColor.color(0x888888))
                    .append(Component.text(r.getTier().getDisplayName()).color(TextColor.color(r.getTier().getColor()))));
                player.sendMessage(Component.text("     Размер: ").color(TextColor.color(0x888888))
                    .append(Component.text(s + "×" + s + "×" + s + " блоков").color(TextColor.color(0xFFCC55))));
                player.sendMessage(Component.text("     От: ").color(TextColor.color(0x888888))
                    .append(Component.text(
                        r.minX() + ", " + r.minY() + ", " + r.minZ()
                    ).color(TextColor.color(0xAAFFAA)))
                    .append(Component.text("  До: ").color(TextColor.color(0x888888)))
                    .append(Component.text(
                        r.maxX() + ", " + r.maxY() + ", " + r.maxZ()
                    ).color(TextColor.color(0xAAFFAA))));
                player.sendMessage(Component.text("     Блок: ").color(TextColor.color(0x888888))
                    .append(Component.text(
                        r.getCenter().getBlockX() + ", " + r.getCenter().getBlockY() + ", " + r.getCenter().getBlockZ()
                    ).color(TextColor.color(0xCCCCCC))));
                if (!r.getMembers().isEmpty()) {
                    player.sendMessage(Component.text("     Участники: ").color(TextColor.color(0x888888))
                        .append(Component.text(String.join(", ", r.getMembers())).color(TextColor.color(0xCCCCCC))));
                }
                player.sendMessage(Component.text(""));
            }
        }
        player.sendMessage(
            Component.text("══════════════════════════").color(TextColor.color(0xFFAA00)).decoration(TextDecoration.BOLD, true)
        );
    }
}
