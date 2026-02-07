package com.example.reciperesync;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandHandler {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("reciperesync")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(this::resyncPlayer))
                .executes(this::resyncSelf));

        RecipeResync.LOGGER.info("Registered /reciperesync command");
    }

    private int resyncSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getEntity() instanceof ServerPlayer player) {
            RecipeSyncHelper.resyncRecipesForPlayer(player);
            source.sendSuccess(() -> Component.literal("Recipes resynced for yourself."), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("This command must be run by a player or specify a target player."));
            return 0;
        }
    }

    private int resyncPlayer(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            RecipeSyncHelper.resyncRecipesForPlayer(targetPlayer);
            context.getSource().sendSuccess(() ->
                    Component.literal("Recipes resynced for player " + targetPlayer.getName().getString() + "."), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to resync recipes: " + e.getMessage()));
            return 0;
        }
    }
}
