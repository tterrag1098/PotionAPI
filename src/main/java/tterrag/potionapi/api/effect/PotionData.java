package tterrag.potionapi.api.effect;

import javax.annotation.concurrent.Immutable;

import net.minecraft.util.MathHelper;
import tterrag.potionapi.api.brewing.IPotion;

/**
 * Contains data about a potion for use in effects, and for easy use in potion method parameters.
 */
@Immutable
public class PotionData
{
    /**
     * The {@link IPotion} with these stats.
     */
    public final IPotion potion;

    /**
     * The power level of the current potion effect instance
     */
    public final int powerLevel;

    /**
     * The time level of the current potion effect instance.
     */
    public final int timeLevel;

    public PotionData(IPotion potion, int powerLevel, int timeLevel)
    {
        this.potion = potion;
        this.powerLevel = powerLevel;
        this.timeLevel = timeLevel;
    }

    /**
     * Increments the power level by one if possible.
     * 
     * @return A new {@code PotionData} instance.
     */
    public PotionData incrPower()
    {
        return new PotionData(potion, powerLevel + 1, timeLevel);
    }

    /**
     * Increments the time level by one if possible.
     * 
     * @return A new {@code PotionData} instance.
     */
    public PotionData incrTime()
    {
        return new PotionData(potion, powerLevel, timeLevel + 1);
    }

    /**
     * Creates a new {@code PotionData} with the same stats, except for the passed powerLevel.
     * 
     * @param powerLevel
     *            The new powerLevel to set.
     * @return A new {@code PotionData} instance.
     */
    public PotionData setPowerLevel(int powerLevel)
    {
        return new PotionData(potion, MathHelper.clamp_int(powerLevel, 0, potion.getMaxPower()), timeLevel);
    }

    /**
     * Creates a new {@code PotionData} with the same stats, except for the passed timeLevel.
     * 
     * @param timeLevel
     *            The new timeLevel to set.
     * @return A new {@code PotionData} instance.
     */
    public PotionData setTimeLevel(int timeLevel)
    {
        return new PotionData(potion, powerLevel, MathHelper.clamp_int(timeLevel, 0, potion.getMaxTime()));
    }
}
