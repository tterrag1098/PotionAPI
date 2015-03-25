package tterrag.potionapi.common;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.item.IPotionItem;

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
        for (int i : field_145947_i)
        {
            ItemStack base = brewingItemStacks[i];
            if (base != null && base.getItem() instanceof IPotionItem)
            {
                IPotion potion = ((IPotionItem) base.getItem()).getPotion(base);
                if (potion != null)
                {
                    ItemStack ingredient = brewingItemStacks[input];
                    return ingredient != null && potion.isPowerAmplifier(base, ingredient) || potion.isTimeAmplifier(base, ingredient);
                }
            }
        }
        return false;
    }
}
