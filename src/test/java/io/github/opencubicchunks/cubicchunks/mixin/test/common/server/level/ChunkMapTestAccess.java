package io.github.opencubicchunks.cubicchunks.mixin.test.common.server.level;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapTestAccess {
    @Invoker
    @Nullable ChunkHolder invokeUpdateChunkScheduling(long p_140177_, int p_140178_, @Nullable ChunkHolder p_140179_, int p_140180_);

    @Invoker
    CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> invokeGetChunkRangeFuture(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_);

    @Invoker ChunkStatus invokeGetDependencyStatus(ChunkStatus p_140263_, int p_140264_);
}
