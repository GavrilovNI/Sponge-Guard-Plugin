package me.doggy.justguard.command.region.modify;

import me.doggy.justguard.JustGuard;
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

import java.util.Optional;

public class CommandSetOwnership implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);
        Optional<Player> playerToChangeOpt = args.getOne("player");
        Optional<Region.PlayerOwnership> stateOpt = args.getOne("state");

        if(!regionIdOpt.isPresent() || !playerToChangeOpt.isPresent() || !stateOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        Region region = JustGuard.REGIONS.get(regionIdOpt.get());

        if(!CommandUtils.canContinueModifyRegion(region, src, true))
            return CommandResult.success();

        region.setPlayerOwnership(playerToChangeOpt.get().getUniqueId(), stateOpt.get());
        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_SETOWNERSHIP,
                regionIdOpt.get(),
                playerToChangeOpt.get().getName(),
                TextManager.getText(stateOpt.get().name().toLowerCase())
        )));

        return CommandResult.success();
    }
}