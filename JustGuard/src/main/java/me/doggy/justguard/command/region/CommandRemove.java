package me.doggy.justguard.command.region;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.consts.Permissions;
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
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);

        if(!regionIdOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String regionId = regionIdOpt.get();

        Region region = JustGuard.REGIONS.get(regionId);

        if(!src.hasPermission(Permissions.REGION_REMOVE_ANY)) {
            if(!CommandUtils.canContinueModifyRegion(region, src, true)) {
                return CommandResult.success();
            }
        }

        JustGuard.getInstance().removeRegion(regionId);
        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_REGION_REMOVED,
                regionId
        )));

        return CommandResult.success();
    }
}