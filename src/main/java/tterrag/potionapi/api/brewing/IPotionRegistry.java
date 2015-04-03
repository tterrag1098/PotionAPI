package tterrag.potionapi.api.brewing;

import java.util.Collection;

import net.minecraft.util.IIcon;

public interface IPotionRegistry
{
    void registerPotion(IPotion potion);

    IPotion getPotionByID(String string);

    Collection<IPotion> getPotions();

    IIcon getIconFor(IPotion potion);
}
