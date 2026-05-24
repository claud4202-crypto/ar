package me.ariscrates.gui;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import me.ariscrates.models.CrateReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Анимация рулетки: предметы прокручиваются в ряду, замедляясь.
 */
public class CrateAnimationGui {

    public static final String TITLE_PREFIX = "★ КРЕЙТ: ";

    private final ArisCratesPlugin plugin;

    public CrateAnimationGui(ArisCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public void play(Player player, Crate crate) {
        CrateReward finalReward = crate.roll();

        Inventory inv = Bukkit.createInventory(null, 27,
                Msg.parse("<grad:#FFD700:#FF4500>" + TITLE_PREFIX + "</grad>" + Msg.parse(crate.displayName())));

        // Fill border
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        gm.displayName(Component.text(" "));
        glass.setItemMeta(gm);
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        // Pointer
        ItemStack pointer = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta pm = pointer.getItemMeta();
        pm.displayName(Msg.parse("&a▼ &eНаграда &a▼"));
        pointer.setItemMeta(pm);
        inv.setItem(4, pointer);
        inv.setItem(22, pointer);

        player.openInventory(inv);

        // Build reward strip (slots 9-17)
        List<CrateReward> strip = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            strip.add(crate.roll());
        }
        // Place final reward at winning position
        strip.set(strip.size() - 5, finalReward);

        new BukkitRunnable() {
            int tick = 0;
            int offset = 0;
            int delay = 1;
            int ticksSinceShift = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.getOpenInventory().getTopInventory() != inv) {
                    giveReward(player, crate, finalReward);
                    cancel();
                    return;
                }

                ticksSinceShift++;
                if (ticksSinceShift >= delay) {
                    ticksSinceShift = 0;
                    offset++;

                    // Update displayed items
                    for (int slot = 0; slot < 9; slot++) {
                        int idx = offset + slot;
                        if (idx < strip.size()) {
                            CrateReward r = strip.get(idx);
                            ItemStack display = r.item().clone();
                            ItemMeta im = display.getItemMeta();
                            im.displayName(Msg.parse(r.display()));
                            display.setItemMeta(im);
                            inv.setItem(9 + slot, display);
                        }
                    }

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f);

                    // Slow down
                    tick++;
                    if (tick > 15) delay = 2;
                    if (tick > 22) delay = 3;
                    if (tick > 28) delay = 5;
                    if (tick > 32) delay = 8;

                    // Check if final reward is in center (slot 13 = index 4 in row)
                    int centerIdx = offset + 4;
                    if (centerIdx >= strip.size() - 5) {
                        // Show final reward
                        ItemStack finalItem = finalReward.item().clone();
                        ItemMeta im = finalItem.getItemMeta();
                        im.displayName(Msg.parse("&6&l★ " + finalReward.display() + " &6&l★"));
                        List<Component> lore = new ArrayList<>();
                        lore.add(Msg.parse("&aПоздравляем!"));
                        im.lore(lore);
                        finalItem.setItemMeta(im);
                        inv.setItem(13, finalItem);

                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                        // Give reward after short delay
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            giveReward(player, crate, finalReward);
                            player.closeInventory();
                        }, 40L);

                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private void giveReward(Player player, Crate crate, CrateReward reward) {
        ItemStack item = reward.item().clone();
        var leftover = player.getInventory().addItem(item);
        for (ItemStack drop : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }
        player.sendMessage(Msg.parse("&aВы выиграли: " + reward.display() + " &aиз крейта " + crate.displayName()));
        if (crate.broadcastWin()) {
            Component bc = Msg.parse("&6[Крейт] &e" + player.getName() + " &7выиграл " + reward.display()
                    + " &7из " + crate.displayName() + "&7!");
            Bukkit.broadcast(bc);
        }
    }
}
