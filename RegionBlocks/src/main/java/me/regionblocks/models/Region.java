package me.regionblocks.models;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private final String name;
    private final String owner;
    private final Location center;
    private final RegionTier tier;
    private final List<String> members = new ArrayList<>();

    public Region(String name, String owner, Location center, RegionTier tier) {
        this.name = name;
        this.owner = owner;
        this.center = center.clone();
        this.tier = tier;
    }

    public String getName()     { return name; }
    public String getOwner()    { return owner; }
    public Location getCenter() { return center; }
    public RegionTier getTier() { return tier; }
    public List<String> getMembers() { return members; }

    public void addMember(String nick)    { if (!members.contains(nick.toLowerCase())) members.add(nick.toLowerCase()); }
    public void removeMember(String nick) { members.remove(nick.toLowerCase()); }
    public boolean isMember(String nick)  { return members.contains(nick.toLowerCase()); }
    public boolean isOwner(String nick)   { return owner.equalsIgnoreCase(nick); }
    public boolean hasAccess(String nick) { return isOwner(nick) || isMember(nick); }

    /**
     * Куб размером size×size×size, **центрированный** на блоке региона.
     * Для size=3: cx-1 .. cx+1 (3 блока).
     * Для size=6: cx-2 .. cx+3 (6 блоков, для чётных небольшой +bias).
     * Для size=9: cx-4 .. cx+4 (9 блоков).
     */
    public boolean contains(Location loc) {
        if (!loc.getWorld().equals(center.getWorld())) return false;
        int lo = tier.lowHalf();
        int hi = tier.highHalf();
        int cx = center.getBlockX(), cy = center.getBlockY(), cz = center.getBlockZ();
        int lx = loc.getBlockX(),    ly = loc.getBlockY(),    lz = loc.getBlockZ();
        return lx >= cx - lo && lx <= cx + hi &&
               ly >= cy - lo && ly <= cy + hi &&
               lz >= cz - lo && lz <= cz + hi;
    }

    public int minX() { return center.getBlockX() - tier.lowHalf(); }
    public int maxX() { return center.getBlockX() + tier.highHalf(); }
    public int minY() { return center.getBlockY() - tier.lowHalf(); }
    public int maxY() { return center.getBlockY() + tier.highHalf(); }
    public int minZ() { return center.getBlockZ() - tier.lowHalf(); }
    public int maxZ() { return center.getBlockZ() + tier.highHalf(); }
}
