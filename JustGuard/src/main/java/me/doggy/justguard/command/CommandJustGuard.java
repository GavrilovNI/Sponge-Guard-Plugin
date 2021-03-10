package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.utils.FileUtils;
import me.doggy.justguard.utils.FlagUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandJustGuard implements CommandExecutor
{
    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public CommandResult execute(CommandSource src, CommandContext args) {

        /*ConfigurationNode node = FileUtils.getFileNode("defaultFlags.conf", configManager.getConfigDir());
        ConfigurationNode flagNode = FlagUtils.getFlag(node, "player", "stranger", "block-place", "minecraft:wool");
        logger.info(flagNode.toString());*/

        return CommandResult.success();
    }
}
