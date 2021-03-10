package me.doggy.justguard.command.region;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
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

import java.util.*;

public class CommandList implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Map<String, Region> REGIONS = JustGuard.REGIONS;


        Optional<Region.PlayerOwnership> ownershipOpt = args.getOne("ownership");
        Optional<Integer> pageOpt = args.getOne(CommandsRegistrator.PAGE);


        final int pageLength = 10;
        int page = pageOpt.orElse(1);

        List<RegionPair> regions = RegionUtils.swapToList(RegionUtils.getAllRegions());

        if(ownershipOpt.isPresent())
        {
            if(CommandUtils.cmdOnlyForPlayers(src, true))
                return CommandResult.success();

            regions = RegionUtils.getRegionsByOwnership(regions, (Player) src, ownershipOpt.get());
        }

        MessageUtils.SendList(src, Arrays.asList(regions.toArray()), page, pageLength, (key) -> { return Text.of(((RegionPair)key).name); });


        return CommandResult.success();
    }
}