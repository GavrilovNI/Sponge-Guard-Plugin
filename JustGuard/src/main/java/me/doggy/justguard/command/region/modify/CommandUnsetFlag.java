package me.doggy.justguard.command.region.modify;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class CommandUnsetFlag implements CommandExecutor
{
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        args.putArg("value", "null");
        return new CommandSetFlag().execute(src, args);
    }
}