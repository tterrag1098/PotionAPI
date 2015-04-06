package tterrag.potionapi.api.effect;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.lwjgl.opengl.GL11;

import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.common.brewing.PotionRegistry;
import tterrag.potionapi.common.effect.EffectData;
import tterrag.potionapi.common.effect.MessageEffect;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EffectUtil
{
    public static final EffectUtil INSTANCE = new EffectUtil();

    private EffectUtil()
    {
    }

    public void register()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static void applyEffect(Effect effect, EntityLivingBase entity)
    {
        EffectData.getInstance(entity).addEffect(effect);
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event)
    {
        EffectData.getInstance(event.entityLiving).tickEffects();
    }

    private static final ResourceLocation INVENTORY_TEXTURES = new ResourceLocation("textures/gui/container/inventory.png");

    @SubscribeEvent
    public void onOverlayRender(GuiScreenEvent.DrawScreenEvent event)
    {
        if (event.gui != null && event.gui instanceof InventoryEffectRenderer)
        {
            InventoryEffectRenderer gui = (InventoryEffectRenderer) event.gui;
            int x = gui.guiLeft - 124;
            int y = gui.guiTop;
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Collection<Effect> effects = EffectData.getInstance(player).getActiveEffects();

            if (!effects.isEmpty())
            {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                int yOffset = 33;
                int totalSize = effects.size() + player.getActivePotionEffects().size();

                if (player.getActivePotionEffects().size() > 5)
                {
                    yOffset = 132 / (totalSize - 1);
                }
                
                y += yOffset * (totalSize - 1);

                for (Iterator<Effect> iterator = effects.iterator(); iterator.hasNext(); y += yOffset)
                {
                    Effect effect = iterator.next();
                    PotionData data = effect.getPotionData();
                    IPotion potion = data.potion;
                    GL11.glColor4f(1, 1, 1, 1);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(INVENTORY_TEXTURES);
                    gui.drawTexturedModalRect(x, y, 0, 166, 140, 32);

                    IIcon potionIcon = PotionRegistry.INSTANCE.getIconFor(potion);
                    if (potionIcon != null)
                    {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                        gui.drawTexturedModelRectFromIcon(x + 6, y + 7, potionIcon, 18, 18);
                    }

                    String s = I18n.format(effect.getLocalizedName(), new Object[0]);
                    s = appendLevel(s, data);

                    FontRenderer fnt = Minecraft.getMinecraft().fontRenderer;
                    fnt.drawStringWithShadow(s, x + 10 + 18, y + 6, 16777215);
                    fnt.drawStringWithShadow(StringUtils.ticksToElapsedTime(effect.getTimeRemaining()), x + 10 + 18, y + 6 + 10, 8355711);
                }
            }
        }
    }

    public static String appendLevel(String s, PotionData data)
    {
        if (data.powerLevel > 1)
        {
            s += " " + StatCollector.translateToLocal("enchantment.level." + data.powerLevel);
        }
        return s;
    }
        
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.target instanceof EntityLivingBase)
        {
            syncDataFor((EntityLivingBase) event.target, (EntityPlayerMP) event.entityPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        syncDataFor(event.player, (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerChangedDimensionEvent event)
    {
        syncDataFor(event.player, (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerRespawnEvent event)
    {
        syncDataFor(event.player, (EntityPlayerMP) event.player);
    }

    private void syncDataFor(EntityLivingBase entity, EntityPlayerMP to)
    {
        EffectData data = EffectData.getInstance(entity);
        if (!data.getActiveEffects().isEmpty())
        {
            NBTTagCompound tag = new NBTTagCompound();
            data.saveNBTData(tag);
            PotionAPI.PACKET_HANDLER.sendTo(new MessageEffect(entity, tag), to);
        }
    }
}
