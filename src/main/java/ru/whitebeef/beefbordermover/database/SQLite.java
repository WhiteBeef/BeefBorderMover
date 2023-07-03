package ru.whitebeef.beefbordermover.database;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.beefbordermover.entities.BorderEntity;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database {

    private static final String DATABASE = "database";
    private final File dataFolder;

    public SQLite(File folder) {
        dataFolder = new File(folder, DATABASE + ".db");
        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) {
                    Bukkit.getLogger().info("Could not create a database file!");
                }
            } catch (IOException e) {
                Bukkit.getLogger().info("File write error: " + DATABASE + ".db");
            }
        }
    }
    @Override
    public boolean saveBorderEntity(BorderEntity borderEntity) {
        boolean saved = true;

        String SQL = "INSERT INTO " + BEEF_BORDER_MOVER_TABLE + " (id, skills) VALUES (?,?) " +
                "ON CONFLICT(id) DO UPDATE SET skills = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setString(1, "border");
            statement.setString(2, getSkills(borderEntity.getSkills()));

            statement.setString(3, getSkills(borderEntity.getSkills()));

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            saved = false;
        }
        return saved;
    }

    @Override
    @Nullable
    public BorderEntity getBorderEntity() {
        BorderEntity rpgPlayer = null;
        String SQL = "SELECT * FROM " + BEEF_BORDER_MOVER_TABLE + " WHERE id = 'border' LIMIT 1;";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(SQL)) {
            if (rs.next()) {
                rpgPlayer = new BorderEntity(getSkills(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rpgPlayer;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
    }


    @Override
    public void close() {
        // We have nothing to close
    }
}
