package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.MessageUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CommandSave implements CommandExecutor
{
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        JustGuard.getInstance().saveRegions();
        MessageUtils.Send(src, Text.of(TextManager.getText(Texts.CMD_ANSWER_REGIONS_SAVED)));

        return CommandResult.success();
    }
}