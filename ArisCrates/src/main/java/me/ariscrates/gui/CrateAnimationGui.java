package me.ariscrates.gui;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import me.ariscrates.models.CrateReward;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Анимация крейта с частицами и визуальными эффектами.
 * Предметы крутятся в ряду, замедляясь. Одновременно частицы вокруг игрока.
 */
public class CrateAnimationGui {

    public static final String TITLE_PREFIX = "★ КРЕЙТ: ";

    // Цветные стёкла для "крутящейся" рамки
    private static final Material[] BORDER_COLORS = {
            Material.RED_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE
    };

    // Позиции рамки (слоты по периметру 27-слотового инв)
    private static final int[] BORDER_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            17, 26, 25, 24, 23, 22, 21, 20, 19, 18,
            9
    };

    private final ArisCratesPlugin plugin;

    public CrateAnimationGui(ArisCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public void play(Player player, Crate crate) {
        CrateReward finalReward = crate.roll();

        Inventory inv = Bukkit.createInventory(null, 27,
                Msg.parse("&6&l" + TITLE_PREFIX + crate.displayName()));

        // Initial border
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.displayName(Component.text(" "));
        glass.setItemMeta(gm);
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        // Pointer arrows
        ItemStack pointer = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta pm = pointer.getItemMeta();
        pm.displayName(Msg.parse("&a&l▼ &e&lНАГРАДА &a&l▼"));
        pointer.setItemMeta(pm);
        inv.setItem(4, pointer);
        inv.setItem(22, pointer);

        player.openInventory(inv);

        // Build reward strip
        List<CrateReward> strip = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            strip.add(crate.roll());
        }
        strip.set(strip.size() - 5, finalReward);

        Location loc = player.getLocation();

        new BukkitRunnable() {
            int tick = 0;
            int offset = 0;
            int delay = 1;
            int ticksSinceShift = 0;
            int borderOffset = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.getOpenInventory().getTopInventory() != inv) {
                    giveReward(player, crate, finalReward);
                    cancel();
                    return;
                }

                // ═══ Spinning border animation ═══
                borderOffset++;
                for (int i = 0; i < BORDER_SLOTS.length; i++) {
                    int colorIdx = (i + borderOffset) % BORDER_COLORS.length;
                    ItemStack borderGlass = new ItemStack(BORDER_COLORS[colorIdx]);
                    ItemMeta bm = borderGlass.getItemMeta();
                    bm.displayName(Component.text(" "));
                    borderGlass.setItemMeta(bm);
                    inv.setItem(BORDER_SLOTS[i], borderGlass);
                }
                // Re-set pointers (they're in border area)
                inv.setItem(4, pointer);
                inv.setItem(22, pointer);

                // ═══ Particle effects around player ═══
                spawnParticles(player, tick);

                ticksSinceShift++;
                if (ticksSinceShift >= delay) {
                    ticksSinceShift = 0;
                    offset++;

                    // Update roulette row (slots 10-16)
                    for (int slot = 0; slot < 7; slot++) {
                        int idx = offset + slot;
                        if (idx < strip.size()) {
                            CrateReward r = strip.get(idx);
                            ItemStack display = r.item().clone();
                            ItemMeta im = display.getItemMeta();
                            String rarityColor = CrateOpenGui.getRarityColor(r.rarity());
                            String rarityName = CrateOpenGui.getRarityName(r.rarity());
                            im.displayName(Msg.parse(rarityColor + r.display()));
                            List<Component> lore = new ArrayList<>();
                            lore.add(Msg.parse("&7Редкость: " + rarityColor + rarityName));
                            lore.add(Msg.parse(rarityColor + CrateOpenGui.getRaritySymbols(r.rarity())));
                            im.lore(lore);
                            display.setItemMeta(im);
                            inv.setItem(10 + slot, display);
                        }
                    }

                    // Sound tick
                    float pitch = 1.0f + (tick * 0.02f);
                    if (pitch > 2.0f) pitch = 2.0f;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f, pitch);

                    // Slow down
                    tick++;
                    if (tick > 18) delay = 2;
                    if (tick > 25) delay = 3;
                    if (tick > 30) delay = 5;
                    if (tick > 34) delay = 8;
                    if (tick > 37) delay = 12;

                    // Check if done
                    int centerIdx = offset + 3;
                    if (centerIdx >= strip.size() - 5) {
                        showWin(player, inv, crate, finalReward);
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private void spawnParticles(Player player, int tick) {
        Location center = player.getLocation().add(0, 1, 0);
        double radius = 1.5;
        int points = 8;
        double angle = Math.toRadians(tick * 15);

        for (int i = 0; i < points; i++) {
            double a = angle + (2 * Math.PI * i / points);
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            double y = center.getY() + Math.sin(tick * 0.2 + i) * 0.3;
            Location pLoc = new Location(center.getWorld(), x, y, z);

            // Spiral particles
            player.getWorld().spawnParticle(Particle.END_ROD, pLoc, 1, 0, 0, 0, 0);

            // Color based on tick
            if (tick % 4 == 0) {
                player.getWorld().spawnParticle(Particle.FLAME, pLoc, 1, 0.05, 0.05, 0.05, 0);
            }
        }

        // Helix going up
        double helixAngle = Math.toRadians(tick * 30);
        double hx = center.getX() + Math.cos(helixAngle) * 0.8;
        double hz = center.getZ() + Math.sin(helixAngle) * 0.8;
        double hy = center.getY() + (tick % 40) * 0.05;
        Location helixLoc = new Location(center.getWorld(), hx, hy, hz);
        player.getWorld().spawnParticle(Particle.WITCH, helixLoc, 2, 0, 0, 0, 0);

        // Ground ring
        if (tick % 3 == 0) {
            for (int i = 0; i < 16; i++) {
                double ga = (2 * Math.PI * i / 16);
                double gx = center.getX() + Math.cos(ga) * 2.0;
                double gz = center.getZ() + Math.sin(ga) * 2.0;
                Location gLoc = new Location(center.getWorld(), gx, center.getY() - 1, gz);
                player.getWorld().spawnParticle(Particle.ENCHANT, gLoc, 1, 0, 0.2, 0, 0);
            }
        }
    }

    private void showWin(Player player, Inventory inv, Crate crate, CrateReward reward) {
        // Final reward in center
        ItemStack finalItem = reward.item().clone();
        ItemMeta im = finalItem.getItemMeta();
        String rarityColor = CrateOpenGui.getRarityColor(reward.rarity());
        String rarityName = CrateOpenGui.getRarityName(reward.rarity());
        im.displayName(Msg.parse("&6&l★ " + rarityColor + reward.display() + " &6&l★"));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Msg.parse("&aПоздравляем!"));
        lore.add(Msg.parse("&7Редкость: " + rarityColor + "&l" + rarityName));
        lore.add(Msg.parse(rarityColor + CrateOpenGui.getRaritySymbols(reward.rarity())));
        if (reward.rarity().equalsIgnoreCase("donate") || reward.rarity().equalsIgnoreCase("донат")) {
            lore.add(Component.empty());
            lore.add(Msg.parse("&c&l✦ ДОНАТНАЯ НАГРАДА ✦"));
        }
        im.lore(lore);
        finalItem.setItemMeta(im);
        inv.setItem(13, finalItem);

        // Make border flash gold
        for (int slot : BORDER_SLOTS) {
            ItemStack gold = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemMeta gm = gold.getItemMeta();
            gm.displayName(Msg.parse("&6&l★"));
            gold.setItemMeta(gm);
            inv.setItem(slot, gold);
        }

        // Sound + particles burst
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);

        Location center = player.getLocation().add(0, 1.5, 0);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, center, 80, 0.5, 0.5, 0.5, 0.3);
        player.getWorld().spawnParticle(Particle.FIREWORK, center, 50, 1, 1, 1, 0.1);

        // Flash border animation then give reward
        new BukkitRunnable() {
            int flash = 0;
            @Override
            public void run() {
                if (!player.isOnline()) { giveReward(player, crate, reward); cancel(); return; }
                flash++;
                Material mat = flash % 2 == 0 ? Material.YELLOW_STAINED_GLASS_PANE : Material.ORANGE_STAINED_GLASS_PANE;
                for (int slot : BORDER_SLOTS) {
                    ItemStack g = new ItemStack(mat);
                    ItemMeta gim = g.getItemMeta();
                    gim.displayName(Msg.parse("&6&l★"));
                    g.setItemMeta(gim);
                    inv.setItem(slot, g);
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.4f, 1.0f + flash * 0.1f);

                if (flash >= 6) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        giveReward(player, crate, reward);
                        player.closeInventory();
                    }, 20L);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 4L, 4L);
    }

    private void giveReward(Player player, Crate crate, CrateReward reward) {
        String rarityColor = CrateOpenGui.getRarityColor(reward.rarity());
        String rarityName = CrateOpenGui.getRarityName(reward.rarity());

        if (reward.isDonateReward()) {
            // Donate reward — give rank via ArisDonate
            var di = plugin.getDonateIntegration();
            if (di != null && di.isAvailable()) {
                String rankName = di.getRankGradientName(reward.donateRankId());
                boolean given = di.giveRankIfHigher(player, reward.donateRankId());
                if (given) {
                    player.sendMessage(Msg.parse("&a&l✓ &aВы выиграли донат: " + rankName
                            + " &7(" + rarityColor + rarityName + "&7) &aиз " + crate.displayName()));
                } else {
                    player.sendMessage(Msg.parse("&e&l! &eВы выиграли " + rankName
                            + "&e, но у вас уже есть донат выше! Ранг не изменён."));
                }
            } else {
                player.sendMessage(Msg.parse("&cОшибка: ArisDonate не доступен. Обратитесь к администрации."));
            }
        } else {
            // Item reward
            ItemStack item = reward.item().clone();
            var leftover = player.getInventory().addItem(item);
            for (ItemStack drop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
            player.sendMessage(Msg.parse("&a&l✓ &aВы выиграли: " + rarityColor + reward.display()
                    + " &7(" + rarityColor + rarityName + "&7) &aиз " + crate.displayName()));
        }

        if (crate.broadcastWin()) {
            Component bc = Msg.parse("&6&l[Крейт] &e" + player.getName() + " &7выиграл "
                    + rarityColor + reward.display() + " &7(" + rarityColor + rarityName + "&7) из " + crate.displayName() + "&7!");
            Bukkit.broadcast(bc);
        }
    }
}
