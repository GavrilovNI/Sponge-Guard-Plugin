package me.doggy.justguard.command.region;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.Pending;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Metas;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MathUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.AABBBuilder;
import me.doggy.justguard.utils.help.PendingRegion;
import me.doggy.justguard.utils.help.RegionPair;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;

import java.util.List;
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

        PendingRegion region = Pending.getRegion(source);
        if(region==null) {
            MessageUtils.SendError(source, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
            return CommandResult.success();
        }

        Optional<Direction> directionOpt = args.getOne("direction");
        int count = countOpt.get();
        if(directionOpt.isPresent()) {

            Direction direction = directionOpt.get();
            if(!AABBBuilder.isDirectionAvaliableToExpand(direction)) {
                MessageUtils.SendError(source, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
                return CommandResult.success();

            }

            region.aabbBuilder.expand(count, direction);
            MessageUtils.Send(source, Text.of(TextManager.getText(
                    Texts.CMD_ANSWER_BOUNDS_EXPANDED,
                    TextManager.getText(direction.name().toLowerCase()),
                    String.valueOf(count)
            )));
        } else {
            region.aabbBuilder.expand(count);
            MessageUtils.Send(source, Text.of(TextManager.getText(
                    Texts.CMD_ANSWER_BOUNDS_EXPANDED,
                    TextManager.getText(Texts.DIRECTION_ALL),
                    String.valueOf(count)
            )));
        }

        return CommandResult.success();
    }
}