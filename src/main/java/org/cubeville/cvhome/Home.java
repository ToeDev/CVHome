package org.cubeville.cvhome;

import java.util.UUID;

import org.bukkit.Location;

public class Home {

    private Location home;
    private final int homeNumber;
    private long dateSet;

    private final UUID playerId;
    private String playerName;

    public Home(Location home, int homeNumber, UUID playerId, String playerName, long dateSet) {
        this.home = home;
        this.homeNumber = homeNumber;
        this.playerId = playerId;
        this.playerName = playerName;
        this.dateSet = dateSet;
    }

    public int getHomeNumber() {
        return this.homeNumber;
    }
    
    public Location getHomeLocation() {
        return this.home;
    }
    
    public void setHome(Location loc) { //int homeNumber
        this.home = loc;
    }
    
    public UUID getPlayerId() {
        return this.playerId;
    }

    public String getPlayerName() {
        return  this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getDateSet() {
        return this.dateSet;
    }

    public void setDateSet(long dateSet) {
        this.dateSet = dateSet;
    }
}
