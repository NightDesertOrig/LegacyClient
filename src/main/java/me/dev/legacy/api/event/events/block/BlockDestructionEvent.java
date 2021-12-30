package me.dev.legacy.api.event.events.block;

import me.dev.legacy.api.event.EventStage;
import net.minecraft.util.math.BlockPos;

public class BlockDestructionEvent extends EventStage {
    BlockPos nigger;

    public BlockDestructionEvent(BlockPos nigger) {
    }

    public BlockPos getBlockPos() {
        return this.nigger;
    }
}
