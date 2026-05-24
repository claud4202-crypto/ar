package me.arisdonate.listeners;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeMoveListener implements Listener {

    private final ArisDonatePlugin plugin;

    public FreezeMoveListener(ArisDonatePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!plugin.getFreezeManager().isFrozen(e.getPlayer().getUniqueId())) return;
        if (e.getFrom().getBlockX() != e.getTo().getBlockX()
                || e.getFrom().getBlockZ() != e.getTo().getBlockZ()
                || e.getFrom().getBlockY() != e.getTo().getBlockY()) {
            e.setTo(e.getFrom());
        }
    }
}
