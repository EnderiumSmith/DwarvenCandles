package dwarvenCandles.entity;

import dwarvenCandles.core.StuffRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderDynamite{

	public static final Factory FACTORY = new Factory();
	
	public static class Factory implements IRenderFactory<EntityDynamite> {

        @Override
        public Render<? super EntityDynamite> createRenderFor(RenderManager manager) {
            return new RenderSnowball<>(manager, StuffRegistry.dynamite_item, Minecraft.getMinecraft().getRenderItem());
        }

    }
	
}
