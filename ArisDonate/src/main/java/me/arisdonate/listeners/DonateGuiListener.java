package me.arisdonate.listeners;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.gui.DonateGui;
import me.arisdonate.util.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DonateGuiListener implements Listener {

    private final ArisDonatePlugin plugin;

    public DonateGuiListener(ArisDonatePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        Component title = e.getView().title();
        if (title == null) return;
        if (!Msg.stripFormatting(title).contains("ДОНАТЫ СЕРВЕРА")) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ItemStack it = e.getCurrentItem();
        if (it == null) return;
        if (it.getType() == org.bukkit.Material.BARRIER) {
            p.closeInventory();
        }
    }
}
