package club.minnced.discord.rpc;

import com.sun.jna.Callback;
import java.util.Collections;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;
import com.sun.jna.Structure;

public class DiscordEventHandlers extends Structure
{
    private static final List<String> FIELD_ORDER;
    public OnReady ready;
    public OnStatus disconnected;
    public OnStatus errored;
    public OnGameUpdate joinGame;
    public OnGameUpdate spectateGame;
    public OnJoinRequest joinRequest;
    
    public boolean equals(final Object a1) {
        /*SL:112*/if (this == a1) {
            /*SL:113*/return true;
        }
        /*SL:114*/if (!(a1 instanceof DiscordEventHandlers)) {
            /*SL:115*/return false;
        }
        final DiscordEventHandlers v1 = /*EL:116*/(DiscordEventHandlers)a1;
        /*SL:117*/return Objects.equals(this.ready, v1.ready) && /*EL:118*/Objects.equals(this.disconnected, v1.disconnected) && /*EL:119*/Objects.equals(this.errored, v1.errored) && /*EL:120*/Objects.equals(this.joinGame, v1.joinGame) && /*EL:121*/Objects.equals(this.spectateGame, v1.spectateGame) && /*EL:122*/Objects.equals(this.joinRequest, v1.joinRequest);
    }
    
    public int hashCode() {
        /*SL:128*/return Objects.hash(this.ready, this.disconnected, this.errored, this.joinGame, this.spectateGame, this.joinRequest);
    }
    
    protected List<String> getFieldOrder() {
        /*SL:134*/return DiscordEventHandlers.FIELD_ORDER;
    }
    
    static {
        FIELD_ORDER = Collections.<String>unmodifiableList((List<? extends String>)Arrays.<? extends T>asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest"));
    }
    
    public interface OnJoinRequest extends Callback
    {
        void accept(DiscordUser p0);
    }
    
    public interface OnGameUpdate extends Callback
    {
        void accept(String p0);
    }
    
    public interface OnStatus extends Callback
    {
        void accept(int p0, String p1);
    }
    
    public interface OnReady extends Callback
    {
        void accept(DiscordUser p0);
    }
}
