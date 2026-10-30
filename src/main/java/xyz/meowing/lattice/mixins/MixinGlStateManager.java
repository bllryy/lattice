package xyz.meowing.lattice.mixins;

//? if >=26.3 {
import com.mojang.renderpearl.backend.opengl.GlStateManager;
//?} else {
/*import com.mojang.blaze3d.opengl.GlStateManager;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.meowing.lattice.render.GLState;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
    @Inject(method = "_bindTexture", at = @At("HEAD"), remap = false)
    private static void lattice$onBindTexture(int texture, CallbackInfo ci) {
        GLState.setPreviousBoundTexture(texture);
    }
}
