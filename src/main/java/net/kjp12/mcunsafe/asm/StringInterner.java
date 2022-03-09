package net.kjp12.mcunsafe.asm;// Created 2022-08-03T23:08:52

import net.gudenau.minecraft.asm.api.v1.AsmUtils;
import net.gudenau.minecraft.asm.api.v1.Identifier;
import net.gudenau.minecraft.asm.api.v1.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author KJP12
 * @since ${version}
 **/
class StringInterner implements Transformer {
    private static final Identifier identifier = new Identifier("mcunsafe", "string-interner");

    @Override
    public Identifier getName() {
        return identifier;
    }

    @Override
    public boolean handlesClass(String name, String transformedName) {
        return true;
    }

    @Override
    public boolean transform(ClassNode classNode, Flags flags) {
        boolean transformed = false;
        for (var method : classNode.methods) {
            boolean f = false;
            for (var insn : AsmUtils.findMatchingNodes(method, node -> node instanceof FieldInsnNode fnode &&
                    (node.getOpcode() == Opcodes.PUTFIELD || node.getOpcode() == Opcodes.PUTSTATIC) &&
                    "Ljava/lang/String;".equals(fnode.desc))) {
                var il = new InsnList();
                var la = new LabelNode();
                il.add(new InsnNode(Opcodes.DUP));
                il.add(new JumpInsnNode(Opcodes.IFNULL, la));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "intern", "()Ljava/lang/String;"));
                il.add(la);
                method.instructions.insertBefore(insn, il);
                transformed = f = true;
            }
            if (f)
                System.out.printf("MCU-SI: Successfully transformed %s/%s%s\n", classNode.name, method.name, method.desc);
        }
        if (transformed) {
            flags.requestFrames();
            flags.requestMaxes();
        }
        return transformed;
    }
}
