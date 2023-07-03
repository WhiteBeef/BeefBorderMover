package ru.whitebeef.beefbordermover.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.whitebeef.beefbordermover.BeefBorderMover;
import ru.whitebeef.beefbordermover.entities.BorderEntity;
import ru.whitebeef.beefbordermover.entities.SkillType;
import ru.whitebeef.beeflibrary.chat.MessageSender;
import ru.whitebeef.beeflibrary.commands.AbstractCommand;
import ru.whitebeef.beeflibrary.commands.Alias;
import ru.whitebeef.beeflibrary.commands.SubCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BorderPaySubCommand extends SubCommand {

    public BorderPaySubCommand(String name, String permission, String description, String usageMessage, boolean onlyForPlayers, BiConsumer<CommandSender, String[]> onCommand, BiFunction<CommandSender, String[], List<String>> onTabComplete, Map<String, AbstractCommand> subCommands, List<Alias> aliases, int minArgsCount) {
        super(name, permission, description, usageMessage, onlyForPlayers, onCommand, onTabComplete, subCommands, aliases, minArgsCount);

    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        double value;
        try {
            value = Double.parseDouble(args[0]);
        } catch (Exception e) {
            MessageSender.sendMessageType(sender, BeefBorderMover.getInstance(), "not_a_number");
            return;
        }

        BigDecimal sum = BigDecimal.valueOf(value);

        if (value <= 0) {
            MessageSender.sendMessageType(sender, BeefBorderMover.getInstance(), "negative_number");
            return;
        }

        int response = BeefBorderMover.getInstance().getApi()
                .changePlayerBalance(player.getUniqueId(), player.getName(), sum, false, BeefBorderMover.getInstance().getName());

        if (response == 2) {
            MessageSender.sendMessageType(sender, BeefBorderMover.getInstance(), "not_enough_money");
            return;
        }
        BorderEntity.getInstance().getSkill(SkillType.of("balance")).incrementEXP(sum.doubleValue());
        MessageSender.sendMessageType(sender, BeefBorderMover.getInstance(), "success");
    }
}
