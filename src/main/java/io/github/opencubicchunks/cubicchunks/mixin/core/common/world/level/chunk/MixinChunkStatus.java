package io.github.opencubicchunks.cubicchunks.mixin.core.common.world.level.chunk;

import static io.github.opencubicchunks.cc_core.utils.Utils.unsafeCast;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import io.github.opencubicchunks.cubicchunks.world.level.chunklike.CloAccess;
import io.github.opencubicchunks.cubicchunks.world.level.chunklike.ProtoClo;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkStatus.class)
public class MixinChunkStatus {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void onGenerate(Executor pExectutor, ServerLevel pLevel, ChunkGenerator pChunkGenerator, StructureTemplateManager pStructureTemplateManager,
                            ThreadedLevelLightEngine pLightEngine, Function<CloAccess, CompletableFuture<Either<CloAccess, ChunkHolder.ChunkLoadingFailure>>> pTask,
                            List<CloAccess> pCache, CallbackInfoReturnable<CompletableFuture<Either<CloAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        CloAccess chunkaccess = pCache.get(pCache.size() / 2);
        if (chunkaccess instanceof ChunkAccess) return;
        cir.setReturnValue(((Object) this == ChunkStatus.FULL ? pTask.apply(chunkaccess) : CompletableFuture.completedFuture(Either.left(chunkaccess)))
            .thenApply(
                p_281217_ -> {
                    p_281217_.ifLeft(p_290029_ -> {
                        if (p_290029_ instanceof ProtoClo protochunk && !protochunk.getStatus().isOrAfter((ChunkStatus) (Object) this)) {
                            protochunk.setStatus((ChunkStatus) (Object) this);
                        }
                    });

                    return unsafeCast(p_281217_);
                }
            ));
    }

    // Somehow this doesn't get hit in all cases from dasm'd code? very strange
    // TODO (P2) proper generation logic; this currently ignores everything and only handles promotion from ProtoClo to LevelClo
    public CompletableFuture<Either<CloAccess, ChunkHolder.ChunkLoadingFailure>> cc_generate(
        Executor pExectutor,
        ServerLevel pLevel,
        ChunkGenerator pChunkGenerator,
        StructureTemplateManager pStructureTemplateManager,
        ThreadedLevelLightEngine pLightEngine,
        Function<CloAccess, CompletableFuture<Either<CloAccess, ChunkHolder.ChunkLoadingFailure>>> pTask,
        List<CloAccess> pCache
    ) {
        CloAccess chunkaccess = pCache.get(pCache.size() / 2);
        return ((Object) this == ChunkStatus.FULL ? pTask.apply(chunkaccess) : CompletableFuture.completedFuture(Either.left(chunkaccess)))
            .thenApply(
                p_281217_ -> {
                    p_281217_.ifLeft(p_290029_ -> {
                        if (p_290029_ instanceof ProtoClo protochunk && !protochunk.getStatus().isOrAfter((ChunkStatus) (Object) this)) {
                            protochunk.setStatus((ChunkStatus) (Object) this);
                        }
                    });

                    return unsafeCast(p_281217_);
                }
            );
    }
}
