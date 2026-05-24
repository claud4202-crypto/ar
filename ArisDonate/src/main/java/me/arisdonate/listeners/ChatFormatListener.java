package me.arisdonate.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.util.Msg;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Префикс доната перед ником в чате, без скобок, с RGB-градиентом.
 * Формат:  <grad>RANK</grad> Nick: message
 *
 * Если у игрока нет доната — префикса нет.
 */
public class ChatFormatListener implements Listener {

    private final ArisDonatePlugin plugin;

    public ChatFormatListener(ArisDonatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        if (plugin.getMuteManager().isMuted(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Msg.parse("&cВы заглушены и не можете писать в чат."));
            return;
        }

        Player p = e.getPlayer();
        Component prefix = plugin.getChatFormatter().buildPrefix(p);
        Component name = Component.text(p.getName()).color(NamedTextColor.WHITE);
        Component message = e.message();

        Component out;
        if (prefix == Component.empty() || Msg.stripFormatting(prefix).isEmpty()) {
            out = name.append(Component.text(": ", NamedTextColor.GRAY)).append(message);
        } else {
            out = prefix.append(Component.text(" "))
                    .append(name)
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(message);
        }
        e.renderer((source, sourceDisplayName, msg, viewer) -> out);
    }
}
