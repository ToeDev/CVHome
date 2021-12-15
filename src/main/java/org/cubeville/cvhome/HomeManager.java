package org.cubeville.cvhome;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HomeManager {

    private final Plugin plugin;
    private final List<Home> playerHomes;
    private final HomeManager instance;
    private final HomeDB homeDB;
    
    public HomeManager(Plugin plugin, HomeDB homeDB) {
        this.plugin = plugin;
        this.homeDB = homeDB;
        this.playerHomes = new ArrayList<>();
        importHomesFromDB(homeDB);
        instance = this;
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    public HomeManager getInstance() {
        return this.instance;
    }

    public void importHomesFromDB(HomeDB homeDB) {
        try {
            ResultSet homesSet = homeDB.getAllHomes();
            if(homesSet == null) return;
            while(homesSet.next()) {
                Home home = new Home(new Location(Bukkit.getWorld(homesSet.getString("world")), homesSet.getFloat("x"), homesSet.getFloat("y"), homesSet.getFloat("z"), homesSet.getFloat("yaw"), homesSet.getFloat("pitch")), homesSet.getInt("homeNumber"), UUID.fromString(homesSet.getString("playerID")), homesSet.getString("playerName"), homesSet.getLong("timeCreated"));
                this.playerHomes.add(home);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean doesPlayerHomeExist(Player player, int homeNumber) {
        return getPlayerHome(player, homeNumber) != null;
    }

    public boolean doesPlayerHomeExist(UUID playerID, int homeNumber) {
        return getPlayerHome(playerID, homeNumber) != null;
    }
    
    public boolean doesPlayerHomeExist(String playerName, int homeNumber) {
        return getPlayerHome(playerName, homeNumber) != null;
    }

    public boolean doesPlayerHaveHomes(Player player) {
        return getAllPlayerHomes(player.getUniqueId()).size() > 0;
    }

    public boolean doesPlayerHaveHomes(UUID playerID) {
        return getAllPlayerHomes(playerID).size() > 0;
    }

    public boolean doesPlayerHaveHomes(String playerName) {
        return getAllPlayerHomes(playerName).size() > 0;
    }
    
    public void setPlayerHome(Player player, int homeNumber, Location location) {
        if(!doesPlayerHomeExist(player, homeNumber)) { return; }
        Home playerHome = getPlayerHome(player, homeNumber);
        playerHome.setHome(location);
        playerHome.setDateSet(System.currentTimeMillis());
        updatePlayerHome(player.getUniqueId(), playerHome, homeNumber);
        saveExistingHome(playerHome);
    }
    
    public void setPlayerHome(String playerName, int homeNumber, Location location) {
        if(!doesPlayerHomeExist(playerName, homeNumber)) { return; }
        Home playerHome = getPlayerHome(playerName, homeNumber);
        playerHome.setHome(location);
        playerHome.setDateSet(System.currentTimeMillis());
        updatePlayerHome(playerName, playerHome, homeNumber);
        saveExistingHome(playerHome);
    }
    
    public void addPlayerHome(Home playerHome) {
        playerHomes.add(playerHome);
        saveNewHome(playerHome);
    }
    
    private void updatePlayerHome(UUID playerId, Home playerHome, int homeNumber) {
        int i = 0;
        for(Home home : playerHomes) {
            if(home.getPlayerId().equals(playerId) && home.getHomeNumber() == homeNumber) {
                playerHomes.set(i, playerHome);
                break;
            }
            i++;
        }
    }
    
    private void updatePlayerHome(String playerName, Home playerHome, int homeNumber) {
        int i = 0;
        for(Home home : playerHomes) {
            if(Objects.equals(home.getPlayerName(), playerName) && home.getHomeNumber() == homeNumber) {
                 playerHomes.set(i, playerHome);
                 break;
            }
            i++;
        }
    }

    public List<Home> getAllPlayerHomes(UUID playerID) {
        List<Home> allPlayerHomes = new ArrayList<>();
        for(Home home : playerHomes) {
            if(home.getPlayerId().equals(playerID)) {
                allPlayerHomes.add(home);
            }
        }
        return allPlayerHomes;
    }

    public List<Home> getAllPlayerHomes(String playerName) {
        List<Home> allPlayerHomes = new ArrayList<>();
        for(Home home : playerHomes) {
            if(Objects.equals(home.getPlayerName(), playerName)) {
                allPlayerHomes.add(home);
            }
        }
        return allPlayerHomes;
    }

    public Home getPlayerHome(Player player, int homeNumber) {
        for(Home home : this.playerHomes) {
            if(home.getPlayerId().equals(player.getUniqueId()) && home.getHomeNumber() == homeNumber ) {
                return home;
            }
        }
        return null;
    }

    public Home getPlayerHome(UUID playerID, int homeNumber) {
        for(Home home : this.playerHomes) {
            if(home.getPlayerId().equals(playerID) && home.getHomeNumber() == homeNumber ) {
                return home;
            }
        }
        return null;
    }
    
    public Home getPlayerHome(String playerName, int homeNumber) {
        for(Home home : this.playerHomes) {
            if(Objects.equals(home.getPlayerName(), playerName) && home.getHomeNumber() == homeNumber ) {
                return home;
            }
        }
        return null;
    }

    private void saveNewHome(Home home) {
        homeDB.addHome(home);
    }

    private void saveExistingHome(Home home) {
        homeDB.updateHome(home);
    }

    public void updatePlayerName(Player player) {
        if(!doesPlayerHaveHomes(player)) return;
        for(Home home : getAllPlayerHomes(player.getUniqueId())) {
            if(!home.getPlayerName().equals(player.getName().toLowerCase())) {
                home.setPlayerName(player.getName().toLowerCase());
                homeDB.updateName(player.getUniqueId(), player.getName().toLowerCase());
            }
        }
    }
}
