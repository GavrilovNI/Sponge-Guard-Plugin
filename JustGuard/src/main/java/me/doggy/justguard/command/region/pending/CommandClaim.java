package me.doggy.justguard.command.region.pending;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.consts.Metas;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MathUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.Pending;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.MyAABB;
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

import java.util.Optional;
import java.util.List;

public class CommandClaim implements CommandExecutor
{
    Logger logger = JustGuard.getInstance().getLogger();

    public CommandResult execute(CommandSource source, CommandContext args)
    {
        Optional<String> nameOpt = args.getOne(CommandsRegistrator.REGION_ID);

        if(!nameOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String name = nameOpt.get();


        PendingRegion region = Pending.getRegion(source);
        if(region==null)
            MessageUtils.SendError(source, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
        else if(JustGuard.REGIONS.containsKey(name))
            MessageUtils.SendError(source, Text.of(TextManager.getText(Texts.ERR_REGION_ALREADY_EXISTS, name)));
        else
        {
            if(hasPermission(source, region))
            {
                if(Pending.uploadRegion(source, name)) {
                    MessageUtils.Send(source, Text.of(TextManager.getText(Texts.CMD_ANSWER_REGION_CLAIMED, name)));
                    if(source instanceof Player)
                    {
                        Region uploadedRegion = JustGuard.REGIONS.get(name);
                        uploadedRegion.setPlayerOwnership(((Player) source).getUniqueId(), Region.PlayerOwnership.Owner);
                    }
                }
                else {
                    MessageUtils.SendError(source, Text.of(TextManager.getText(Texts.ERR_UNKNOWN)));
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
                    MessageUtils.SendError(source, Text.of(TextManager.getText(
                            Texts.MAX_REGION_SIZE_VIOLATION,
                            String.valueOf(regionSize),
                            String.valueOf(maxSize)
                    )));
                    return false;
                }
            }

            List<RegionPair> intersectRegions = RegionUtils.getRegionsIntersectWith(region.world, regionAABB);
            for (RegionPair regionPair : intersectRegions)
            {
                //intersect with, check for ownership
                String playerStateStr = regionPair.region.getPlayerOwnership(player.getUniqueId()).name().toLowerCase();
                if(!player.hasPermission(Permissions.CAN_INTERSECT_REGION_WITH_OWNERSHIP_PREFIX + playerStateStr))
                {
                    MessageUtils.SendError(source, Text.of(TextManager.getText(
                            Texts.NO_PERMISSION_TO_CLAIM_REGION_INTERSECT_WITH_REGION_OWNERSHIP,
                            regionPair.name,
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