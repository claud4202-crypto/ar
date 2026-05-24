package me.regionblocks.models;

import org.bukkit.Material;

public enum RegionTier {

    //               Название              Материал                  Сторона  Цвет RGB
    COMMON   ("§7Обычный",       Material.IRON_ORE,           3,  0xAAAAAA),
    RARE     ("§6Редкий",        Material.GOLD_ORE,           6,  0xFFD700),
    EPIC     ("§5Эпик",          Material.EMERALD_ORE,        9,  0x00CC66),
    MYTHIC   ("§bМифический",    Material.DIAMOND_ORE,       12,  0x00FFFF),
    LEGENDARY("§cЛегендарный",   Material.ANCIENT_DEBRIS,    24,  0xFF4400),
    ARIS     ("§6Арис",          Material.RED_MUSHROOM_BLOCK, 52,  0xFF8000);

    // size = точная длина стороны куба (например 3 → блоки -1,0,+1 от центра → 3 штуки)
    // Проверка: расстояние от центра < size/2.0  (не halfSize целочисленный!)

    private final String displayName;
    private final Material blockMaterial;
    private final int size;   // длина стороны куба
    private final int color;

    RegionTier(String displayName, Material blockMaterial, int size, int color) {
        this.displayName = displayName;
        this.blockMaterial = blockMaterial;
        this.size = size;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public Material getBlockMaterial() { return blockMaterial; }
    public int getSize() { return size; }
    /** Сколько блоков «вниз» от центра входит в куб (для нечётных = (s-1)/2). */
    public int lowHalf()  { return (size - 1) / 2; }
    /** Сколько блоков «вверх» от центра входит в куб (для нечётных = (s-1)/2, для чётных = s/2). */
    public int highHalf() { return size / 2; }
    /** Совместимость: используется в старом коде проверки. */
    public int getHalf() { return lowHalf(); }
    public int getColor() { return color; }

    public static RegionTier fromMaterial(Material mat) {
        for (RegionTier t : values()) {
            if (t.blockMaterial == mat) return t;
        }
        return null;
    }
}
