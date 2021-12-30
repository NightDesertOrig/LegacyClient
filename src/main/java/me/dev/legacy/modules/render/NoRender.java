package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Stream;

public class NoRender extends Module {
    private static NoRender INSTANCE = new NoRender();
    public Setting portal = this.register(new Setting("Portal", false));
    public Setting pumpkin = this.register(new Setting("Pumpkin", false));
    public Setting totemPops = this.register(new Setting("TotemPop", false));
    public Setting items = this.register(new Setting("Items", false));
    public Setting nausea = this.register(new Setting("Nausea", false));
    public Setting hurtcam = this.register(new Setting("HurtCam", false));
    public Setting fog;
    public Setting noWeather;
    public Setting boss;
    public Setting scale;
    public Setting bats;
    public Setting noArmor;
    public Setting skylight;
    public Setting barriers;
    public Setting blocks;
    public Setting pigmen;
    public Setting timeChange;
    public Setting time;

    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.fog = this.register(new Setting("Fog", NoRender.Fog.NONE));
        this.noWeather = this.register(new Setting("Weather", false));
        this.boss = this.register(new Setting("BossBars", NoRender.Boss.NONE));
        this.scale = this.register(new Setting("Scale", 0.0F, 0.5F, 1.0F, (v) -> {
            return this.boss.getValue() == NoRender.Boss.MINIMIZE || this.boss.getValue() != NoRender.Boss.STACK;
        }, "Scale of the bars."));
        this.bats = this.register(new Setting("Bats", false));
        this.noArmor = this.register(new Setting("NoArmor", NoRender.NoArmor.NONE));
        this.skylight = this.register(new Setting("Skylight", NoRender.Skylight.NONE));
        this.barriers = this.register(new Setting("Barriers", false));
        this.blocks = this.register(new Setting("Blocks", false));
        this.pigmen = this.register(new Setting("Pigmen", false));
        this.timeChange = this.register(new Setting("TimeChange", false));
        this.time = this.register(new Setting("Time", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(23000), (v) -> {
            return ((Boolean) this.timeChange.getValue()).booleanValue();
        }));
        this.setInstance();
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) this.items.getValue()).booleanValue()) {
            Stream var10000 = mc.field_71441_e.field_72996_f.stream();
            EntityItem.class.getClass();
            var10000 = var10000.filter(EntityItem.class::isInstance);
            EntityItem.class.getClass();
            var10000.map(EntityItem.class::cast).forEach(Entity::func_70106_y);
        }

        if (((Boolean) this.noWeather.getValue()).booleanValue() && mc.field_71441_e.func_72896_J()) {
            mc.field_71441_e.func_72894_k(0.0F);
        }

        if (((Boolean) this.timeChange.getValue()).booleanValue()) {
            mc.field_71441_e.func_72877_b((long) ((Integer) this.time.getValue()).intValue());
        }

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate & ((Boolean) this.timeChange.getValue()).booleanValue()) {
            event.setCanceled(true);
        }

    }

    public void doVoidFogParticles(int posX, int posY, int posZ) {
        int i = true;
        Random random = new Random();
        ItemStack itemstack = mc.field_71439_g.func_184614_ca();
        boolean flag = !((Boolean) this.barriers.getValue()).booleanValue() || mc.field_71442_b.func_178889_l() == GameType.CREATIVE && !itemstack.func_190926_b() && itemstack.func_77973_b() == Item.func_150898_a(Blocks.field_180401_cv);
        MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

        for (int j = 0; j < 667; ++j) {
            this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
            this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
        }

    }

    public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, MutableBlockPos pos) {
        int i = x + mc.field_71441_e.field_73012_v.nextInt(offset) - mc.field_71441_e.field_73012_v.nextInt(offset);
        int j = y + mc.field_71441_e.field_73012_v.nextInt(offset) - mc.field_71441_e.field_73012_v.nextInt(offset);
        int k = z + mc.field_71441_e.field_73012_v.nextInt(offset) - mc.field_71441_e.field_73012_v.nextInt(offset);
        pos.func_181079_c(i, j, k);
        IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
        iblockstate.func_177230_c().func_180655_c(iblockstate, mc.field_71441_e, pos, random);
        if (!holdingBarrier && iblockstate.func_177230_c() == Blocks.field_180401_cv) {
            mc.field_71441_e.func_175688_a(EnumParticleTypes.BARRIER, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    @SubscribeEvent
    public void onRenderPre(Pre event) {
        if (event.getType() == ElementType.BOSSINFO && this.boss.getValue() != NoRender.Boss.NONE) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onRenderPost(Post event) {
        if (event.getType() == ElementType.BOSSINFO && this.boss.getValue() != NoRender.Boss.NONE) {
            Map map;
            int l;
            String text;
            if (this.boss.getValue() == NoRender.Boss.MINIMIZE) {
                map = mc.field_71456_v.func_184046_j().field_184060_g;
                if (map == null) {
                    return;
                }

                ScaledResolution scaledresolution = new ScaledResolution(mc);
                int i = scaledresolution.func_78326_a();
                l = 12;

                for (Iterator var6 = map.entrySet().iterator(); var6.hasNext(); l += 10 + mc.field_71466_p.field_78288_b) {
                    Entry entry = (Entry) var6.next();
                    BossInfoClient info = (BossInfoClient) entry.getValue();
                    text = info.func_186744_e().func_150254_d();
                    int k = (int) ((float) i / ((Float) this.scale.getValue()).floatValue() / 2.0F - 91.0F);
                    GL11.glScaled((double) ((Float) this.scale.getValue()).floatValue(), (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                    if (!event.isCanceled()) {
                        GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
                        mc.func_110434_K().func_110577_a(GuiBossOverlay.field_184058_a);
                        mc.field_71456_v.func_184046_j().func_184052_a(k, l, info);
                        mc.field_71466_p.func_175063_a(text, (float) i / ((Float) this.scale.getValue()).floatValue() / 2.0F - (float) (mc.field_71466_p.func_78256_a(text) / 2), (float) (l - 9), 16777215);
                    }

                    GL11.glScaled(1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                }
            } else if (this.boss.getValue() == NoRender.Boss.STACK) {
                map = mc.field_71456_v.func_184046_j().field_184060_g;
                HashMap to = new HashMap();
                Iterator var14 = map.entrySet().iterator();

                while (var14.hasNext()) {
                    Entry entry2 = (Entry) var14.next();
                    String s = ((BossInfoClient) entry2.getValue()).func_186744_e().func_150254_d();
                    NoRender.Pair p;
                    if (to.containsKey(s)) {
                        p = (NoRender.Pair) to.get(s);
                        p = new NoRender.Pair(p.getKey(), ((Integer) p.getValue()).intValue() + 1);
                        to.put(s, p);
                    } else {
                        p = new NoRender.Pair(entry2.getValue(), Integer.valueOf(1));
                        to.put(s, p);
                    }
                }

                ScaledResolution scaledresolution2 = new ScaledResolution(mc);
                l = scaledresolution2.func_78326_a();
                int m = 12;

                for (Iterator var20 = to.entrySet().iterator(); var20.hasNext(); m += 10 + mc.field_71466_p.field_78288_b) {
                    Entry entry3 = (Entry) var20.next();
                    text = (String) entry3.getKey();
                    BossInfoClient info2 = (BossInfoClient) ((NoRender.Pair) entry3.getValue()).getKey();
                    int a = ((Integer) ((NoRender.Pair) entry3.getValue()).getValue()).intValue();
                    text = text + " x" + a;
                    int k2 = (int) ((float) l / ((Float) this.scale.getValue()).floatValue() / 2.0F - 91.0F);
                    GL11.glScaled((double) ((Float) this.scale.getValue()).floatValue(), (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                    if (!event.isCanceled()) {
                        GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
                        mc.func_110434_K().func_110577_a(GuiBossOverlay.field_184058_a);
                        mc.field_71456_v.func_184046_j().func_184052_a(k2, m, info2);
                        mc.field_71466_p.func_175063_a(text, (float) l / ((Float) this.scale.getValue()).floatValue() / 2.0F - (float) (mc.field_71466_p.func_78256_a(text) / 2), (float) (m - 9), 16777215);
                    }

                    GL11.glScaled(1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                }
            }
        }

    }

    @SubscribeEvent
    public void onRenderLiving(net.minecraftforge.client.event.RenderLivingEvent.Pre event) {
        if (((Boolean) this.bats.getValue()).booleanValue() && event.getEntity() instanceof EntityBat) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundAtEntityEvent event) {
        if (((Boolean) this.bats.getValue()).booleanValue() && event.getSound().equals(SoundEvents.field_187740_w) || event.getSound().equals(SoundEvents.field_187742_x) || event.getSound().equals(SoundEvents.field_187743_y) || event.getSound().equals(SoundEvents.field_189108_z) || event.getSound().equals(SoundEvents.field_187744_z)) {
            event.setVolume(0.0F);
            event.setPitch(0.0F);
            event.setCanceled(true);
        }

    }

    static {
        INSTANCE = new NoRender();
    }

    public static class Pair {
        private Object key;
        private Object value;

        public Pair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public static enum NoArmor {
        NONE,
        ALL,
        HELMET;
    }

    public static enum Boss {
        NONE,
        REMOVE,
        STACK,
        MINIMIZE;
    }

    public static enum Fog {
        NONE,
        AIR,
        NOFOG;
    }

    public static enum Skylight {
        NONE,
        WORLD,
        ENTITY,
        ALL;
    }
}
