package tterrag.potionapi.api.brewing;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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

    String getLocalizedName(PotionData data);

    int getColor(ItemStack potion);

    /**
     * If the passed stack is a valid stack this potion can be brewed into. Used for determining validity of items being placed into the GUI.
     * 
     * @param base
     *            The itemstack to check
     * @return True if this potion can be applied to the passed stack.
     */
    boolean canBeAppliedTo(ItemStack base);

    /**
     * True if this potion can be created from the passed potion and passed ingredient.
     *
     * @param base
     *            The potion the ingredient is being added to. This can be any item, will typically be a water bottle or some other no-effect potion.
     * @param ingredient
     *            The ingredient being added to the potion. Can be any item.
     * @return True if the passed {@link ItemStack ItemStacks} will create this potion.
     */
    boolean isIngredient(ItemStack base, ItemStack ingredient);

    /**
     * This method can be used to finely control the power amplifier.
     * 
     * @param potion
     *            The current potion
     * @param ingredient
     *            The ingredient stack
     * @return True if the passed ingredient can be applied to the passed potion as a power amplifier
     */
    boolean isPowerAmplifier(PotionData potion, ItemStack ingredient);

    /**
     * This method can be used to finely control the time amplifier.
     * 
     * @param potion
     *            The current potion
     * @param ingredient
     *            The ingredient stack
     * @return True if the passed ingredient can be applied to the passed potion as a time amplifier
     */
    boolean isTimeAmplifier(PotionData potion, ItemStack ingredient);

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
     * The duration (in ticks) for the current time level
     * 
     * @param powerLevel
     *            The current power level
     * @param timeLevel
     *            The current time level
     * @return A time, in seconds
     */
    int getTimeForLevel(int powerLevel, int timeLevel);

    /**
     * Called when this potion renders on the player's GUI, so that custom effects may be rendered.
     * 
     * @param player
     *            The client player
     * @param data
     *            The current effect
     */
    void renderHook(EntityPlayer player, PotionData data);

    /**
     * Register and return this potion's icon for inventory rendering.
     * 
     * @param register
     *            The IIconRegister instance
     * @return The potion's icon
     */
    IIcon getIcon(IIconRegister register);

    @Nullable
    Effect createEffect(ItemStack potion, EntityLivingBase entity);

    void onApplied(PotionData data, EntityLivingBase entity);

    void onUpdate(PotionData data, EntityLivingBase entity);

    void onRemoved(PotionData data, EntityLivingBase entity);
}
