package me.doggy.justguard.command.region.pending;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.consts.Metas;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MathUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.Pending;
import me.doggy.justguard.utils.help.MyAABB;
import me.doggy.justguard.utils.help.PendingRegion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;

public class CommandClaim implements CommandExecutor
{
    Logger logger = JustGuard.getInstance().getLogger();

    public CommandResult execute(CommandSource source, CommandContext args)
    {
        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);

        if(!regionIdOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String regionId = regionIdOpt.get();


        PendingRegion region = Pending.getRegion(source);
        if(region==null)
            MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
        else if(RegionsHolder.hasRegion(regionId))
            MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_REGION_ALREADY_EXISTS, regionId)));
        else
        {
            if(hasPermission(source, region))
            {
                if(Pending.uploadRegion(source, regionId)) {
                    MessageUtils.send(source, Text.of(TextManager.getText(Texts.CMD_ANSWER_REGION_CLAIMED, regionId)));
                    if(source instanceof Player)
                    {
                        Region uploadedRegion = RegionsHolder.getRegion(regionId);
                        uploadedRegion.setPlayerOwnership(((Player) source).getUniqueId(), Region.PlayerOwnership.Owner);
                    }
                }
                else {
                    MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_UNKNOWN)));
                }
            }
        }

        return CommandResult.success();
    }

    private boolean hasPermission(CommandSource source, PendingRegion region)
    {
        if(source instanceof Player)
        {
            Player player = (Player) source;

            MyAABB regionAABB = region.aabbBuilder.build();

            if(!player.hasPermission(Permissions.REGION_CLAIM_INFINITE_SIZE)) {

                LuckPerms luckPerms = JustGuard.getInstance().getLuckPerms();
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());

                int maxSize = MathUtils.tryParseInt(user.getCachedData().getMetaData().getMetaValue(Metas.REGION_MAX_SIZE), 0);
                Vector3d regionSize = regionAABB.getSize();
                int regionVolume = (int) Math.ceil(regionSize.getX() * regionSize.getY() * regionSize.getZ());

                if (regionVolume > maxSize) {
                    MessageUtils.sendError(source, Text.of(TextManager.getText(
                            Texts.MAX_REGION_SIZE_VIOLATION,
                            String.valueOf(regionVolume),
                            String.valueOf(maxSize)
                    )));
                    return false;
                }
            }

            Map<String, Region> intersectRegions = RegionsHolder.getRegions(x -> x.getValue().intersects(region.world, regionAABB));
            for (Map.Entry<String, Region> regionPair : intersectRegions.entrySet())
            {
                //intersect with, check for ownership
                String playerStateStr = regionPair.getValue().getPlayerOwnership(player.getUniqueId()).name().toLowerCase();
                if(!player.hasPermission(Permissions.CAN_INTERSECT_REGION_WITH_OWNERSHIP_PREFIX + playerStateStr))
                {
                    MessageUtils.sendError(source, Text.of(TextManager.getText(
                            Texts.NO_PERMISSION_TO_CLAIM_REGION_INTERSECT_WITH_REGION_OWNERSHIP,
                            regionPair.getKey(),
                            TextManager.getText(playerStateStr)
                    )));
                    return false;
                }
            }


            return true;
        }
        else
        {
            return true;
        }
    }
}