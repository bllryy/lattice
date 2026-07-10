package xyz.meowing.lattice.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.meowing.lattice.Lattice;
import xyz.meowing.lattice.event.ClientEvent;
import xyz.meowing.lattice.event.TickEvent;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("HEAD"))
    private void lattice$onStartTick(CallbackInfo info) {
        Lattice.getEventBus().post(new TickEvent.Start());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void lattice$onEndTick(CallbackInfo info) {
        Lattice.getEventBus().post(new TickEvent.End());
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void lattice$onClientStart(CallbackInfo ci) {
        Lattice.getEventBus().post(new ClientEvent.Start());
    }

    @Inject(method = "stop", at = @At("RETURN"))
    private void lattice$onClientStop(CallbackInfo ci) {
        Lattice.getEventBus().post(new ClientEvent.Stop());
    }
}
