package tterrag.potionapi.common;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import tterrag.potionapi.api.brewing.IPotion;
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
                IPotion potion = ((IPotionItem) base.getItem()).getPotion(base);
                if (potion != null)
                {
                    return ingredient != null && potion.isPowerAmplifier(base, ingredient) || potion.isTimeAmplifier(base, ingredient);
                }
            }
            for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
            {
                if (potion.isIngredient(base, ingredient))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
