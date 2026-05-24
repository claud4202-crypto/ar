package me.arisauction.gui;

import me.arisauction.ArisAuctionPlugin;
import me.arisauction.managers.Msg;
import me.arisauction.models.AuctionListing;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionGui {

    public static final String TITLE_MAIN = "Аукцион";
    public static final String TITLE_EXPIRED = "Истёкшие лоты";
    public static final String TITLE_MY = "Мои лоты";

    private static final DecimalFormat FMT = new DecimalFormat("#,##0.##");
    private final ArisAuctionPlugin plugin;

    public AuctionGui(ArisAuctionPlugin plugin) { this.plugin = plugin; }

    public void openMain(Player player, int page) {
        List<AuctionListing> active = plugin.getAuctionManager().getActiveListings();
        openPage(player, TITLE_MAIN, active, page);
    }

    public void openSearch(Player player, String query, int page) {
        List<AuctionListing> result = plugin.getAuctionManager().search(query);
        openPage(player, TITLE_MAIN + " [" + query + "]", result, page);
    }

    public void openMyListings(Player player, int page) {
        List<AuctionListing> my = plugin.getAuctionManager().getListingsBySeller(player.getUniqueId());
        openPage(player, TITLE_MY, my, page);
    }

    public void openExpired(Player player, int page) {
        List<AuctionListing> expired = plugin.getAuctionManager().getExpiredItems(player.getUniqueId());
        openPage(player, TITLE_EXPIRED, expired, page);
    }

    private void openPage(Player player, String title, List<AuctionListing> items, int page) {
        int pageSize = 45;
        int totalPages = Math.max(1, (int) Math.ceil((double) items.size() / pageSize));
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Inventory inv = Bukkit.createInventory(null, 54, Msg.parse("&6" + title + " &7(стр. " + (page + 1) + "/" + totalPages + ")"));

        int start = page * pageSize;
        int end = Math.min(start + pageSize, items.size());

        for (int i = start; i < end; i++) {
            AuctionListing l = items.get(i);
            ItemStack display = l.item().clone();
            ItemMeta im = display.getItemMeta();
            List<Component> lore = im.lore() != null ? new ArrayList<>(im.lore()) : new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Msg.parse("&7Цена: &e" + FMT.format(l.price()) + "$"));
            lore.add(Msg.parse("&7Продавец: &f" + l.sellerName()));
            long remain = l.expiresAt() - System.currentTimeMillis();
            if (remain > 0) {
                long hours = remain / 3600000;
                long mins = (remain % 3600000) / 60000;
                lore.add(Msg.parse("&7Осталось: &f" + hours + "ч " + mins + "м"));
            }
            if (l.seller().equals(player.getUniqueId())) {
                lore.add(Msg.parse("&c[ЛКМ — забрать]"));
            } else {
                lore.add(Msg.parse("&a[ЛКМ — купить]"));
            }
            im.lore(lore);
            display.setItemMeta(im);
            inv.setItem(i - start, display);
        }

        // Navigation bar (row 6)
        ItemStack filler = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) inv.setItem(i, filler);

        if (page > 0) inv.setItem(45, makeItem(Material.ARROW, "&e← Назад"));
        inv.setItem(47, makeItem(Material.COMPASS, "&eПоиск"));
        inv.setItem(49, makeItem(Material.CHEST, "&eМои лоты"));
        inv.setItem(50, makeItem(Material.HOPPER, "&eИстёкшие"));
        if (page < totalPages - 1) inv.setItem(53, makeItem(Material.ARROW, "&eВперёд →"));

        player.openInventory(inv);
        plugin.getPlayerPages().put(player.getUniqueId(), page);
    }

    private ItemStack makeItem(Material mat, String name) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        im.displayName(Msg.parse(name));
        it.setItemMeta(im);
        return it;
    }
}
