package me.doggy.justguard.command.region;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.config.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FileUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.Pending;
import me.doggy.justguard.utils.help.PendingRegion;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class CommandCreate implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<WorldProperties> worldPropertiesOpt = args.getOne("world");
        Optional<Pending.RegionType> regionTypeOpt = args.getOne("region-type");

        if(!worldPropertiesOpt.isPresent() || !regionTypeOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        Optional<World> worldOpt = Sponge.getServer().getWorld(worldPropertiesOpt.get().getUniqueId());

        if(!worldOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        World world = worldOpt.get();
        ConfigurationNode flags = JustGuard.getInstance().getConfigManager().getDefaultRegionFlags();

        Pending.createRegion(src, regionTypeOpt.get(), flags, world);



        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_REGION_CREATED,
                world.getName()
        )));

        return CommandResult.success();
    }
}
