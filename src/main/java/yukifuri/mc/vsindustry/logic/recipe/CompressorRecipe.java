package yukifuri.mc.vsindustry.logic.recipe;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.registries.VRecipes;

@MethodsReturnNonnullByDefault
public class CompressorRecipe implements Recipe<Container> {
    public static final RecipeType<CompressorRecipe> TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return VSIndustry.MOD_ID + ":compressing";
        }
    };

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int duration;
    private final int consumes;

    public CompressorRecipe(
            ResourceLocation id, Ingredient ingredient,
            ItemStack result, int duration, int consumes
    ) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.duration = duration;
        this.consumes = consumes;
    }

    @Override
    public boolean matches(Container container, Level level) {
        var is = container.getItem(0);
        return ingredient.test(is) && is.getCount() >= consumes;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return VRecipes.COMPRESSING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, ingredient);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getDuration() {
        return duration;
    }

    public int getConsumes() {
        return consumes;
    }


    public static class Serializer implements RecipeSerializer<CompressorRecipe> {
        @Override
        public CompressorRecipe fromJson(ResourceLocation id, JsonObject json) {
            var ingredient = Ingredient.fromJson(json.get("ingredient"));
            var resultJson = GsonHelper.getAsJsonObject(json, "result");
            var result = ShapedRecipe.itemStackFromJson(resultJson);
            int duration = GsonHelper.getAsInt(json, "duration", 16);
            int consumes = GsonHelper.getAsInt(json, "consumes", 1);
            return new CompressorRecipe(id, ingredient, result, duration, consumes);
        }

        @Override
        public CompressorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            var ingredient = Ingredient.fromNetwork(buf);
            var result = buf.readItem();
            int duration = buf.readVarInt();
            int consumes = buf.readVarInt();
            return new CompressorRecipe(id, ingredient, result, duration, consumes);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CompressorRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.duration);
            buf.writeVarInt(recipe.consumes);
        }
    }
}