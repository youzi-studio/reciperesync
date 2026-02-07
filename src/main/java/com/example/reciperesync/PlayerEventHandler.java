package com.example.reciperesync;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            int delayTicks = Config.delayTicks.get();
            RecipeResync.LOGGER.info("Player {} joined, scheduling recipe resync in {} ticks ({} seconds)",
                    serverPlayer.getName().getString(), delayTicks, delayTicks / 20.0);

            // Schedule the recipe resync after the configured delay
            serverPlayer.getServer().execute(() -> {
                scheduleDelayedResync(serverPlayer, delayTicks);
            });
        }
    }

    private void scheduleDelayedResync(ServerPlayer player, int delayTicks) {
        // Use a simple tick counter approach
        final int[] ticksRemaining = {delayTicks};

        Runnable tickTask = new Runnable() {
            @Override
            public void run() {
                // Check if player is still online
                if (player.hasDisconnected()) {
                    RecipeResync.LOGGER.debug("Player {} disconnected before recipe resync", player.getName().getString());
                    return;
                }

                ticksRemaining[0]--;
                if (ticksRemaining[0] <= 0) {
                    // Time to resync
                    RecipeSyncHelper.resyncRecipesForPlayer(player);
                } else {
                    // Schedule next tick
                    player.getServer().execute(this);
                }
            }
        };

        player.getServer().execute(tickTask);
    }
}
