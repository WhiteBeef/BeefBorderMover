package ru.whitebeef.beefbordermover.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.whitebeef.beefbordermover.entities.BorderEntity;
import ru.whitebeef.beefbordermover.entities.Skill;
import ru.whitebeef.beefbordermover.entities.SkillType;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "beefbordermover";
    }

    @Override
    public String getAuthor() {
        return "_WhiteBeef_";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.isEmpty()) {
            return null;
        }
        if (player == null) {
            return "%" + getIdentifier() + "_" + params + "%";
        }
        //%beefrewards_rareReward_time%
        String[] split = params.split("_");
        Skill skill = BorderEntity.getInstance().getSkill(SkillType.of("balance"));

        if (skill == null) {
            return "%" + getIdentifier() + "_" + params + "%";
        }
        return switch (split[0].toLowerCase()) {
            case "balance" -> String.valueOf(Math.round(skill.getShowExp() * 100) / 100);
            case "totalbalance" -> String.valueOf(Math.round(skill.getExp() * 100) / 100);
            case "level" -> String.valueOf(skill.getLevel());
            case "nextlevelup" -> {
                int level = skill.getLevel();
                yield SkillType.of("balance").levelUPs().size() >= level ?
                        String.valueOf(skill.getSkillType().levelUPs().get(level - 1).getValue()) : "MAX";
            }
            case "description" -> String.join("\n", SkillType.of("balance").description());
            default -> "%" + getIdentifier() + "_" + params + "%";
        };
    }
}
