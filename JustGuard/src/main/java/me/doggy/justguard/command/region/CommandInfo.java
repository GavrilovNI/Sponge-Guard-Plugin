package me.doggy.justguard.command.region;

import com.google.common.collect.Lists;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.config.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.RegionPair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.AABB;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class CommandInfo implements CommandExecutor
{
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextManager.getText(Texts.ERR_CMD_ONLY_FOR_PLAYERS)));
        }

        Optional<Integer> pageOpt = args.getOne(CommandsRegistrator.PAGE);
        final int pageLength = 10;
        int page = pageOpt.orElse(1);

        Player player = (Player) src;
        //AABB
        List<RegionPair> regions = RegionUtils.getRegionsInLocation(player.getLocation());
        java.util.List<String> namesList = Lists.transform(regions, (pair) -> { return pair.name; });

        MessageUtils.SendList(src, namesList, page, pageLength/*, (key)->{
            Region region = regions.stream().filter(x->x.name.equals(key)).findFirst().get().region;

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