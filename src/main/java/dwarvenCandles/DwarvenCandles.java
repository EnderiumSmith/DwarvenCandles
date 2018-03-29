package dwarvenCandles;

import dwarvenCandles.core.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=DwarvenCandles.MODID, name=DwarvenCandles.MODNAME, version=DwarvenCandles.VERSION)
public class DwarvenCandles {
	
	public static final String MODID="dwarven_candles";
	public static final String MODNAME="Dwarven Candles";
	public static final String VERSION="1.1";
	
	@Mod.Instance
	public static DwarvenCandles instance;
	
	@SidedProxy(clientSide="dwarvenCandles.core.ClientProxy",
			serverSide="dwarvenCandles.core.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.preInit(event);
	}
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}

}
