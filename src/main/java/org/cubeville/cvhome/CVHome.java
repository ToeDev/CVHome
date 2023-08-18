package org.cubeville.cvhome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvhome.commands.HomeInfo;
import org.cubeville.cvhome.commands.HomeSet;
import org.cubeville.cvhome.commands.HomeTeleport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CVHome extends JavaPlugin implements Listener {

    private CommandParser infoHomeCommandParser;
    private CommandParser setHomeCommandParser;
    private CommandParser tpHomeCommandParser;

    private HomeDB homeDB;
    private HomeManager homeManager;
    private Integer deathDelay = 0;
    private Map<UUID, Long> deathTimes;
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        final File dataDir = getDataFolder();
        if(!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File configFile = new File(dataDir, "config.yml");
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                final InputStream inputStream = this.getResource(configFile.getName());
                final FileOutputStream fileOutputStream = new FileOutputStream(configFile);
                final byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = Objects.requireNonNull(inputStream).read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch(IOException e) {
                System.out.println("Unable to generate config file! " + e);
            }
        }
        YamlConfiguration mainConfig = new YamlConfiguration();
        try {
            mainConfig.load(configFile);
            deathDelay = mainConfig.getInt("death-delay");
        } catch(IOException | InvalidConfigurationException e) {
            System.out.println("Unable to load config file! " + e);
        }
        deathTimes = new HashMap<>();

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
        this.tpHomeCommandParser.addCommand(new HomeTeleport(this, homeManager));
        
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.homeManager.updatePlayerName(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.deathTimes.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
    }

    public boolean isDeathTimerExpired(UUID uuid) {
        if(deathTimes.containsKey(uuid)) {
            return ((System.currentTimeMillis() - deathTimes.get(uuid)) / 1000) >= deathDelay;
        }
        return true;
    }

    public Integer getDeathDelay() {
        return this.deathDelay;
    }
    
    @Override
    public void onDisable() {
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