package org.cubeville.cvhome.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;

public class HomeInfo extends Command {

    private final HomeManager homeManager;
    private final DateFormat dateFormat;

    public HomeInfo(HomeManager homeManager) {
        super("");
        addFlag("1");
        addFlag("2");
        addFlag("3");
        addFlag("4");
        addBaseParameter(new CommandParameterString());
        this.homeManager = homeManager.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {

        if(!player.hasPermission("cvhome.admin.infohome")) {
            throw new CommandExecutionException("&cNo permission.");
        }

        String playerName = (String) baseParameters.get(0);
        
        if(flags.size() == 0) {
            return getPlayerHomeInfo(homeManager, playerName.toLowerCase(), 0);
        }
        else if(flags.size() == 1) {
            int homeNumber;
            if(flags.contains("4")) { homeNumber = 4; }
            else if(flags.contains("3")) { homeNumber = 3; }
            else if(flags.contains("2")) { homeNumber = 2; }
            else if(flags.contains("1")) { homeNumber = 1; }
            else {
                throw new CommandExecutionException("&cHow did you even do that? Nevermind, don't do it again.");
            }
            return getPlayerHomeInfo(homeManager, playerName.toLowerCase(), homeNumber);
        }
        else {
            throw new CommandExecutionException("&cSyntax: /homeinfo <player> [homenumber]");
        }
    }
    
    private CommandResponse getPlayerHomeInfo(HomeManager homeManager, String playerName, int homeNumber)
            throws CommandExecutionException {
        
        if(homeManager.doesPlayerHaveHomes(playerName)) {
            CommandResponse info = new CommandResponse();
            Location loc;
            if(homeNumber == 0) {
                info.addMessage("&6Full home info for: &a" + playerName);
                info.addMessage("&bTotal Homes: " + homeManager.getAllPlayerHomes(playerName).size());
                for(Home home : homeManager.getAllPlayerHomes(playerName)) {
                    loc = home.getHomeLocation();
                    info.addMessage("&bHome " + home.getHomeNumber() + ":");
                    info.addMessage("&b - Date Set: " + dateFormat.format(new Date(home.getDateSet())));
                    info.addMessage("&b - World: " + Objects.requireNonNull(loc.getWorld()).getName());
                    info.addMessage("&b - x pos: " + loc.getX());
                    info.addMessage("&b - y pos: " + loc.getY());
                    info.addMessage("&b - z pos: " + loc.getZ());
                    info.addMessage("&b - Pitch: " + loc.getPitch());
                    info.addMessage("&b - Yaw: " + loc.getYaw());
                }
            }
            else {
                info.addMessage("&6Partial home info for: &a" + playerName);
                info.addMessage("&bHome " + homeNumber + ":");
                if(homeManager.getPlayerHome(playerName, homeNumber) == null) {
                    info.addMessage("&c - Home not set.");
                }
                else {
                    Home home = homeManager.getPlayerHome(playerName, homeNumber);
                    loc = home.getHomeLocation();
                    info.addMessage("&b - Date Set: " + dateFormat.format(new Date(home.getDateSet())));
                    info.addMessage("&b - World: " + Objects.requireNonNull(loc.getWorld()).getName());
                    info.addMessage("&b - x pos: " + loc.getX());
                    info.addMessage("&b - y pos: " + loc.getY());
                    info.addMessage("&b - z pos: " + loc.getZ());
                    info.addMessage("&b - Pitch: " + loc.getPitch());
                    info.addMessage("&b - Yaw: " + loc.getYaw());
                }
            }
            return info;
        }
        else {
            throw new CommandExecutionException("&cPlayer home not found.");
        }
    }
}
