package org.cubeville.cvhome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvhome.commands.HomeInfo;
import org.cubeville.cvhome.commands.HomeSet;
import org.cubeville.cvhome.commands.HomeTeleport;

import java.io.File;
import java.io.IOException;

public class CVHome extends JavaPlugin implements Listener {

    private CommandParser infoHomeCommandParser;
    private CommandParser setHomeCommandParser;
    private CommandParser tpHomeCommandParser;

    private HomeDB homeDB;
    private HomeManager homeManager;

    private static CVHome instance;
    
    public static CVHome getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        
        getServer().getPluginManager().registerEvents(this, this);

        final File dataDir = getDataFolder();
        if(!dataDir.exists()) {
            dataDir.mkdirs();
        }
        homeDB = new HomeDB(this);
        try {
            homeDB.createBackup(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        homeDB.load();

        homeManager = new HomeManager(this, homeDB);
        
        this.infoHomeCommandParser = new CommandParser();
        this.infoHomeCommandParser.addCommand(new HomeInfo(homeManager));
        
        this.setHomeCommandParser = new CommandParser();
        this.setHomeCommandParser.addCommand(new HomeSet(homeManager));
        
        this.tpHomeCommandParser = new CommandParser();
        this.tpHomeCommandParser.addCommand(new HomeTeleport(homeManager));
        
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.homeManager.updatePlayerName(event.getPlayer());
    }
    
    @Override
    public void onDisable() {
        instance = null;
        this.homeDB.disconnect();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("homeinfo")) {
            return this.infoHomeCommandParser.execute(sender, args);
        }
        else if(command.getName().equals("sethome")) {
            return this.setHomeCommandParser.execute(sender, args);
        }
        else if(command.getName().equals("home")) {
            return this.tpHomeCommandParser.execute(sender, args);
        }
        else {
            return false;
        }
    }
}