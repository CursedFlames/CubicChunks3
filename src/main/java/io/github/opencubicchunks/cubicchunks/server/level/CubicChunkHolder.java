package io.github.opencubicchunks.cubicchunks.server.level;

import javax.annotation.Nullable;

import io.github.opencubicchunks.cubicchunks.world.level.chunklike.CloPos;
import io.github.opencubicchunks.cubicchunks.world.level.cube.CubeAccess;
import io.github.opencubicchunks.cubicchunks.world.level.cube.LevelCube;
import net.minecraft.core.BlockPos;

public interface CubicChunkHolder {
    CloPos cc_getPos();

    @Nullable LevelCube cc_getTickingChunk();

    @Nullable LevelCube cc_getChunkToSend();

    @Nullable LevelCube cc_getFullChunk();

    @Nullable CubeAccess cc_getLastAvailable();
}
