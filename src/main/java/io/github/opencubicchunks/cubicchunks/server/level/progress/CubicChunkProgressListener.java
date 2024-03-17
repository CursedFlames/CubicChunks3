package io.github.opencubicchunks.cubicchunks.server.level.progress;

import javax.annotation.Nullable;

import io.github.opencubicchunks.cubicchunks.world.level.chunklike.CloPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public interface CubicChunkProgressListener {
    void cc_updateSpawnPos(CloPos pCenter);

    void cc_onStatusChange(CloPos pChunkPosition, @Nullable ChunkStatus pNewStatus);

    void start();

    void stop();
}
