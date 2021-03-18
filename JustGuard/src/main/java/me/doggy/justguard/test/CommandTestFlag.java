package me.doggy.justguard.test;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class CommandTestFlag implements CommandExecutor
{
    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public CommandResult execute(CommandSource src, CommandContext args) {

        Optional<String> flagIdOpt = args.getOne("flags-id");
        Optional<String> flagPathOpt = args.getOne(CommandsRegistrator.FLAG_PATH);

        if(!flagPathOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String flagId = flagIdOpt.isPresent() ? flagIdOpt.get() : ConfigManager.DEFAULT_REGION_FLAGS_NAME;
        Flags flags = configManager.getRegionFlags(flagId);
        FlagPath flagPath = FlagPath.parse(flagPathOpt.get());

        if(flags == null) {
            MessageUtils.sendError(src, Text.of("Flags '"+flagId+"' not found!"));
            return CommandResult.success();
        }
        MessageUtils.send(src, Text.of("Flag '"+flagPath.getFullPath()+"' Value: '"+flags.getFlag(flagPath).getValueToString()+"'"));

        return CommandResult.success();
    }
}