package tterrag.potionapi.common;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.core.common.util.TTItemUtils;
import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.PotionUtil;
import tterrag.potionapi.api.effect.Effect.PotionData;
import tterrag.potionapi.api.item.IPotionItem;
import tterrag.potionapi.common.brewing.PotionRegistry;

public class TileBetterBrewing extends TileEntityBrewingStand
{
    private int input = field_145941_a[0];

    @Override
    protected boolean canBrew()
    {
        if (super.canBrew())
        {
            return true;
        }
        ItemStack ingredient = brewingItemStacks[input];
        for (int i : field_145947_i)
        {
            ItemStack base = brewingItemStacks[i];
            if (base != null && base.getItem() instanceof IPotionItem)
            {
                PotionData data = PotionUtil.getDataFromNBT(TTItemUtils.getNBTTag(base));
                if (data.potion != null && ingredient != null
                        && (PotionUtil.canAmpPower(data, ingredient) || PotionUtil.canAmpTime(data, ingredient)))
                {
                    return true;
                }
            }
            for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
            {
                if (potion.canBeAppliedTo(base) && potion.isIngredient(base, ingredient))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void brewPotions()
    {
        ItemStack ingredient = this.brewingItemStacks[3];

        if (canBrew())
        {
            boolean[] changed = new boolean[3];
            for (int i = 0; i < 3; ++i)
            {
                ItemStack base = this.brewingItemStacks[i];
                if (base != null)
                {
                    if (base.getItem() instanceof IPotionItem)
                    {
                        PotionData data = PotionUtil.getDataFromNBT(TTItemUtils.getNBTTag(base));
                        if (PotionUtil.canAmpPower(data, ingredient))
                        {
                            data = data.incrPower();
                            changed[i] = true;
                        }
                        if (PotionUtil.canAmpTime(data, ingredient))
                        {
                            data = data.incrTime();
                            changed[i] = true;
                        }
                        if (changed[i])
                        {
                            PotionUtil.writePotionNBT(base.stackTagCompound, data);
                        }
                    }
                    else if (!changed[i])
                    {
                        IPotion res = PotionUtil.getResult(base, ingredient);
                        if (res != null)
                        {
                            ItemStack newPotion = new ItemStack(PotionAPI.potion);
                            ((IPotionItem) newPotion.getItem()).setPotion(newPotion, res, 1, 1);
                            this.brewingItemStacks[i] = newPotion;
                            changed[i] = true;
                        }
                    }
                }
            }

            if (ArrayUtils.contains(changed, true))
            {
                if (ingredient.getItem().hasContainerItem(ingredient))
                {
                    this.brewingItemStacks[input] = ingredient.getItem().getContainerItem(ingredient);
                }
                else
                {
                    --this.brewingItemStacks[input].stackSize;

                    if (this.brewingItemStacks[input].stackSize <= 0)
                    {
                        this.brewingItemStacks[input] = null;
                    }
                }
                return;
            }
        }
        super.brewPotions();
    }
}
