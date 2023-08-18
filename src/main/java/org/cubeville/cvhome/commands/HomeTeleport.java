package org.cubeville.cvhome.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.CVHome;
import org.cubeville.cvhome.HomeManager;

public class HomeTeleport extends Command {

    private final CVHome plugin;
    private final HomeManager homeManager;

    public HomeTeleport(CVHome plugin, HomeManager homeManager) {
        super("");
        addFlag("1");
        addFlag("2");
        addFlag("3");
        addFlag("4");
        addOptionalBaseParameter(new CommandParameterString());
        this.plugin = plugin;
        this.homeManager = homeManager.getInstance();
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {

        boolean adminOverride = player.hasPermission("cvhome.admin.teleporthome");
        boolean home2Perm = player.hasPermission("cvhome.home2");
        boolean home3Perm = player.hasPermission("cvhome.home3");
        boolean home4Perm = player.hasPermission("cvhome.home4");

        if(!plugin.isDeathTimerExpired(player.getUniqueId())) {
            throw new CommandExecutionException("&cYou must wait " + plugin.getDeathDelay() + " seconds after death before using /home.");
        }
        
        if(baseParameters.size() == 0) {
            if(flags.size() == 0) {
                return teleportToPlayerHome(homeManager, player, player.getName().toLowerCase(), 1);
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
                return teleportToPlayerHome(homeManager, player, player.getName().toLowerCase(), homeNumber);
            }
            else {
                throw new CommandExecutionException("&cSyntax: /home");
                //TODO: REMOVE ABOVE, ADD BELOW, ONCE MULTIPLE HOMES HAS BEEN APPROVED.
                //throw new CommandExecutionException("&cSyntax: /home [number]");
            }
        }
        else {
            String playerName = (String) baseParameters.get(0);
            if(adminOverride) {
                
                if(flags.size() == 0) {
                    return teleportToPlayerHome(homeManager, player, playerName.toLowerCase(), 1);
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
                    return teleportToPlayerHome(homeManager, player, playerName.toLowerCase(), homeNumber);
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
    
    private CommandResponse teleportToPlayerHome(HomeManager homeManager, Player sender, String playerName,
            int homeNumber) throws CommandExecutionException {
        
        if(homeManager.doesPlayerHomeExist(playerName, homeNumber)) {
            sender.teleport(homeManager.getPlayerHome(playerName, homeNumber).getHomeLocation());
            return new CommandResponse("&aTeleported.");
        }
        else {
            throw new CommandExecutionException("&cPlayer home not found.");
        }
    }
}
