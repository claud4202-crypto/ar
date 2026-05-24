package me.arischat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.arischat.ArisChatPlugin;
import me.arischat.managers.ChatManager;
import me.arischat.managers.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final ArisChatPlugin plugin;

    public ChatListener(ArisChatPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        ChatManager cm = plugin.getChatManager();

        if (cm.isSpamming(p) && !p.hasPermission("arischat.bypass.spam")) {
            p.sendMessage(Msg.parse("&cПодождите перед следующим сообщением."));
            return;
        }

        String raw = PlainTextComponentSerializer.plainText().serialize(e.message());

        // Filters
        raw = cm.filterCaps(raw);
        raw = cm.filterBannedWords(raw);

        // Color codes for permitted players
        if (p.hasPermission("arischat.color")) {
            // Keep color codes
        } else {
            raw = raw.replaceAll("&[0-9a-fk-or]", "");
        }

        ChatManager.ChatMode mode = cm.getMode(p);
        int radius = plugin.getConfig().getInt("local-radius", 100);

        String format;
        if (mode == ChatManager.ChatMode.LOCAL) {
            format = plugin.getConfig().getString("local-format", "&7[&aL&7] {player}&7: &f{message}");
        } else {
            format = plugin.getConfig().getString("global-format", "&7[&fG&7] {player}&7: &f{message}");
        }

        String finalMsg = format
                .replace("{player}", p.getName())
                .replace("{message}", raw)
                .replace("{prefix}", "")
                .replace("{suffix}", "")
                .replace("{world}", p.getWorld().getName());

        Component component = Msg.parse(finalMsg);

        if (mode == ChatManager.ChatMode.LOCAL) {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (o.getWorld().equals(p.getWorld()) && o.getLocation().distance(p.getLocation()) <= radius) {
                    if (!cm.isIgnoring(o, p.getUniqueId())) {
                        o.sendMessage(component);
                    }
                }
            }
            // Console too
            Bukkit.getConsoleSender().sendMessage(component);
        } else {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (!cm.isIgnoring(o, p.getUniqueId())) {
                    o.sendMessage(component);
                }
            }
            Bukkit.getConsoleSender().sendMessage(component);
        }

        // Mentions
        if (p.hasPermission("arischat.mention")) {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (raw.contains("@" + o.getName())) {
                    String soundName = plugin.getConfig().getString("mention-sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    try {
                        Sound s = Sound.valueOf(soundName);
                        o.playSound(o.getLocation(), s, 1.0f, 1.0f);
                    } catch (Exception ignored) {}
                    o.sendMessage(Msg.parse("&e" + p.getName() + " &7упомянул вас в чате!"));
                }
            }
        }
    }
}
