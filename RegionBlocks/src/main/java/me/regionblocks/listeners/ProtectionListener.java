package me.regionblocks.listeners;

import me.regionblocks.RegionBlocks;
import me.regionblocks.models.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectionListener implements Listener {

    private final RegionBlocks plugin;

    public ProtectionListener(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    // ── взаимодействия игрока ─────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (deny(e.getPlayer(), e.getBlock().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (deny(e.getPlayer(), e.getBlock().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (deny(e.getPlayer(), e.getClickedBlock().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (deny(e.getPlayer(), e.getRightClicked().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        if (deny(e.getPlayer(), e.getRightClicked().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (deny(e.getPlayer(), e.getBlockClicked().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (deny(e.getPlayer(), e.getBlockClicked().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player p
            && deny(p, e.getEntity().getLocation())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent e) {
        if (e.getPlayer() != null
            && deny(e.getPlayer(), e.getEntity().getLocation())) e.setCancelled(true);
    }

    // ── среда: пистоны, вода/лава, поршни, огонь, мобы ─────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        Region origin = plugin.getRegionManager().getRegionAt(e.getBlock().getLocation());
        for (Block b : e.getBlocks()) {
            Region inside  = plugin.getRegionManager().getRegionAt(b.getLocation());
            Region outside = plugin.getRegionManager().getRegionAt(b.getRelative(e.getDirection()).getLocation());
            if (differentRegion(origin, inside) || differentRegion(origin, outside)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        Region origin = plugin.getRegionManager().getRegionAt(e.getBlock().getLocation());
        for (Block b : e.getBlocks()) {
            Region inside  = plugin.getRegionManager().getRegionAt(b.getLocation());
            Region outside = plugin.getRegionManager().getRegionAt(b.getRelative(e.getDirection()).getLocation());
            if (differentRegion(origin, inside) || differentRegion(origin, outside)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLiquidFlow(BlockFromToEvent e) {
        Region from = plugin.getRegionManager().getRegionAt(e.getBlock().getLocation());
        Region to   = plugin.getRegionManager().getRegionAt(e.getToBlock().getLocation());
        if (differentRegion(from, to)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent e) {
        Region region = plugin.getRegionManager().getRegionAt(e.getBlock().getLocation());
        if (region == null) return;
        if (e.getPlayer() != null) {
            if (!hasAccess(e.getPlayer(), region)) {
                e.setCancelled(true);
                noAccess(e.getPlayer(), region);
            }
            return;
        }
        // Любой не-игровой огонь в чужом регионе — глушим
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent e) {
        if (plugin.getRegionManager().getRegionAt(e.getBlock().getLocation()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (plugin.getRegionManager().getRegionAt(e.getBlock().getLocation()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplodeFiltered(EntityExplodeEvent e) {
        // Кастомные TNT (Apocalypse / Titan и пр.) полностью обрабатывает TntExplosionListener:
        // он сам решает, что войдёт в blockList. Поэтому TNT не трогаем здесь.
        if (e.getEntityType().name().contains("TNT")) return;
        e.blockList().removeIf(b -> plugin.getRegionManager().getRegionAt(b.getLocation()) != null);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private boolean deny(Player player, Location loc) {
        if (player.hasPermission("regionblocks.admin")) return false;
        Region region = plugin.getRegionManager().getRegionAt(loc);
        if (region == null) return false;
        if (region.hasAccess(player.getName())) return false;
        noAccess(player, region);
        return true;
    }

    private boolean hasAccess(Player player, Region region) {
        return player.hasPermission("regionblocks.admin") || region.hasAccess(player.getName());
    }

    private boolean differentRegion(Region a, Region b) {
        if (a == null && b == null) return false;
        if (a == null || b == null) return true;
        return !a.getName().equalsIgnoreCase(b.getName());
    }

    private void noAccess(Player player, Region region) {
        player.sendMessage(
            Component.text("✗ Это территория региона ")
                .color(TextColor.color(0xFF4444))
            .append(Component.text(region.getName())
                .color(TextColor.color(0xFFFFFF)))
            .append(Component.text(" [" + region.getTier().getDisplayName() + "§r]")
                .color(TextColor.color(region.getTier().getColor())))
        );
        player.sendMessage(
            Component.text("  Владелец: ").color(TextColor.color(0xAAAAAA))
            .append(Component.text(region.getOwner()).color(TextColor.color(0xFFCC55)))
        );
    }
}
