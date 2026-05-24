package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Простое сокрытие игроков (/v). */
public class VanishManager {

    private final ArisDonatePlugin plugin;
    private final Set<UUID> vanished = new HashSet<>();

    public VanishManager(ArisDonatePlugin plugin) { this.plugin = plugin; }

    public boolean isVanished(UUID id) { return vanished.contains(id); }

    public void setVanished(Player p, boolean v) {
        if (v) {
            vanished.add(p.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(p)) continue;
                if (!other.hasPermission("arisdonate.vanish.see")) {
                    other.hidePlayer(plugin, p);
                }
            }
        } else {
            vanished.remove(p.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(p)) continue;
                other.showPlayer(plugin, p);
            }
        }
    }

    public Set<UUID> getVanished() { return vanished; }

    public void refreshFor(Player joining) {
        for (UUID id : vanished) {
            Player v = Bukkit.getPlayer(id);
            if (v != null && !joining.hasPermission("arisdonate.vanish.see")) {
                joining.hidePlayer(plugin, v);
            }
        }
    }
}
