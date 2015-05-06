package tterrag.potionapi.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import tterrag.potionapi.client.render.RenderBetterSplashPotion;
import tterrag.potionapi.common.CommonProxy;
import tterrag.potionapi.common.entity.EntityBetterSplashPotion;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public World getClientWorld()
    {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityBetterSplashPotion.class, new RenderBetterSplashPotion());
    }
}
