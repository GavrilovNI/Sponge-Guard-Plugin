package me.doggy.justguard.test;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CommandRemoveTestWorld implements CommandExecutor
{
    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public CommandResult execute(CommandSource src, CommandContext args) {

        if(TestWorld.getWorld().isPresent()) {
            if (TestWorld.removeWorld())
                MessageUtils.send(src, Text.of("Test world removed."));
            else
                MessageUtils.sendError(src, Text.of("Test world not removed."));
        } else {
            MessageUtils.sendError(src, Text.of("Test world already removed."));
        }
        return CommandResult.success();
    }
}