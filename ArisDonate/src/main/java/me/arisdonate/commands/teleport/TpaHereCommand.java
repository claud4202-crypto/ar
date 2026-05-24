package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.TeleportManager;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpaHereCommand extends BaseCommand {
    public TpaHereCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/tpahere <ник>")); return; }
        Player target = Players.online(args[0]);
        if (target == null || target.equals(p)) { p.sendMessage(Msg.parse("&cИгрок не найден.")); return; }

        plugin.getTeleportManager().request(p, target, TeleportManager.Type.TPAHERE);

        p.sendMessage(Msg.parse("&aЗапрос отправлен игроку &e" + target.getName()));
        Component msg = Msg.parse("&e" + p.getName() + " &7хочет, чтобы вы телепортировались к нему. ")
                .append(Component.text("[✔ Принять]").color(net.kyori.adventure.text.format.NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/tpaccept"))
                        .hoverEvent(HoverEvent.showText(Msg.parse("&aПринять"))))
                .append(Component.text("  "))
                .append(Component.text("[✘ Отклонить]").color(net.kyori.adventure.text.format.NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/tpadeny"))
                        .hoverEvent(HoverEvent.showText(Msg.parse("&cОтклонить"))));
        target.sendMessage(msg);
        target.playSound(target.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1f, 1.5f);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return Players.onlineNames(args[0]);
        return List.of();
    }
}
