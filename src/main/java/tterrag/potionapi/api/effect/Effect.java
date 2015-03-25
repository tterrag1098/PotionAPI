package tterrag.potionapi.api.effect;

import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;
import tterrag.potionapi.api.brewing.IPotion;

@AllArgsConstructor
public class Effect
{
    @AllArgsConstructor
    public static class PotionData
    {
        public final IPotion potion;
        public final int powerLevel, timeLevel;
    }

    private PotionData data;
    private int timeRemaining;

    public int getTimeRemaining()
    {
        return timeRemaining;
    }

    public void onUpdate(EntityLivingBase entity)
    {
        timeRemaining--;
        data.potion.onUpdate(data, entity);
    }

    public void onApplied(EntityLivingBase entity)
    {
        data.potion.onApplied(data, entity);
    }

    public void onRemoved(EntityLivingBase entity)
    {
        data.potion.onRemoved(data, entity);
    }

    public PotionData getPotionData()
    {
        return data;
    }
}
