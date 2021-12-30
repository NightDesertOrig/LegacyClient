package me.dev.legacy.api.mixin.mixins.accessors;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CPacketUseEntity.class})
public interface AccessorCPacketUseEntity {
    @Accessor("entityId")
    void setEntityId(int var1);

    @Accessor("action")
    void setAction(Action var1);
}
