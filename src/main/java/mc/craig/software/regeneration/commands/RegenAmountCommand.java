package mc.craig.software.regeneration.commands;

import mc.craig.software.regeneration.Regeneration;
import mc.craig.software.regeneration.RegenerationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegenAmountCommand implements CommandExecutor, TabCompleter {

    private final Regeneration plugin;

    public RegenAmountCommand(Regeneration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 2 && args[0].equalsIgnoreCase("set-regens")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    Player targetPlayer = player; // Default to the command sender
                    if (args.length == 3) {
                        // If a third argument is provided, assume it's a player name
                        String playerName = args[2];
                        Player specifiedPlayer = Bukkit.getPlayer(playerName);
                        if (specifiedPlayer != null) {
                            targetPlayer = specifiedPlayer;
                        } else {
                            player.sendMessage("Player not found: " + playerName);
                            return true;
                        }
                    }

                    RegenerationManager.setRegenerationsLeft(targetPlayer, amount);
                    player.sendMessage("Regens set to " + amount + " for " + targetPlayer.getName());
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid number format for regens.");
                }
            } else {
                player.sendMessage("Usage: /regen set-regens AMOUNTHERE [PLAYER]");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("set-regens");
            return completions;
        } else if (strings.length == 2 && strings[0].equalsIgnoreCase("set-regens")) {
            return null;
        } else if (strings.length == 3 && strings[0].equalsIgnoreCase("set-regens")) {
            // Suggest online player names as the third argument
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
