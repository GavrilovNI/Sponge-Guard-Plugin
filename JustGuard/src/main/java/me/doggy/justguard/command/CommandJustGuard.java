package me.doggy.justguard.command;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class CommandJustGuard implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        return CommandResult.success();
    }
}
