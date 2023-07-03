package ru.whitebeef.beefbordermover.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.beefbordermover.entities.BorderEntity;
import ru.whitebeef.beefbordermover.entities.Skill;
import ru.whitebeef.beefbordermover.entities.SkillType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class Database {
    private static Database database;

    public static Database getDatabase() {
        return database;
    }

    public static void setupDatabase(Plugin plugin) {
        if (plugin.getConfig().getBoolean("database.use-mysql")) {
            try {
                database = new MySQL(plugin.getConfig());
                setupTables();
                return;
            } catch (Exception ex) {
                Bukkit.getLogger().info("Couldn't connect to the database! Using SQLite instead.");
            }
        }
        database = new SQLite(plugin.getDataFolder());
        setupTables();
    }

    public static void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    protected static final String BEEF_BORDER_MOVER_TABLE = "BeefBorderMover";

    private static final String BEEF_BORDER_MOVER_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + BEEF_BORDER_MOVER_TABLE + "(id TEXT PRIMARY KEY, " +
            " skills TEXT NOT NULL) ;";

    public static void setupTables() {
        try (Connection con = openConnection();
             PreparedStatement products = con.prepareStatement(BEEF_BORDER_MOVER_TABLE_QUERY)) {

            products.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Connection openConnection() throws SQLException {
        return database.getConnection();
    }

    public abstract Connection getConnection() throws SQLException;

    public abstract void close();

    public abstract boolean saveBorderEntity(BorderEntity borderEntity);

    @Nullable
    public abstract BorderEntity getBorderEntity();

    protected Map<String, Skill> getSkills(ResultSet rs) throws SQLException {
        HashMap<String, Skill> map = new HashMap<>();
        String[] skills = rs.getString("skills").split(";");
        for (String skill : skills) {
            String[] arr = skill.split(":");
            SkillType type = SkillType.of(arr[0]);
            double exp;
            try {
                exp = Double.parseDouble(arr[1]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (type == null) {
                continue;
            }
            map.put(arr[0], new Skill(type, exp));
        }
        return map;
    }

    protected String getSkills(Map<String, Skill> skills) {
        StringBuilder sb = new StringBuilder();

        for (Skill skill : skills.values()) {
            sb.append(skill.getSkillType().namespace()).append(":").append(skill.getExp()).append(";");
        }

        return sb.toString();
    }
}
