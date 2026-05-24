package me.ariscrates.gui;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import me.ariscrates.models.CrateReward;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

/**
 * Анимация крейта: рулетка в GUI + крутящиеся блоки вокруг сундука + частицы.
 */
public class CrateAnimationGui {

    public static final String TITLE_PREFIX = "★ КРЕЙТ: ";

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

    private static final int[] BORDER_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            17, 26, 25, 24, 23, 22, 21, 20, 19, 18,
            9
    };

    private static final Material[] ORBIT_BLOCKS = {
            Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK,
            Material.EMERALD_BLOCK,
            Material.REDSTONE_BLOCK,
            Material.LAPIS_BLOCK,
            Material.AMETHYST_BLOCK
    };

    private final ArisCratesPlugin plugin;

    public CrateAnimationGui(ArisCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public void play(Player player, Crate crate, Location crateBlockLoc) {
        CrateReward finalReward = crate.roll();

        Inventory inv = Bukkit.createInventory(null, 27,
                Msg.parse("&6&l" + TITLE_PREFIX + crate.displayName()));

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.displayName(Component.text(" "));
        glass.setItemMeta(gm);
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        ItemStack pointer = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta pm = pointer.getItemMeta();
        pm.displayName(Msg.parse("&a&l▼ &e&lНАГРАДА &a&l▼"));
        pointer.setItemMeta(pm);
        inv.setItem(4, pointer);
        inv.setItem(22, pointer);

        player.openInventory(inv);

        List<CrateReward> strip = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            strip.add(crate.roll());
        }
        strip.set(strip.size() - 5, finalReward);

        // Spawn orbiting ArmorStands with blocks on heads
        Location center = crateBlockLoc != null
                ? crateBlockLoc.clone().add(0.5, 0.5, 0.5)
                : player.getLocation().add(0, 1, 0);
        List<ArmorStand> orbitStands = spawnOrbitStands(center);

        new BukkitRunnable() {
            int tick = 0;
            int offset = 0;
            int delay = 1;
            int ticksSinceShift = 0;
            int borderOffset = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.getOpenInventory().getTopInventory() != inv) {
                    removeOrbitStands(orbitStands);
                    giveReward(player, crate, finalReward);
                    cancel();
                    return;
                }

                // ═══ Spinning border in GUI ═══
                borderOffset++;
                for (int i = 0; i < BORDER_SLOTS.length; i++) {
                    int colorIdx = (i + borderOffset) % BORDER_COLORS.length;
                    ItemStack borderGlass = new ItemStack(BORDER_COLORS[colorIdx]);
                    ItemMeta bm = borderGlass.getItemMeta();
                    bm.displayName(Component.text(" "));
                    borderGlass.setItemMeta(bm);
                    inv.setItem(BORDER_SLOTS[i], borderGlass);
                }
                inv.setItem(4, pointer);
                inv.setItem(22, pointer);

                // ═══ Orbit blocks spin around chest ═══
                double angle = Math.toRadians(tick * 12);
                double radius = 1.8;
                double yOffset = Math.sin(tick * 0.15) * 0.4;
                for (int i = 0; i < orbitStands.size(); i++) {
                    ArmorStand as = orbitStands.get(i);
                    if (!as.isValid()) continue;
                    double a = angle + (2 * Math.PI * i / orbitStands.size());
                    double x = center.getX() + Math.cos(a) * radius;
                    double z = center.getZ() + Math.sin(a) * radius;
                    double y = center.getY() + yOffset + 0.5;
                    as.teleport(new Location(center.getWorld(), x, y, z));
                    as.setHeadPose(new EulerAngle(tick * 0.1, tick * 0.2, 0));
                }

                // ═══ Particles around chest ═══
                spawnParticles(center, tick);

                ticksSinceShift++;
                if (ticksSinceShift >= delay) {
                    ticksSinceShift = 0;
                    offset++;

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

                    float pitch = 1.0f + (tick * 0.02f);
                    if (pitch > 2.0f) pitch = 2.0f;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f, pitch);

                    tick++;
                    if (tick > 18) delay = 2;
                    if (tick > 25) delay = 3;
                    if (tick > 30) delay = 5;
                    if (tick > 34) delay = 8;
                    if (tick > 37) delay = 12;

                    int centerIdx = offset + 3;
                    if (centerIdx >= strip.size() - 5) {
                        showWin(player, inv, crate, finalReward, center, orbitStands);
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private List<ArmorStand> spawnOrbitStands(Location center) {
        List<ArmorStand> stands = new ArrayList<>();
        for (int i = 0; i < ORBIT_BLOCKS.length; i++) {
            double a = (2 * Math.PI * i / ORBIT_BLOCKS.length);
            double x = center.getX() + Math.cos(a) * 1.8;
            double z = center.getZ() + Math.sin(a) * 1.8;
            Location loc = new Location(center.getWorld(), x, center.getY() + 0.5, z);
            ArmorStand as = (ArmorStand) center.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setGravity(false);
            as.setMarker(true);
            as.setSmall(true);
            as.setInvulnerable(true);
            as.setPersistent(false);
            as.getEquipment().setHelmet(new ItemStack(ORBIT_BLOCKS[i]));
            stands.add(as);
        }
        return stands;
    }

    private void removeOrbitStands(List<ArmorStand> stands) {
        for (ArmorStand as : stands) {
            if (as.isValid()) as.remove();
        }
    }

    private void spawnParticles(Location center, int tick) {
        double radius = 1.5;
        int points = 8;
        double angle = Math.toRadians(tick * 15);

        for (int i = 0; i < points; i++) {
            double a = angle + (2 * Math.PI * i / points);
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            double y = center.getY() + Math.sin(tick * 0.2 + i) * 0.3;
            Location pLoc = new Location(center.getWorld(), x, y, z);
            center.getWorld().spawnParticle(Particle.END_ROD, pLoc, 1, 0, 0, 0, 0);
            if (tick % 4 == 0) {
                center.getWorld().spawnParticle(Particle.FLAME, pLoc, 1, 0.05, 0.05, 0.05, 0);
            }
        }

        double helixAngle = Math.toRadians(tick * 30);
        double hx = center.getX() + Math.cos(helixAngle) * 0.8;
        double hz = center.getZ() + Math.sin(helixAngle) * 0.8;
        double hy = center.getY() + (tick % 40) * 0.05;
        center.getWorld().spawnParticle(Particle.WITCH, new Location(center.getWorld(), hx, hy, hz), 2, 0, 0, 0, 0);

        if (tick % 3 == 0) {
            for (int i = 0; i < 16; i++) {
                double ga = (2 * Math.PI * i / 16);
                double gx = center.getX() + Math.cos(ga) * 2.0;
                double gz = center.getZ() + Math.sin(ga) * 2.0;
                center.getWorld().spawnParticle(Particle.ENCHANT,
                        new Location(center.getWorld(), gx, center.getY() - 0.5, gz), 1, 0, 0.2, 0, 0);
            }
        }
    }

    private void showWin(Player player, Inventory inv, Crate crate, CrateReward reward,
                         Location center, List<ArmorStand> orbitStands) {
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
        if (reward.isDonateReward()) {
            lore.add(Component.empty());
            lore.add(Msg.parse("&c&l✦ ДОНАТ: " + reward.display() + " ✦"));
        }
        im.lore(lore);
        finalItem.setItemMeta(im);
        inv.setItem(13, finalItem);

        for (int slot : BORDER_SLOTS) {
            ItemStack gold = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemMeta gim = gold.getItemMeta();
            gim.displayName(Msg.parse("&6&l★"));
            gold.setItemMeta(gim);
            inv.setItem(slot, gold);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);

        center.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, center, 80, 0.5, 0.5, 0.5, 0.3);
        center.getWorld().spawnParticle(Particle.FIREWORK, center, 50, 1, 1, 1, 0.1);

        new BukkitRunnable() {
            int flash = 0;
            @Override
            public void run() {
                if (!player.isOnline()) {
                    removeOrbitStands(orbitStands);
                    giveReward(player, crate, reward);
                    cancel();
                    return;
                }
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

                // Orbit blocks rise up during flash
                for (ArmorStand as : orbitStands) {
                    if (as.isValid()) {
                        Location cur = as.getLocation();
                        as.teleport(cur.add(0, 0.15, 0));
                    }
                }

                if (flash >= 6) {
                    removeOrbitStands(orbitStands);
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
            var di = plugin.getDonateIntegration();
            if (di != null && di.isAvailable()) {
                String rankName = di.getRankGradientName(reward.donateRankId());
                boolean given = di.giveRankIfHigher(player, reward.donateRankId());
                if (given) {
                    player.sendMessage(Msg.parse("&a&l✓ &aВам выдан донат: " + rankName
                            + " &7(" + rarityColor + rarityName + "&7) &aиз " + crate.displayName()));
                } else {
                    player.sendMessage(Msg.parse("&e&l! &eВы выиграли " + rankName
                            + "&e, но у вас уже есть донат выше! Ранг не изменён."));
                }
            } else {
                plugin.getLogger().warning("DonateIntegration недоступна! di=" + di
                        + " available=" + (di != null ? di.isAvailable() : "null"));
                player.sendMessage(Msg.parse("&cОшибка: ArisDonate не доступен. Обратитесь к администрации."));
            }
        } else {
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
