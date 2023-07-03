package ru.whitebeef.beefbordermover.entities;

import org.bukkit.Bukkit;
import ru.whitebeef.beeflibrary.chat.MessageSender;
import ru.whitebeef.beeflibrary.utils.ScheduleUtils;

import java.util.ArrayList;

public class Skill {

    private final SkillType skillType;
    private double exp;
    private int level = -1;

    public Skill(BorderEntity borderEntity, SkillType skillType) {
        this.skillType = skillType;
        this.exp = 0;
        borderEntity.addSkill(this);
        borderEntity.lazySave();
    }

    public Skill(SkillType skillType, double exp) {
        this.skillType = skillType;
        this.exp = exp;
    }

    public void incrementEXP(double value) {
        exp += value;
        checkLevelUp(exp - value);
        BorderEntity.getInstance().lazySave();
    }

    private void checkLevelUp(double lastExpValue) {
        int lastLevel = getLevel(lastExpValue);
        int level = getLevel(exp);
        if (lastLevel < level) {
            for (int i = 1; i <= level - lastLevel; i++) {
                levelUP(skillType.levelUPs().get(lastLevel + i - 2));
            }
        }
    }

    public int getLevel() {
        if (level != -1) {
            return level;
        }
        return getLevel(exp);
    }

    private int getLevel(double exp) {
        int level = 1;
        while (level <= skillType.levelUPs().size() && (getShowExp(exp, level) >= skillType.levelUPs().get(level - 1).getValue())) {
            level++;
        }

        return level;
    }

    public void setExp(double exp) {
        double last = this.exp;
        this.exp = exp;
        checkLevelUp(last);
        BorderEntity.getInstance().lazySave();
    }

    /**
     * @return real exp value
     */
    public double getExp() {
        return exp;
    }

    /**
     * @return exp value of level
     */
    public double getShowExp() {
        return getShowExp(this.exp, getLevel());
    }

    /**
     * @return exp value of level
     */
    private double getShowExp(double exp, int level) {
        for (int i = 1; i < level; i++) {
            exp -= skillType.levelUPs().get(i - 1).getValue();
        }
        return exp;
    }

    private void levelUP(LevelUp levelUP) {
        ScheduleUtils.runTask(() -> {
            levelUP.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            new ArrayList<>(Bukkit.getOnlinePlayers())
                    .forEach(player -> levelUP.getMessages().forEach(message -> MessageSender.sendMessage(player, message)));
        });
    }

    public SkillType getSkillType() {
        return skillType;
    }
}
