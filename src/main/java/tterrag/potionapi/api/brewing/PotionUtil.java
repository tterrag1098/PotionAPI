package tterrag.potionapi.api.brewing;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tterrag.potionapi.api.brewing.PotionBase.PotionNoEffect;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.EffectUtil;
import tterrag.potionapi.api.effect.PotionData;
import tterrag.potionapi.common.brewing.PotionRegistry;
import tterrag.potionapi.common.util.NBTUtil;

public class PotionUtil
{
    public static final String NAME_KEY = "potionname";
    public static final String POWER_LEVEL_KEY = "potionpower";
    public static final String TIME_LEVEL_KEY = "potiontime";

    public static void applyPotion(ItemStack stack, EntityLivingBase entity)
    {
        applyPotion(getDataFromNBT(NBTUtil.getNBTTag(stack)), entity);
    }

    public static void applyPotion(PotionData data, EntityLivingBase entity)
    {
        Effect effect = data.potion.createEffect(data, entity);
        if (effect != null)
        {
            EffectUtil.applyEffect(effect, entity);
        }
    }

    public static void writePotionNBT(NBTTagCompound tag, IPotion potion, int powerLevel, int timeLevel)
    {
        writePotionNBT(tag, new PotionData(potion, powerLevel, timeLevel));
    }

    public static void writePotionNBT(NBTTagCompound tag, PotionData potionData)
    {
        tag.setString(NAME_KEY, potionData.potion.getIdentifier());
        tag.setInteger(POWER_LEVEL_KEY, potionData.powerLevel);
        tag.setInteger(TIME_LEVEL_KEY, potionData.timeLevel);
    }

    public static PotionData getDataFromNBT(NBTTagCompound tag)
    {
        return new PotionData(getPotionFromNBT(tag), getPowerLevelFromNBT(tag), getTimeLevelFromNBT(tag));
    }

    public static IPotion getPotionFromNBT(NBTTagCompound tag)
    {
        IPotion potion = PotionRegistry.INSTANCE.getPotionByID(tag.getString(NAME_KEY));
        return potion != null ? potion : new PotionNoEffect("BROKEN", new ItemStack(Items.diamond), 0);
    }

    public static int getPowerLevelFromNBT(NBTTagCompound tag)
    {
        return tag.getInteger(POWER_LEVEL_KEY);
    }

    public static int getTimeLevelFromNBT(NBTTagCompound tag)
    {
        return tag.getInteger(TIME_LEVEL_KEY);
    }

    public static IPotion getResult(ItemStack base, ItemStack ingredient)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            if (potion.isIngredient(base, ingredient))
            {
                return potion;
            }
        }
        return null;
    }

    public static IPotion getPotionForPowerAmp(PotionData data, ItemStack powerAmp)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            if (potion.isPowerAmplifier(data, powerAmp))
            {
                return potion;
            }
        }
        return null;
    }

    public static IPotion getPotionForTimeAmp(PotionData data, ItemStack timeAmp)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            if (potion.isTimeAmplifier(data, timeAmp))
            {
                return potion;
            }
        }
        return null;
    }

    public static IPotion getBase(ItemStack base)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            if (potion.canBeAppliedTo(base))
            {
                return potion;
            }
        }
        return null;
    }

    public static boolean isIngredient(ItemStack base, ItemStack ingredient)
    {
        return getResult(base, ingredient) != null;
    }

    public static boolean isPowerAmp(PotionData data, ItemStack powerAmp)
    {
        return data.potion.isPowerAmplifier(data, powerAmp);
    }

    public static boolean isTimeAmp(PotionData data, ItemStack timeAmp)
    {
        return data.potion.isTimeAmplifier(data, timeAmp);
    }

    public static boolean isBase(ItemStack base)
    {
        return getBase(base) != null;
    }

    public static boolean canAmpPower(PotionData data, ItemStack powerAmp)
    {
        return data.powerLevel < data.potion.getMaxPower() && isPowerAmp(data, powerAmp);
    }

    public static boolean canAmpTime(PotionData data, ItemStack timeAmp)
    {
        return data.timeLevel < data.potion.getMaxTime() && isTimeAmp(data, timeAmp);
    }
}
