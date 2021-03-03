package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandsRegistrator {

    public static void register()
    {
        CommandSpec cmdVersion = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.version")
                .executor(new CommandVersion())
                .build();

        CommandSpec cmdReload = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.reload")
                .executor(new CommandReload())
                .build();

        CommandSpec cmdJustGuard = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.justguard")
                .executor(new CommandJustGuard())
                .child(cmdVersion, "version")
                .child(cmdReload, "reload")
                .build();

        JustGuard plugin = JustGuard.getInstance();
        Sponge.getCommandManager().register(plugin, cmdJustGuard, "justguard", "jg");
        plugin.getLogger().debug("Commands loaded successfully!");
    }

}
