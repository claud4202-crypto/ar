package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContainerCommand extends BaseCommand {
    private final String kind;
    public ContainerCommand(ArisDonatePlugin plugin, String kind) { super(plugin); this.kind = kind; }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        try {
            switch (kind) {
                case "anvil":       p.openAnvil(null, true); break;
                case "grindstone":  p.openGrindstone(null, true); break;
                case "loom":        p.openLoom(null, true); break;
                case "smithing":    p.openSmithingTable(null, true); break;
                case "cartography": p.openCartographyTable(null, true); break;
                default: p.sendMessage(Msg.parse("&cНеизвестный контейнер.")); return;
            }
        } catch (Throwable t) {
            p.sendMessage(Msg.parse("&cОшибка открытия " + kind + ": " + t.getMessage()));
        }
    }
}
