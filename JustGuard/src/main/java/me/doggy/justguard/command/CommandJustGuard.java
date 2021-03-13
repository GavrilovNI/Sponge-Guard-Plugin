package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.utils.FileUtils;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.flag.FlagValue;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.Optional;

public class CommandJustGuard implements CommandExecutor
{
    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public CommandResult execute(CommandSource src, CommandContext args) {

        Optional<String> flagOpt = args.getOne("flag");

        if(!flagOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        Flags flags = new Flags(configManager.getDefaultRegionFlags());
        FlagValue flag = flags.getFlag(FlagPath.parse(flagOpt.get()));
        logger.info(flag.toString());

        return CommandResult.success();
    }
}
