package tterrag.potionapi.common;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import tterrag.core.common.util.TTItemUtils;
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
                if (data.potion != null)
                {
                    return ingredient != null && data.potion.isPowerAmplifier(data, ingredient) || data.potion.isTimeAmplifier(data, ingredient);
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
