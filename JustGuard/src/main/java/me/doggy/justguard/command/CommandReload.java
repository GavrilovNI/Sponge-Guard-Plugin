package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CommandReload implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        JustGuard.getInstance().getConfigManager().loadConfig();
        src.sendMessage(Text.of(TextManager.getText(Texts.CMD_ANSWER_RELOAD)));
        return CommandResult.success();
    }
}