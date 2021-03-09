package me.doggy.justguard.command.region;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandSetOwnership implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> regionNameOpt = args.getOne(CommandsRegistrator.REGION_ID);
        Optional<Player> playerToChangeOpt = args.getOne("player");
        Optional<Region.PlayerState> stateOpt = args.getOne("state");

        if(!regionNameOpt.isPresent()
                || !playerToChangeOpt.isPresent()
                || !stateOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        Region region = JustGuard.REGIONS.get(regionNameOpt.get());
        if(region == null) {
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_NOT_REGION_OWNER)));
            return CommandResult.success();
        }
        else if(src instanceof Player && !region.getPlayerState(((Player) src).getUniqueId()).equals(Region.PlayerState.Owner)) {
            JustGuard.getInstance().getLogger().info(region.getPlayerState(((Player) src).getUniqueId()).name());
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_NOT_REGION_OWNER)));
            return CommandResult.success();
        }

        region.setPlayerState(playerToChangeOpt.get().getUniqueId(), stateOpt.get());
        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_SETOWNERSHIP,
                regionNameOpt.get(),
                playerToChangeOpt.get().getName(),
                TextManager.getText(stateOpt.get().name().toLowerCase())
        )));

        return CommandResult.success();
    }
}