package tterrag.potionapi.api.brewing;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.Effect.PotionData;

public interface IPotion
{
    /**
     * The global identifier for this potion, used for data saving and lookups.
     * 
     * @return A string ID for the potion.
     */
    String getIdentifier();

    String getLocalizedName(ItemStack potion);

    int getColor(ItemStack potion);

    /**
     * True if this potion can be created from the passed potion and passed ingredient.
     *
     * @param potion
     *            The potion the ingredient is being added to. This can be any item, will typically be a water bottle or some other no-effect potion.
     * @param ingredient
     *            The ingredient being added to the potion. Can be any item.
     * @return True if the passed {@link ItemStack ItemStacks} will create this potion.
     */
    boolean isIngredient(ItemStack potion, ItemStack ingredient);

    /**
     * This method can be used to finely control the power amplifier.
     * 
     * @param potion
     *            The current potion stack
     * @param ingredient
     *            The ingredient stack
     * @return True if the passed ingredient can be applied to the passed potion as a power amplifier
     */
    boolean isPowerAmplifier(ItemStack potion, ItemStack ingredient);

    /**
     * This method can be used to finely control the time amplifier.
     * 
     * @param potion
     *            The current potion stack
     * @param ingredient
     *            The ingredient stack
     * @return True if the passed ingredient can be applied to the passed potion as a time amplifier
     */
    boolean isTimeAmplifier(ItemStack potion, ItemStack ingredient);

    /**
     * The max power level.
     * 
     * @return The amount of times a power amplifier can be applied
     */
    int getMaxPower();

    /**
     * The max time level
     * 
     * @return The amount of times a time amplifier can be applied
     */
    int getMaxTime();

    /**
     * The duration for the current time level
     * 
     * @param powerLevel
     *            The current power level
     * @param timeLevel
     *            The current time level
     * @return A time, in seconds
     */
    int getTimeForLevel(int powerLevel, int timeLevel);

    @Nullable
    Effect createEffect(ItemStack potion, EntityLivingBase entity);

    void onApplied(PotionData data, EntityLivingBase entity);

    void onUpdate(PotionData data, EntityLivingBase entity);

    void onRemoved(PotionData data, EntityLivingBase entity);
}
