package me.doggy.justguard.command.region.pending;

import com.flowpowered.math.vector.Vector3i;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.Pending;
import me.doggy.justguard.utils.help.MyAABB;
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
        Optional<MyAABB.Builder.BoundType> boundTypeOpt = args.getOne("type");
        Optional<Integer> posXOpt = args.getOne("pos-x");
        Optional<Integer> posYOpt = args.getOne("pos-y");
        Optional<Integer> posZOpt = args.getOne("pos-z");

        if(!boundTypeOpt.isPresent()
           || !posXOpt.isPresent()
           || !posYOpt.isPresent()
           || !posZOpt.isPresent())
            return CommandResult.builder().successCount(0).build();


        MyAABB.Builder.BoundType boundType = boundTypeOpt.get();
        Vector3i pos = new Vector3i(posXOpt.get(), posYOpt.get(), posZOpt.get());

        Region.Builder regionBuilder = Pending.getRegion(src);

        if(regionBuilder == null) {
            MessageUtils.sendError(src, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
            return CommandResult.success();
        }

        regionBuilder.getAABBBuilder().set(pos, boundType);
        MessageUtils.send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_BOUND_SETTED,
                boundType.name().toLowerCase(),
                pos.toString()
        )));


        return CommandResult.success();
    }
}