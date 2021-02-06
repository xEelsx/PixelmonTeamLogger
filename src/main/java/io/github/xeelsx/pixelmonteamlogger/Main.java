package io.github.xeelsx.pixelmonteamlogger;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

@Plugin(
        id = "pixelmonteamlogger",
        name = "PixelmonTeamLogger",
        version = "1.0",
        description = "Pulls players party & exports it to an external source"
)
public class Main {
    @Inject
    private Logger logger;

    @Listener
    public void onInit (GameInitializationEvent event) {
        CommandManager cmdManager = Sponge.getCommandManager();

        CommandSpec commandLogTeam = CommandSpec.builder()
                .description(Text.of("Log Team Command"))
                .permission("pixelmonteamlogger.command.logteam")
                .arguments (
                        GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("argument"), LogTeamArgument.class)),
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player")))
                )
                .executor(new LogTeam())
                .build();

        cmdManager.register(this, commandLogTeam, "logteam");
    }

}
