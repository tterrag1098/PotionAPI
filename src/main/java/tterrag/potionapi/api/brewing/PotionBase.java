package tterrag.potionapi.api.brewing;

import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import tterrag.core.common.util.TTItemUtils;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.Effect.PotionData;

@AllArgsConstructor
public abstract class PotionBase implements IPotion
{
    private String identifier;
    private ItemStack ingredient;
    private ItemStack powerAmp, timeAmp;
    private int maxPower, maxTime;
    private int color;

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getLocalizedName(ItemStack stack)
    {
        return StatCollector.translateToLocal("potion." + identifier + ".name");
    }

    @Override
    public int getColor(ItemStack potion)
    {
        return color;
    }

    @Override
    public boolean canBeAppliedTo(ItemStack base)
    {
        return base.getItem() == Items.potionitem && base.getItemDamage() == 16;
    }

    @Override
    public boolean isIngredient(ItemStack potion, ItemStack ingredient)
    {
        return OreDictionary.itemMatches(ingredient, this.ingredient, false);
    }

    @Override
    public boolean isPowerAmplifier(PotionData potion, ItemStack ingredient)
    {
        return OreDictionary.itemMatches(ingredient, powerAmp, false);
    }

    @Override
    public boolean isTimeAmplifier(PotionData potion, ItemStack ingredient)
    {
        return OreDictionary.itemMatches(ingredient, timeAmp, false);
    }

    @Override
    public int getMaxPower()
    {
        return maxPower;
    }

    @Override
    public int getMaxTime()
    {
        return maxTime;
    }

    @Override
    public int getTimeForLevel(int powerLevel, int timeLevel)
    {
        return 20 * (timeLevel * 120 - powerLevel * 30);
    }

    @Override
    public Effect createEffect(ItemStack potion, EntityLivingBase entity)
    {
        PotionData data = PotionUtil.getDataFromNBT(TTItemUtils.getNBTTag(potion));
        return new Effect(data, getTimeForLevel(data.powerLevel, data.timeLevel));
    }

    @Override
    public void onApplied(PotionData data, EntityLivingBase entity)
    {
        ;
    }

    @Override
    public void onRemoved(PotionData data, EntityLivingBase entity)
    {
        ;
    }

    public static class PotionNoEffect extends PotionBase
    {
        public PotionNoEffect(String identifier, ItemStack ingredient, int color)
        {
            super(identifier, ingredient, null, null, 0, 0, color);
        }

        @Override
        public Effect createEffect(ItemStack potion, EntityLivingBase entity)
        {
            return null;
        }

        @Override
        public void onUpdate(PotionData data, EntityLivingBase entity)
        {
            ;
        }
    }

    public static abstract class PotionSimple extends PotionBase
    {
        private ItemStack base;

        public PotionSimple(String identifier, ItemStack ingredient, ItemStack base, ItemStack powerAmp, ItemStack timeAmp, int maxPower,
                int maxTime, int color)
        {
            super(identifier, ingredient, powerAmp, timeAmp, maxPower, maxTime, color);
            this.base = base;
        }

        public PotionSimple(String identifier, ItemStack ingredient, ItemStack base, int powerMax, int timeMax, int color)
        {
            this(identifier, ingredient, base, new ItemStack(Items.redstone), new ItemStack(Items.glowstone_dust), powerMax, timeMax, color);
        }

        @Override
        public boolean canBeAppliedTo(ItemStack base)
        {
            return OreDictionary.itemMatches(base, this.base, false);
        }
    }
}
