package me.dev.legacy.api.manager;

import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.util.Timer;
import net.minecraft.client.network.NetHandlerPlayClient;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;

public class ServerManager extends AbstractModule {
    private final float[] tpsCounts = new float[10];
    private final DecimalFormat format = new DecimalFormat("##.00#");
    private final Timer timer = new Timer();
    private float TPS = 20.0F;
    private long lastUpdate = -1L;
    private String serverBrand = "";

    public void onPacketReceived() {
        this.timer.reset();
    }

    public long serverRespondingTime() {
        return this.timer.getPassedTimeMs();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (this.lastUpdate == -1L) {
            this.lastUpdate = currentTime;
        } else {
            long timeDiff = currentTime - this.lastUpdate;
            float tickTime = (float) timeDiff / 20.0F;
            if (tickTime == 0.0F) {
                tickTime = 50.0F;
            }

            float tps;
            if ((tps = 1000.0F / tickTime) > 20.0F) {
                tps = 20.0F;
            }

            System.arraycopy(this.tpsCounts, 0, this.tpsCounts, 1, this.tpsCounts.length - 1);
            this.tpsCounts[0] = tps;
            double total = 0.0D;
            float[] var9 = this.tpsCounts;
            int var10 = var9.length;

            for (int var11 = 0; var11 < var10; ++var11) {
                float f = var9[var11];
                total += (double) f;
            }

            if ((total /= (double) this.tpsCounts.length) > 20.0D) {
                total = 20.0D;
            }

            this.TPS = Float.parseFloat(this.format.format(total));
            this.lastUpdate = currentTime;
        }
    }

    public void reset() {
        Arrays.fill(this.tpsCounts, 20.0F);
        this.TPS = 20.0F;
    }

    public float getTpsFactor() {
        return 20.0F / this.TPS;
    }

    public float getTPS() {
        return this.TPS;
    }

    public String getServerBrand() {
        return this.serverBrand;
    }

    public void setServerBrand(String brand) {
        this.serverBrand = brand;
    }

    public int getPing() {
        if (fullNullCheck()) {
            return 0;
        } else {
            try {
                return ((NetHandlerPlayClient) Objects.requireNonNull(mc.func_147114_u())).func_175102_a(mc.func_147114_u().func_175105_e().getId()).func_178853_c();
            } catch (Exception var2) {
                return 0;
            }
        }
    }
}
