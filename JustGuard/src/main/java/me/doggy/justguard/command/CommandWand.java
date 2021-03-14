package me.doggy.justguard.command;

import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.CommandUtils;
import me.doggy.justguard.utils.InventoryUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandWand implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        if(!CommandUtils.isPlayerExecuteCmd(src)) {
            return CommandResult.success();
        }

        Player player = (Player) src;
        InventoryUtils.addItemStackToInventoryDelayed(player, InventoryUtils.GetSelector());
        src.sendMessage(Text.of(TextManager.getText(Texts.CMD_ANSWER_WAND)));

        return CommandResult.success();
    }
}