package me.regionblocks.listeners;

import me.regionblocks.RegionBlocks;
import me.regionblocks.managers.ArisItemManager;
import me.regionblocks.managers.LegendaryItemManager;
import me.regionblocks.models.RegionTier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    private final RegionBlocks plugin;

    public BlockPlaceListener(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack item = e.getItemInHand();

        // Красный гриб — только Арис с PDC-меткой
        if (block.getType() == Material.RED_MUSHROOM_BLOCK) {
            if (!ArisItemManager.isArisBlock(item)) return;
            tryCreate(player, block, RegionTier.ARIS);
            return;
        }

        // Ancient Debris — только с PDC-меткой из /shop создаёт регион;
        // обычный блок просто ставится как декоративный, без региона
        if (block.getType() == Material.ANCIENT_DEBRIS) {
            if (LegendaryItemManager.isLegendaryBlock(item)) {
                tryCreate(player, block, RegionTier.LEGENDARY);
            }
            return;
        }

        RegionTier tier = RegionTier.fromMaterial(block.getType());
        if (tier == null) return;

        tryCreate(player, block, tier);
    }

    private void tryCreate(Player player, Block block, RegionTier tier) {
        if (plugin.getRegionManager().overlapsAny(block.getLocation(), tier)) {
            player.sendMessage(
                Component.text("✗ Здесь уже есть регион! Нельзя ставить регионный блок.")
                    .color(TextColor.color(0xFF4444))
            );
            block.setType(Material.AIR);
            player.getInventory().addItem(new ItemStack(tier.getBlockMaterial()));
            return;
        }

        String name = plugin.getRegionManager().createRegion(
            player.getName(), block.getLocation(), tier
        );

        if (name == null) {
            player.sendMessage(Component.text("✗ Не удалось создать регион.").color(TextColor.color(0xFF4444)));
            return;
        }

        int s = tier.getSize();
        me.regionblocks.models.Region r = plugin.getRegionManager().getRegion(name);
        player.sendMessage(Component.text(""));
        player.sendMessage(
            Component.text("✦ Регион создан! ").color(TextColor.color(0x55FF55))
                .append(Component.text("[" + tier.getDisplayName() + "§r]").color(TextColor.color(tier.getColor())))
        );
        player.sendMessage(
            Component.text("  Название: ").color(TextColor.color(0xAAAAAA))
                .append(Component.text(name).color(TextColor.color(0xFFFFFF)))
        );
        player.sendMessage(
            Component.text("  Размер:   ").color(TextColor.color(0xAAAAAA))
                .append(Component.text(s + "×" + s + "×" + s + " блоков (вокруг блока)").color(TextColor.color(0xFFCC55)))
        );
        if (r != null) {
            player.sendMessage(
                Component.text("  Защита от: ").color(TextColor.color(0xAAAAAA))
                    .append(Component.text(r.minX() + ", " + r.minY() + ", " + r.minZ())
                        .color(TextColor.color(0xAAFFAA)))
                    .append(Component.text("  до: ").color(TextColor.color(0xAAAAAA)))
                    .append(Component.text(r.maxX() + ", " + r.maxY() + ", " + r.maxZ())
                        .color(TextColor.color(0xAAFFAA)))
            );
        }
        player.sendMessage(Component.text(""));
    }
}
