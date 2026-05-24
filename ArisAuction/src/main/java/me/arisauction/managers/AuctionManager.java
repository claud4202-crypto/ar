package me.arisauction.managers;

import me.arisauction.ArisAuctionPlugin;
import me.arisauction.models.AuctionListing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuctionManager {

    private final ArisAuctionPlugin plugin;
    private final List<AuctionListing> listings = new ArrayList<>();
    private final List<AuctionListing> expiredItems = new ArrayList<>();
    private File file;

    public AuctionManager(ArisAuctionPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public boolean addListing(AuctionListing listing) {
        long count = listings.stream().filter(l -> !l.isSold() && !l.isExpired() && l.seller().equals(listing.seller())).count();
        if (count >= plugin.getConfig().getInt("max-listings", 10)) return false;
        listings.add(listing);
        save();
        return true;
    }

    public List<AuctionListing> getActiveListings() {
        return listings.stream()
                .filter(l -> !l.isSold() && !l.isExpired())
                .sorted(Comparator.comparingLong(AuctionListing::createdAt).reversed())
                .collect(Collectors.toList());
    }

    public List<AuctionListing> getListingsBySeller(UUID seller) {
        return listings.stream()
                .filter(l -> l.seller().equals(seller) && !l.isSold() && !l.isExpired())
                .collect(Collectors.toList());
    }

    public List<AuctionListing> getExpiredItems(UUID seller) {
        checkExpired();
        return expiredItems.stream()
                .filter(l -> l.seller().equals(seller))
                .collect(Collectors.toList());
    }

    public List<AuctionListing> search(String query) {
        String q = query.toLowerCase();
        return getActiveListings().stream()
                .filter(l -> l.item().getType().name().toLowerCase().contains(q)
                        || (l.item().hasItemMeta() && l.item().getItemMeta().hasDisplayName()
                        && net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                        .serialize(l.item().getItemMeta().displayName()).toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public boolean buy(AuctionListing listing) {
        if (listing.isSold() || listing.isExpired()) return false;
        listing.setSold(true);
        save();
        return true;
    }

    public void removeListing(AuctionListing listing) {
        listings.remove(listing);
        expiredItems.remove(listing);
        save();
    }

    public void removeExpiredItem(AuctionListing listing) {
        expiredItems.remove(listing);
        save();
    }

    public AuctionListing getById(UUID id) {
        for (AuctionListing l : listings) if (l.id().equals(id)) return l;
        for (AuctionListing l : expiredItems) if (l.id().equals(id)) return l;
        return null;
    }

    public double getTaxPercent() { return plugin.getConfig().getDouble("tax-percent", 5.0); }

    private void checkExpired() {
        Iterator<AuctionListing> it = listings.iterator();
        while (it.hasNext()) {
            AuctionListing l = it.next();
            if (!l.isSold() && l.isExpired()) {
                l.setExpired(true);
                expiredItems.add(l);
                it.remove();
            }
        }
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "listings.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        loadSection(cfg, "active", listings);
        loadSection(cfg, "expired", expiredItems);
    }

    private void loadSection(FileConfiguration cfg, String section, List<AuctionListing> target) {
        ConfigurationSection sec = cfg.getConfigurationSection(section);
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            ConfigurationSection ls = sec.getConfigurationSection(key);
            if (ls == null) continue;
            try {
                UUID id = UUID.fromString(key);
                UUID seller = UUID.fromString(ls.getString("seller", ""));
                String sellerName = ls.getString("seller-name", "???");
                ItemStack item = ls.getItemStack("item");
                if (item == null) continue;
                double price = ls.getDouble("price");
                long created = ls.getLong("created");
                long expires = ls.getLong("expires");
                boolean sold = ls.getBoolean("sold", false);
                AuctionListing al = new AuctionListing(id, seller, sellerName, item, price, created, expires);
                al.setSold(sold);
                target.add(al);
            } catch (Exception ignored) {}
        }
    }

    public void save() {
        checkExpired();
        FileConfiguration cfg = new YamlConfiguration();
        saveSection(cfg, "active", listings);
        saveSection(cfg, "expired", expiredItems);
        try { cfg.save(file); } catch (IOException ignored) {}
    }

    private void saveSection(FileConfiguration cfg, String section, List<AuctionListing> source) {
        for (AuctionListing l : source) {
            String path = section + "." + l.id().toString();
            cfg.set(path + ".seller", l.seller().toString());
            cfg.set(path + ".seller-name", l.sellerName());
            cfg.set(path + ".item", l.item());
            cfg.set(path + ".price", l.price());
            cfg.set(path + ".created", l.createdAt());
            cfg.set(path + ".expires", l.expiresAt());
            cfg.set(path + ".sold", l.isSold());
        }
    }
}
