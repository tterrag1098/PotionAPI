package tterrag.potionapi.common.asm;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tterrag.potionapi.api.effect.Effect;
import tterrag.potionapi.common.effect.EffectData;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

import static org.objectweb.asm.Opcodes.*;

@MCVersion(value = "1.7.10")
public class EntityParticleTransformer implements IClassTransformer
{
    public static final String className = "net.minecraft.entity.EntityLivingBase";
    public static final String methodNameSrg = "func_70679_bo";
    public static final String methodNameDeobf = "updatePotionEffects";

    @SuppressWarnings("deprecation")
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (transformedName.equals(className))
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);

            Iterator<MethodNode> methods = classNode.methods.iterator();

            while (methods.hasNext())
            {
                MethodNode m = methods.next();
                if (methodNameSrg.equals(m.name) || methodNameDeobf.equals(m.name))
                {
                    for (int i = 0; i < m.instructions.size(); i++)
                    {
                        AbstractInsnNode node = m.instructions.get(i);
                        if (node instanceof MethodInsnNode && node.getOpcode() == INVOKESTATIC
                                && ((MethodInsnNode) node).desc.equals("(Ljava/util/Collection;)I"))
                        {
                            String callMethodClass = "tterrag.potionapi.common.asm.EntityParticleTransformer";
                            String callMethodName = "getColor";
                            String callMethodSig = "(Ljava/util/Collection;Lnet/minecraft/entity/EntityLivingBase;)I";

                            m.instructions.insertBefore(node, new VarInsnNode(ALOAD, 0));
                            m.instructions.set(node, new MethodInsnNode(INVOKESTATIC, callMethodClass, callMethodName, callMethodSig));
                        }
                    }
                }
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(cw);
            LogManager.getLogger().info("Transforming " + transformedName + " Finished.");
            return cw.toByteArray();
        }
        else
        {
            return basicClass;
        }
    }

    @SuppressWarnings("rawtypes")
    public static int getColor(Collection activeEffects, EntityLivingBase entity)
    {
        int i = 3694022;

        if (activeEffects != null && !activeEffects.isEmpty())
        {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            Iterator iterator = activeEffects.iterator();

            while (iterator.hasNext())
            {
                PotionEffect potioneffect = (PotionEffect) iterator.next();
                int j = Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor();

                for (int k = 0; k <= potioneffect.getAmplifier(); ++k)
                {
                    f += (float) (j >> 16 & 255) / 255.0F;
                    f1 += (float) (j >> 8 & 255) / 255.0F;
                    f2 += (float) (j >> 0 & 255) / 255.0F;
                    ++f3;
                }
            }

            if (entity != null)
            {
                iterator = EffectData.getInstance(entity).getActiveEffects().iterator();
                while (iterator.hasNext())
                {
                    Effect effect = (Effect) iterator.next();
                    int j = effect.getPotionData().potion.getColor(effect.getPotionData());

                    for (int k = 0; k <= effect.getPotionData().powerLevel; ++k)
                    {
                        f += (float) (j >> 16 & 255) / 255.0F;
                        f1 += (float) (j >> 8 & 255) / 255.0F;
                        f2 += (float) (j >> 0 & 255) / 255.0F;
                        ++f3;
                    }
                }
            }

            f = f / f3 * 255.0F;
            f1 = f1 / f3 * 255.0F;
            f2 = f2 / f3 * 255.0F;
            return (int) f << 16 | (int) f1 << 8 | (int) f2;
        }
        else
        {
            return i;
        }
    }
}
