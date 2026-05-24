package me.regionblocks.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ArisAdvertiseTask extends BukkitRunnable {

    @Override
    public void run() {
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(buildSeparator());

        Bukkit.broadcast(
            Component.text("  ")
            .append(Component.text("K").color(TextColor.color(0xFF3300)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("u").color(TextColor.color(0xFF4400)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("p").color(TextColor.color(0xFF5500)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("i").color(TextColor.color(0xFF6600)).decoration(TextDecoration.BOLD, true))
            .append(Component.text(" ").color(TextColor.color(0xFF7700)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("p").color(TextColor.color(0xFF8800)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("r").color(TextColor.color(0xFF9900)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("i").color(TextColor.color(0xFFAA00)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("v").color(TextColor.color(0xFFBB00)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("a").color(TextColor.color(0xFFCC00)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("t").color(TextColor.color(0xFFDD00)).decoration(TextDecoration.BOLD, true))
            .append(Component.text(" - ").color(TextColor.color(0xFFCC00)))
            .append(Component.text("A").color(TextColor.color(0xFF5500)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("r").color(TextColor.color(0xFF6600)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("i").color(TextColor.color(0xFF7700)).decoration(TextDecoration.BOLD, true))
            .append(Component.text("s").color(TextColor.color(0xFF8800)).decoration(TextDecoration.BOLD, true))
            .append(Component.text(" !").color(TextColor.color(0xFF9900)).decoration(TextDecoration.BOLD, true))
        );

        Bukkit.broadcast(
            Component.text("  ")
            .append(Component.text("Legendarnyj privat ").color(TextColor.color(0xFFBB33)))
            .append(Component.text("52x52x52").color(TextColor.color(0xFFFF77)).decoration(TextDecoration.BOLD, true))
        );

        Bukkit.broadcast(
            Component.text("  ")
            .append(Component.text("Vsego za ").color(TextColor.color(0xCCCCCC)))
            .append(Component.text("10 000").color(TextColor.color(0xFF8000)).decoration(TextDecoration.BOLD, true))
            .append(Component.text(" Arisov v ").color(TextColor.color(0xCCCCCC)))
            .append(Component.text("/shop").color(TextColor.color(0x55FFFF)).decoration(TextDecoration.BOLD, true))
        );

        Bukkit.broadcast(
            Component.text("  ")
            .append(Component.text("Balans Arisov: ").color(TextColor.color(0x888888)))
            .append(Component.text("/aris").color(TextColor.color(0xFFAA33)).decoration(TextDecoration.BOLD, true))
        );

        Bukkit.broadcast(buildSeparator());
        Bukkit.broadcast(Component.text(""));
    }

    private Component buildSeparator() {
        int[] colors = {
            0xFF2200, 0xFF3300, 0xFF4400, 0xFF5500, 0xFF6600,
            0xFF7700, 0xFF8800, 0xFF9900, 0xFFAA00, 0xFFBB00,
            0xFFCC00, 0xFFDD00, 0xFFCC00, 0xFFBB00, 0xFFAA00,
            0xFF9900, 0xFF8800, 0xFF7700, 0xFF6600, 0xFF5500,
            0xFF4400, 0xFF3300, 0xFF2200
        };
        Component sep = Component.text("  ");
        for (int c : colors) {
            sep = sep.append(Component.text("=").color(TextColor.color(c)).decoration(TextDecoration.BOLD, true));
        }
        return sep;
    }
}
