package me.ariscrates.managers;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.models.Crate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Управляет голограммами (невидимые ArmorStand) над крейтами.
 */
public class HologramManager {

    private final ArisCratesPlugin plugin;
    private final Map<String, ArmorStand> holograms = new HashMap<>();

    public HologramManager(ArisCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnAll() {
        removeAll();
        for (Map.Entry<String, String> entry : plugin.getLocationManager().allLocations().entrySet()) {
            String locKey = entry.getKey();
            String crateId = entry.getValue();
            Crate crate = plugin.getCrateManager().getCrate(crateId);
            if (crate == null) continue;
            String holoText = crate.hologramText();
            if (holoText == null || holoText.isEmpty()) holoText = crate.displayName();
            Location loc = parseLocKey(locKey);
            if (loc == null) continue;
            spawnHologram(locKey, loc.add(0.5, 1.5, 0.5), holoText);
        }
    }

    public void spawnForLocation(String locKey, String crateId) {
        removeHologram(locKey);
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return;
        String holoText = crate.hologramText();
        if (holoText == null || holoText.isEmpty()) holoText = crate.displayName();
        Location loc = parseLocKey(locKey);
        if (loc == null) return;
        spawnHologram(locKey, loc.add(0.5, 1.5, 0.5), holoText);
    }

    public void removeForLocation(String locKey) {
        removeHologram(locKey);
    }

    public void removeAll() {
        for (ArmorStand as : holograms.values()) {
            if (as.isValid()) as.remove();
        }
        holograms.clear();
    }

    private void spawnHologram(String key, Location loc, String text) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setMarker(true);
        as.setSmall(true);
        as.setCustomNameVisible(true);
        as.customName(Msg.parse(text));
        as.setInvulnerable(true);
        as.setPersistent(false);
        holograms.put(key, as);
    }

    private void removeHologram(String key) {
        ArmorStand as = holograms.remove(key);
        if (as != null && as.isValid()) as.remove();
    }

    private Location parseLocKey(String key) {
        String[] parts = key.split(",");
        if (parts.length != 4) return null;
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        try {
            return new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
