package tterrag.potionapi.api.item;

import net.minecraft.item.ItemStack;
import tterrag.potionapi.api.brewing.IPotion;

public interface IPotionItem
{
    void setPotion(ItemStack stack, IPotion potion, int powerLevel, int timeLevel);

    IPotion getPotion(ItemStack stack);

    int getPowerLevel(ItemStack stack);

    int getTimeLevel(ItemStack stack);
}
