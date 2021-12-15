package org.cubeville.cvhome;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.UUID;

public class HomeDB extends HomeSQL {

    public HomeDB(CVHome cvHome) {
        super(cvHome);
    }

    public String SQLiteCreateHomesTable = "CREATE TABLE IF NOT EXISTS homes (" +
            "`homeID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`homeNumber` TINYINT NOT NULL," +
            "`playerID` varchar(32) NOT NULL," +
            "`playerName` varchar(16) NOT NULL," +
            "`world` varchar(64) NOT NULL," +
            "`x` BIGINT NOT NULL," +
            "`y` BIGINT NOT NULL," +
            "`z` BIGINT NOT NULL," +
            "`pitch` BIGINT NOT NULL," +
            "`yaw` BIGINT NOT NULL," +
            "`timeCreated` BIGINT NOT NULL" +
            ");";

    public void load() {
        connect();
        update(SQLiteCreateHomesTable);
    }

    public ResultSet getAllHomes() {
        return getResult("SELECT * FROM `homes`");
    }

    public void createBackup(JavaPlugin plugin) throws IOException {
        File dbFile = new File(plugin.getDataFolder(), "homes.db");
        if(dbFile.exists()) {
            Path source = dbFile.toPath();
            Path target = plugin.getDataFolder().toPath();
            Files.copy(source, target.resolve("homes-backup.db"), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void addHome(Home home) {
        Location loc = home.getHomeLocation();
        update("INSERT INTO `homes` (homeNumber, playerID, playerName, world, x, y, z, pitch, yaw, timeCreated) " +
                "VALUES(\"" + home.getHomeNumber() + "\", \"" + home.getPlayerId().toString() + "\", \"" + home.getPlayerName() + "\", \"" + Objects.requireNonNull(loc.getWorld()).getName() + "\", \"" + loc.getX() + "\", \"" + loc.getY() + "\", \"" + loc.getZ() + "\", \"" + loc.getPitch() + "\", \"" + loc.getYaw() + "\", \"" + home.getDateSet() + "\");"
        );
    }

    public void updateHome(Home home) {
        Location loc = home.getHomeLocation();
        update("UPDATE `homes`" +
                " SET" +
                " world = \"" + Objects.requireNonNull(loc.getWorld()).getName() + "\"," +
                " x = \"" + loc.getX() + "\"," +
                " y = \"" + loc.getY() + "\"," +
                " z = \"" + loc.getZ() + "\"," +
                " pitch = \"" + loc.getPitch() + "\"," +
                " yaw = \"" + loc.getYaw() + "\"," +
                " timeCreated = \"" + home.getDateSet() + "\"" +
                " WHERE playerID = \"" + home.getPlayerId() + "\"" +
                " AND homeNumber = \"" + home.getHomeNumber() + "\";"
        );
    }

    public void updateName(UUID playerID, String playerName) {
        update("UPDATE `homes`" +
                " SET playerName = \"" + playerName + "\"" +
                " WHERE playerID = \"" + playerID + "\";"
        );
    }
}
