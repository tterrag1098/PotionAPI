package tterrag.potionapi.common.effect;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;
import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.PotionUtil;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.PotionData;

import com.google.common.collect.Lists;

public class EffectData implements IExtendedEntityProperties
{
    public static final String IDENT = PotionAPI.MODID;
    public static final String LIST_KEY = "effectlist";
    public static final String TIME_KEY = "timeleft";
    public static final String POTION_KEY = "potiondata";

    private EntityLivingBase entity;
    private List<Effect> effects = Lists.newArrayList();

    public void addEffect(Effect effect)
    {
        int idx = -1;
        for (int i = 0; i < effects.size(); i++)
        {
            if (effect.getPotionData().potion == effects.get(i).getPotionData().potion)
            {
                idx = i;
                break;
            }
        }
        if (idx >= 0)
        {
            effects.set(idx, effect);
        }
        else
        {
            effects.add(effect);
            effect.onApplied(entity);
            entity.potionsNeedUpdate = true;
        }
    }

    public void removeEffect(IPotion potion)
    {
        Iterator<Effect> iter = effects.iterator();
        while (iter.hasNext())
        {
            if (iter.next().getPotionData().potion == potion)
            {
                iter.remove();
                return;
            }
        }
    }

    public void tickEffects()
    {
        if (entity == null || effects.isEmpty())
        {
            return;
        }

        Iterator<Effect> iter = effects.iterator();
        while (iter.hasNext())
        {
            Effect effect = iter.next();
            if (effect.getTimeRemaining() <= 0)
            {
                iter.remove();
                effect.onRemoved(entity);
            }
            else
            {
                effect.onUpdate(entity);
            }
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
        if (entity instanceof EntityPlayer)
        {
            System.out.println("Player!");
        }
        this.entity = (EntityLivingBase) entity;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagList list = new NBTTagList();
        for (Effect effect : effects)
        {
            NBTTagCompound base = new NBTTagCompound();
            base.setInteger(TIME_KEY, effect.getTimeRemaining());
            NBTTagCompound potion = new NBTTagCompound();
            PotionData data = effect.getPotionData();
            PotionUtil.writePotionNBT(potion, data.potion, data.powerLevel, data.timeLevel);
            base.setTag(POTION_KEY, potion);
            list.appendTag(base);
        }
        compound.setTag(LIST_KEY, list);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        readFromNBT(compound);
    }

    public void readFromNBT(NBTTagCompound tag)
    {
        effects.clear();
        NBTTagList list = tag.getTagList(LIST_KEY, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound base = list.getCompoundTagAt(i);
            int time = base.getInteger(TIME_KEY);
            NBTTagCompound potionTag = base.getCompoundTag(POTION_KEY);
            PotionData data = PotionUtil.getDataFromNBT(potionTag);
            Effect effect = data.potion.createEffect(data, entity);
            if (effect != null)
            {
                effect.setTimeRemaining(time);
                effects.add(effect);
            }
        }
    }

    public Collection<Effect> getActiveEffects()
    {
        return effects;
    }

    public void clearEffects()
    {
        for (Effect e : effects)
        {
            e.onRemoved(entity);
        }
        effects.clear();
    }

    public static EffectData getInstance(EntityLivingBase entity)
    {
        return (EffectData) entity.getExtendedProperties(IDENT);
    }

    public static PotionData getPotionData(EntityLivingBase entity, IPotion potion)
    {
        EffectData ed = getInstance(entity);
        for (Effect e : ed.getActiveEffects())
        {
            if (e.getPotionData().potion == potion)
            {
                return e.getPotionData();
            }
        }
        return null;
    }
}
