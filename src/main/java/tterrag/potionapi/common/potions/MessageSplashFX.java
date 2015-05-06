package tterrag.potionapi.common.potions;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.brewing.PotionUtil;
import tterrag.potionapi.api.effect.PotionData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSplashFX implements IMessage
{
    public MessageSplashFX()
    {
    }

    private PotionData data;
    private double x, y, z;

    public MessageSplashFX(PotionData data, double x, double y, double z)
    {
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        NBTTagCompound tag = new NBTTagCompound();
        PotionUtil.writePotionNBT(tag, data);
        ByteBufUtils.writeTag(buf, tag);

        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        this.data = PotionUtil.getDataFromNBT(tag);

        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }
    
    public static class Handler implements IMessageHandler<MessageSplashFX, IMessage>
    {
        private static final Random rand = new Random();
        
        @Override
        public IMessage onMessage(MessageSplashFX message, MessageContext ctx)
        {
            World world = PotionAPI.proxy.getClientWorld();
            RenderGlobal renderer = Minecraft.getMinecraft().renderGlobal;
            
            double x = message.x;
            double y = message.y;
            double z = message.z;
            String s = "iconcrack_" + Item.getIdFromItem(PotionAPI.potionThrowable) + "_" + 0;

            for (int i = 0; i < 8; ++i)
            {
                renderer.spawnParticle(s, x, y, z, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D);
            }

            int color = message.data.potion.getColor(message.data);
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color >> 0 & 255) / 255.0F;
            String s1 = "spell";

            for (int i = 0; i < 100; ++i)
            {
                double velMult = rand.nextDouble() * 4.0D;
                double velXDir = rand.nextDouble() * Math.PI * 2.0D;
                double velX = Math.cos(velXDir) * velMult;
                double velZDir = 0.01D + rand.nextDouble() * 0.5D;
                double velZ = Math.sin(velXDir) * velMult;
                EntityFX entityfx = renderer.doSpawnParticle(s1, x + velX * 0.1D, y + 0.3D, z + velZ * 0.1D, velX, velZDir, velZ);

                if (entityfx != null)
                {
                    float f4 = 0.75F + rand.nextFloat() * 0.25F;
                    entityfx.setRBGColorF(r * f4, g * f4, b * f4);
                    entityfx.multiplyVelocity((float)velMult);
                }
            }

            world.playSound(x, y, z, "game.potion.smash", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F, false);
            return null;
        }
    }
}
