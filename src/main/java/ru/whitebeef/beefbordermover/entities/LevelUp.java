package ru.whitebeef.beefbordermover.entities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LevelUp implements Comparable<LevelUp> {

    private final int index;
    private final double value;
    private final List<String> messages;
    private final List<String> commands;

    public LevelUp(int index, double value, List<String> messages, List<String> commands) {
        this.index = index;
        this.value = value;
        this.messages = messages;
        this.commands = commands;
    }

    public double getValue() {
        return value;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public int compareTo(@NotNull LevelUp o) {
        return Integer.compare(index, o.index);
    }
}
