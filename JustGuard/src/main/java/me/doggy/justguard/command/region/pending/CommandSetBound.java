package me.doggy.justguard.command.region.pending;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.Pending;
import me.doggy.justguard.utils.help.AABBBuilder;
import me.doggy.justguard.utils.help.PendingRegion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandSetBound implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<AABBBuilder.BoundType> boundTypeOpt = args.getOne("type");
        Optional<Integer> posXOpt = args.getOne("pos-x");
        Optional<Integer> posYOpt = args.getOne("pos-y");
        Optional<Integer> posZOpt = args.getOne("pos-z");

        if(!boundTypeOpt.isPresent()
           || !posXOpt.isPresent()
           || !posYOpt.isPresent()
           || !posZOpt.isPresent())
            return CommandResult.builder().successCount(0).build();


        AABBBuilder.BoundType boundType = boundTypeOpt.get();
        Vector3d pos = new Vector3d(posXOpt.get(), posYOpt.get(), posZOpt.get());

        PendingRegion region = Pending.getRegion(src);

        if(region == null) {
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
            return CommandResult.success();
        }
        /*else if(!region.regionType.equals(Region.RegionType.Local))
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_SETBOUND_ONLY_FOR_LOCAL_REGIONS)));*/
        /*else if(region.aabbBuilder.set(pos, boundType))
            MessageUtils.Send(src, Text.of(TextManager.getText(
                    Texts.CMD_ANSWER_BOUND_SETTED,
                    boundType.name().toLowerCase(),
                    pos.toString()
            )));
        else
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_UNKNOWN)));*/

        region.aabbBuilder.set(pos, boundType);
        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_BOUND_SETTED,
                boundType.name().toLowerCase(),
                pos.toString()
        )));


        return CommandResult.success();
    }
}