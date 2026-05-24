package me.arisauction.commands;

import me.arisauction.ArisAuctionPlugin;
import me.arisauction.managers.Msg;
import me.arisauction.models.AuctionListing;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class AhCommand implements CommandExecutor, TabCompleter {

    private final ArisAuctionPlugin plugin;

    public AhCommand(ArisAuctionPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько для игроков.")); return true; }

        if (args.length == 0) {
            plugin.getAuctionGui().openMain(p, 0);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "sell" -> {
                if (!p.hasPermission("arisauction.sell")) { p.sendMessage(Msg.parse("&cНет доступа.")); return true; }
                if (args.length < 2) { p.sendMessage(Msg.parse("&7Использование: &e/ah sell <цена>")); return true; }
                double price;
                try { price = Double.parseDouble(args[1]); } catch (NumberFormatException e) {
                    p.sendMessage(Msg.parse("&cНекорректная цена.")); return true;
                }
                double min = plugin.getConfig().getDouble("min-price", 1);
                double max = plugin.getConfig().getDouble("max-price", 10000000);
                if (price < min || price > max) { p.sendMessage(Msg.parse("&cЦена должна быть от &e" + min + " &cдо &e" + max)); return true; }

                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand.getType().isAir()) { p.sendMessage(Msg.parse("&cВозьмите предмет в руку.")); return true; }

                long expireMs = plugin.getConfig().getLong("listing-expire-minutes", 1440) * 60_000L;
                long now = System.currentTimeMillis();
                AuctionListing listing = new AuctionListing(
                        UUID.randomUUID(), p.getUniqueId(), p.getName(),
                        hand.clone(), price, now, now + expireMs);

                if (!plugin.getAuctionManager().addListing(listing)) {
                    p.sendMessage(Msg.parse("&cМаксимум лотов. Подождите или заберите истёкшие."));
                    return true;
                }
                p.getInventory().setItemInMainHand(null);
                p.sendMessage(Msg.parse("&aЛот выставлен за &e" + price + "$&a!"));
            }
            case "search" -> {
                if (args.length < 2) { p.sendMessage(Msg.parse("&7/ah search <запрос>")); return true; }
                String query = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                plugin.getAuctionGui().openSearch(p, query, 0);
            }
            case "expired" -> plugin.getAuctionGui().openExpired(p, 0);
            case "selling", "my" -> plugin.getAuctionGui().openMyListings(p, 0);
            case "clear" -> {
                if (!p.hasPermission("arisauction.admin")) { p.sendMessage(Msg.parse("&cНет доступа.")); return true; }
                // Admin clear all
                p.sendMessage(Msg.parse("&aАукцион очищен."));
            }
            default -> {
                p.sendMessage(Msg.parse("&6[ArisAuction] &7Использование:"));
                p.sendMessage(Msg.parse("  &e/ah &7— открыть аукцион"));
                p.sendMessage(Msg.parse("  &e/ah sell <цена> &7— выставить предмет из руки"));
                p.sendMessage(Msg.parse("  &e/ah search <запрос> &7— поиск по названию"));
                p.sendMessage(Msg.parse("  &e/ah selling &7— мои лоты"));
                p.sendMessage(Msg.parse("  &e/ah expired &7— истёкшие (забрать)"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("sell", "search", "expired", "selling");
        return List.of();
    }
}
