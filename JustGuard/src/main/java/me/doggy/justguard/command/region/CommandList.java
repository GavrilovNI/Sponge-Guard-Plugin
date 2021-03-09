package me.doggy.justguard.command.region;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class CommandList implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Map<String, Region> REGIONS = JustGuard.REGIONS;

        Optional<Integer> pageOpt = args.getOne(CommandsRegistrator.PAGE);
        final int pageLength = 10;
        int page = pageOpt.orElse(1);

        List<String> namesList = new ArrayList<String>(REGIONS.keySet());

        MessageUtils.SendList(src, namesList, page, pageLength/*, (key)->{
            Region region = REGIONS.get(key);

            TextColor nameColor = TextColors.RED;
            Region.RegionType regionType = region.getRegionType();
            if (regionType.equals(Region.RegionType.Global))
                nameColor = TextColors.GREEN;
            else if (regionType.equals(Region.RegionType.Local))
                nameColor = TextColors.WHITE;

            return Text.of(nameColor, key);
        }*/);

        return CommandResult.success();
    }
}