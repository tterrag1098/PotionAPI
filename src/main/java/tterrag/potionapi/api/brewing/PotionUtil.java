package tterrag.potionapi.api.brewing;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.Effect.PotionData;
import tterrag.potionapi.api.effect.EffectUtil;
import tterrag.potionapi.common.brewing.PotionRegistry;

public class PotionUtil
{
    public static final String NAME_KEY = "potionname";
    public static final String POWER_LEVEL_KEY = "potionpower";
    public static final String TIME_LEVEL_KEY = "potiontime";

    public static void applyPotion(IPotion potion, ItemStack stack, EntityLivingBase entity)
    {
        Effect effect = potion.createEffect(stack, entity);
        if (effect != null)
        {
            EffectUtil.applyEffect(effect, entity);
        }
    }

    public static void writePotionNBT(NBTTagCompound tag, IPotion potion, int powerLevel, int timeLevel)
    {
        tag.setString(NAME_KEY, potion.getIdentifier());
        tag.setInteger(POWER_LEVEL_KEY, powerLevel);
        tag.setInteger(TIME_LEVEL_KEY, timeLevel);
    }

    public static PotionData getDataFromNBT(NBTTagCompound tag)
    {
        return new PotionData(getPotionFromNBT(tag), getPowerLevelFromNBT(tag), getTimeLevelFromNBT(tag));
    }

    public static IPotion getPotionFromNBT(NBTTagCompound tag)
    {
        return PotionRegistry.INSTANCE.getPotionByID(tag.getString(NAME_KEY));
    }

    public static int getPowerLevelFromNBT(NBTTagCompound tag)
    {
        return tag.getInteger(POWER_LEVEL_KEY);
    }

    public static int getTimeLevelFromNBT(NBTTagCompound tag)
    {
        return tag.getInteger(TIME_LEVEL_KEY);
    }

    public static boolean isItemIngredient(ItemStack base, ItemStack ingredient)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            if (potion.isIngredient(base, ingredient))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isItemBase(ItemStack base)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            if (potion.canBeAppliedTo(base))
            {
                return true;
            }
        }
        return false;
    }
}
