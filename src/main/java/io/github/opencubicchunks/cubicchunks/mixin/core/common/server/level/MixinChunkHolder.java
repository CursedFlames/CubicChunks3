package io.github.opencubicchunks.cubicchunks.mixin.core.common.server.level;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import io.github.opencubicchunks.cubicchunks.CubicChunks;
import io.github.opencubicchunks.cubicchunks.mixin.DasmRedirect;
import io.github.opencubicchunks.cubicchunks.mixin.TransformFrom;
import io.github.opencubicchunks.cubicchunks.server.level.CubicChunkHolder;
import io.github.opencubicchunks.cubicchunks.world.level.chunklike.CloPos;
import io.github.opencubicchunks.cubicchunks.world.level.cube.CubeAccess;
import io.github.opencubicchunks.cubicchunks.world.level.cube.ImposterProtoCube;
import io.github.opencubicchunks.cubicchunks.world.level.cube.LevelCube;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@DasmRedirect()
@Mixin(ChunkHolder.class)
public abstract class MixinChunkHolder implements CubicChunkHolder {
    private CloPos cc_cloPos;

    @Override public CloPos cc_getPos() {
        return cc_cloPos;
    }

    @TransformFrom("<init>(Lnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/lighting/LevelLightEngine;Lnet/minecraft/server/level/ChunkHolder$LevelChangeListener;Lnet/minecraft/server/level/ChunkHolder$PlayerProvider;)V")
    public MixinChunkHolder() {
        throw new IllegalStateException("dasm failed to apply");
    }

    @TransformFrom("getTickingChunk()Lnet/minecraft/world/level/chunk/LevelChunk;")
    @Override @Nullable public native LevelCube cc_getTickingChunk();

    @TransformFrom("getChunkToSend()Lnet/minecraft/world/level/chunk/LevelChunk;")
    @Override @Nullable public native LevelCube cc_getChunkToSend();

    @TransformFrom("getFullChunk()Lnet/minecraft/world/level/chunk/LevelChunk;")
    @Override @Nullable public native LevelCube cc_getFullChunk();

    @TransformFrom("getLastAvailable()Lnet/minecraft/world/level/chunk/ChunkAccess;")
    @Override @Nullable public native CubeAccess cc_getLastAvailable();

    // TODO blockChanged - mixin or dasm, not sure

    // TODO sectionLightChanged - used for sending light updates to client in broadcastChanges; might want to rip out and replace {sky|block}ChangedLightSectionFilter

    // TODO broadcastChanges - probably want to fully replace this for CC

    @TransformFrom("getOrScheduleFuture(Lnet/minecraft/world/level/chunk/ChunkStatus;Lnet/minecraft/server/level/ChunkMap;)Ljava/util/concurrent/CompletableFuture;")
    public native CompletableFuture<Either<CubeAccess, ChunkHolder.ChunkLoadingFailure>> cc_getOrScheduleFuture(ChunkStatus p_140050_, ChunkMap p_140051_);

    @TransformFrom("updateChunkToSave(Ljava/util/concurrent/CompletableFuture;Ljava/lang/String;)V")
    private native void cc_updateChunkToSave(CompletableFuture<? extends Either<? extends CubeAccess, ChunkHolder.ChunkLoadingFailure>> p_143018_, String p_143019_);

    @TransformFrom("scheduleFullChunkPromotion(Lnet/minecraft/server/level/ChunkMap;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;"
        + "Lnet/minecraft/server/level/FullChunkStatus;)V")
    private native void cc_scheduleFullChunkPromotion(
        ChunkMap p_142999_, CompletableFuture<Either<LevelCube, ChunkHolder.ChunkLoadingFailure>> p_143000_, Executor p_143001_, FullChunkStatus p_287621_
    );

    @TransformFrom("demoteFullChunk(Lnet/minecraft/server/level/ChunkMap;Lnet/minecraft/server/level/FullChunkStatus;)V")
    private native void cc_demoteFullChunk(ChunkMap p_287599_, FullChunkStatus p_287649_);

    @TransformFrom("updateFutures(Lnet/minecraft/server/level/ChunkMap;Ljava/util/concurrent/Executor;)V")
    protected native void cc_updateFutures(ChunkMap p_143004_, Executor p_143005_);

    @TransformFrom("replaceProtoChunk(Lnet/minecraft/world/level/chunk/ImposterProtoChunk;)V")
    public native void cc_replaceProtoChunk(ImposterProtoCube p_140053_);

}
