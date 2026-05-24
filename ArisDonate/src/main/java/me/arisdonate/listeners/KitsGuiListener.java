package me.arisdonate.listeners;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.KitManager;
import me.arisdonate.util.Msg;
import me.arisdonate.util.TimeUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class KitsGuiListener implements Listener {

    private final ArisDonatePlugin plugin;

    public KitsGuiListener(ArisDonatePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView() == null) return;
        Component title = e.getView().title();
        if (title == null) return;
        if (!Msg.stripFormatting(title).contains("КИТЫ СЕРВЕРА")) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) return;
        if (it.getType() == Material.BARRIER) { p.closeInventory(); return; }
        ItemMeta im = it.getItemMeta();
        if (im == null) return;
        String kitId = im.getPersistentDataContainer().get(plugin.keyKitId(), PersistentDataType.STRING);
        if (kitId == null) return;
        KitManager.Kit kit = plugin.getKitManager().getKit(kitId);
        if (kit == null) { p.sendMessage(Msg.parse("&cКит не найден.")); return; }
        if (!plugin.getKitManager().canTakeKit(p, kit)) {
            p.sendMessage(Msg.parse("&cНет доступа к этому киту. Купите донат: &e/donate"));
            return;
        }
        long left = plugin.getKitManager().cooldownLeft(p, kit.id);
        if (left > 0) {
            p.sendMessage(Msg.parse("&cКит на кулдауне: ещё &e" + TimeUtil.fmt(left)));
            return;
        }
        plugin.getKitManager().giveKit(p, kit);
        p.sendMessage(Msg.parse("&aПолучен кит " + kit.displayName));
        p.closeInventory();
    }
}
