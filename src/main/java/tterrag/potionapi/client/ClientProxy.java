package tterrag.potionapi.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import tterrag.potionapi.common.CommonProxy;

public class ClientProxy extends CommonProxy
{
    @Override
    public World getClientWorld()
    {
        return Minecraft.getMinecraft().theWorld;
    }
}
