package com.example.reciperesync;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue delayTicks;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("RecipeResync Configuration");
        BUILDER.push("general");

        delayTicks = BUILDER
                .comment("Delay in ticks before resending recipes to a player after they join.",
                        "20 ticks = 1 second. Default is 60 ticks (3 seconds).",
                        "Increase this value if recipes are still not syncing properly.")
                .defineInRange("delayTicks", 60, 1, 600);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
