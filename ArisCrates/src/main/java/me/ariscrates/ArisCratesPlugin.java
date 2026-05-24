package me.ariscrates;

import me.ariscrates.commands.CrateCommand;
import me.ariscrates.commands.KeyCommand;
import me.ariscrates.gui.CrateAnimationGui;
import me.ariscrates.gui.CratePreviewGui;
import me.ariscrates.managers.CrateLocationManager;
import me.ariscrates.managers.CrateManager;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisCratesPlugin extends JavaPlugin implements Listener {

    private CrateManager crateManager;
    private CrateLocationManager locationManager;
    private CrateAnimationGui animationGui;
    private CratePreviewGui previewGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        crateManager = new CrateManager(this);
        locationManager = new CrateLocationManager(this);
        animationGui = new CrateAnimationGui(this);
        previewGui = new CratePreviewGui(this);

        PluginCommand crateCmd = getCommand("crate");
        CrateCommand cc = new CrateCommand(this);
        if (crateCmd != null) { crateCmd.setExecutor(cc); crateCmd.setTabCompleter(cc); }

        PluginCommand keyCmd = getCommand("key");
        KeyCommand kc = new KeyCommand(this);
        if (keyCmd != null) { keyCmd.setExecutor(kc); keyCmd.setTabCompleter(kc); }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("ArisCrates v" + getDescription().getVersion() + " включён.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        String crateId = locationManager.getCrateAt(block.getLocation());
        if (crateId == null) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        Crate crate = crateManager.getCrate(crateId);
        if (crate == null) { p.sendMessage(Msg.parse("&cКрейт &e" + crateId + " &cне найден в конфиге.")); return; }

        ItemStack hand = p.getInventory().getItemInMainHand();
        String keyId = crateManager.getKeyId(hand);

        if (keyId == null || !keyId.equalsIgnoreCase(crate.id())) {
            // No key — show preview
            if (p.isSneaking()) {
                previewGui.open(p, crate);
            } else {
                p.sendMessage(Msg.parse("&cНужен ключ от " + crate.displayName() + "&c! (Shift+ПКМ — предпросмотр)"));
            }
            return;
        }

        // Has correct key — open crate
        hand.setAmount(hand.getAmount() - 1);
        animationGui.play(p, crate);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        String title = "";
        if (e.getView().title() != null) {
            title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(e.getView().title());
        }
        if (title.contains(CrateAnimationGui.TITLE_PREFIX) || title.contains(CratePreviewGui.TITLE_PREFIX)) {
            e.setCancelled(true);
        }
    }

    public CrateManager getCrateManager() { return crateManager; }
    public CrateLocationManager getLocationManager() { return locationManager; }
    public CrateAnimationGui getAnimationGui() { return animationGui; }
    public CratePreviewGui getPreviewGui() { return previewGui; }
}
