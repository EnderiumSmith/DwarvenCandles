package dwarvenCandles.core;

import net.minecraftforge.common.config.Configuration;

public class Config {
	
	public static float explosionDamage, maxBlastStrength, fishChance;
	public static void readcfg(){
		Configuration cfg=CommonProxy.config;
		try{
			cfg.load();
			initConfig(cfg);
		} catch(Exception e){
			System.out.println("Charcoal Pit mod could not load configs");
		} finally{
			if(cfg.hasChanged()){
				cfg.save();
			}
		}
	}
	private static void initConfig(Configuration cfg){
		cfg.addCustomCategoryComment("Config", "Configuration");
		explosionDamage=cfg.getFloat("blastDamage", "Config", 1F, 0F, 50F, "Size of the explosion. Used for damage calculation. Fireballs are 1, creepers 3, TNT 4");
		maxBlastStrength=cfg.getFloat("maxBlastStrenght", "Config", 15F, 1F, 1000000F, "The maximum blast resistance of blocks that the explosion can break. Note that the values on MC wiki are 3x the ones used in code. Ores are 5, stone 10 and endstone 15");
		fishChance=cfg.getFloat("fishChance", "Config", 0.1F, 0F, 1F, "The Chance that a fish will drop, per block, when the explosion eccurs underwater");
	}
	
}
