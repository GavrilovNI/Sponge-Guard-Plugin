package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CommandJustGuard implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        src.sendMessage(Text.of("JustGuard v"+JustGuard.PLUGIN_VERSION+" here."));

        return CommandResult.success();
    }
}
