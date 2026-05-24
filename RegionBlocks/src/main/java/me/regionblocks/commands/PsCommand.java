package me.regionblocks.commands;

import me.regionblocks.RegionBlocks;
import me.regionblocks.models.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PsCommand implements CommandExecutor {

    private final RegionBlocks plugin;

    public PsCommand(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }

        // /ps add <регион> <ник>
        // /ps remove <регион> <ник>
        if (args.length < 3) {
            player.sendMessage(usage());
            return true;
        }

        String sub = args[0].toLowerCase();
        String regionName = args[1];
        String targetNick = args[2];

        Region region = plugin.getRegionManager().getRegion(regionName);

        if (region == null) {
            player.sendMessage(
                Component.text("✗ Регион «" + regionName + "» не найден.")
                    .color(TextColor.color(0xFF4444))
            );
            return true;
        }

        if (!region.isOwner(player.getName()) && !player.hasPermission("regionblocks.admin")) {
            player.sendMessage(
                Component.text("✗ Вы не владелец этого региона!")
                    .color(TextColor.color(0xFF4444))
            );
            return true;
        }

        switch (sub) {
            case "add" -> {
                if (region.isOwner(targetNick)) {
                    player.sendMessage(
                        Component.text("✗ Этот игрок уже владелец региона.")
                            .color(TextColor.color(0xFF8800))
                    );
                    return true;
                }
                region.addMember(targetNick);
                plugin.getRegionManager().save();
                player.sendMessage(
                    Component.text("✓ ").color(TextColor.color(0x55FF55))
                    .append(Component.text(targetNick).color(TextColor.color(0xFFFFFF)))
                    .append(Component.text(" добавлен в регион ").color(TextColor.color(0x55FF55)))
                    .append(Component.text(regionName).color(TextColor.color(0xFFCC55)))
                );
            }
            case "remove" -> {
                if (!region.isMember(targetNick)) {
                    player.sendMessage(
                        Component.text("✗ «" + targetNick + "» не является участником региона.")
                            .color(TextColor.color(0xFF4444))
                    );
                    return true;
                }
                region.removeMember(targetNick);
                plugin.getRegionManager().save();
                player.sendMessage(
                    Component.text("✓ ").color(TextColor.color(0x55FF55))
                    .append(Component.text(targetNick).color(TextColor.color(0xFFFFFF)))
                    .append(Component.text(" удалён из региона ").color(TextColor.color(0x55FF55)))
                    .append(Component.text(regionName).color(TextColor.color(0xFFCC55)))
                );
            }
            default -> player.sendMessage(usage());
        }

        return true;
    }

    private Component usage() {
        return Component.text("Использование:").color(TextColor.color(0xAAAAAA))
            .appendNewline()
            .append(Component.text("  /ps add <регион> <ник>").color(TextColor.color(0xFFFF55)))
            .appendNewline()
            .append(Component.text("  /ps remove <регион> <ник>").color(TextColor.color(0xFFFF55)));
    }
}
