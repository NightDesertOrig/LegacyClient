package club.minnced.discord.rpc;

import com.sun.jna.Native;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import com.sun.jna.Library;

public interface DiscordRPC extends Library
{
    public static final DiscordRPC INSTANCE = (DiscordRPC)Native.loadLibrary("discord-rpc", (Class)DiscordRPC.class);
    public static final int DISCORD_REPLY_NO = 0;
    public static final int DISCORD_REPLY_YES = 1;
    public static final int DISCORD_REPLY_IGNORE = 2;
    
    void Discord_Initialize(@Nonnull String p0, @Nullable DiscordEventHandlers p1, boolean p2, @Nullable String p3);
    
    void Discord_Shutdown();
    
    void Discord_RunCallbacks();
    
    void Discord_UpdateConnection();
    
    void Discord_UpdatePresence(@Nullable DiscordRichPresence p0);
    
    void Discord_ClearPresence();
    
    void Discord_Respond(@Nonnull String p0, int p1);
    
    void Discord_UpdateHandlers(@Nullable DiscordEventHandlers p0);
    
    void Discord_Register(String p0, String p1);
    
    void Discord_RegisterSteamGame(String p0, String p1);
}
