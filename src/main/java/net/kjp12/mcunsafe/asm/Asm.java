package net.kjp12.mcunsafe.asm;// Created 2022-08-03T23:06:37

import net.gudenau.minecraft.asm.api.v1.AsmInitializer;
import net.gudenau.minecraft.asm.api.v1.AsmRegistry;

/**
 * @author KJP12
 * @since ${version}
 **/
public class Asm implements AsmInitializer {
    @Override
    public void onInitializeAsm() {
        System.err.printf("""
                === WARNING: MAJOR TRANSFORMERS PRESENT VIA McUnsafe ===
                                
                Your mods may break, your worlds may cry and usage of
                IdentityMaps will wish that you never introduced McUnsafe.
                                
                === WARNING: MAJOR TRANSFORMERS PRESENT VIA McUnsafe ===
                """);
        var gud = AsmRegistry.getInstance();
        gud.registerTransformer(new StringInterner());
    }
}
