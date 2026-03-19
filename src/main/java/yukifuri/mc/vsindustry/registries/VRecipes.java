package yukifuri.mc.vsindustry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.logic.recipe.CompressorRecipe;

public class VRecipes {
    public static final RecipeSerializer<CompressorRecipe> COMPRESSING_SERIALIZER = new CompressorRecipe.Serializer();

    public static void register() {
        register(CompressorRecipe.TYPE, COMPRESSING_SERIALIZER, "compressing");
    }

    static <R extends Recipe<? extends Container>> void register(
            RecipeType<R> type, RecipeSerializer<R> serializer, String name
    ) {
        Registry.register(
                BuiltInRegistries.RECIPE_TYPE,
                new ResourceLocation(VSIndustry.MOD_ID, name),
                type
        );

        Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                new ResourceLocation(VSIndustry.MOD_ID, name),
                serializer
        );
    }
}