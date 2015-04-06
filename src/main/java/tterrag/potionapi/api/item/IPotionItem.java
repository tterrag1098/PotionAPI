package tterrag.potionapi.api.item;

import net.minecraft.item.ItemStack;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.effect.PotionData;

public interface IPotionItem
{
    void setPotion(ItemStack stack, PotionData data);

    IPotion getPotion(ItemStack stack);

    int getPowerLevel(ItemStack stack);

    int getTimeLevel(ItemStack stack);
}
