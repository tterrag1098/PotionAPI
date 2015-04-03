package tterrag.potionapi.api.effect;

import net.minecraft.entity.EntityLivingBase;
import tterrag.potionapi.api.brewing.IPotion;

public class Effect
{
    public static class PotionData
    {
        public final IPotion potion;
        public final int powerLevel, timeLevel;

        public PotionData(IPotion potion, int powerLevel, int timeLevel)
        {
            this.potion = potion;
            this.powerLevel = powerLevel;
            this.timeLevel = timeLevel;
        }

        public PotionData incrPower()
        {
            return new PotionData(potion, powerLevel + 1, timeLevel);
        }

        public PotionData incrTime()
        {
            return new PotionData(potion, powerLevel, timeLevel + 1);
        }
    }

    private PotionData data;
    private int timeRemaining;

    public Effect(PotionData data, int timeRemaining)
    {
        this.data = data;
        this.timeRemaining = timeRemaining;
    }

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
