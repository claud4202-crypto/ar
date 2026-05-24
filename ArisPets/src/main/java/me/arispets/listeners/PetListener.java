package me.arispets.listeners;

import me.arispets.ArisPetsPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class PetListener implements Listener {

    private final ArisPetsPlugin plugin;

    public PetListener(ArisPetsPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPetManager().despawn(e.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Entity pet = plugin.getPetManager().getActivePet(e.getPlayer().getUniqueId());
        if (pet != null && pet.isValid()) {
            pet.teleport(e.getTo().add(1, 0, 1));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (plugin.getPetManager().isOurPet(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (plugin.getPetManager().isOurPet(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (plugin.getPetManager().isOurPet(e.getEntity())) {
            e.setCancelled(true);
        }
        if (e.getTarget() != null && plugin.getPetManager().isOurPet(e.getTarget())) {
            e.setCancelled(true);
        }
    }
}
