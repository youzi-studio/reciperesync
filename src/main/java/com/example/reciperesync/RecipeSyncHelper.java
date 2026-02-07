package com.example.reciperesync;

import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;

public class RecipeSyncHelper {

    public static void resyncRecipesForPlayer(ServerPlayer player) {
        if (player.hasDisconnected()) {
            RecipeResync.LOGGER.debug("Player {} is no longer connected, skipping recipe resync",
                    player.getName().getString());
            return;
        }

        try {
            RecipeResync.LOGGER.info("Resending tags and recipes to player {}", player.getName().getString());

            var server = player.getServer();
            if (server == null) {
                RecipeResync.LOGGER.warn("Server is null for player {}", player.getName().getString());
                return;
            }

            // Step 1: Send tags (ClientboundUpdateTagsPacket)
            // Tags define item/block/fluid groups that recipes depend on for ingredient matching
            // This is critical - without tags, mod recipes won't work even if recipe data is sent
            var playerList = server.getPlayerList();
            var registries = server.registries();
            var tagsPacket = new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(registries));
            player.connection.send(tagsPacket);
            RecipeResync.LOGGER.debug("Sent tags to player {}", player.getName().getString());

            // Step 2: Send all recipes (ClientboundUpdateRecipesPacket)
            // This packet contains all recipe definitions
            var recipeManager = server.getRecipeManager();
            Collection<RecipeHolder<?>> allRecipes = recipeManager.getOrderedRecipes();
            var updateRecipesPacket = new ClientboundUpdateRecipesPacket(allRecipes);
            player.connection.send(updateRecipesPacket);
            RecipeResync.LOGGER.debug("Sent {} recipe definitions to player {}",
                    allRecipes.size(), player.getName().getString());

            // Step 3: Send the player's recipe book state (ClientboundRecipePacket with INIT state)
            // This tells the client which recipes the player has unlocked
            ServerRecipeBook recipeBook = player.getRecipeBook();
            recipeBook.sendInitialRecipeBook(player);
            RecipeResync.LOGGER.debug("Sent recipe book state to player {}", player.getName().getString());

            RecipeResync.LOGGER.info("Successfully resynced tags and recipes for player {}", player.getName().getString());

        } catch (Exception e) {
            RecipeResync.LOGGER.error("Failed to resync recipes for player {}: {}",
                    player.getName().getString(), e.getMessage(), e);
        }
    }
}
