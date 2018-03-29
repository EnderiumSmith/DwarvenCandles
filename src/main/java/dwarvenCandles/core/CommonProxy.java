package dwarvenCandles.core;

import java.io.File;

import dwarvenCandles.DwarvenCandles;
import dwarvenCandles.entity.EntityDynamite;
import dwarvenCandles.tile.TileDynamite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public static Configuration config;
	public void preInit(FMLPreInitializationEvent event){
		File file=event.getModConfigurationDirectory();
		config=new Configuration(new File(file.getPath(),"dwarven_candles.cfg"));
		Config.readcfg();
		EntityRegistry.registerModEntity(new ResourceLocation("dynamite"), EntityDynamite.class, "dynamite", 0, DwarvenCandles.instance, 64, 10, true);
	}
	public void init(FMLInitializationEvent event){
		GameRegistry.registerTileEntity(TileDynamite.class, DwarvenCandles.MODID+"_dynamite");
		Crafting.registerRecipes();
	}
	public void postInit(FMLPostInitializationEvent event){
		if(config.hasChanged()){
			config.save();
		}
	}
	
}
