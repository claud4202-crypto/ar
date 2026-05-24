package me.regionblocks.listeners;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.KitManager;
import me.arisdonate.models.Sphere;
import me.arisdonate.util.Msg;
import me.regionblocks.RegionBlocks;
import me.regionblocks.managers.ArisItemManager;
import me.regionblocks.managers.LegendaryItemManager;
import me.regionblocks.managers.MinecartItemManager;
import me.regionblocks.managers.TntItemManager;
import me.regionblocks.models.MinecartType;
import me.regionblocks.models.RegionTier;
import me.regionblocks.models.TntType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShopListener implements Listener {

    private static final String TITLE_PRIVATES  = "✦ Магазин — Приваты ✦";
    private static final String TITLE_TNT       = "✦ Магазин — ТНТ ✦";
    private static final String TITLE_MINECART  = "✦ Магазин — Вагонетки ✦";
    private static final String TITLE_SPHERES   = "✦ Магазин — Сферы / Шары ✦";
    private static final String TITLE_KITS      = "✦ Магазин — Киты ✦";

    /**
     * Цены китов в Арисах. Заданы тут, а не в config.yml, чтобы баланс
     * был неизменным даже если у админа стоит старая версия ArisDonate.
     * Если у кита нет id в таблице — используется DEFAULT_KIT_PRICE.
     */
    private static final long DEFAULT_KIT_PRICE = 50_000L;
    private static final Map<String, Long> KIT_PRICES = new LinkedHashMap<>();
    static {
        KIT_PRICES.put("spark",     5_000L);
        KIT_PRICES.put("luna",     12_000L);
        KIT_PRICES.put("stellar",  25_000L);
        KIT_PRICES.put("nova",     50_000L);
        KIT_PRICES.put("comet",    75_000L);
        KIT_PRICES.put("galaxy",  100_000L);
        KIT_PRICES.put("nebula",  150_000L);
        KIT_PRICES.put("cosmos",  200_000L);
        KIT_PRICES.put("phoenix", 300_000L);
        KIT_PRICES.put("aris",      500_000L);
        KIT_PRICES.put("arisplus", 1_000_000L);
    }

    private final RegionBlocks plugin;

    public ShopListener(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    // ══ Открытие разделов ════════════════════════════════════════════════════

    public void openShop(Player player) { openPrivatesTab(player); }

    public void openPrivatesTab(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            Component.text(TITLE_PRIVATES).color(TextColor.color(0xFFAA00)));
        fillBorder(inv);
        long bal = plugin.getArisManager().getBalance(player.getName());

        inv.setItem(10, shopItem(RegionTier.COMMON,    bal, 100));
        inv.setItem(12, shopItem(RegionTier.RARE,      bal, 300));
        inv.setItem(14, shopItem(RegionTier.EPIC,      bal, 700));
        inv.setItem(16, shopItem(RegionTier.MYTHIC,    bal, 1500));
        inv.setItem(29, shopItem(RegionTier.LEGENDARY, bal, 4000));
        inv.setItem(33, arisShopItem(bal, 10000));
        inv.setItem(31, balanceDisplay(bal));

        // Навигация: ТНТ, вагонетки, сферы, киты
        inv.setItem(45, tabButton(Material.TNT,                  "§c💣 ТНТ",         "Открыть раздел ТНТ"));
        inv.setItem(47, tabButton(Material.TNT_MINECART,         "§6🚃 Вагонетки",  "Открыть раздел вагонеток"));
        inv.setItem(51, sphereTabButton());
        inv.setItem(53, kitsTabButton());

        player.openInventory(inv);
    }

    public void openTntTab(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            Component.text(TITLE_TNT).color(TextColor.color(0xFF4422)));
        fillBorder(inv);
        long bal = plugin.getArisManager().getBalance(player.getName());

        int[] slots = {11, 13, 15, 20};
        TntType[] types = TntType.values();
        for (int i = 0; i < types.length && i < slots.length; i++)
            inv.setItem(slots[i], tntShopItem(types[i], bal));

        inv.setItem(29, rgbItem(Material.RED_DYE,   "§cКрасный порошок",  "Усиливает взрыв"));
        inv.setItem(31, rgbItem(Material.GREEN_DYE, "§aЗелёный порошок",  "Расширяет радиус"));
        inv.setItem(33, rgbItem(Material.BLUE_DYE,  "§9Синий порошок",    "Пробивает защиту"));
        inv.setItem(22, balanceDisplay(bal));

        inv.setItem(45, tabButton(Material.IRON_ORE,      "§6🏠 Приваты",    "Вернуться к приватам"));
        inv.setItem(47, tabButton(Material.TNT_MINECART,  "§6🚃 Вагонетки",  "Открыть раздел вагонеток"));
        inv.setItem(51, sphereTabButton());
        inv.setItem(53, kitsTabButton());

        player.openInventory(inv);
    }

    public void openMinecartTab(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            Component.text(TITLE_MINECART).color(TextColor.color(0xFF8800)));
        fillBorder(inv);
        long bal = plugin.getArisManager().getBalance(player.getName());

        int[] slots = {11, 13, 15, 20};
        MinecartType[] types = MinecartType.values();
        for (int i = 0; i < types.length && i < slots.length; i++)
            inv.setItem(slots[i], minecartShopItem(types[i], bal));

        inv.setItem(22, balanceDisplay(bal));

        inv.setItem(45, tabButton(Material.IRON_ORE, "§6🏠 Приваты",  "Вернуться к приватам"));
        inv.setItem(47, tabButton(Material.TNT,      "§c💣 ТНТ",      "Открыть раздел ТНТ"));
        inv.setItem(51, sphereTabButton());
        inv.setItem(53, kitsTabButton());

        player.openInventory(inv);
    }

    /** Вкладка сфер/шаров. Доступна только если есть ArisDonate. */
    public void openSpheresTab(Player player) {
        if (!plugin.hasArisDonate()) {
            player.sendMessage(Component.text("✗ ArisDonate не загружен на сервере — сферы недоступны.")
                    .color(TextColor.color(0xFF4444)));
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 54,
                Component.text(TITLE_SPHERES).color(TextColor.color(0xFF55FF)));
        fillBorder(inv);
        long bal = plugin.getArisManager().getBalance(player.getName());

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int i = 0;
        for (Sphere s : plugin.getArisDonate().getSphereManager().all()) {
            if (s.price() <= 0) continue; // выдаётся только в китах
            if (i >= slots.length) break;
            inv.setItem(slots[i++], sphereShopItem(s, bal));
        }
        inv.setItem(4, balanceDisplay(bal));

        inv.setItem(45, tabButton(Material.IRON_ORE,     "§6🏠 Приваты",    "Вернуться к приватам"));
        inv.setItem(47, tabButton(Material.TNT,          "§c💣 ТНТ",        "Открыть раздел ТНТ"));
        inv.setItem(49, tabButton(Material.TNT_MINECART, "§6🚃 Вагонетки",  "Открыть раздел вагонеток"));
        inv.setItem(53, kitsTabButton());

        player.openInventory(inv);
    }

    /** Вкладка китов. Доступна только если есть ArisDonate. */
    public void openKitsTab(Player player) {
        if (!plugin.hasArisDonate()) {
            player.sendMessage(Component.text("✗ ArisDonate не загружен на сервере — киты недоступны.")
                    .color(TextColor.color(0xFF4444)));
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 54,
                Component.text(TITLE_KITS).color(TextColor.color(0xFFDD00)));
        fillBorder(inv);
        long bal = plugin.getArisManager().getBalance(player.getName());

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int i = 0;
        for (KitManager.Kit kit : plugin.getArisDonate().getKitManager().all()) {
            if (i >= slots.length) break;
            inv.setItem(slots[i++], kitShopItem(kit, bal));
        }
        inv.setItem(4, balanceDisplay(bal));

        inv.setItem(45, tabButton(Material.IRON_ORE,     "§6🏠 Приваты",    "Вернуться к приватам"));
        inv.setItem(47, tabButton(Material.TNT,          "§c💣 ТНТ",        "Открыть раздел ТНТ"));
        inv.setItem(49, tabButton(Material.TNT_MINECART, "§6🚃 Вагонетки",  "Открыть раздел вагонеток"));
        inv.setItem(51, sphereTabButton());

        player.openInventory(inv);
    }

    // ══ Обработка кликов ═════════════════════════════════════════════════════

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
            .plainText().serialize(e.getView().title());

        boolean isPrivates = plain.contains("Приваты");
        boolean isTnt      = plain.contains("ТНТ");
        boolean isMinecart = plain.contains("Вагонетки");
        boolean isSpheres  = plain.contains("Сферы");
        boolean isKits     = plain.contains("Киты");
        if (!isPrivates && !isTnt && !isMinecart && !isSpheres && !isKits) return;

        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        int slot = e.getSlot();

        if (isPrivates) {
            switch (slot) {
                case 10 -> buy(player, RegionTier.COMMON,    100);
                case 12 -> buy(player, RegionTier.RARE,      300);
                case 14 -> buy(player, RegionTier.EPIC,      700);
                case 16 -> buy(player, RegionTier.MYTHIC,    1500);
                case 29 -> buy(player, RegionTier.LEGENDARY, 4000);
                case 33 -> buyAris(player, 10000);
                case 45 -> openTntTab(player);
                case 47 -> openMinecartTab(player);
                case 51 -> openSpheresTab(player);
                case 53 -> openKitsTab(player);
            }
        } else if (isTnt) {
            switch (slot) {
                case 11 -> buyTnt(player, TntType.BASIC);
                case 13 -> buyTnt(player, TntType.STRONG);
                case 15 -> buyTnt(player, TntType.MEGA);
                case 20 -> buyTnt(player, TntType.TITAN);
                case 45 -> openPrivatesTab(player);
                case 47 -> openMinecartTab(player);
                case 51 -> openSpheresTab(player);
                case 53 -> openKitsTab(player);
            }
        } else if (isMinecart) {
            switch (slot) {
                case 11 -> buyMinecart(player, MinecartType.BASIC);
                case 13 -> buyMinecart(player, MinecartType.STRONG);
                case 15 -> buyMinecart(player, MinecartType.MEGA);
                case 20 -> buyMinecart(player, MinecartType.TITAN);
                case 45 -> openPrivatesTab(player);
                case 47 -> openTntTab(player);
                case 51 -> openSpheresTab(player);
                case 53 -> openKitsTab(player);
            }
        } else if (isSpheres) {
            switch (slot) {
                case 45 -> openPrivatesTab(player);
                case 47 -> openTntTab(player);
                case 49 -> openMinecartTab(player);
                case 53 -> openKitsTab(player);
                default -> handleSphereClick(player, e.getCurrentItem());
            }
        } else { // киты
            switch (slot) {
                case 45 -> openPrivatesTab(player);
                case 47 -> openTntTab(player);
                case 49 -> openMinecartTab(player);
                case 51 -> openSpheresTab(player);
                default -> handleKitClick(player, e.getCurrentItem());
            }
        }
    }

    // ══ Покупки ══════════════════════════════════════════════════════════════

    private void buy(Player player, RegionTier tier, long price) {
        if (!plugin.getArisManager().take(player.getName(), price)) { noMoney(player, price); return; }
        ItemStack item = (tier == RegionTier.LEGENDARY)
            ? LegendaryItemManager.createLegendaryBlock()
            : new ItemStack(tier.getBlockMaterial(), 1);
        player.getInventory().addItem(item);
        player.closeInventory();
        bought(player, tier.getDisplayName(), price);
    }

    private void buyAris(Player player, long price) {
        if (!plugin.getArisManager().take(player.getName(), price)) { noMoney(player, price); return; }
        player.getInventory().addItem(ArisItemManager.createArisBlock());
        player.closeInventory();
        bought(player, "§6Арис", price);
    }

    private void buyTnt(Player player, TntType type) {
        if (!plugin.getArisManager().take(player.getName(), type.getPrice())) { noMoney(player, type.getPrice()); return; }
        player.getInventory().addItem(TntItemManager.createTnt(type));
        player.closeInventory();
        bought(player, type.getDisplayName(), type.getPrice());
    }

    private void buyMinecart(Player player, MinecartType type) {
        if (!plugin.getArisManager().take(player.getName(), type.getPrice())) { noMoney(player, type.getPrice()); return; }
        player.getInventory().addItem(MinecartItemManager.createMinecart(type));
        player.closeInventory();
        bought(player, type.getDisplayName(), type.getPrice());
    }

    private void handleSphereClick(Player player, ItemStack clicked) {
        if (!plugin.hasArisDonate()) return;
        if (clicked.getItemMeta() == null) return;
        ArisDonatePlugin ad = plugin.getArisDonate();
        String sid = clicked.getItemMeta().getPersistentDataContainer()
                .get(ad.keySphereId(), PersistentDataType.STRING);
        if (sid == null) return;
        Sphere s = ad.getSphereManager().getSphere(sid);
        if (s == null) return;
        if (s.price() <= 0) {
            player.sendMessage(Component.text("✗ Эта сфера выдаётся только в составе кита.")
                    .color(TextColor.color(0xFF4444)));
            return;
        }
        long price = s.price();
        if (!plugin.getArisManager().take(player.getName(), price)) { noMoney(player, price); return; }
        player.getInventory().addItem(s.toItem(ad));
        bought(player, s.displayName(), price);
        // Перерисовать вкладку с актуальным балансом
        openSpheresTab(player);
    }

    private void handleKitClick(Player player, ItemStack clicked) {
        if (!plugin.hasArisDonate()) return;
        if (clicked.getItemMeta() == null) return;
        ArisDonatePlugin ad = plugin.getArisDonate();
        String kitId = clicked.getItemMeta().getPersistentDataContainer()
                .get(ad.keyKitId(), PersistentDataType.STRING);
        if (kitId == null) return;
        KitManager.Kit kit = ad.getKitManager().getKit(kitId);
        if (kit == null) return;
        long price = kitPrice(kit.id);
        if (!plugin.getArisManager().take(player.getName(), price)) { noMoney(player, price); return; }
        // Выдаём предметы кита напрямую (без кулдауна — это разовая покупка).
        for (ItemStack it : kit.items) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(it.clone());
            for (ItemStack drop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
        bought(player, kit.displayName, price);
        openKitsTab(player);
    }

    private long kitPrice(String id) {
        return KIT_PRICES.getOrDefault(id == null ? "" : id.toLowerCase(), DEFAULT_KIT_PRICE);
    }

    private void noMoney(Player player, long price) {
        player.sendMessage(
            Component.text("✗ Недостаточно Арисов! Нужно: ").color(TextColor.color(0xFF4444))
            .append(Component.text(fmt(price) + " ✦").color(TextColor.color(0xFF8000))
                .decoration(TextDecoration.BOLD, true))
        );
        player.closeInventory();
    }

    private void bought(Player player, String name, long price) {
        long bal = plugin.getArisManager().getBalance(player.getName());
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("  ✦ Куплено: ").color(TextColor.color(0x55FF55))
            .append(Component.text(name + "§r")));
        player.sendMessage(Component.text("  Списано: ").color(TextColor.color(0xAAAAAA))
            .append(Component.text(fmt(price) + " ✦").color(TextColor.color(0xFF8000))
                .decoration(TextDecoration.BOLD, true)));
        player.sendMessage(Component.text("  Остаток: ").color(TextColor.color(0xAAAAAA))
            .append(Component.text(fmt(bal) + " ✦").color(TextColor.color(0xFFAA33))));
        player.sendMessage(Component.text(""));
    }

    // ══ Предметы-иконки ══════════════════════════════════════════════════════

    private ItemStack shopItem(RegionTier tier, long bal, long price) {
        ItemStack item = new ItemStack(tier.getBlockMaterial());
        ItemMeta meta = item.getItemMeta();
        boolean can = bal >= price;
        int s = tier.getSize();
        meta.displayName(Component.text(tier.getDisplayName() + " §r приват")
            .color(TextColor.color(tier.getColor()))
            .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text(""),
            Component.text("  Размер: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(s + "×" + s + "×" + s).color(TextColor.color(0xFFCC55)).decoration(TextDecoration.ITALIC, false)),
            Component.text(""),
            Component.text("  Цена: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(price) + " ✦").color(TextColor.color(0xFF8000))
                    .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)),
            Component.text("  Баланс: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(bal) + " ✦")
                    .color(can ? TextColor.color(0x55FF55) : TextColor.color(0xFF4444))
                    .decoration(TextDecoration.ITALIC, false)),
            Component.text(""),
            can ? Component.text("  ► Нажмите, чтобы купить").color(TextColor.color(0x55FF55)).decoration(TextDecoration.ITALIC, false)
                : Component.text("  ✗ Недостаточно Арисов").color(TextColor.color(0xFF4444)).decoration(TextDecoration.ITALIC, false),
            Component.text("")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack arisShopItem(long bal, long price) {
        ItemStack item = ArisItemManager.createArisBlock();
        ItemMeta meta = item.getItemMeta();
        boolean can = bal >= price;
        meta.displayName(
            Component.text("✦ ").color(TextColor.color(0xFF4400))
            .append(Component.text("А").color(TextColor.color(0xFF5500)))
            .append(Component.text("р").color(TextColor.color(0xFF6600)))
            .append(Component.text("и").color(TextColor.color(0xFF7700)))
            .append(Component.text("с").color(TextColor.color(0xFF8800)))
            .append(Component.text(" ✦").color(TextColor.color(0xFF9900)))
            .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true)
        );
        meta.lore(List.of(
            Component.text(""),
            Component.text("  Легендарный артефакт").color(TextColor.color(0xFFAA33)).decoration(TextDecoration.ITALIC, false),
            Component.text("  из запретных измерений.").color(TextColor.color(0xFF8C00)).decoration(TextDecoration.ITALIC, false),
            Component.text(""),
            Component.text("  Размер: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text("52×52×52").color(TextColor.color(0xFFCC55)).decoration(TextDecoration.ITALIC, false)),
            Component.text(""),
            Component.text("  Цена: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(price) + " ✦").color(TextColor.color(0xFF8000))
                    .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)),
            Component.text("  Баланс: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(bal) + " ✦")
                    .color(can ? TextColor.color(0x55FF55) : TextColor.color(0xFF4444))
                    .decoration(TextDecoration.ITALIC, false)),
            Component.text(""),
            can ? Component.text("  ► Нажмите, чтобы купить").color(TextColor.color(0x55FF55)).decoration(TextDecoration.ITALIC, false)
                : Component.text("  ✗ Недостаточно Арисов").color(TextColor.color(0xFF4444)).decoration(TextDecoration.ITALIC, false),
            Component.text("")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack tntShopItem(TntType type, long bal) {
        ItemStack item = TntItemManager.createTnt(type);
        ItemMeta meta = item.getItemMeta();
        boolean can = bal >= type.getPrice();
        List<Component> lore = new ArrayList<>(meta.lore() != null ? meta.lore() : List.of());
        lore.add(Component.text("  Баланс: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
            .append(Component.text(fmt(bal) + " ✦")
                .color(can ? TextColor.color(0x55FF55) : TextColor.color(0xFF4444))
                .decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text(""));
        lore.add(can
            ? Component.text("  ► Нажмите, чтобы купить").color(TextColor.color(0x55FF55)).decoration(TextDecoration.ITALIC, false)
            : Component.text("  ✗ Недостаточно Арисов").color(TextColor.color(0xFF4444)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(""));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack minecartShopItem(MinecartType type, long bal) {
        ItemStack item = MinecartItemManager.createMinecart(type);
        ItemMeta meta = item.getItemMeta();
        boolean can = bal >= type.getPrice();
        List<Component> lore = new ArrayList<>(meta.lore() != null ? meta.lore() : List.of());
        lore.add(Component.text("  Баланс: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
            .append(Component.text(fmt(bal) + " ✦")
                .color(can ? TextColor.color(0x55FF55) : TextColor.color(0xFF4444))
                .decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text(""));
        lore.add(can
            ? Component.text("  ► Нажмите, чтобы купить").color(TextColor.color(0x55FF55)).decoration(TextDecoration.ITALIC, false)
            : Component.text("  ✗ Недостаточно Арисов").color(TextColor.color(0xFF4444)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(""));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack sphereShopItem(Sphere s, long bal) {
        // Берём готовую item-форму сферы (с pdc keySphereId + полным описанием)
        // и добавляем стоимость в Арисах + статус "хватает/не хватает".
        ItemStack item = s.toItem(plugin.getArisDonate());
        ItemMeta meta = item.getItemMeta();
        boolean can = bal >= s.price();
        List<Component> lore = meta.lore() != null
                ? new ArrayList<>(meta.lore())
                : new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("  Цена: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(s.price()) + " ✦").color(TextColor.color(0xFF8000))
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text("  Баланс: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(bal) + " ✦")
                        .color(can ? TextColor.color(0x55FF55) : TextColor.color(0xFF4444))
                        .decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text(""));
        lore.add(can
                ? Component.text("  ► Нажмите, чтобы купить").color(TextColor.color(0x55FF55)).decoration(TextDecoration.ITALIC, false)
                : Component.text("  ✗ Недостаточно Арисов").color(TextColor.color(0xFF4444)).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack kitShopItem(KitManager.Kit kit, long bal) {
        Material icon = iconForKit(kit.id);
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        long price = kitPrice(kit.id);
        boolean can = bal >= price;

        meta.displayName(Msg.parse(kit.displayName)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("  Содержимое кита:").color(TextColor.color(0xFFCC55)).decoration(TextDecoration.ITALIC, false));
        // Считаем содержимое
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ItemStack it : kit.items) {
            String label = describeKitItem(it);
            counts.merge(label, it.getAmount(), Integer::sum);
        }
        int shown = 0;
        for (Map.Entry<String, Integer> e2 : counts.entrySet()) {
            if (shown++ >= 8) { lore.add(Component.text("  …").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)); break; }
            lore.add(Component.text("   • " + e2.getKey()
                            + (e2.getValue() > 1 ? " ×" + e2.getValue() : ""))
                    .color(TextColor.color(0xCCCCCC)).decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.text(""));
        lore.add(Component.text("  Цена: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(price) + " ✦").color(TextColor.color(0xFF8000))
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text("  Баланс: ").color(TextColor.color(0x888888)).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(bal) + " ✦")
                        .color(can ? TextColor.color(0x55FF55) : TextColor.color(0xFF4444))
                        .decoration(TextDecoration.ITALIC, false)));
        lore.add(Component.text(""));
        lore.add(can
                ? Component.text("  ► Нажмите, чтобы купить").color(TextColor.color(0x55FF55)).decoration(TextDecoration.ITALIC, false)
                : Component.text("  ✗ Недостаточно Арисов").color(TextColor.color(0xFF4444)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("  §8(разовая покупка, без кулдауна /kit)").decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        // Метим pdc, чтобы handleKitClick знал id кита.
        meta.getPersistentDataContainer().set(plugin.getArisDonate().keyKitId(),
                PersistentDataType.STRING, kit.id);
        item.setItemMeta(meta);
        return item;
    }

    private String describeKitItem(ItemStack it) {
        if (it == null) return "?";
        ItemMeta im = it.getItemMeta();
        if (im != null && im.hasDisplayName()) {
            String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                    .plainText().serialize(im.displayName());
            if (!plain.isBlank()) return plain;
        }
        return prettyMaterial(it.getType());
    }

    private String prettyMaterial(Material m) {
        String n = m.name().toLowerCase().replace('_', ' ');
        if (n.isEmpty()) return m.name();
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }

    private Material iconForKit(String id) {
        if (id == null) return Material.CHEST;
        return switch (id.toLowerCase()) {
            case "spark"     -> Material.LIME_SHULKER_BOX;
            case "luna"      -> Material.LIGHT_BLUE_SHULKER_BOX;
            case "stellar"   -> Material.CYAN_SHULKER_BOX;
            case "nova"      -> Material.MAGENTA_SHULKER_BOX;
            case "comet"     -> Material.WHITE_SHULKER_BOX;
            case "galaxy"    -> Material.PURPLE_SHULKER_BOX;
            case "nebula"    -> Material.PINK_SHULKER_BOX;
            case "cosmos"    -> Material.BLUE_SHULKER_BOX;
            case "phoenix"   -> Material.RED_SHULKER_BOX;
            case "aris"      -> Material.ORANGE_SHULKER_BOX;
            case "arisplus"  -> Material.YELLOW_SHULKER_BOX;
            default          -> Material.SHULKER_BOX;
        };
    }

    private ItemStack sphereTabButton() {
        return tabButton(Material.MAGMA_CREAM, "§d✦ Сферы / Шары",
                plugin.hasArisDonate() ? "Сферы с бафами/дебафами"
                                       : "Требуется ArisDonate");
    }

    private ItemStack kitsTabButton() {
        return tabButton(Material.CHEST, "§e📦 Киты",
                plugin.hasArisDonate() ? "Купить кит (очень дорого!)"
                                       : "Требуется ArisDonate");
    }

    private ItemStack rgbItem(Material mat, String name, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text(""),
            Component.text("  " + desc).color(TextColor.color(0xAAAAAA)).decoration(TextDecoration.ITALIC, false),
            Component.text(""),
            Component.text("  (Декоративный ингредиент)").color(TextColor.color(0x555555)).decoration(TextDecoration.ITALIC, true)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack tabButton(Material mat, String name, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text(""),
            Component.text("  " + desc).color(TextColor.color(0x55FFFF)).decoration(TextDecoration.ITALIC, false),
            Component.text("")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack balanceDisplay(long bal) {
        ItemStack item = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("✦ Ваш баланс ✦").color(TextColor.color(0xFFDD00))
            .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text(""),
            Component.text("  ").decoration(TextDecoration.ITALIC, false)
                .append(Component.text(fmt(bal) + " Арисов ✦")
                    .color(TextColor.color(0xFF8000))
                    .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)),
            Component.text("")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private void fillBorder(Inventory inv) {
        ItemStack g = borderGlass();
        for (int i = 0; i < 9; i++)  inv.setItem(i, g);
        for (int i = 45; i < 54; i++) inv.setItem(i, g);
        for (int i = 9; i < 45; i += 9)  inv.setItem(i, g);
        for (int i = 17; i < 54; i += 9) inv.setItem(i, g);
    }

    private ItemStack borderGlass() {
        ItemStack g = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = g.getItemMeta();
        m.displayName(Component.text(" ").decoration(TextDecoration.ITALIC, false));
        g.setItemMeta(m);
        return g;
    }

    private String fmt(long n) { return String.format("%,d", n).replace(',', ' '); }
}
