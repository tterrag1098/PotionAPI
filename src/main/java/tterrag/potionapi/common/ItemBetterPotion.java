package tterrag.potionapi.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.PotionUtil;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.EffectUtil;
import tterrag.potionapi.api.effect.PotionData;
import tterrag.potionapi.api.item.IPotionItem;
import tterrag.potionapi.common.brewing.PotionRegistry;
import tterrag.potionapi.common.entity.EntityBetterSplashPotion;
import tterrag.potionapi.common.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBetterPotion extends Item implements IPotionItem
{
    @SideOnly(Side.CLIENT)
    private IIcon potionIcon;
    @SideOnly(Side.CLIENT)
    private IIcon splashIcon;
    @SideOnly(Side.CLIENT)
    private IIcon colorIcon;

    private final boolean throwable;
    
    public ItemBetterPotion(boolean throwable)
    {
        super();
        setUnlocalizedName("potionapi.potion");
        setCreativeTab(CreativeTabs.tabBrewing);
        this.throwable = throwable;
    }

    @Override
    public void registerIcons(IIconRegister register)
    {
        this.potionIcon = register.registerIcon(PotionAPI.MODID + ":potionbase");
        this.splashIcon = register.registerIcon(PotionAPI.MODID + ":potionsplash");
        this.colorIcon = register.registerIcon(PotionAPI.MODID + ":potioncolor");
    }

    @Override
    public void setPotion(ItemStack stack, PotionData data)
    {
        PotionUtil.writePotionNBT(NBTUtil.getNBTTag(stack), data);
    }

    @Override
    public PotionData getData(ItemStack stack)
    {
        return PotionUtil.getDataFromNBT(NBTUtil.getNBTTag(stack));
    }

    @Override
    public IPotion getPotion(ItemStack stack)
    {
        return PotionUtil.getPotionFromNBT(NBTUtil.getNBTTag(stack));
    }

    @Override
    public int getPowerLevel(ItemStack stack)
    {
        return PotionUtil.getPowerLevelFromNBT(NBTUtil.getNBTTag(stack));
    }

    @Override
    public int getTimeLevel(ItemStack stack)
    {
        return PotionUtil.getTimeLevelFromNBT(NBTUtil.getNBTTag(stack));
    }
    
    @Override
    public boolean isThrowable(ItemStack stack)
    {
        return throwable;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            for (int powerLevel = 1; powerLevel <= potion.getMaxPower(); powerLevel++)
            {
                for (int timeLevel = 1; timeLevel <= potion.getMaxTime(); timeLevel++)
                {
                    ItemStack stack = new ItemStack(item);
                    PotionUtil.writePotionNBT(NBTUtil.getNBTTag(stack), potion, powerLevel, timeLevel);
                    list.add(stack);
                }
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        return pass == 1 ? 0xFFFFFF : getPotion(stack).getColor(getData(stack));
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return pass == 0 ? colorIcon : throwable ? splashIcon : potionIcon;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return getIcon(stack, renderPass);
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        return potionIcon;
    }

    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return 2;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass)
    {
        return pass == 0;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return getPotion(stack).getLocalizedName(PotionUtil.getDataFromNBT(NBTUtil.getNBTTag(stack)));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (isThrowable(stack))
        {
            if (!player.capabilities.isCreativeMode)
            {
                --stack.stackSize;
            }

            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(new EntityBetterSplashPotion(world, player, stack));
            }

            return stack;
        }
        else
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return isThrowable(p_77661_1_) ? EnumAction.none : EnumAction.drink;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack p_77626_1_)
    {
        return 32;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
        PotionUtil.applyPotion(stack, player);
        stack.stackSize--;
        return stack.stackSize <= 0 ? null : stack;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        PotionData data = getData(stack);
        Effect effect = data.potion.createEffect(data, null);
        if (effect != null)
        {
            String desc = effect.getLocalizedName();
            desc = EffectUtil.appendLevel(desc, data);
            desc += " (" + StringUtils.ticksToElapsedTime(getPotion(stack).getTimeForLevel(getPowerLevel(stack), getTimeLevel(stack))) + ")";
            list.add(desc);
        }
    }

    // @SuppressWarnings("rawtypes")
    // @Override
    // public List getEffects(int p_77834_1_)
    // {
    // return null;
    // }
    //
    // @SuppressWarnings("rawtypes")
    // @Override
    // public List getEffects(ItemStack p_77832_1_)
    // {
    // return null;
    // }
}
