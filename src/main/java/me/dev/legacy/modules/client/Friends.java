package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.util.ColorUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Friends extends Module {
    private int color;
    public Setting friendX = this.register(new Setting("PosX", Integer.valueOf(740), Integer.valueOf(0), Integer.valueOf(1000)));
    public Setting friendY = this.register(new Setting("PosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000)));

    public Friends() {
        super("FriendList", "Lists ur friends in render", Module.Category.CLIENT, true, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        this.color = ColorUtil.toRGBA(((Integer) Colors.getInstance().red.getValue()).intValue(), ((Integer) Colors.getInstance().green.getValue()).intValue(), ((Integer) Colors.getInstance().blue.getValue()).intValue());
        this.renderFriends();
    }

    private void renderFriends() {
        List friends = new ArrayList();
        Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

        while (var2.hasNext()) {
            EntityPlayer player = (EntityPlayer) var2.next();
            if (Legacy.friendManager.isFriend(player.func_70005_c_())) {
                friends.add(player.func_70005_c_());
            }
        }

        Iterator var4;
        String friend;
        int y;
        int x;
        if (((Boolean) Colors.getInstance().rainbow.getValue()).booleanValue()) {
            if (Colors.getInstance().rainbowModeHud.getValue() == Colors.rainbowMode.Static) {
                y = ((Integer) this.friendY.getValue()).intValue();
                x = ((Integer) this.friendX.getValue()).intValue();
                if (friends.isEmpty()) {
                    this.renderer.drawString("No friends online", (float) x, (float) y, ColorUtil.rainbow(((Integer) Colors.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    this.renderer.drawString("Friend(s) near you:", (float) x, (float) y, ColorUtil.rainbow(((Integer) Colors.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    y += 12;

                    for (var4 = friends.iterator(); var4.hasNext(); y += 12) {
                        friend = (String) var4.next();
                        this.renderer.drawString(friend, (float) x, (float) y, ColorUtil.rainbow(((Integer) Colors.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    }
                }
            }
        } else {
            y = ((Integer) this.friendY.getValue()).intValue();
            x = ((Integer) this.friendX.getValue()).intValue();
            if (friends.isEmpty()) {
                this.renderer.drawString("No friends online", (float) x, (float) y, this.color, true);
            } else {
                this.renderer.drawString("Friend(s) near you:", (float) x, (float) y, this.color, true);
                y += 12;

                for (var4 = friends.iterator(); var4.hasNext(); y += 12) {
                    friend = (String) var4.next();
                    this.renderer.drawString(friend, (float) x, (float) y, this.color, true);
                }
            }
        }

    }
}
