package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CommandVersion implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        src.sendMessage(Text.of("Version: "+JustGuard.PLUGIN_VERSION));

        JustGuard.getInstance().getConfigManager().test();

        return CommandResult.success();
    }
}