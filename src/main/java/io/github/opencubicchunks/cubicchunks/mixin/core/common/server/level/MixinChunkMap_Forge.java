package io.github.opencubicchunks.cubicchunks.mixin.core.common.server.level;

import net.minecraft.server.level.ChunkMap;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkMap.class)
public class MixinChunkMap_Forge {
    @Dynamic @Redirect(method = {
        "dasm$redirect$lambda$scheduleUnload$14",
        "dasm$redirect$lambda$protoChunkToFullChunk$34",
        "cc_dasm$cc_save"
    }, at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
    private Event onForgeHook(IEventBus instance, Event t) {
        // don't fire events in cubic methods for now.
        // FIXME this is a temporary hack
        return t;
    }
}
