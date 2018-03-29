package dwarvenCandles.core;

import dwarvenCandles.blocks.BlockDynamite;
import dwarvenCandles.blocks.ItemBlockDynamite;
import dwarvenCandles.entity.EntityDynamite;
import dwarvenCandles.entity.RenderDynamite;
import dwarvenCandles.items.ItemDynamiteRemote;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class StuffRegistry {
	
	public static BlockDynamite dynamite_block=new BlockDynamite();
	public static ItemBlockDynamite dynamite_item=new ItemBlockDynamite(dynamite_block);
	public static ItemDynamiteRemote dynamite_remote=new ItemDynamiteRemote();
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		event.getRegistry().register(dynamite_block);
	}
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		event.getRegistry().register(dynamite_item);
		event.getRegistry().register(dynamite_remote);
	}
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event){
		ModelLoader.setCustomModelResourceLocation(dynamite_item, 0, new ModelResourceLocation(dynamite_item.getRegistryName().toString()));
		ModelLoader.setCustomModelResourceLocation(dynamite_remote, 0, new ModelResourceLocation(dynamite_remote.getRegistryName().toString()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, RenderDynamite.FACTORY);
	}
	
}
