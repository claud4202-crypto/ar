package me.arischat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.arischat.ArisChatPlugin;
import me.arischat.managers.ChatManager;
import me.arischat.managers.DonateHook;
import me.arischat.managers.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (!p.hasPermission("arischat.color")) {
            raw = raw.replaceAll("&[0-9a-fk-or]", "");
        }

        // Get prefix Component from ArisDonate (with RGB gradients)
        Component prefixComp = Component.empty();
        DonateHook hook = plugin.getDonateHook();
        if (hook != null && hook.isAvailable()) {
            prefixComp = hook.getPrefixComponent(p);
        }

        ChatManager.ChatMode mode = cm.getMode(p);
        int radius = plugin.getConfig().getInt("local-radius", 100);

        // Build message as Component chain to preserve gradients
        Component modeTag;
        if (mode == ChatManager.ChatMode.LOCAL) {
            modeTag = Msg.parse("&7[&aL&7] ");
        } else {
            modeTag = Msg.parse("&7[&fG&7] ");
        }

        boolean hasPrefix = !PlainTextComponentSerializer.plainText().serialize(prefixComp).isEmpty();

        Component component = modeTag;
        if (hasPrefix) {
            component = component.append(prefixComp).append(Component.text(" "));
        }
        component = component
                .append(Component.text(p.getName(), NamedTextColor.WHITE))
                .append(Msg.parse("&7: &f"))
                .append(Msg.parse(raw));

        Component finalComponent = component;

        if (mode == ChatManager.ChatMode.LOCAL) {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (o.getWorld().equals(p.getWorld()) && o.getLocation().distance(p.getLocation()) <= radius) {
                    if (!cm.isIgnoring(o, p.getUniqueId())) {
                        o.sendMessage(finalComponent);
                    }
                }
            }
            Bukkit.getConsoleSender().sendMessage(finalComponent);
        } else {
            for (Player o : Bukkit.getOnlinePlayers()) {
                if (!cm.isIgnoring(o, p.getUniqueId())) {
                    o.sendMessage(finalComponent);
                }
            }
            Bukkit.getConsoleSender().sendMessage(finalComponent);
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
