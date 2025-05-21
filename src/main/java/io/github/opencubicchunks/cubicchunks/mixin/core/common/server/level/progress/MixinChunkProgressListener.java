package io.github.opencubicchunks.cubicchunks.mixin.core.common.server.level.progress;

import io.github.opencubicchunks.cubicchunks.server.level.progress.CloProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkProgressListener.class)
public interface MixinChunkProgressListener {
}
