package me.regionblocks.listeners;

import me.regionblocks.RegionBlocks;
import me.regionblocks.managers.ArisItemManager;
import me.regionblocks.models.Region;
import me.regionblocks.models.RegionTier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private final RegionBlocks plugin;

    public BlockBreakListener(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        // Ищем регион, у которого именно этот блок — угловой центр
        Region region = plugin.getRegionManager().getRegionAt(block.getLocation());
        if (region == null) return;

        // Проверяем, что ломают именно угловой (начальный) блок региона
        Block corner = region.getCenter().getBlock();
        if (!corner.equals(block)) return;

        // Только владелец или админ
        if (!region.isOwner(player.getName()) && !player.hasPermission("regionblocks.admin")) {
            e.setCancelled(true);
            player.sendMessage(
                Component.text("✗ Только владелец может убрать регионный блок!")
                    .color(TextColor.color(0xFF4444))
            );
            return;
        }

        e.setDropItems(false);
        plugin.getRegionManager().removeRegion(region.getName());

        // Вернуть блок владельцу
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack drop = region.getTier() == RegionTier.ARIS
                ? ArisItemManager.createArisBlock()
                : new ItemStack(region.getTier().getBlockMaterial(), 1);
            player.getInventory().addItem(drop);
        }

        player.sendMessage(
            Component.text("✓ Регион ").color(TextColor.color(0xFFAA00))
                .append(Component.text(region.getName()).color(TextColor.color(0xFFFFFF)))
                .append(Component.text(" удалён. Блок возвращён.").color(TextColor.color(0xFFAA00)))
        );
    }
}
