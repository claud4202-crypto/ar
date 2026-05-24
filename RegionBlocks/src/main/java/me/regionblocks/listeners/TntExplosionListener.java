package me.regionblocks.listeners;

import me.regionblocks.RegionBlocks;
import me.regionblocks.managers.TntItemManager;
import me.regionblocks.models.Region;
import me.regionblocks.models.RegionTier;
import me.regionblocks.models.TntType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
 *   - ломает обсидиан, плачущий обсидиан, Арис-блок
 *   - удаляет ВСЕ регионы включая Арис
 *   - обычные блоки НЕ ломает (только спец-блоки)
 *
 * Ванильное ТНТ:
 *   - стандартный список Bukkit, убираем только Арис-блок
 *   - регионы НЕ удаляет
 */
public class TntExplosionListener implements Listener {

    private final RegionBlocks plugin;

    public TntExplosionListener(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack hand = e.getItemInHand();
        TntType type = TntItemManager.getTntType(hand);
        if (type == null) return;

        e.setCancelled(true);
        hand.setAmount(hand.getAmount() - 1);

        Location loc = e.getBlockPlaced().getLocation().add(0.5, 0, 0.5);
        TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.TNT);
        tnt.setFuseTicks(80);
        tnt.setYield(type.getPower());
        tnt.setIsIncendiary(false);
        tnt.getPersistentDataContainer()
            .set(TntItemManager.getKey(), PersistentDataType.STRING, type.name());

        e.getPlayer().sendMessage(
            Component.text("💣 ").color(TextColor.color(0xFF4444))
            .append(Component.text(type.getDisplayName()).decoration(TextDecoration.BOLD, true))
            .append(Component.text(" установлено! Детонация через 4 сек...")
                .color(TextColor.color(0xFFAAAA)).decoration(TextDecoration.BOLD, false))
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof TNTPrimed tnt)) return;

        World world = e.getLocation().getWorld();

        String typeName = tnt.getPersistentDataContainer()
            .get(TntItemManager.getKey(), PersistentDataType.STRING);

        TntType type = null;
        if (typeName != null) {
            try { type = TntType.valueOf(typeName); }
            catch (IllegalArgumentException ignored) {}
        }

        boolean isTitan = type == TntType.TITAN;

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
                            // Titan: ТОЛЬКО обсидиан, плачущий обсидиан и Арис-блок
                            if (!isObsidian(block) && !isArisMarkerBlock(block)) continue;
                        } else {
                            // Остальные: всё кроме Арис-блока
                            if (isArisMarkerBlock(block)) continue;
                        }

                        toBreak.add(block);
                    }
                }
            }
            e.blockList().addAll(toBreak);

            // Все кастомные ТНТ удаляют регионы, Titan — включая Арис
            destroyRegionsInRadius(center, radius, world, isTitan);

        } else {
            // Ванильное ТНТ — убираем только Арис-блок, регионы не трогаем
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

    private boolean isObsidian(Block block) {
        return block.getType() == Material.OBSIDIAN
            || block.getType() == Material.CRYING_OBSIDIAN;
    }

    /**
     * @param includeAris true = удалять и Арис-регионы (только Titan)
     */
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
                    Component.text("💥 Регион ").color(TextColor.color(0xFF2222))
                    .append(Component.text(name).color(TextColor.color(0xFFFFFF))
                        .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(" уничтожен взрывом ТНТ!")
                        .color(TextColor.color(0xFF2222)))
                )
            );
        }
    }
}
