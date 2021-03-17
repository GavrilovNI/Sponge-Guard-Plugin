package me.doggy.justguard.command.region;

import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.CommandUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandRemove implements CommandExecutor
{
    //not recommended to execute while any offline player in this world, otherwise when player connect the new world will be created
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);

        if(!regionIdOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String regionId = regionIdOpt.get();

        Region region = RegionsHolder.getRegion(regionId);

        if(!CommandUtils.isRegionFound(src, region, regionId))
            return CommandResult.success();
        if(!CommandUtils.canRemoveRegion(src, region, regionId))
            return CommandResult.success();

        RegionsHolder.removeRegion(regionId);
        MessageUtils.send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_REGION_REMOVED,
                regionId
        )));

        return CommandResult.success();
    }
}