package me.arisauction;

import me.arisauction.commands.AhCommand;
import me.arisauction.gui.AuctionGui;
import me.arisauction.managers.AuctionManager;
import me.arisauction.managers.Msg;
import me.arisauction.models.AuctionListing;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArisAuctionPlugin extends JavaPlugin implements Listener {

    private AuctionManager auctionManager;
    private AuctionGui auctionGui;
    private final Map<UUID, Integer> playerPages = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        auctionManager = new AuctionManager(this);
        auctionGui = new AuctionGui(this);

        PluginCommand ahCmd = getCommand("ah");
        AhCommand ac = new AhCommand(this);
        if (ahCmd != null) { ahCmd.setExecutor(ac); ahCmd.setTabCompleter(ac); }

        getServer().getPluginManager().registerEvents(this, this);

        // Auto-save every 5 minutes
        getServer().getScheduler().runTaskTimer(this, auctionManager::save, 6000L, 6000L);

        getLogger().info("ArisAuction v" + getDescription().getVersion() + " включён.");
    }

    @Override
    public void onDisable() {
        if (auctionManager != null) auctionManager.save();
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (!title.contains(AuctionGui.TITLE_MAIN) && !title.contains(AuctionGui.TITLE_EXPIRED)
                && !title.contains(AuctionGui.TITLE_MY)) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();
        if (slot < 0 || slot > 53) return;

        // Navigation
        if (slot == 45) {
            int page = playerPages.getOrDefault(p.getUniqueId(), 0) - 1;
            if (title.contains(AuctionGui.TITLE_EXPIRED)) auctionGui.openExpired(p, page);
            else if (title.contains(AuctionGui.TITLE_MY)) auctionGui.openMyListings(p, page);
            else auctionGui.openMain(p, page);
            return;
        }
        if (slot == 53) {
            int page = playerPages.getOrDefault(p.getUniqueId(), 0) + 1;
            if (title.contains(AuctionGui.TITLE_EXPIRED)) auctionGui.openExpired(p, page);
            else if (title.contains(AuctionGui.TITLE_MY)) auctionGui.openMyListings(p, page);
            else auctionGui.openMain(p, page);
            return;
        }
        if (slot == 49) { auctionGui.openMyListings(p, 0); return; }
        if (slot == 50) { auctionGui.openExpired(p, 0); return; }
        if (slot >= 45) return;

        // Item click — buy or cancel
        int page = playerPages.getOrDefault(p.getUniqueId(), 0);
        List<AuctionListing> items;
        if (title.contains(AuctionGui.TITLE_EXPIRED)) items = auctionManager.getExpiredItems(p.getUniqueId());
        else if (title.contains(AuctionGui.TITLE_MY)) items = auctionManager.getListingsBySeller(p.getUniqueId());
        else items = auctionManager.getActiveListings();

        int idx = page * 45 + slot;
        if (idx >= items.size()) return;
        AuctionListing listing = items.get(idx);

        if (title.contains(AuctionGui.TITLE_EXPIRED)) {
            // Return expired item
            var leftover = p.getInventory().addItem(listing.item().clone());
            for (ItemStack drop : leftover.values()) p.getWorld().dropItemNaturally(p.getLocation(), drop);
            auctionManager.removeExpiredItem(listing);
            p.sendMessage(Msg.parse("&aПредмет возвращён!"));
            auctionGui.openExpired(p, page);
            return;
        }

        if (listing.seller().equals(p.getUniqueId())) {
            // Cancel own listing
            var leftover = p.getInventory().addItem(listing.item().clone());
            for (ItemStack drop : leftover.values()) p.getWorld().dropItemNaturally(p.getLocation(), drop);
            auctionManager.removeListing(listing);
            p.sendMessage(Msg.parse("&aЛот снят."));
            auctionGui.openMyListings(p, page);
            return;
        }

        // Buy
        if (!p.hasPermission("arisauction.buy")) { p.sendMessage(Msg.parse("&cНет доступа.")); return; }

        // Check economy (Vault)
        net.milkbowl.vault.economy.Economy eco = getEconomy();
        if (eco == null) { p.sendMessage(Msg.parse("&cЭкономика (Vault) не найдена.")); return; }
        if (!eco.has(p, listing.price())) {
            p.sendMessage(Msg.parse("&cНедостаточно денег. Нужно: &e" + listing.price() + "$"));
            return;
        }

        if (!auctionManager.buy(listing)) {
            p.sendMessage(Msg.parse("&cЛот уже продан или истёк."));
            auctionGui.openMain(p, page);
            return;
        }

        eco.withdrawPlayer(p, listing.price());
        double tax = listing.price() * (auctionManager.getTaxPercent() / 100.0);
        double sellerAmount = listing.price() - tax;
        eco.depositPlayer(getServer().getOfflinePlayer(listing.seller()), sellerAmount);

        var leftover = p.getInventory().addItem(listing.item().clone());
        for (ItemStack drop : leftover.values()) p.getWorld().dropItemNaturally(p.getLocation(), drop);

        p.sendMessage(Msg.parse("&aКуплено за &e" + listing.price() + "$&a!"));
        Player seller = getServer().getPlayer(listing.seller());
        if (seller != null && seller.isOnline()) {
            seller.sendMessage(Msg.parse("&aВаш лот куплен! Получено: &e" + String.format("%.2f", sellerAmount) + "$"));
        }
        auctionGui.openMain(p, page);
    }

    private net.milkbowl.vault.economy.Economy getEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return null;
        var rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        return rsp == null ? null : rsp.getProvider();
    }

    public AuctionManager getAuctionManager() { return auctionManager; }
    public AuctionGui getAuctionGui() { return auctionGui; }
    public Map<UUID, Integer> getPlayerPages() { return playerPages; }
}
