package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand extends BaseCommand {
    public SkullCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        String nick = args.length > 0 ? args[0] : p.getName();
        OfflinePlayer op = Players.offline(nick);
        if (op == null) { p.sendMessage(Msg.parse("&cИгрок не найден.")); return; }
        ItemStack it = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) it.getItemMeta();
        sm.setOwningPlayer(op);
        it.setItemMeta(sm);
        p.getInventory().addItem(it);
        p.sendMessage(Msg.parse("&aГолова игрока &e" + nick + " &aполучена."));
    }
}
