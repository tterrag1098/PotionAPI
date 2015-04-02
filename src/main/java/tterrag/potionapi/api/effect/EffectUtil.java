package tterrag.potionapi.api.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import tterrag.potionapi.common.effect.EffectData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EffectUtil
{
    public static void applyEffect(Effect effect, EntityLivingBase entity)
    {
        EffectData.getInstance(entity).addEffect(effect);
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event)
    {
        EffectData.getInstance(event.entityLiving).tickEffects();
    }
}
