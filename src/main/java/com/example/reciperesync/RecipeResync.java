package com.example.reciperesync;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RecipeResync.MOD_ID)
public class RecipeResync {
    public static final String MOD_ID = "reciperesync";
    public static final Logger LOGGER = LoggerFactory.getLogger(RecipeResync.class);

    public RecipeResync(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("RecipeResync initializing...");

        // Register config
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);

        // Register event handlers
        NeoForge.EVENT_BUS.register(new PlayerEventHandler());

        // Register commands
        NeoForge.EVENT_BUS.register(new CommandHandler());

        LOGGER.info("RecipeResync initialized successfully!");
    }
}
