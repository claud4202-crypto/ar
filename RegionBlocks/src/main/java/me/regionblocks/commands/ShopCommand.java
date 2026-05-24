package me.regionblocks.commands;

import me.regionblocks.RegionBlocks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {

    private final RegionBlocks plugin;

    public ShopCommand(RegionBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return true;
        }
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "privates", "private", "приваты" -> { plugin.getShopListener().openPrivatesTab(player); return true; }
                case "tnt", "тнт" -> { plugin.getShopListener().openTntTab(player); return true; }
                case "minecart", "minecarts", "вагонетки" -> { plugin.getShopListener().openMinecartTab(player); return true; }
                case "spheres", "sphere", "шары", "шарики", "сферы" -> { plugin.getShopListener().openSpheresTab(player); return true; }
                case "kits", "kit", "киты" -> { plugin.getShopListener().openKitsTab(player); return true; }
            }
        }
        plugin.getShopListener().openShop(player);
        return true;
    }
}
