package club.minnced.discord.rpc;

import java.util.Collections;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;
import com.sun.jna.Structure;

public class DiscordUser extends Structure
{
    private static final List<String> FIELD_ORDER;
    public String userId;
    public String username;
    public String discriminator;
    public String avatar;
    
    public DiscordUser(final String a1) {
        this.setStringEncoding(a1);
    }
    
    public DiscordUser() {
        this("UTF-8");
    }
    
    public boolean equals(final Object a1) {
        /*SL:79*/if (this == a1) {
            /*SL:80*/return true;
        }
        /*SL:81*/if (!(a1 instanceof DiscordUser)) {
            /*SL:82*/return false;
        }
        final DiscordUser v1 = /*EL:83*/(DiscordUser)a1;
        /*SL:84*/return Objects.equals(this.userId, v1.userId) && /*EL:85*/Objects.equals(this.username, v1.username) && /*EL:86*/Objects.equals(this.discriminator, v1.discriminator) && /*EL:87*/Objects.equals(this.avatar, v1.avatar);
    }
    
    public int hashCode() {
        /*SL:93*/return Objects.hash(this.userId, this.username, this.discriminator, this.avatar);
    }
    
    protected List<String> getFieldOrder() {
        /*SL:99*/return DiscordUser.FIELD_ORDER;
    }
    
    static {
        FIELD_ORDER = Collections.<String>unmodifiableList((List<? extends String>)Arrays.<? extends T>asList("userId", "username", "discriminator", "avatar"));
    }
}
