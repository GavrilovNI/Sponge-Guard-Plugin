package me.doggy.justguard.test;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class CommandRemoveTestWorld implements CommandExecutor
{
    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public CommandResult execute(CommandSource src, CommandContext args) {

        TestWorld.removeWorld();
        return CommandResult.success();
    }
}