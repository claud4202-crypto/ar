package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.Sphere;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Управление сферами: загрузка конфига, такер-эффекты экипированных сфер.
 */
public class SphereManager {

    private final ArisDonatePlugin plugin;
    private final Map<String, Sphere> spheres = new LinkedHashMap<>();
    private BukkitTask tickTask;

    public SphereManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
        start();
    }

    public Sphere getSphere(String id) {
        return id == null ? null : spheres.get(id.toLowerCase());
    }

    public List<Sphere> all() {
        return new ArrayList<>(spheres.values());
    }

    public void load() {
        spheres.clear();
        ConfigurationSection root = plugin.getConfig().getConfigurationSection("spheres");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            if (s == null) continue;
            Material mat = Material.matchMaterial(s.getString("material", "MAGMA_CREAM"));
            if (mat == null) mat = Material.MAGMA_CREAM;
            Sphere.Slot slot;
            try {
                slot = Sphere.Slot.valueOf(s.getString("slot", "ANY").toUpperCase());
            } catch (Exception e) { slot = Sphere.Slot.ANY; }
            spheres.put(id.toLowerCase(), new Sphere(
                    id.toLowerCase(),
                    s.getString("display-name", id),
                    mat,
                    s.getString("start-hex", "FFFFFF").replace("#", ""),
                    s.getString("end-hex", "FFFFFF").replace("#", ""),
                    s.getStringList("buffs"),
                    s.getStringList("debuffs"),
                    s.getInt("price", 0),
                    slot,
                    s.getStringList("lore")
            ));
        }
    }

    public void start() {
        if (tickTask != null) tickTask.cancel();
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAll, 20L, 40L);
    }

    public void stop() {
        if (tickTask != null) { tickTask.cancel(); tickTask = null; }
    }

    private void tickAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            tickPlayer(p);
        }
    }

    private void tickPlayer(Player p) {
        Sphere head = sphereFromItem(p.getInventory().getHelmet());
        Sphere off  = sphereFromItem(p.getInventory().getItemInOffHand());
        if (head != null && head.slot() != Sphere.Slot.OFFHAND) {
            for (PotionEffect e : head.potionEffects()) p.addPotionEffect(e, true);
        }
        if (off != null && off.slot() != Sphere.Slot.HEAD) {
            for (PotionEffect e : off.potionEffects()) p.addPotionEffect(e, true);
        }
    }

    private Sphere sphereFromItem(ItemStack it) {
        if (it == null || it.getType().isAir()) return null;
        ItemMeta im = it.getItemMeta();
        if (im == null) return null;
        String id = im.getPersistentDataContainer().get(plugin.keySphereId(), PersistentDataType.STRING);
        if (id == null) return null;
        return getSphere(id);
    }
}
