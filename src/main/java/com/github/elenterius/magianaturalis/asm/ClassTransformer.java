package com.github.elenterius.magianaturalis.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

public class ClassTransformer implements IClassTransformer, Opcodes {

    private static final String classToBeTransformed = "net.minecraft.client.renderer.entity.RendererLivingEntity";

    private static void transformRendererLivingEntity(ClassNode classNode, int isObfuscated) {
        final String RENDER_MODEL = isObfuscated == 1 ? "a" : "renderModel";
        final String RENDER_MODEL_DESC = isObfuscated == 1 ? "(Lsv;FFFFFF)V"
            : "(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V";

        for (MethodNode method : classNode.methods) {
            if (method.name.equals(RENDER_MODEL) && method.desc.equals(RENDER_MODEL_DESC)) {
                AbstractInsnNode targetNode = null;
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction.getOpcode() == ALOAD && ((VarInsnNode) instruction).var == 1)
                        if (instruction.getNext()
                            .getOpcode() == INVOKESTATIC) {
                                targetNode = instruction;
                                break;
                            }
                }

                if (targetNode != null) {
                    /*
                     * ----FROM
                     * THIS---------------------------------------------------------------------------------------------
                     * ------------------------------------------------------------------
                     * #mv.visitVarInsn(ALOAD, 1);
                     * #mv.visitMethodInsn(INVOKESTATIC, "net/minecraft/client/Minecraft", "getMinecraft",
                     * "()Lnet/minecraft/client/Minecraft;", false);
                     * #mv.visitFieldInsn(GETFIELD, "net/minecraft/client/Minecraft", "thePlayer",
                     * "Lnet/minecraft/client/entity/EntityClientPlayerMP;");
                     * #mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase",
                     * "isInvisibleToPlayer", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false);
                     * Label l5 = new Label();
                     * mv.visitJumpInsn(IFNE, l5);
                     * ----TO
                     * THIS---------------------------------------------------------------------------------------------
                     * --------------------------------------------------------------------
                     * mv.visitMethodInsn(INVOKESTATIC, "trinarybrain/magia/naturalis/asm/MethodStub",
                     * "showInvisibleEntityToPlayer", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false);
                     * Label l5 = new Label();
                     * mv.visitJumpInsn(IFEQ, l5);
                     */

                    // ALOAD METHOD GETFIELD
                    targetNode = targetNode.getNext()
                        .getNext();

                    final String DESC = isObfuscated == 1 ? "(Lsv;)Z" : "(Lnet/minecraft/entity/EntityLivingBase;)Z";
                    MethodInsnNode newNode = new MethodInsnNode(
                        INVOKESTATIC,
                        "com/github/elenterius/magianaturalis/asm/MethodStub",
                        "showInvisibleEntityToPlayer",
                        DESC,
                        false);
                    method.instructions.set(targetNode.getPrevious(), newNode);

                    targetNode = targetNode.getNext(); // INVOEKVIRTUAL
                    method.instructions.remove(targetNode.getPrevious()); // GETFIELD

                    targetNode = targetNode.getNext(); // JUMP/LABEL
                    method.instructions.remove(targetNode.getPrevious()); // INVOEKVIRTUAL

                    targetNode = targetNode.getNext();
                    JumpInsnNode jumpNode = new JumpInsnNode(IFEQ, ((JumpInsnNode) targetNode.getPrevious()).label);
                    method.instructions.set(targetNode.getPrevious(), jumpNode);
                } else {
                    MagiaNaturalis.LOGGER.info("can't find place for byte injection");
                }
                break;
            }
        }
    }

    @Override
    public byte[] transform(String className, String transformedName, byte[] classfileBuffer) {
        int isObfuscated = !className.equals(transformedName) ? 1 : 0;
        if (!transformedName.equals(classToBeTransformed)) return classfileBuffer;

        MagiaNaturalis.LOGGER.info("Transforming: " + classToBeTransformed);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classfileBuffer);
            classReader.accept(classNode, 0);

            transformRendererLivingEntity(classNode, isObfuscated);

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            classfileBuffer = classWriter.toByteArray();

            MagiaNaturalis.LOGGER.info("Successfully transformed class: " + classToBeTransformed);
        } catch (Exception e) {
            MagiaNaturalis.LOGGER.catching(e);
        }

        // BYTECODE DUMP
        // String[] desc = {"dev", "obf"};
        // try(FileOutputStream fos = new FileOutputStream("D:/dump/RendererLivingEntity_bytecode_" + desc[isObfuscated]
        // + ".class"))
        // {
        // Log.logger.info("Dumping bytecode as D:/dump/RendererLivingEntity_bytecode_" + desc[isObfuscated] +
        // ".class");
        // fos.write(classfileBuffer);
        // }
        // catch(IOException ioe)
        // {
        // Log.logger.catching(ioe);
        // }

        return classfileBuffer;
    }

}
