package me.arisdonate.listeners;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.commands.CommandSpyCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class CommandSpyListener implements Listener {

    private final ArisDonatePlugin plugin;

    public CommandSpyListener(ArisDonatePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        for (UUID id : CommandSpyCommand.SPIES) {
            if (id.equals(p.getUniqueId())) continue;
            Player spy = Bukkit.getPlayer(id);
            if (spy == null) continue;
            spy.sendMessage(Msg.parse("&8[cmd] &f" + p.getName() + " &7→ &e" + e.getMessage()));
        }
    }
}
