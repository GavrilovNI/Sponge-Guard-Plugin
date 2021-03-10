package me.doggy.justguard.command.region;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandRemove implements CommandExecutor
{
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> nameOpt = args.getOne(CommandsRegistrator.REGION_ID);

        if(!nameOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String name = nameOpt.get();

        Region region = JustGuard.REGIONS.get(name);
        if(region == null) {
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_NO_REGION_FOUND)));
            return CommandResult.success();
        }
        else if(src instanceof Player)
        {
            Player player = (Player) src;
            if(!player.hasPermission(Permissions.REGION_REMOVE_ANY) &&
                !region.getPlayerOwnership(player.getUniqueId()).equals(Region.PlayerOwnership.Owner))
            {
                MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_NOT_REGION_OWNER)));
                return CommandResult.success();
            }
        }

        JustGuard.getInstance().removeRegion(name);
        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_REGION_REMOVED,
                name
        )));

        return CommandResult.success();
    }
}