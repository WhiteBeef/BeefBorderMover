package ru.whitebeef.beefbordermover;

import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.plugin.java.JavaPlugin;
import ru.whitebeef.beefbordermover.commands.BorderPaySubCommand;
import ru.whitebeef.beefbordermover.database.Database;
import ru.whitebeef.beefbordermover.entities.BorderEntity;
import ru.whitebeef.beefbordermover.entities.SkillType;
import ru.whitebeef.beefbordermover.placeholderapi.PlaceholderAPIHook;
import ru.whitebeef.beeflibrary.BeefLibrary;
import ru.whitebeef.beeflibrary.commands.AbstractCommand;
import ru.whitebeef.beeflibrary.commands.SimpleCommand;

public final class BeefBorderMover extends JavaPlugin {

    private static BeefBorderMover instance;
    private XConomyAPI api;

    public static BeefBorderMover getInstance() {
        return instance;
    }

    public XConomyAPI getApi() {
        return api;
    }

    @Override
    public void onEnable() {
        instance = this;
        api = new XConomyAPI();

        BeefLibrary.loadConfig(this);

        Database.closeDatabase();
        Database.setupDatabase(this);
        SkillType.registerTypes(getConfig().getConfigurationSection("skills"));

        AbstractCommand.builder("border", SimpleCommand.class)
                .addSubCommand(AbstractCommand.builder("pay", BorderPaySubCommand.class)
                        .setMinArgsCount(1)
                        .build())
                .setMinArgsCount(2)
                .setOnlyForPlayers(true)
                .setPermission("beefbordermover.command.border")
                .build().register(this);

        BorderEntity.lazyLoad();

        BeefLibrary.getInstance().registerPlaceholders(this, new PlaceholderAPIHook());
        BorderEntity.startLazySaveTask();
    }

    @Override
    public void onDisable() {
        BorderEntity.stopLazySaveTask();
        BorderEntity.saveAll();

        Database.closeDatabase();
    }
}
