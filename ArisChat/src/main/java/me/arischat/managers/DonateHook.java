package me.arischat.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Hook в ArisDonate для получения префикса (донат/стаф ранг) в чате.
 */
public class DonateHook {

    private boolean available;
    private Plugin arisDonate;

    public DonateHook() {
        arisDonate = Bukkit.getPluginManager().getPlugin("ArisDonate");
        available = arisDonate != null && arisDonate.isEnabled();
    }

    public boolean isAvailable() { return available; }

    /**
     * Возвращает префикс игрока как Component (с RGB-градиентами).
     * Приоритет: стаф → донат → "Игрок".
     */
    public Component getPrefixComponent(Player player) {
        if (!available) return Component.empty();
        try {
            Method getCF = arisDonate.getClass().getMethod("getChatFormatter");
            Object formatter = getCF.invoke(arisDonate);
            Method buildPrefix = formatter.getClass().getMethod("buildPrefix", Player.class);
            Object comp = buildPrefix.invoke(formatter, player);
            if (comp instanceof Component c) {
                return c;
            }
            return Component.empty();
        } catch (Exception e) {
            return Component.empty();
        }
    }
}
