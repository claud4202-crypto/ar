package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemCommand extends BaseCommand {
    public GiveItemCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.give")) return;
        if (args.length < 2) { sender.sendMessage(Msg.parse("&7Использование: &e/give <ник> <предмет> [кол]")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        Material m = Material.matchMaterial(args[1]);
        if (m == null) { sender.sendMessage(Msg.parse("&cПредмет не найден.")); return; }
        int n = args.length > 2 ? safeInt(args[2], 1) : 1;
        t.getInventory().addItem(new ItemStack(m, n));
        sender.sendMessage(Msg.parse("&aВыдано &e" + n + " " + m.name() + " &aигроку &e" + t.getName()));
    }

    private int safeInt(String s, int d) {
        try { return Integer.parseInt(s); } catch (Exception e) { return d; }
    }
}
