package io.github.opencubicchunks.cubicchunks.mixin.core.common.server.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import io.github.opencubicchunks.cc_core.api.CubicConstants;
import io.github.opencubicchunks.cc_core.utils.Coords;
import io.github.opencubicchunks.cubicchunks.CubicChunks;
import io.github.opencubicchunks.cubicchunks.MarkableAsCubic;
import io.github.opencubicchunks.cubicchunks.mixin.TransformFrom;
import io.github.opencubicchunks.cubicchunks.world.level.chunklike.CloPos;
import io.github.opencubicchunks.cubicchunks.world.level.cube.CubeAccess;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap extends ChunkStorage {
    public MixinChunkMap() {
        super(null, null, false);
    }

    // euclideanDistanceSquared - cubic equivalent

    // isChunkTracked

    // isChunkOnTrackedBorder

    // getChunkDebugData - low prio

    @Inject(method = "getChunkRangeFuture", at = @At("HEAD"), cancellable = true)
    private void cc_onGetChunkRangeFuture(ChunkHolder cloHolder, int radius, IntFunction<ChunkStatus> statusByRadius,
                                          CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir) {
        if(true) return;
//        if (!chunkHolder.isCubic()) return;
        CloPos pos = CloPos.cube(0,0,0);//cloHolder.cc_getCloPos();
        List<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> dependencyFutures = new ArrayList<>();
        List<ChunkHolder> cloHolders = new ArrayList<>();
        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                // We want the chunks intersecting this column of cubes to be loaded at the maximum level of any of those cubes;
                // this occurs when dy=0, so we only consider x/z distance
                int chunkDistance = Math.max(Math.abs(dz), Math.abs(dx));
                for (int sectionZ = 0; sectionZ < CubicConstants.DIAMETER_IN_SECTIONS; sectionZ++) {
                    for (int sectionX = 0; sectionX < CubicConstants.DIAMETER_IN_SECTIONS; sectionX++) {
                        ChunkHolder holder = this.getUpdatingChunkIfPresent(CloPos.asLong(Coords.cubeToSection(pos.getX()+dx, sectionX), Coords.cubeToSection(pos.getZ()+dz, sectionZ)));
                        // TODO do we really want Mojang's janky error handling? can we just crash instead?
                        if (holder == null) {
                            var pos1 = new ChunkPos(Coords.cubeToSection(pos.getX()+dx, sectionX), Coords.cubeToSection(pos.getZ()+dz, sectionZ));
                            CompletableFuture.completedFuture(Either.right(new ChunkHolder.ChunkLoadingFailure() {
                                @Override
                                public String toString() {
                                    return "Unloaded " + pos1;
                                }
                            }));
                        }
                        ChunkStatus expectedStatus = statusByRadius.apply(chunkDistance);
                        var future = holder.getOrScheduleFuture(expectedStatus, (ChunkMap) (Object) this);
                        cloHolders.add(holder);
                        dependencyFutures.add(future);
                    }
                }
                for (int dy = -radius; dy <= radius; dy++) {
                    ChunkHolder holder = this.getUpdatingChunkIfPresent(CloPos.asLong(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz));
                    // TODO do we really want Mojang's janky error handling? can we just crash instead?
                    if (holder == null) {
                        var pos1 = CloPos.cube(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz);
                        CompletableFuture.completedFuture(Either.right(new ChunkHolder.ChunkLoadingFailure() {
                            @Override
                            public String toString() {
                                return "Unloaded " + pos1;
                            }
                        }));
                    }
                    ChunkStatus expectedStatus = statusByRadius.apply(Math.max(chunkDistance, Math.abs(dy)));
                    var future = holder.getOrScheduleFuture(expectedStatus, (ChunkMap) (Object) this);
                    cloHolders.add(holder);
                    dependencyFutures.add(future);
                }
            }
        }
        var sequencedFuture = Util.sequence(dependencyFutures);
        CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> combinedFuture = sequencedFuture.thenApply(p_183730_ -> {
                List<ChunkAccess> list2 = Lists.newArrayList();
                int k1 = 0;

                for(final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either : p_183730_) {
                    if (either == null) {
//                        throw this.debugFuturesAndCreateReportedException(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
                    }

                    Optional<ChunkAccess> optional = either.left();
                    if (optional.isEmpty()) {
                        final int l1 = k1;
                        return Either.right(new ChunkHolder.ChunkLoadingFailure() {
                            @Override
                            public String toString() {
                                // TODO
                                return "Unloaded ";// + new ChunkPos(i + l1 % (p_282030_ * 2 + 1), j + l1 / (p_282030_ * 2 + 1)) + " " + either.right().get();
                            }
                        });
                    }

                    list2.add(optional.get());
                    ++k1;
                }

                return Either.left(list2);
            }
        );

        for (ChunkHolder holder : cloHolders) {
//            holder.addSaveDependency("getChunkRangeFuture " + pos + " " + radius, combinedFuture);
        }

        cir.setReturnValue(combinedFuture);
    }

    // updateChunkScheduling constructs a ChunkPos

    // saveAllChunks filter lambda needs to be targeted (instanceof LevelChunk || instanceof ImposterProtoChunk), but not the method itself

    // P4: scheduleUnload lambda we'll want to mirror the forge API for cubes

    // schedule - DASM?

    @TransformFrom("Lnet/minecraft/server/level/ChunkMap;scheduleChunkLoad(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/concurrent/CompletableFuture;")
    private native CompletableFuture<Either<CubeAccess, ChunkHolder.ChunkLoadingFailure>> cc_scheduleChunkLoad(CloPos p_140418_);

    @TransformFrom("Lnet/minecraft/server/level/ChunkMap;handleChunkLoadFailure(Ljava/lang/Throwable;Lnet/minecraft/world/level/ChunkPos;)Lcom/mojang/datafixers/util/Either;")
    private native Either<CubeAccess, ChunkHolder.ChunkLoadingFailure> cc_handleChunkLoadFailure(Throwable p_214902_, CloPos p_214903_);

    @TransformFrom("createEmptyChunk(Lnet/minecraft/world/level/ChunkPos;)Lnet/minecraft/world/level/chunk/ChunkAccess;")
    private native ChunkAccess cc_createEmptyChunk(ChunkPos p_214962_);

    @TransformFrom("markPositionReplaceable(Lnet/minecraft/world/level/ChunkPos;)V")
    private native void cc_markPositionReplaceable(ChunkPos p_140423_);

    @TransformFrom("markPosition(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/ChunkStatus$ChunkType;)B")
    private native byte cc_markPosition(ChunkPos p_140230_, ChunkStatus.ChunkType p_140231_);

    // scheduleChunkGeneration - complex, a lot of lambdas.

    // releaseLightTicket - DASM, lambda

    // protoChunkToFullChunk - complex, lambdas

    // prepareTickingChunk - complex, lambdas

    @TransformFrom("onChunkReadyToSend(Lnet/minecraft/world/level/chunk/LevelChunk;)V")
    private native void cc_onChunkReadyToSend(LevelChunk p_296003_);

    // prepareAccessibleChunk lambda DASM

    // saveChunkIfNeeded - mixin

    // save - DASM + mixin (for forge patches)

    // This calls ChunkSerializer.getChunkTypeFromTag, which could be an issue?
    @TransformFrom("isExistingChunkFull(Lnet/minecraft/world/level/ChunkPos;)Z")
    private native boolean cc_isExistingChunkFull(ChunkPos p_140426_);

    @TransformFrom("markChunkPendingToSend(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/ChunkPos;)V")
    private native void cc_markChunkPendingToSend(ServerPlayer p_294638_, ChunkPos p_296183_);

    @TransformFrom("markChunkPendingToSend(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/chunk/LevelChunk;)V")
    private static native void cc_markChunkPendingToSend(ServerPlayer p_295834_, LevelChunk p_296281_);

    @TransformFrom("dropChunk(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/ChunkPos;)V")
    private static native void cc_dropChunk(ServerPlayer p_294215_, ChunkPos p_294758_);

    @TransformFrom("getChunkToSend(J)Lnet/minecraft/world/level/chunk/LevelChunk;")
    public native LevelChunk cc_getChunkToSend(long p_300929_);

    // dumpChunks - redirect callers to dummy method unless we care about this. can DASM it, might need mixin? or just have our own impl somewhere and redirect calls to it

    // printFuture - only ever called in dumpChunks

    // readChunk: this.upgradeChunkTag might need a dasm redirect?

    @TransformFrom("readChunk(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/concurrent/CompletableFuture;")
    private native CompletableFuture<Optional<CompoundTag>> cc_readChunk(ChunkPos p_214964_);

    @TransformFrom("anyPlayerCloseEnoughForSpawning(Lnet/minecraft/world/level/ChunkPos;)Z")
    native boolean cc_anyPlayerCloseEnoughForSpawning(ChunkPos p_183880_);

    @TransformFrom("getPlayersCloseForSpawning(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/List;")
    public native List<ServerPlayer> cc_getPlayersCloseForSpawning(ChunkPos p_183889_);

    @TransformFrom("playerIsCloseEnoughForSpawning(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/ChunkPos;)Z")
    private native boolean cc_playerIsCloseEnoughForSpawning(ServerPlayer p_183752_, ChunkPos p_183753_);

    // updatePlayerStatus - DASM due to calling DASM-duplicated methods?

    // move - DASM due to calling DASM-duplicated methods?

    // updateChunkTracking - DASM; possibly conditional redirect original method to copy

    // applyChunkTrackingView - complex

    // getPlayers - complex

    // tick - DASM or mixin, there's a single `.chunk()` call in there on a sectionpos

    // resendBiomesForChunks - complex

    @TransformFrom("onFullChunkStatusChange(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/server/level/FullChunkStatus;)V")
    native void cc_onFullChunkStatusChange(ChunkPos p_287612_, FullChunkStatus p_287685_);

    @TransformFrom("waitForLightBeforeSending(Lnet/minecraft/world/level/ChunkPos;I)V")
    public native void cc_waitForLightBeforeSending(ChunkPos p_301194_, int p_301130_);

    // TrackedEntity.updatePlayer - in its own mixin class bc inner class - complex



    @Shadow
    protected abstract ChunkHolder getUpdatingChunkIfPresent(long aLong);
}
