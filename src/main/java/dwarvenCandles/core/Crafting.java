package dwarvenCandles.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Crafting {
	
	public static void registerRecipes(){
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(StuffRegistry.dynamite_item, 4), new Object[]{
				"gunpowder","gunpowder",Items.CLAY_BALL,"string"
		}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(StuffRegistry.dynamite_remote, new Object[]{
				Blocks.STONE_BUTTON,Blocks.REDSTONE_TORCH,"enderpearl","stone"
		}));
	}
	
}
