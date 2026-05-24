package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpArisCommand extends BaseCommand {
    public HelpArisCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Msg.parse("<grad:#FFD700:#FF8C00>═══ ArisDonate — справка ═══</grad>"));
        sender.sendMessage(Msg.parse("&e/donate &7— открыть меню донатов"));
        sender.sendMessage(Msg.parse("&e/arisdonate give|set|remove|list &7— админ"));
        sender.sendMessage(Msg.parse("&e/sethome /home /delhome /renamehome /homes"));
        sender.sendMessage(Msg.parse("&e/setwarp /warp /delwarp /warps"));
        sender.sendMessage(Msg.parse("&e/spawn /setspawn"));
        sender.sendMessage(Msg.parse("&e/tp /tpa /tphere /tpahere /tpaccept /tpadeny /tpall /tppos /back"));
        sender.sendMessage(Msg.parse("&e/msg /tell /r /me /broadcast /afk"));
        sender.sendMessage(Msg.parse("&e/vanish /gm /gmc /gms /gma /gmsp /fly /speed"));
        sender.sendMessage(Msg.parse("&e/ban /tempban /unban /kick /mute /unmute /freeze /jail /unjail"));
        sender.sendMessage(Msg.parse("&e/heal /feed /god /cure /effect /clearinv"));
        sender.sendMessage(Msg.parse("&e/invsee /ec /enderchest"));
        sender.sendMessage(Msg.parse("&e/time /day /night /weather /sun /rain /thunder /worldtp"));
        sender.sendMessage(Msg.parse("&e/seen /near /playerlist /tps /ping /whois"));
        sender.sendMessage(Msg.parse("&e/top /jump /repair /craft /anvil /grindstone /loom /smithing /cartography"));
        sender.sendMessage(Msg.parse("&e/give /i /more /skull /hat /smite /burn /extinguish"));
        sender.sendMessage(Msg.parse("&e/clearchat /nick /unnick /realname /condense /sudo /commandspy /socialspy"));
        sender.sendMessage(Msg.parse("&e/kit /kits /shop /rules /motd"));
        sender.sendMessage(Msg.parse("&7Полный список: см. plugin.yml"));
    }
}
