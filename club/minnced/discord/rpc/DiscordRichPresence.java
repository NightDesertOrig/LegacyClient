package club.minnced.discord.rpc;

import java.util.Collections;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;
import com.sun.jna.Structure;

public class DiscordRichPresence extends Structure
{
    private static final List<String> FIELD_ORDER;
    public String state;
    public String details;
    public long startTimestamp;
    public long endTimestamp;
    public String largeImageKey;
    public String largeImageText;
    public String smallImageKey;
    public String smallImageText;
    public String partyId;
    public int partySize;
    public int partyMax;
    public String matchSecret;
    public String joinSecret;
    public String spectateSecret;
    public byte instance;
    
    public DiscordRichPresence(final String a1) {
        this.setStringEncoding(a1);
    }
    
    public DiscordRichPresence() {
        this("UTF-8");
    }
    
    public boolean equals(final Object a1) {
        /*SL:195*/if (this == a1) {
            /*SL:196*/return true;
        }
        /*SL:197*/if (!(a1 instanceof DiscordRichPresence)) {
            /*SL:198*/return false;
        }
        final DiscordRichPresence v1 = /*EL:199*/(DiscordRichPresence)a1;
        /*SL:200*/return this.startTimestamp == v1.startTimestamp && this.endTimestamp == v1.endTimestamp && this.partySize == v1.partySize && this.partyMax == v1.partyMax && this.instance == v1.instance && /*EL:205*/Objects.equals(this.state, v1.state) && /*EL:206*/Objects.equals(this.details, v1.details) && /*EL:207*/Objects.equals(this.largeImageKey, v1.largeImageKey) && /*EL:208*/Objects.equals(this.largeImageText, v1.largeImageText) && /*EL:209*/Objects.equals(this.smallImageKey, v1.smallImageKey) && /*EL:210*/Objects.equals(this.smallImageText, v1.smallImageText) && /*EL:211*/Objects.equals(this.partyId, v1.partyId) && /*EL:212*/Objects.equals(this.matchSecret, v1.matchSecret) && /*EL:213*/Objects.equals(this.joinSecret, v1.joinSecret) && /*EL:214*/Objects.equals(this.spectateSecret, v1.spectateSecret);
    }
    
    public int hashCode() {
        /*SL:220*/return Objects.hash(this.state, this.details, this.startTimestamp, this.endTimestamp, this.largeImageKey, this.largeImageText, this.smallImageKey, this.smallImageText, this.partyId, this.partySize, /*EL:221*/this.partyMax, this.matchSecret, this.joinSecret, this.spectateSecret, this.instance);
    }
    
    protected List<String> getFieldOrder() {
        /*SL:227*/return DiscordRichPresence.FIELD_ORDER;
    }
    
    static {
        FIELD_ORDER = Collections.<String>unmodifiableList((List<? extends String>)Arrays.<? extends T>asList("state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "matchSecret", "joinSecret", "spectateSecret", "instance"));
    }
}
