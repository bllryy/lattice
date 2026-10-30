package xyz.meowing.lattice.mixins;

//? if <26.3 {
/*import net.minecraft.client.DeltaTracker;
*///?}
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.meowing.lattice.Lattice;
import xyz.meowing.lattice.event.RenderEvent;
import xyz.meowing.lattice.render.Resolution;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;endFrame()V", shift = At.Shift.AFTER), cancellable = true)
    //? if >=26.3 {
    public void lattice$onRenderGui(CallbackInfo ci) {
    //?} else {
    /*public void lattice$onRenderGui(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
    *///?}
        if (!Lattice.getEventBus().hasSubscribers(RenderEvent.Gui.class)) return;
        if (!Lattice.getRenderer().isAvailable()) return;

        Lattice.getRenderer().beginFrame(Resolution.getWindowWidth(), Resolution.getWindowHeight());
        boolean cancelled = Lattice.getEventBus().post(new RenderEvent.Gui());
        Lattice.getRenderer().endFrame();

        if (cancelled) ci.cancel();
    }
}
