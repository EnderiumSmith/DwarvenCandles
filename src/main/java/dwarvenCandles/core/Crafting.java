package dwarvenCandles.core;

import dwarvenCandles.DwarvenCandles;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Crafting {
	
	public static void registerRecipes(){
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(new ResourceLocation(DwarvenCandles.MODID+DwarvenCandles.MODNAME),new ItemStack(StuffRegistry.dynamite_item, 4), new Object[]{
				"gunpowder","gunpowder",Items.CLAY_BALL,"string"
		}).setRegistryName("dwarven_candle"));
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(new ResourceLocation(DwarvenCandles.MODID+DwarvenCandles.MODNAME),StuffRegistry.dynamite_remote, new Object[]{
				Blocks.STONE_BUTTON,Blocks.REDSTONE_TORCH,"enderpearl","stone"
		}).setRegistryName("candle_remote"));
	}
	
}
