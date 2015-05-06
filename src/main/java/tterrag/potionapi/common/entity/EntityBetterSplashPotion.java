package tterrag.potionapi.common.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.api.effect.EffectUtil;
import tterrag.potionapi.api.effect.PotionData;
import tterrag.potionapi.api.item.IPotionItem;
import tterrag.potionapi.common.potions.MessageSplashFX;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityBetterSplashPotion extends EntityPotion
{
    private static final int ID_POTION = 24;

    public EntityBetterSplashPotion(World world)
    {
        super(world);
    }

    public EntityBetterSplashPotion(World world, ItemStack potion)
    {
        super(world, 0, 0, 0, potion);
        setPotion(potion);
    }

    public EntityBetterSplashPotion(World world, EntityLivingBase thrower, ItemStack potion)
    {
        super(world, thrower, potion);
        setPotion(potion);
    }

    public EntityBetterSplashPotion(World world, double x, double y, double z, ItemStack potion)
    {
        super(world, x, y, z, potion);
        setPotion(potion);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObjectByDataType(ID_POTION, 5);
    }

    public void setPotion(ItemStack potion)
    {
        dataWatcher.updateObject(ID_POTION, potion);
    }

    public ItemStack getPotion()
    {
        return dataWatcher.getWatchableObjectItemStack(ID_POTION);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onImpact(MovingObjectPosition mop)
    {
        if (!worldObj.isRemote)
        {
            AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D, 4.0D, 4.0D);
            List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
            if (entities != null && !entities.isEmpty())
            {
                Iterator<EntityLivingBase> iterator = entities.iterator();
                while (iterator.hasNext())
                {
                    EntityLivingBase hitEntity = iterator.next();
                    double distanceToHit = this.getDistanceSqToEntity(hitEntity);
                    if (distanceToHit < 16.0D)
                    {
                        double durationMultiplier = 1.0D - Math.sqrt(distanceToHit) / 4.0D;
                        if (hitEntity == mop.entityHit)
                        {
                            durationMultiplier = 1.0D;
                        }

                        ItemStack potion = getPotion();
                        PotionData data = ((IPotionItem) potion.getItem()).getData(potion);
                        Effect effect = data.potion.createEffect(data, hitEntity);
                        if (effect != null)
                        {
                            effect.setTimeRemaining((int) (effect.getTimeRemaining() * durationMultiplier));
                            EffectUtil.applyEffect(effect, hitEntity);
                            EffectUtil.syncDataFor(hitEntity, (EntityPlayerMP) getThrower());
                            PotionAPI.PACKET_HANDLER.sendToAllAround(new MessageSplashFX(data, posX, posY, posZ), new TargetPoint(
                                    worldObj.provider.dimensionId, posX, posY, posZ, 16));
                        }
                    }
                }
            }
            this.setDead();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        super.readEntityFromNBT(tagCompound);
        this.setPotion(ItemStack.loadItemStackFromNBT(tagCompound.getCompoundTag("Potion")));
    }
}