package io.github.opencubicchunks.cubicchunks.mixin.core.common.server.level;

import io.github.notstirred.dasm.api.annotations.Dasm;
import io.github.notstirred.dasm.api.annotations.redirect.redirects.AddFieldToSets;
import io.github.notstirred.dasm.api.annotations.redirect.redirects.AddMethodToSets;
import io.github.notstirred.dasm.api.annotations.redirect.redirects.AddTransformToSets;
import io.github.notstirred.dasm.api.annotations.selector.FieldSig;
import io.github.notstirred.dasm.api.annotations.selector.MethodSig;
import io.github.notstirred.dasm.api.annotations.selector.Ref;
import io.github.notstirred.dasm.api.annotations.transform.TransformFromMethod;
import io.github.opencubicchunks.cc_core.api.CubePos;
import io.github.opencubicchunks.cubicchunks.mixin.core.common.world.entity.MixinEntity;
import io.github.opencubicchunks.cubicchunks.mixin.dasmsets.ChunkToCloSet;
import io.github.opencubicchunks.cubicchunks.mixin.dasmsets.ChunkToCubeSet;
import io.github.opencubicchunks.cubicchunks.server.level.CloTrackingView;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Dasm(ChunkToCloSet.class)
@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends MixinEntity {
    @AddFieldToSets(sets = ChunkToCloSet.class, owner = @Ref(ServerPlayer.class), field = @FieldSig(type = @Ref(ChunkTrackingView.class), name = "chunkTrackingView"))
    private CloTrackingView cc_cloTrackingView = CloTrackingView.EMPTY;

//    public MixinServerPlayer() {
//        super(null, null, 0, null);
//    }

    // FIXME temporary
    @AddMethodToSets(sets = ChunkToCubeSet.class, owner = @Ref(ServerPlayer.class), method = @MethodSig("chunkPosition()Lnet/minecraft/world/level/ChunkPos;"))
    public CubePos cc_cubePosition() {
        return super.cc_cubePosition();
    }

    @AddTransformToSets(ChunkToCloSet.class) @TransformFromMethod(@MethodSig("getChunkTrackingView()Lnet/minecraft/server/level/ChunkTrackingView;"))
    public native CloTrackingView cc_getCloTrackingView();

    @AddTransformToSets(ChunkToCloSet.class) @TransformFromMethod(@MethodSig("setChunkTrackingView(Lnet/minecraft/server/level/ChunkTrackingView;)V"))
    public native void cc_setCloTrackingView(CloTrackingView chunkTrackingView);

//    /**
//     * This mixin steals the x/y/z coordinates from a call to ChunkPos and replaces the ChunkPos in the addRegionTicketCall with a CloPos instead.
//     */
//    @WrapWithCondition(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level"
//        + "/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"))
//    public <T> boolean cc_wrapAddRegionTicket(ServerChunkCache instance, TicketType<T> type, ChunkPos pos, int distance, T value,
//                                              @Local(ordinal = 0, argsOnly = true)double x,
//                                              @Local(ordinal = 1, argsOnly = true)double y,
//                                              @Local(ordinal = 2, argsOnly = true)double z) {
//        if (!cc_isCubic) {
//            return true;
//        }
//
//        ((ServerCubeCache)instance).cc_addRegionTicket(type, CloPos.cube(BlockPos.containing(x, y, z)), distance, value);
//        return false;
//    }

    // TODO P3 :: findDimensionEntryPoint

    // TODO P3 :: changeDimension
}
