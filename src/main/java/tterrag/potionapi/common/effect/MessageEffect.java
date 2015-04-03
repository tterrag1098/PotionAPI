package tterrag.potionapi.common.effect;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tterrag.potionapi.PotionAPI;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageEffect implements IMessage
{
    public MessageEffect()
    {
    }

    private int entityID;
    private NBTTagCompound data;

    public MessageEffect(EntityLivingBase target, NBTTagCompound tag)
    {
        this.entityID = target.getEntityId();
        this.data = tag;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.data = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<MessageEffect, IMessage>
    {
        @Override
        public IMessage onMessage(MessageEffect message, MessageContext ctx)
        {
            World world = PotionAPI.proxy.getClientWorld();
            EntityLivingBase entity = (EntityLivingBase) world.getEntityByID(message.entityID);
            if (entity != null)
            {
                EffectData data = EffectData.getInstance(entity);
                data.loadNBTData(message.data);
            }
            return null;
        }
    }
}
