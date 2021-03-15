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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;

public class CommandInfo implements CommandExecutor
{
    public CommandResult execute(CommandSource src, CommandContext args)
    {


        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);
        Optional<Integer> pageOpt = args.getOne(CommandsRegistrator.PAGE);

        if(regionIdOpt.isPresent()) {
            if(pageOpt.isPresent()) {
                MessageUtils.sendError(src, Text.of(Texts.ERR_CMD_TO_MUCH_ARGUMENTS));
                return CommandResult.success();
            }

            String regionId = regionIdOpt.get();
            Region region = RegionsHolder.getRegion(regionId);

            if(region == null) {
                MessageUtils.sendError(src, Text.of(TextManager.getText(
                        Texts.ERR_NO_REGION_FOUND,
                        regionId
                )));
                return CommandResult.success();
            }

            MessageUtils.send(src, Text.of(regionId + ": "));
            MessageUtils.sendRegionInfo(src, region);


        }
        else
        {
            if(!CommandUtils.isPlayerExecuteCmd(src)) {
                return CommandResult.success();
            }

            final int pageLength = 10;
            int page = pageOpt.orElse(1);

            Player player = (Player) src;
            Map<String, Region> regions = RegionsHolder.getRegions(x -> x.getValue().contains(player.getLocation()));

            MessageUtils.sendList(src, Arrays.asList(regions.entrySet()), page, pageLength, (key) -> {
                Map.Entry<String, Region> regionPair = (Map.Entry<String, Region>) key;
                MessageUtils.send(src, Text.of(regionPair.getKey() + ": "));
                MessageUtils.sendRegionInfo(src, regionPair.getValue());
            });

        }


        return CommandResult.success();
    }
}