package me.doggy.justguard.command.region.pending;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.Pending;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.utils.help.MyAABB;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;

import java.util.Optional;

public class CommandExpand implements CommandExecutor
{
    Logger logger = JustGuard.getInstance().getLogger();

    public CommandResult execute(CommandSource source, CommandContext args)
    {
        Optional<Integer> countOpt = args.getOne("length");

        if(!countOpt.isPresent()) {
            return CommandResult.builder().successCount(0).build();
        }

        Region.Builder regionBuilder = Pending.getRegion(source);
        if(regionBuilder==null) {
            MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
            return CommandResult.success();
        }

        Optional<Direction> directionOpt = args.getOne("direction");
        int count = countOpt.get();
        if(directionOpt.isPresent()) {

            Direction direction = directionOpt.get();
            if(!MyAABB.Builder.isDirectionAvaliableToExpand(direction)) {
                MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_WRONG_DIRECTION_TO_EXPAND)));
                return CommandResult.success();

            }

            regionBuilder.getAABBBuilder().expand(count, direction);
            MessageUtils.send(source, Text.of(TextManager.getText(
                    Texts.CMD_ANSWER_BOUNDS_EXPANDED,
                    TextManager.getText(direction.name().toLowerCase()),
                    String.valueOf(count)
            )));
        } else {
            regionBuilder.getAABBBuilder().expand(count);
            MessageUtils.send(source, Text.of(TextManager.getText(
                    Texts.CMD_ANSWER_BOUNDS_EXPANDED,
                    TextManager.getText(Texts.DIRECTION_ALL),
                    String.valueOf(count)
            )));
        }

        return CommandResult.success();
    }
}