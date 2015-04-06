package tterrag.potionapi;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.PotionBase.PotionSimple;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.EffectUtil;
import tterrag.potionapi.api.effect.PotionData;
import tterrag.potionapi.client.GuiBetterBrewing;
import tterrag.potionapi.common.BlockBetterBrewing;
import tterrag.potionapi.common.CommonProxy;
import tterrag.potionapi.common.ContainerBetterBrewing;
import tterrag.potionapi.common.ItemBetterPotion;
import tterrag.potionapi.common.TileBetterBrewing;
import tterrag.potionapi.common.brewing.PotionRegistry;
import tterrag.potionapi.common.effect.EffectData;
import tterrag.potionapi.common.effect.MessageEffect;
import tterrag.potionapi.common.util.ReplaceUtil;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import static tterrag.potionapi.PotionAPI.*;

@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = "after:ttCore")
public class PotionAPI implements IGuiHandler
{
    public static final String MODID = "potionapi";
    public static final String NAME = "PotionAPI";
    public static final String VERSION = "@VERSION@";

    public static Item potion;
    public static Block brewingStand;

    @Instance
    public static PotionAPI INSTANCE;

    @SidedProxy(clientSide = "tterrag.potionapi.client.ClientProxy", serverSide = "tterrag.potionapi.common.CommonProxy")
    public static CommonProxy proxy;

    public static final SimpleNetworkWrapper PACKET_HANDLER = new SimpleNetworkWrapper(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ReplaceUtil());
        EffectUtil.INSTANCE.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, INSTANCE);

        PACKET_HANDLER.registerMessage(MessageEffect.Handler.class, MessageEffect.class, 0, Side.CLIENT);

        potion = new ItemBetterPotion();
        GameRegistry.registerItem(potion, "potion");

        brewingStand = new BlockBetterBrewing();
        Blocks.brewing_stand = brewingStand;
        ((ItemReed) Items.brewing_stand).field_150935_a = brewingStand;
        ReplaceUtil.overwriteEntry(Block.blockRegistry, "minecraft:brewing_stand", brewingStand);

        GameRegistry.registerTileEntity(TileBetterBrewing.class, "tileBetterBrewing");

        IPotion leaping = new PotionLeaping();
        MinecraftForge.EVENT_BUS.register(leaping);
        PotionRegistry.INSTANCE.registerPotion(leaping);
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event)
    {
        if (event.entity instanceof EntityLivingBase)
        {
            event.entity.registerExtendedProperties(EffectData.IDENT, new EffectData());
        }
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event)
    {
        try
        {
            // My eyes!
            if (event.command.getCommandName().equals("effect") && event.parameters.length >= 2)
            {
                EntityPlayerMP player = CommandBase.getPlayer(event.sender, event.parameters[0]);
                IPotion potion = PotionRegistry.INSTANCE.getPotionByID(event.parameters[1]);
                EffectData inst = EffectData.getInstance(player);
                if (player == null)
                {
                    return;
                }
                if ("clear".equals(event.parameters[1]))
                {
                    EffectUtil.clearEffects(player);
                    CommandBase.func_152373_a(event.sender, event.command, "commands.effect.success.removed.all", player.getCommandSenderName());
                    event.setCanceled(true);
                }
                else
                {
                    if (potion == null)
                    {
                        return;
                    }
                    int seconds = event.parameters.length > 2 ? CommandBase.parseIntWithMin(event.sender, event.parameters[2], 0) : 30;
                    int level = event.parameters.length > 3 ? CommandBase.parseIntWithMin(event.sender, event.parameters[3], 0) + 1 : 1;
                    Effect effect = new Effect(new PotionData(potion, level, 1), seconds * 20);
                    if (seconds > 0)
                    {
                        CommandBase.func_152373_a(event.sender, event.command, "commands.effect.success",
                                new ChatComponentText(effect.getLocalizedName()), '"' + potion.getIdentifier() + '"', level,
                                player.getCommandSenderName(), seconds);
                        EffectUtil.applyEffect(effect, player);
                        event.setCanceled(true);
                    }
                    else
                    {
                        if (EffectData.getPotionData(player, potion) == null)
                        {
                            throw new CommandException("commands.effect.failure.notActive", new ChatComponentText(potion.getLocalizedName(effect
                                    .getPotionData())), player.getCommandSenderName());
                        }

                        EffectUtil.removeEffect(potion, player);
                        CommandBase.func_152373_a(event.sender, event.command, "commands.effect.success.removed",
                                new ChatComponentText(potion.getLocalizedName(effect.getPotionData())), player.getCommandSenderName());
                        event.setCanceled(true);
                    }
                }
                NBTTagCompound tag = new NBTTagCompound();
                inst.saveNBTData(tag);
                PACKET_HANDLER.sendToDimension(new MessageEffect(player, tag), player.worldObj.provider.dimensionId);
            }
        }
        catch (Exception e)
        {
            event.exception = e;
            event.setCanceled(true);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBetterBrewing)
        {
            return new ContainerBetterBrewing(player.inventory, (TileEntityBrewingStand) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBetterBrewing)
        {
            return new GuiBetterBrewing(player.inventory, (TileEntityBrewingStand) world.getTileEntity(x, y, z));
        }
        return null;
    }

    public static class PotionLeaping extends PotionSimple
    {
        private PotionLeaping()
        {
            super("leaping", new ItemStack(Items.slime_ball), new ItemStack(Items.potionitem, 1, 16), 0x22FF4C);
        }

        @Override
        public IIcon getIcon(IIconRegister register)
        {
            return register.registerIcon("potionapi:leaping");
        }

        @SubscribeEvent
        public void onLivingJump(LivingJumpEvent event)
        {
            PotionData data = EffectData.getPotionData(event.entityLiving, this);
            if (data != null)
            {
                event.entityLiving.motionY += data.powerLevel * 0.1F;
            }
        }
    }
}
