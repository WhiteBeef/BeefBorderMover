package ru.whitebeef.beefbordermover.entities;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import ru.whitebeef.beefbordermover.BeefBorderMover;
import ru.whitebeef.beefbordermover.database.Database;

import java.util.HashMap;
import java.util.Map;

public class BorderEntity {

    private static BorderEntity instance;

    public static BorderEntity getInstance() {
        BorderEntity borderEntity = instance;

        if (borderEntity != null) {
            return borderEntity;
        }

        borderEntity = Database.getDatabase().getBorderEntity();

        if (borderEntity != null) {
            instance = borderEntity;
            return borderEntity;
        }

        borderEntity = new BorderEntity();
        borderEntity.lazySave();

        instance = borderEntity;

        return borderEntity;
    }

    private static boolean toSave = false;
    private static final BukkitRunnable lazySaveTask = new BukkitRunnable() {
        @Override
        public void run() {
            BorderEntity.saveAll();
        }
    };

    public static void startLazySaveTask() {
        lazySaveTask.runTaskTimerAsynchronously(BeefBorderMover.getInstance(), 6000L, 6000L);
    }

    public static void stopLazySaveTask() {
        lazySaveTask.cancel();
    }

    public static void lazyLoad() {
        Bukkit.getScheduler().runTaskAsynchronously(BeefBorderMover.getInstance(), BorderEntity::getInstance);
    }

    public static void saveAll() {
        getInstance().save();
    }


    private Map<String, Skill> skills = new HashMap<>();

    private BorderEntity() {
        SkillType.getRegisteredTypes().values().forEach(skillType -> {
            skills.put(skillType.namespace(), new Skill(this, skillType));
        });
    }

    public BorderEntity(Map<String, Skill> skills) {
        this.skills = skills;
    }

    @Nullable
    public Skill getSkill(SkillType skillType) {
        if (skillType == null) {
            return null;
        }
        return skills.getOrDefault(skillType.namespace(), new Skill(this, skillType));
    }


    public boolean addSkill(Skill skill) {
        if (skills.containsKey(skill.getSkillType().namespace())) {
            return false;
        }
        skills.put(skill.getSkillType().namespace(), skill);
        return true;
    }

    public Map<String, Skill> getSkills() {
        return skills;
    }

    public void lazySave() {
        toSave = true;
    }

    public boolean save() {
        return Database.getDatabase().saveBorderEntity(this);
    }

}
