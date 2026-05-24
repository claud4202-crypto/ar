package me.regionblocks.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ArisItemManager {

    private static NamespacedKey ARIS_KEY;

    public static void init(Plugin plugin) {
        ARIS_KEY = new NamespacedKey(plugin, "aris_block");
    }

    public static ItemStack createArisBlock() {
        ItemStack item = new ItemStack(Material.RED_MUSHROOM_BLOCK, 1);
        ItemMeta meta = item.getItemMeta();

        // Название: RGB жёлто-оранжевый градиент
        meta.displayName(
            Component.text("✦ Арис ✦")
                .color(TextColor.color(0xFF8000))
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true)
        );

        // Описание
        meta.lore(List.of(
            Component.text(""),
            Component.text("  Древний артефакт из запретных ")
                .color(TextColor.color(0xFFAA33))
                .decoration(TextDecoration.ITALIC, false),
            Component.text("  измерений. Его алая поверхность ")
                .color(TextColor.color(0xFF8C00))
                .decoration(TextDecoration.ITALIC, false),
            Component.text("  пульсирует загадочной энергией.")
                .color(TextColor.color(0xFF6600))
                .decoration(TextDecoration.ITALIC, false),
            Component.text(""),
            Component.text("  ► Тир: ")
                .color(TextColor.color(0xAAAAAA))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Арис")
                    .color(TextColor.color(0xFF8000))
                    .decoration(TextDecoration.BOLD, true)),
            Component.text("  ► Регион: ")
                .color(TextColor.color(0xAAAAAA))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("53×53×53 блоков")
                    .color(TextColor.color(0xFFCC66))),
            Component.text(""),
            Component.text("  Поставьте блок, чтобы")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, true),
            Component.text("  создать легендарный регион.")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, true),
            Component.text("")
        ));

        // PDC-метка для идентификации
        meta.getPersistentDataContainer().set(ARIS_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isArisBlock(ItemStack item) {
        if (item == null || item.getType() != Material.RED_MUSHROOM_BLOCK) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta()
            .getPersistentDataContainer()
            .has(ARIS_KEY, PersistentDataType.BYTE);
    }

    public static NamespacedKey getKey() { return ARIS_KEY; }
}
