package me.arisauction.models;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionListing {
    private final UUID id;
    private final UUID seller;
    private final String sellerName;
    private final ItemStack item;
    private final double price;
    private final long createdAt;
    private final long expiresAt;
    private boolean sold;
    private boolean expired;

    public AuctionListing(UUID id, UUID seller, String sellerName, ItemStack item, double price, long createdAt, long expiresAt) {
        this.id = id;
        this.seller = seller;
        this.sellerName = sellerName;
        this.item = item;
        this.price = price;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID id() { return id; }
    public UUID seller() { return seller; }
    public String sellerName() { return sellerName; }
    public ItemStack item() { return item; }
    public double price() { return price; }
    public long createdAt() { return createdAt; }
    public long expiresAt() { return expiresAt; }
    public boolean isSold() { return sold; }
    public void setSold(boolean sold) { this.sold = sold; }
    public boolean isExpired() { return expired || System.currentTimeMillis() > expiresAt; }
    public void setExpired(boolean expired) { this.expired = expired; }
}
