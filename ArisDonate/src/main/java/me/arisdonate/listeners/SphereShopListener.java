package me.arisdonate.listeners;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.Sphere;
import me.arisdonate.util.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SphereShopListener implements Listener {

    private final ArisDonatePlugin plugin;

    public SphereShopListener(ArisDonatePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        Component title = e.getView().title();
        if (title == null) return;
        if (!Msg.stripFormatting(title).contains("МАГАЗИН СФЕР")) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) return;
        if (it.getType() == Material.BARRIER) { p.closeInventory(); return; }
        ItemMeta im = it.getItemMeta();
        if (im == null) return;
        String sid = im.getPersistentDataContainer().get(plugin.keySphereId(), PersistentDataType.STRING);
        if (sid == null) return;
        Sphere s = plugin.getSphereManager().getSphere(sid);
        if (s == null) return;
        if (s.price() <= 0) {
            p.sendMessage(Msg.parse("&cЭта сфера выдаётся только в составе кита."));
            return;
        }
        if (!plugin.getEconomyManager().take(p.getUniqueId(), s.price())) {
            p.sendMessage(Msg.parse("&cНе хватает Aris-coins."));
            return;
        }
        p.getInventory().addItem(s.toItem(plugin));
        p.sendMessage(Msg.parse("&aКуплена сфера " + s.gradientName() + " &7за &e" + s.price() + " &7coins."));
        plugin.getSphereShopGui().open(p);
    }
}
