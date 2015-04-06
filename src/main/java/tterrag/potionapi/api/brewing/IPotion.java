package tterrag.potionapi.api.brewing;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.PotionData;

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
     * A hook for special behavior when this potion is brewed. Can be used to decrement other stats when one stat is changed (e.g. vanilla potion
     * behavior).
     * 
     * @param type
     *            The {@link BrewingType type} of brewing that is occuring
     * @param oldData
     *            The data of the potion before it was brewed. This can be null if the old ItemStack is not a potion, e.g. in the
     *            {@link BrewingType#POTION} case.
     * @param newData
     *            The future data of the potion after it is brewed. Returning a non-null PotionData from this method will override these values.
     * @return A PotionData representing the properties of this potion after it is brewed. Return {@code null} or {@code newData} to maintain default
     *         behavior.
     */
    @Nullable
    PotionData onBrewed(BrewingType type, @Nullable PotionData oldData, PotionData newData);

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
     * Register and return this potion's icon for inventory rendering. The icon is stitched to the item sheet, so it must be located in
     * textures/items.
     * 
     * @param register
     *            The IIconRegister instance
     * @return The potion's icon
     */
    IIcon getIcon(IIconRegister register);

    /**
     * Create an {@link Effect} for the current potion data.
     * 
     * @param data
     *            Current potion data.
     * @param entity
     *            The entity the effect is being applied to. Can be null for use in tooltips, etc.
     * @return An {@link Effect} to apply to the given entity.
     */
    @Nullable
    Effect createEffect(PotionData data, @Nullable EntityLivingBase entity);

    void onApplied(PotionData data, EntityLivingBase entity);

    void onUpdate(PotionData data, EntityLivingBase entity);

    void onRemoved(PotionData data, EntityLivingBase entity);
}
