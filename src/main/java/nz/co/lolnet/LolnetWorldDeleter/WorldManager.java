/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.lolnet.LolnetWorldDeleter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author James
 */
public class WorldManager {

    String name;
    File folder;
    boolean loaded;

    public WorldManager(String name, File folder, boolean loaded) {
        this.name = name;
        this.folder = folder;
        this.loaded = loaded;
    }

    public WorldManager(String name) {
        this.name = name;
        World world = Bukkit.getWorld(name);
        loaded = (world != null);
        File[] files = Bukkit.getServer().getWorldContainer().listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().equalsIgnoreCase(name)) {
                folder = file;
                break;
            }
        }

    }

    public static List<String> listWorldsS() {
        List<String> output = new ArrayList<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            if (!world.getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
                output.add(world.getName());
            }
        }
        File[] files = Bukkit.getServer().getWorldContainer().listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().toLowerCase().contains("dim") && !output.contains(file.getName())) {
                output.add(file.getName());
            }
        }
        return output;
    }

    public static List<WorldManager> listWorlds() {
        List<WorldManager> output = new ArrayList<>();
        for (String worldName : listWorldsS()) {
            output.add(new WorldManager(worldName));
        }
        return output;
    }

    public boolean unloadWord() {
        if (loaded) {
            Bukkit.getServer().unloadWorld(Bukkit.getWorld(name), true);
            loaded = false;
            return true;
        }
        return false;
    }

    public boolean isLoaded() {
        return loaded;
    }
    
    public static WorldManager getWorldManager(String name)
    {
        List<WorldManager> listWorlds = listWorlds();
        for (WorldManager listWorld : listWorlds) {
            if (listWorld.name.equalsIgnoreCase(name))
            {
                return listWorld;
            }
        }
        return null;
    }
    
    public World getWorld()
    {
        return Bukkit.getWorld(name);
    }

    public String getName() {
        return name;
    }

    public boolean deleteWorld() {
        String worldName = name;
       File target = new File(Bukkit.getServer().getWorldContainer(), worldName);
        if (target.exists() && target.isDirectory()) {
            try {
                delete(target);
            } catch (IOException ex) {
                Bukkit.getLogger().warning("failed to delete: " + target);
                return false;
            }
            Bukkit.getLogger().info("Deleted world");
            return true;
        }
        Bukkit.getLogger().warning("Could not find folder or is not a folder: World == " + worldName);
        return false;
    }
    
    private boolean delete(File file)
            throws IOException {
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                delete(subfile);
            }
        }
        if (!file.delete()) {
            Bukkit.getLogger().severe("failed to delete: " + file);
            return false;
        }

        return true;
    }
    
    
    
}
