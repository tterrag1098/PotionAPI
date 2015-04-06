package tterrag.potionapi.api.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;

public class Effect
{
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

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("effect." + data.potion.getIdentifier() + ".name");
    }
}
