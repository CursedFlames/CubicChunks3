package io.github.opencubicchunks.cubicchunks.mixin.test.common.server.level;

import java.util.concurrent.Executor;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkHolder.class)
public interface ChunkHolderTestAccess {
    @Invoker
    void invokeUpdateFutures(ChunkMap chunkMap, Executor executor);
}
