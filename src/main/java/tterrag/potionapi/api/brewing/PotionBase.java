package tterrag.potionapi.api.brewing;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.PotionData;

public abstract class PotionBase implements IPotion
{
    private String identifier;
    private ItemStack ingredient;
    private ItemStack powerAmp, timeAmp;
    private int color;

    protected PotionBase(String identifier, ItemStack ingredient, ItemStack powerAmp, ItemStack timeAmp, int color)
    {
        this.identifier = identifier;
        this.ingredient = ingredient;
        this.powerAmp = powerAmp;
        this.timeAmp = timeAmp;
        this.color = color;
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getLocalizedName(PotionData data)
    {
        return StatCollector.translateToLocal("potion." + identifier + ".name");
    }

    @Override
    public int getColor(PotionData potion)
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
    public PotionData onBrewed(BrewingType type, PotionData oldData, PotionData newData)
    {
        if (oldData != null)
        {
            if (newData.powerLevel > oldData.powerLevel)
            {
                return newData.setTimeLevel(1);
            }
            else if (newData.timeLevel > oldData.timeLevel)
            {
                return newData.setPowerLevel(1);
            }
        }
        return newData;
    }

    @Override
    public int getMaxPower()
    {
        return 2;
    }

    @Override
    public int getMaxTime()
    {
        return 2;
    }

    @Override
    public int getTimeForLevel(int powerLevel, int timeLevel)
    {
        return 20 * (timeLevel * 120 - powerLevel * 30);
    }

    @Override
    public void renderHook(EntityPlayer player, PotionData data)
    {
        ;
    }

    @Override
    public Effect createEffect(PotionData data, EntityLivingBase entity)
    {
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
            super(identifier, ingredient, null, null, color);
        }

        @Override
        public Effect createEffect(PotionData potion, EntityLivingBase entity)
        {
            return null;
        }

        @Override
        public void onUpdate(PotionData data, EntityLivingBase entity)
        {
            ;
        }

        @Override
        public int getMaxPower()
        {
            return 1;
        }

        @Override
        public int getMaxTime()
        {
            return 1;
        }

        @Override
        public PotionData onBrewed(BrewingType type, PotionData oldData, PotionData newData)
        {
            return newData;
        }

        @Override
        public IIcon getIcon(IIconRegister register)
        {
            return null;
        }
    }

    public static abstract class PotionSimple extends PotionBase
    {
        private ItemStack base;

        public PotionSimple(String identifier, ItemStack ingredient, ItemStack base, ItemStack powerAmp, ItemStack timeAmp, int color)
        {
            super(identifier, ingredient, powerAmp, timeAmp, color);
            this.base = base;
        }

        public PotionSimple(String identifier, ItemStack ingredient, ItemStack base, int color)
        {
            this(identifier, ingredient, base, new ItemStack(Items.glowstone_dust), new ItemStack(Items.redstone), color);
        }
        
        @Override
        public void onUpdate(PotionData data, EntityLivingBase entity)
        {
            ;
        }

        @Override
        public boolean canBeAppliedTo(ItemStack base)
        {
            return OreDictionary.itemMatches(base, this.base, false);
        }
    }
}
