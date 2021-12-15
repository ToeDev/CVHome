package org.cubeville.cvhome.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;

public class HomeSet extends Command {

    private final HomeManager homeManager;

    public HomeSet(HomeManager homeManager) {
        super("");
        setPermission("cvhome.set");
        addFlag("1");
        addFlag("2");
        addFlag("3");
        addFlag("4");
        addOptionalBaseParameter(new CommandParameterString());
        this.homeManager = homeManager.getInstance();
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {

        boolean adminOverride = player.hasPermission("cvhome.admin.sethome");
        boolean home2Perm = player.hasPermission("cvhome.home2");
        boolean home3Perm = player.hasPermission("cvhome.home3");
        boolean home4Perm = player.hasPermission("cvhome.home4");

        if(baseParameters.size() == 0) {
            
            if(flags.size() == 0) {
                return setPlayerHome(homeManager, player, 1, player.getLocation());
            }
            else if(flags.size() == 1) {
                int homeNumber;
                if(flags.contains("4")) {
                    if(!home4Perm && !adminOverride) throw new CommandExecutionException("&cNo permission.");
                    homeNumber = 4;
                }
                else if(flags.contains("3")) {
                    if(!home3Perm && !adminOverride) throw new CommandExecutionException("&cNo permission.");
                    homeNumber = 3;
                }
                else if(flags.contains("2")) {
                    if(!home2Perm && !adminOverride) throw new CommandExecutionException("&cNo permission.");
                    homeNumber = 2;
                }
                else if(flags.contains("1")) { homeNumber = 1; }
                else {
                    throw new CommandExecutionException("&cInternal error, please try again later.");
                }
                return setPlayerHome(homeManager, player, homeNumber, player.getLocation());
            }
            else {
                throw new CommandExecutionException("&cSyntax: /sethome");
                //TODO: REMOVE ABOVE, ADD BELOW, ONCE MULTIPLE HOMES HAS BEEN APPROVED.
                //throw new CommandExecutionException("&cSyntax: /sethome [number]");
            }
        }
        else {
            if(adminOverride) {

                String playerName = (String) baseParameters.get(0);

                if(flags.size() == 0) {
                    return setPlayerHome(homeManager, playerName, 1, player.getLocation());
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
                    return setPlayerHome(homeManager, playerName, homeNumber, player.getLocation());
                }
                else {
                    throw new CommandExecutionException("&cPlease only use 1 home at a time.");
                }
                
            }
            else {
                throw new CommandExecutionException("&cNo permission.");
            }
        }
    }
    
    private CommandResponse setPlayerHome(HomeManager homeManager, Player player, int homeNumber,
            Location location) throws CommandExecutionException {
        if(homeManager.doesPlayerHomeExist(player, homeNumber)) {
            homeManager.setPlayerHome(player, homeNumber, location);
        }
        else {
            Home playerHome;
            try {
                playerHome = new Home(location, homeNumber, player.getUniqueId(), player.getName().toLowerCase(), System.currentTimeMillis());
                homeManager.addPlayerHome(playerHome);
            }
            catch(IllegalArgumentException e) {
                throw new CommandExecutionException("&cInternal error, please try again later.");
            }
        }
        return new CommandResponse("&aHome set.");
    }
    
    private CommandResponse setPlayerHome(HomeManager homeManager, String playerName, int homeNumber,
            Location location) throws CommandExecutionException {
        
        if(homeManager.doesPlayerHomeExist(playerName.toLowerCase(), homeNumber)) {
            homeManager.setPlayerHome(playerName.toLowerCase(), homeNumber, location);
            return new CommandResponse("&aHome set.");
        }
        else {
            throw new CommandExecutionException("&cPlayer not found!&r &6Please visit&r " + 
                    "&6namemc.com and check other names for that player!&r");
        }
    }
}
