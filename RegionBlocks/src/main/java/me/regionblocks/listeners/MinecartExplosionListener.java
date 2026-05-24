package me.regionblocks.listeners;

import me.regionblocks.RegionBlocks;
import me.regionblocks.managers.MinecartItemManager;
import me.regionblocks.models.MinecartType;
import me.regionblocks.models.Region;
import me.regionblocks.models.RegionTier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * BASIC / STRONG / MEGA:
 *   - ломают все обычные блоки в радиусе
 *   - удаляют регионы в радиусе (кроме Арис-региона)
 *   - НЕ ломают Арис-блок, НЕ удаляют Арис-регион
 *
 * TITAN:
 *   - ломает обсидиан, плачущий обсидиан, Арис-блок и обычные блоки
 *   - удаляет ВСЕ регионы включая Арис
 *
 * Ванильная вагонетка:
 *   - стандартный список Bukkit, убираем только Арис-блок
 *   - регионы НЕ удаляет
 */
public class MinecartExplosionListener implements Listener {

    private final RegionBlocks plugin;

    public MinecartExplosionListener(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock() == null) return;
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        ItemStack hand = e.getItem();
        MinecartType type = MinecartItemManager.getMinecartType(hand);
        if (type == null) return;

        Material mat = e.getClickedBlock().getType();
        if (mat != Material.RAIL && mat != Material.POWERED_RAIL
            && mat != Material.DETECTOR_RAIL && mat != Material.ACTIVATOR_RAIL) return;

        e.setCancelled(true);
        hand.setAmount(hand.getAmount() - 1);

        Location loc = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
        ExplosiveMinecart cart = (ExplosiveMinecart) loc.getWorld().spawnEntity(loc, EntityType.TNT_MINECART);
        cart.setYield(type.getRadius());
        cart.getPersistentDataContainer()
            .set(MinecartItemManager.getKey(), PersistentDataType.STRING, type.name());

        e.getPlayer().sendMessage(
            Component.text("🚃 ").color(TextColor.color(0xFF8800))
            .append(Component.text(type.getDisplayName()).decoration(TextDecoration.BOLD, true))
            .append(Component.text(" размещена! Направьте на цель.")
                .color(TextColor.color(0xFFDDAA)).decoration(TextDecoration.BOLD, false))
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof ExplosiveMinecart cart)) return;

        World world = e.getLocation().getWorld();

        String typeName = cart.getPersistentDataContainer()
            .get(MinecartItemManager.getKey(), PersistentDataType.STRING);

        MinecartType type = null;
        if (typeName != null) {
            try { type = MinecartType.valueOf(typeName); }
            catch (IllegalArgumentException ignored) {}
        }

        boolean isTitan = type == MinecartType.TITAN;

        if (type != null) {
            Location center = e.getLocation();
            int radius = type.getRadius();
            e.blockList().clear();

            List<Block> toBreak = new ArrayList<>();
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (dx*dx + dy*dy + dz*dz > radius*radius) continue;

                        Block block = world.getBlockAt(
                            center.getBlockX() + dx,
                            center.getBlockY() + dy,
                            center.getBlockZ() + dz
                        );

                        if (block.getType() == Material.AIR) continue;
                        if (block.getType() == Material.BEDROCK) continue;

                        if (isTitan) {
                            // Titan: ломает всё включая обсидиан и Арис
                        } else {
                            // Остальные: всё кроме Арис-блока
                            if (isArisMarkerBlock(block)) continue;
                        }

                        toBreak.add(block);
                    }
                }
            }
            e.blockList().addAll(toBreak);

            // Все кастомные вагонетки удаляют регионы, Titan — включая Арис
            destroyRegionsInRadius(center, radius, world, isTitan);

        } else {
            // Ванильная вагонетка — убираем только Арис-блок
            e.blockList().removeIf(this::isArisMarkerBlock);
        }
    }

    private boolean isArisMarkerBlock(Block block) {
        if (block.getType() != Material.RED_MUSHROOM_BLOCK) return false;
        Location loc = block.getLocation();
        for (Region region : plugin.getRegionManager().getAllRegions()) {
            if (region.getTier() != RegionTier.ARIS) continue;
            Location c = region.getCenter();
            if (c.getWorld().equals(loc.getWorld())
                && c.getBlockX() == loc.getBlockX()
                && c.getBlockY() == loc.getBlockY()
                && c.getBlockZ() == loc.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    private void destroyRegionsInRadius(Location center, int radius, World world, boolean includeAris) {
        List<String> toDelete = new ArrayList<>();

        for (Region region : plugin.getRegionManager().getAllRegions()) {
            if (!region.getCenter().getWorld().equals(world)) continue;
            if (region.getTier() == RegionTier.ARIS && !includeAris) continue;

            double dist = center.distance(region.getCenter());
            int half = region.getTier().getSize() / 2 + 1;
            if (dist <= radius + half) {
                toDelete.add(region.getName());
            }
        }

        for (String name : toDelete) {
            plugin.getRegionManager().removeRegion(name);
            world.getPlayers().forEach(p ->
                p.sendMessage(
                    Component.text("💥 Регион ").color(TextColor.color(0xFF6600))
                    .append(Component.text(name).color(TextColor.color(0xFFFFFF))
                        .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(" уничтожен взрывом вагонетки!")
                        .color(TextColor.color(0xFF6600)))
                )
            );
        }
    }
}
