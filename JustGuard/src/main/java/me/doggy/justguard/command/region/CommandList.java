package me.doggy.justguard.command.region;

import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.command.CommandsRegistrator;
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

public class CommandList implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<Region.PlayerOwnership> ownershipOpt = args.getOne("ownership");
        Optional<Integer> pageOpt = args.getOne(CommandsRegistrator.PAGE);


        final int pageLength = 10;
        int page = pageOpt.orElse(1);

        Map<String, Region> regions;

        if(ownershipOpt.isPresent()) {
            if(CommandUtils.isPlayerExecuteCmd(src))
                return CommandResult.success();

            Region.PlayerOwnership ownership = ownershipOpt.get();
            regions = RegionsHolder.getRegions(x->x.getValue().getPlayerOwnership((Player) src).equals(ownership));
        } else {
            regions = RegionsHolder.getRegions();
        }

        MessageUtils.SendList(src, Arrays.asList(regions.entrySet().toArray()), page, pageLength, (key) -> {
            return Text.of(((Map.Entry<String, Region>)key).getKey());
        });


        return CommandResult.success();
    }
}