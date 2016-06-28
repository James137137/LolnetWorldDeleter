/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.lolnet.LolnetWorldDeleter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author James
 */
public class LolnetWorldDeleter extends JavaPlugin {

    static LolnetWorldDeleter plugin;
    private static Logger log = Bukkit.getLogger();
    World mainWorld;

    @Override
    public void onLoad() {
        super.onLoad(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEnable() {
        mainWorld = Bukkit.getWorlds().get(0);
        plugin = this;
        String version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        log.log(Level.INFO, this.getName() + " : Version {0} enabled", version);
    }

    @Override
    public void onDisable() {
        log.log(Level.INFO, "{0}: disabled", this.getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        if (commandName.equalsIgnoreCase("deleteWorld") && sender.hasPermission("LolnetWorldDeleter.deleteWorld")) {
            List<String> worlds = WorldManager.listWorldsS();
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Missing World Name. Worlds available are:");
                for (WorldManager world : WorldManager.listWorlds()) {
                    if (world.isLoaded()) {
                        sender.sendMessage(ChatColor.GREEN + world.getName());
                    } else {
                        sender.sendMessage(ChatColor.GOLD + world.getName());
                    }
                }
                return true;

            }
            String targetName = args[0];
            if (!isVaildWorld(targetName, worlds)) {
                sender.sendMessage(ChatColor.RED + "Can't find that world on the server. Type \"/deleteWorld\" to list them");
                return false;
            }
            WorldManager target = WorldManager.getWorldManager(targetName);
            if (!target.loaded) {
                sender.sendMessage(ChatColor.RED + "world is already unloaded... but that's okay...");
            } else if (args.length <= 1 || !(args[1].equalsIgnoreCase("force") || args[1].equalsIgnoreCase("f"))) {
                if (target.getWorld().getPlayers().size() > 0) {
                    sender.sendMessage(ChatColor.RED + "You can't delete this world. Players are still in this world");
                    sender.sendMessage(ChatColor.RED + "use /deleteWorld " + target.getName() + " force, or teleport the players");
                    return true;
                }

            } else {
                if (target.getWorld().getPlayers().size() > 0) {
                    for (Player player : target.getWorld().getPlayers()) {
                        System.out.println(player.getName());
                        player.teleport(mainWorld.getSpawnLocation());
                        player.sendMessage("You have been moved due to the deletion of that world");
                    }
                }
                boolean unloadWord = target.unloadWord();
                if (!unloadWord) {
                    System.out.println("Failed to unload World");
                }
            }

            boolean result = target.deleteWorld();
            if (result) {
                sender.sendMessage(ChatColor.GREEN + "Deleted world!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to delete world. Please see console for reason");
                return false;
            }
        }
        return false;
    }

    private boolean isVaildWorld(String targetName, List<String> worlds) {
        for (String world : worlds) {
            if (targetName.equalsIgnoreCase(world)) {
                return true;

            }
        }
        return false;
    }

}
