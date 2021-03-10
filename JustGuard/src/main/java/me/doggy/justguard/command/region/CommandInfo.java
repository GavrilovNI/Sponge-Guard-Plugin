package me.doggy.justguard.command.region;

import com.google.common.collect.Lists;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.CommandUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.RegionPair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandInfo implements CommandExecutor
{
    public CommandResult execute(CommandSource src, CommandContext args)
    {


        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);
        Optional<Integer> pageOpt = args.getOne(CommandsRegistrator.PAGE);

        if(regionIdOpt.isPresent()) {
            if(pageOpt.isPresent()) {
                MessageUtils.SendError(src, Text.of(Texts.ERR_CMD_TO_MUCH_ARGUMENTS));
                return CommandResult.success();
            }

            String regionId = regionIdOpt.get();

            Region region = RegionUtils.getAllRegions().get(regionId);

            //send region info here
            MessageUtils.Send(src, Text.of(regionId + ": "));
            MessageUtils.SendRegionInfo(src, region);

        }
        else
        {
            if(!CommandUtils.cmdOnlyForPlayers(src, true)) {
                return CommandResult.success();
            }

            final int pageLength = 10;
            int page = pageOpt.orElse(1);

            Player player = (Player) src;
            List<RegionPair> regions = RegionUtils.getRegionsInLocation(player.getLocation());

            MessageUtils.SendList(src, Arrays.asList(regions.toArray()), page, pageLength, (key) -> {
                RegionPair regionPair = (RegionPair) key;
                MessageUtils.Send(src, Text.of(regionPair.name + ": "));
                MessageUtils.SendRegionInfo(src, regionPair.region);
            });

        }


        return CommandResult.success();
    }
}