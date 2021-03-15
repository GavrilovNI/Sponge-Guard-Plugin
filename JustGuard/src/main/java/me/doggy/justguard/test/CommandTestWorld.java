package me.doggy.justguard.test;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CommandTestWorld implements CommandExecutor
{
    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public CommandResult execute(CommandSource src, CommandContext args) {

        if(!TestWorld.getWorld().isPresent()) {
            if(TestWorld.createWorld()) {
                MessageUtils.send(src, Text.of("Test world created."));
            } else {
                MessageUtils.sendError(src, Text.of("Test world not created!"));
                return CommandResult.success();
            }
        } else {
            MessageUtils.send(src, Text.of("Test world already created."));
        }

        if(!(src instanceof Player))
            return CommandResult.success();

        if(TestWorld.getWorld().isPresent()) {
            Player player = (Player) src;
            Location<World> tpLoc;
            if(player.getWorld().equals(TestWorld.getWorld().get())) {
                tpLoc = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get().getSpawnLocation().add(0,10,0);
            } else {
                tpLoc = new Location<World>(TestWorld.getWorld().get(), TestWorld.ISLANDS_SIZE / 2, TestWorld.ISLANDS_HEIGHT+1,TestWorld.ISLANDS_SIZE / 2);
            }

            player.setLocation(tpLoc);
            MessageUtils.send(player, Text.of("Teleporting..."));
        }

        return CommandResult.success();
    }
}