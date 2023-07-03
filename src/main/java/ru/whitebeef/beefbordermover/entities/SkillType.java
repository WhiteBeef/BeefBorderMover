package ru.whitebeef.beefbordermover.entities;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SkillType(String namespace, List<String> description, List<LevelUp> levelUPs) {

    private static final Map<String, SkillType> registeredTypes = new HashMap<>();

    public static void registerTypes(ConfigurationSection section) {
        for (String skillTypeNamespace : section.getKeys(false)) {
            List<LevelUp> levelUPs = new ArrayList<>();
            ConfigurationSection levelUPsSection = section.getConfigurationSection(skillTypeNamespace + ".levelups");
            for (String index : levelUPsSection.getKeys(false)) {
                levelUPs.add(new LevelUp(Integer.parseInt(index),
                        levelUPsSection.getDouble(index + ".value"),
                        levelUPsSection.getStringList(index + ".messages"),
                        levelUPsSection.getStringList(index + ".awardCommands")));
            }
            registerTypes(new SkillType(skillTypeNamespace,
                    section.getStringList(skillTypeNamespace + ".description"),
                    levelUPs));
        }
    }

    public static void registerTypes(SkillType... types) {
        for (SkillType type : types) {
            registeredTypes.put(type.namespace(), type);
        }
    }

    @Nullable
    public static SkillType of(String namespace) {
        return registeredTypes.get(namespace);
    }

    public static Map<String, SkillType> getRegisteredTypes() {
        return new HashMap<>(registeredTypes);
    }

    public SkillType(String namespace, List<String> description, List<LevelUp> levelUPs) {
        this.namespace = namespace;
        this.description = description;
        this.levelUPs = levelUPs;
        Collections.sort(levelUPs);
    }

    @Override
    public List<LevelUp> levelUPs() {
        return new ArrayList<>(levelUPs);
    }
}

