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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

/**
 * Анимация крейта в мире (без GUI):
 * - Блоки-голограммы с названиями донатов крутятся вокруг сундука
 * - Постепенно замедляются
 * - Победный донат поднимается вверх с эффектами
 */
public class CrateAnimationGui {

    public static final String TITLE_PREFIX = "★ КРЕЙТ: ";

    private static final Material[] ORBIT_MATERIALS = {
            Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK,
            Material.EMERALD_BLOCK,
            Material.REDSTONE_BLOCK,
            Material.LAPIS_BLOCK,
            Material.AMETHYST_BLOCK,
            Material.IRON_BLOCK,
            Material.COPPER_BLOCK
    };

    private final ArisCratesPlugin plugin;

    public CrateAnimationGui(ArisCratesPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Запускает анимацию в мире вокруг сундука-крейта.
     */
    public void play(Player player, Crate crate, Location crateBlockLoc) {
        CrateReward finalReward = crate.roll();

        Location center = crateBlockLoc != null
                ? crateBlockLoc.clone().add(0.5, 1.5, 0.5)
                : player.getLocation().add(0, 1.5, 0);

        // Build list of rewards to show spinning (like a roulette)
        List<CrateReward> spin = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            spin.add(crate.roll());
        }
        spin.set(spin.size() - 1, finalReward);
        int rewardCount = spin.size();

        // Spawn orbit stands — each one shows a reward name hologram + block on head
        int visibleCount = Math.min(6, rewardCount);
        List<ArmorStand> blockStands = new ArrayList<>();
        List<ArmorStand> nameStands = new ArrayList<>();

        for (int i = 0; i < visibleCount; i++) {
            double angle = (2 * Math.PI * i / visibleCount);
            double radius = 2.2;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;

            // Block stand (invisible armor stand with block on head)
            Location bLoc = new Location(center.getWorld(), x, center.getY() - 1.0, z);
            ArmorStand blockAs = spawnStand(bLoc, false);
            blockAs.getEquipment().setHelmet(new ItemStack(ORBIT_MATERIALS[i % ORBIT_MATERIALS.length]));
            blockStands.add(blockAs);

            // Name hologram stand (above the block)
            Location nLoc = new Location(center.getWorld(), x, center.getY() + 0.3, z);
            ArmorStand nameAs = spawnStand(nLoc, true);
            CrateReward reward = spin.get(i % spin.size());
            nameAs.customName(Msg.parse(reward.display()));
            nameAs.setCustomNameVisible(true);
            nameStands.add(nameAs);
        }

        player.playSound(center, Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 0.8f);

        new BukkitRunnable() {
            int tick = 0;
            int spinIndex = 0;
            int delay = 1;
            int ticksSinceShift = 0;
            double currentAngleOffset = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cleanup(blockStands, nameStands);
                    giveReward(player, crate, finalReward);
                    cancel();
                    return;
                }

                tick++;
                double radius = 2.2;
                double speed = Math.max(0.05, 0.25 - tick * 0.004);
                currentAngleOffset += speed;

                // Rotate all stands around center
                for (int i = 0; i < visibleCount; i++) {
                    double angle = currentAngleOffset + (2 * Math.PI * i / visibleCount);
                    double x = center.getX() + Math.cos(angle) * radius;
                    double z = center.getZ() + Math.sin(angle) * radius;
                    double yBounce = Math.sin(tick * 0.15 + i) * 0.15;

                    ArmorStand bs = blockStands.get(i);
                    ArmorStand ns = nameStands.get(i);
                    if (bs.isValid()) {
                        bs.teleport(new Location(center.getWorld(), x, center.getY() - 1.0 + yBounce, z));
                        bs.setHeadPose(new EulerAngle(tick * 0.08, tick * 0.12, 0));
                    }
                    if (ns.isValid()) {
                        ns.teleport(new Location(center.getWorld(), x, center.getY() + 0.3 + yBounce, z));
                    }
                }

                // Particles
                spawnParticles(center, tick);

                // Shift reward names (roulette effect)
                ticksSinceShift++;
                if (ticksSinceShift >= delay) {
                    ticksSinceShift = 0;
                    spinIndex++;

                    for (int i = 0; i < visibleCount; i++) {
                        int idx = (spinIndex + i) % spin.size();
                        CrateReward r = spin.get(idx);
                        ArmorStand ns = nameStands.get(i);
                        if (ns.isValid()) {
                            ns.customName(Msg.parse(r.display()));
                        }
                        // Change block material based on rarity
                        ArmorStand bs = blockStands.get(i);
                        if (bs.isValid()) {
                            bs.getEquipment().setHelmet(new ItemStack(getBlockForRarity(r.rarity(), i)));
                        }
                    }

                    float pitch = 1.0f + (tick * 0.015f);
                    if (pitch > 2.0f) pitch = 2.0f;
                    player.playSound(center, Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f, pitch);

                    // Slow down
                    if (tick > 30) delay = 2;
                    if (tick > 45) delay = 3;
                    if (tick > 55) delay = 5;
                    if (tick > 62) delay = 8;
                    if (tick > 68) delay = 15;

                    // Stop when we've cycled enough and the final reward is displayed
                    if (tick > 75) {
                        showWin(player, crate, finalReward, center, blockStands, nameStands);
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private void showWin(Player player, Crate crate, CrateReward reward,
                         Location center, List<ArmorStand> blockStands, List<ArmorStand> nameStands) {
        // Remove all except one — the winner
        for (int i = 1; i < blockStands.size(); i++) {
            ArmorStand bs = blockStands.get(i);
            ArmorStand ns = nameStands.get(i);
            if (bs.isValid()) bs.remove();
            if (ns.isValid()) ns.remove();
        }

        ArmorStand winBlock = blockStands.get(0);
        ArmorStand winName = nameStands.get(0);

        // Set winner display
        if (winName.isValid()) {
            winName.customName(Msg.parse("&6&l★ " + reward.display() + " &6&l★"));
        }
        if (winBlock.isValid()) {
            winBlock.getEquipment().setHelmet(new ItemStack(Material.NETHER_STAR));
            winBlock.teleport(new Location(center.getWorld(), center.getX(), center.getY() - 1.0, center.getZ()));
        }
        if (winName.isValid()) {
            winName.teleport(new Location(center.getWorld(), center.getX(), center.getY() + 0.3, center.getZ()));
        }

        // Effects
        player.playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(center, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        center.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, center, 80, 0.5, 0.5, 0.5, 0.3);
        center.getWorld().spawnParticle(Particle.FIREWORK, center, 50, 1, 1, 1, 0.1);

        // Winner rises up + fades
        new BukkitRunnable() {
            int t = 0;
            @Override
            public void run() {
                t++;
                if (winBlock.isValid()) {
                    Location cur = winBlock.getLocation();
                    winBlock.teleport(cur.add(0, 0.08, 0));
                }
                if (winName.isValid()) {
                    Location cur = winName.getLocation();
                    winName.teleport(cur.add(0, 0.08, 0));
                }

                if (t % 3 == 0) {
                    player.playSound(center, Sound.BLOCK_NOTE_BLOCK_BELL, 0.3f, 1.0f + t * 0.05f);
                    center.getWorld().spawnParticle(Particle.END_ROD, center.clone().add(0, t * 0.08, 0), 3, 0.3, 0.1, 0.3, 0.02);
                }

                if (t >= 25) {
                    if (winBlock.isValid()) winBlock.remove();
                    if (winName.isValid()) winName.remove();
                    giveReward(player, crate, reward);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 4L, 2L);
    }

    private ArmorStand spawnStand(Location loc, boolean nameOnly) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(true);
        as.setSmall(true);
        as.setInvulnerable(true);
        as.setPersistent(false);
        return as;
    }

    private void cleanup(List<ArmorStand> blocks, List<ArmorStand> names) {
        for (ArmorStand as : blocks) if (as.isValid()) as.remove();
        for (ArmorStand as : names) if (as.isValid()) as.remove();
    }

    private Material getBlockForRarity(String rarity, int idx) {
        return switch (rarity.toLowerCase()) {
            case "common" -> Material.IRON_BLOCK;
            case "uncommon" -> Material.COPPER_BLOCK;
            case "rare" -> Material.DIAMOND_BLOCK;
            case "epic" -> Material.AMETHYST_BLOCK;
            case "legendary" -> Material.GOLD_BLOCK;
            case "mythic" -> Material.EMERALD_BLOCK;
            case "donate", "донат" -> Material.NETHER_STAR;
            default -> ORBIT_MATERIALS[idx % ORBIT_MATERIALS.length];
        };
    }

    private void spawnParticles(Location center, int tick) {
        double radius = 1.8;
        int points = 8;
        double angle = Math.toRadians(tick * 12);

        for (int i = 0; i < points; i++) {
            double a = angle + (2 * Math.PI * i / points);
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            Location pLoc = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnParticle(Particle.END_ROD, pLoc, 1, 0, 0, 0, 0);
        }

        if (tick % 3 == 0) {
            double hAngle = Math.toRadians(tick * 25);
            double hx = center.getX() + Math.cos(hAngle) * 1.0;
            double hz = center.getZ() + Math.sin(hAngle) * 1.0;
            center.getWorld().spawnParticle(Particle.WITCH,
                    new Location(center.getWorld(), hx, center.getY() + (tick % 30) * 0.04, hz), 2, 0, 0, 0, 0);
        }

        if (tick % 5 == 0) {
            for (int i = 0; i < 12; i++) {
                double ga = (2 * Math.PI * i / 12);
                double gx = center.getX() + Math.cos(ga) * 2.5;
                double gz = center.getZ() + Math.sin(ga) * 2.5;
                center.getWorld().spawnParticle(Particle.ENCHANT,
                        new Location(center.getWorld(), gx, center.getY() - 1.0, gz), 1, 0, 0.2, 0, 0);
            }
        }
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
                plugin.getLogger().warning("DonateIntegration недоступна!");
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
