package ru.whitebeef.beefbordermover.database;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.beefbordermover.entities.BorderEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class MySQL extends Database {

    private static Connection connection = null;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String port;
    private final String SQL;


    public MySQL(FileConfiguration config) throws SQLException {
        host = config.getString("database.host");
        port = config.getString("database.port");
        database = config.getString("database.database");
        username = config.getString("database.username");
        password = config.getString("database.password");
        SQL = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&passwordCharacterEncoding=utf8&characterEncoding=utf8&useSSL=false&useTimezone=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        setupPool();
    }

    private void setupPool() throws SQLException {
        connection = getConnection();
    }


    private synchronized Connection connect() {
        try {
            if (!connection.isClosed()) {
                return connection;
            }
        } catch (Exception ignored) {
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return connection = DriverManager.getConnection(SQL, username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
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


    public Connection getConnection(boolean forceConnect) throws SQLException {
        if (forceConnect) {
            connect();
        }
        return getConnection();
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            return connect();
        }
        return connection;
    }

}
