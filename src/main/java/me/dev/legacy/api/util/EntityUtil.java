package me.dev.legacy.api.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.mixin.mixins.IEntityLivingBase;
import me.dev.legacy.modules.player.Blink;
import me.dev.legacy.modules.player.FakePlayer;
import me.dev.legacy.modules.player.Freecam;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

public class EntityUtil implements Util {
    public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0D, -2.0D, 0.0D)};
    public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D)};
    public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D)};
    public static final Vec3d[] OffsetList = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D)};
    public static final Vec3d[] antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D)};
    public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0D, 3.0D, 0.0D)};
    public static final Vec3d[] doubleLegOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D), new Vec3d(0.0D, 0.0D, 2.0D)};

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(entity));
        } else {
            mc.field_71442_b.func_78764_a(mc.field_71439_g, entity);
        }

        if (swingArm) {
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        }

    }

    public static double getRelativeX(float yaw) {
        return (double) MathHelper.func_76126_a(-yaw * 0.017453292F);
    }

    public static double getRelativeZ(float yaw) {
        return (double) MathHelper.func_76134_b(yaw * 0.017453292F);
    }

    public static float calculate(double posX, double posY, double posZ, EntityLivingBase entity) {
        double distancedsize = entity.func_70011_f(posX, posY, posZ) / 12.0D;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) getBlockDensity(vec3d, entity.func_174813_aQ());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D));
        return getBlastReduction(entity, getDamageMultiplied(damage), new Explosion(mc.field_71441_e, (Entity) null, posX, posY, posZ, 6.0F, false, true));
    }

    public static EntityPlayer getClosestPlayer(float range) {
        EntityPlayer player = null;
        double maxDistance = 999.0D;
        int size = mc.field_71441_e.field_73010_i.size();

        for (int i = 0; i < size; ++i) {
            EntityPlayer entityPlayer = (EntityPlayer) mc.field_71441_e.field_73010_i.get(i);
            if (isPlayerValid(entityPlayer, range)) {
                double distanceSq = mc.field_71439_g.func_70068_e(entityPlayer);
                if (maxDistance == 999.0D || distanceSq < maxDistance) {
                    maxDistance = distanceSq;
                    player = entityPlayer;
                }
            }
        }

        return player;
    }

    public static boolean isFakePlayer(EntityPlayer player) {
        Freecam freecam = Freecam.getInstance();
        FakePlayer fakePlayer = FakePlayer.getInstance();
        Blink blink = Blink.getInstance();
        int playerID = player.func_145782_y();
        if (freecam.isOn() && playerID == 69420) {
            return true;
        } else {
            if (fakePlayer.isOn()) {
                Iterator var5 = fakePlayer.fakePlayerIdList.iterator();

                while (var5.hasNext()) {
                    int id = ((Integer) var5.next()).intValue();
                    if (id == playerID) {
                        return true;
                    }
                }
            }

            return blink.isOn() && playerID == 6942069;
        }
    }

    public static boolean isPlayerValid(EntityPlayer player, float range) {
        return player != mc.field_71439_g && mc.field_71439_g.func_70032_d(player) < range && !isDead(player) && !Legacy.friendManager.isFriend(player.func_70005_c_());
    }

    private static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.func_94539_a(explosion);
            damage = CombatRules.func_189427_a(damageI, (float) ep.func_70658_aO(), (float) ep.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
            int k = EnchantmentHelper.func_77508_a(ep.func_184193_aE(), ds);
            float f = MathHelper.func_76131_a((float) k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            if (entity.func_70644_a(MobEffects.field_76429_m)) {
                damage -= damage / 4.0F;
            }

            return damage;
        } else {
            damage = CombatRules.func_189427_a(damageI, (float) entity.func_70658_aO(), (float) entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
            return damage;
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.field_71441_e.func_175659_aa().func_151525_a();
        return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0D / ((bb.field_72336_d - bb.field_72340_a) * 2.0D + 1.0D);
        double d1 = 1.0D / ((bb.field_72337_e - bb.field_72338_b) * 2.0D + 1.0D);
        double d2 = 1.0D / ((bb.field_72334_f - bb.field_72339_c) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        double d5 = bb.field_72340_a + (bb.field_72336_d - bb.field_72340_a) * (double) f;
                        double d6 = bb.field_72338_b + (bb.field_72337_e - bb.field_72338_b) * (double) f1;
                        double d7 = bb.field_72339_c + (bb.field_72334_f - bb.field_72339_c) * (double) f2;
                        if (rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null) {
                            ++j2;
                        }

                        ++k2;
                    }
                }
            }

            return (float) j2 / (float) k2;
        } else {
            return 0.0F;
        }
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32) {
        int i = MathHelper.func_76128_c(vec32.field_72450_a);
        int j = MathHelper.func_76128_c(vec32.field_72448_b);
        int k = MathHelper.func_76128_c(vec32.field_72449_c);
        int l = MathHelper.func_76128_c(vec31.field_72450_a);
        int i1 = MathHelper.func_76128_c(vec31.field_72448_b);
        int j1 = MathHelper.func_76128_c(vec31.field_72449_c);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = mc.field_71441_e.func_180495_p(blockpos);
        Block block = iblockstate.func_177230_c();
        if (block.func_176209_a(iblockstate, false) && block != Blocks.field_150321_G) {
            return iblockstate.func_185910_a(mc.field_71441_e, blockpos, vec31, vec32);
        } else {
            int k1 = 200;
            double d6 = vec32.field_72450_a - vec31.field_72450_a;
            double d7 = vec32.field_72448_b - vec31.field_72448_b;
            double d8 = vec32.field_72449_c - vec31.field_72449_c;

            do {
                if (k1-- < 0) {
                    return null;
                }

                if (l == i && i1 == j && j1 == k) {
                    return null;
                }

                boolean flag2 = true;
                boolean flag = true;
                boolean flag1 = true;
                double d0 = 999.0D;
                double d1 = 999.0D;
                double d2 = 999.0D;
                if (i > l) {
                    d0 = (double) l + 1.0D;
                } else if (i < l) {
                    d0 = (double) l + 0.0D;
                } else {
                    flag2 = false;
                }

                if (j > i1) {
                    d1 = (double) i1 + 1.0D;
                } else if (j < i1) {
                    d1 = (double) i1 + 0.0D;
                } else {
                    flag = false;
                }

                if (k > j1) {
                    d2 = (double) j1 + 1.0D;
                } else if (k < j1) {
                    d2 = (double) j1 + 0.0D;
                } else {
                    flag1 = false;
                }

                double d3 = 999.0D;
                double d4 = 999.0D;
                double d5 = 999.0D;
                if (flag2) {
                    d3 = (d0 - vec31.field_72450_a) / d6;
                }

                if (flag) {
                    d4 = (d1 - vec31.field_72448_b) / d7;
                }

                if (flag1) {
                    d5 = (d2 - vec31.field_72449_c) / d8;
                }

                if (d3 == -0.0D) {
                    d3 = -1.0E-4D;
                }

                if (d4 == -0.0D) {
                    d4 = -1.0E-4D;
                }

                if (d5 == -0.0D) {
                    d5 = -1.0E-4D;
                }

                EnumFacing enumfacing;
                if (d3 < d4 && d3 < d5) {
                    enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                    vec31 = new Vec3d(d0, vec31.field_72448_b + d7 * d3, vec31.field_72449_c + d8 * d3);
                } else if (d4 < d5) {
                    enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                    vec31 = new Vec3d(vec31.field_72450_a + d6 * d4, d1, vec31.field_72449_c + d8 * d4);
                } else {
                    enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                    vec31 = new Vec3d(vec31.field_72450_a + d6 * d5, vec31.field_72448_b + d7 * d5, d2);
                }

                l = MathHelper.func_76128_c(vec31.field_72450_a) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                i1 = MathHelper.func_76128_c(vec31.field_72448_b) - (enumfacing == EnumFacing.UP ? 1 : 0);
                j1 = MathHelper.func_76128_c(vec31.field_72449_c) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                blockpos = new BlockPos(l, i1, j1);
                iblockstate = mc.field_71441_e.func_180495_p(blockpos);
                block = iblockstate.func_177230_c();
            } while (!block.func_176209_a(iblockstate, false) || block == Blocks.field_150321_G);

            return iblockstate.func_185910_a(mc.field_71441_e, blockpos, vec31, vec32);
        }
    }

    public static boolean checkForLiquid(Entity entity, boolean b) {
        if (entity == null) {
            return false;
        } else {
            double posY = entity.field_70163_u;
            double n = b ? 0.03D : (entity instanceof EntityPlayer ? 0.2D : 0.5D);
            double n2 = posY - n;

            for (int i = MathHelper.func_76128_c(entity.field_70165_t); i < MathHelper.func_76143_f(entity.field_70165_t); ++i) {
                for (int j = MathHelper.func_76128_c(entity.field_70161_v); j < MathHelper.func_76143_f(entity.field_70161_v); ++j) {
                    if (mc.field_71441_e.func_180495_p(new BlockPos(i, MathHelper.func_76128_c(n2), j)).func_177230_c() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isAboveLiquid(Entity entity) {
        if (entity == null) {
            return false;
        } else {
            double n = entity.field_70163_u + 0.01D;

            for (int i = MathHelper.func_76128_c(entity.field_70165_t); i < MathHelper.func_76143_f(entity.field_70165_t); ++i) {
                for (int j = MathHelper.func_76128_c(entity.field_70161_v); j < MathHelper.func_76143_f(entity.field_70161_v); ++j) {
                    if (mc.field_71441_e.func_180495_p(new BlockPos(i, (int) n, j)).func_177230_c() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isOnLiquid(double offset) {
        if (mc.field_71439_g.field_70143_R >= 3.0F) {
            return false;
        } else {
            AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(0.0D, -offset, 0.0D) : mc.field_71439_g.func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(0.0D, -offset, 0.0D);
            boolean onLiquid = false;
            int y = (int) bb.field_72338_b;

            for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d + 1.0D); ++x) {
                for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f + 1.0D); ++z) {
                    Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                    if (block != Blocks.field_150350_a) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        onLiquid = true;
                    }
                }
            }

            return onLiquid;
        }
    }

    public static boolean isOnLiquid() {
        double y = mc.field_71439_g.field_70163_u - 0.03D;

        for (int x = MathHelper.func_76128_c(mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f(mc.field_71439_g.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c(mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f(mc.field_71439_g.field_70161_v); ++z) {
                BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
                if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isInLiquid() {
        if (mc.field_71439_g.field_70143_R >= 3.0F) {
            return false;
        } else {
            boolean inLiquid = false;
            AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ() : mc.field_71439_g.func_174813_aQ();
            int y = (int) bb.field_72338_b;

            for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
                for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
                    Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        inLiquid = true;
                    }
                }
            }

            return inLiquid;
        }
    }

    public static void resetTimer() {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0F;
    }

    public static BlockPos getFlooredPos(Entity e) {
        return new BlockPos(Math.floor(e.field_70165_t), Math.floor(e.field_70163_u), Math.floor(e.field_70161_v));
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double) time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double) time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double) time);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).func_178786_a(mc.func_175598_ae().field_78725_b, mc.func_175598_ae().field_78726_c, mc.func_175598_ae().field_78723_d);
    }

    public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
        return (new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c)).func_178786_a(mc.func_175598_ae().field_78725_b, mc.func_175598_ae().field_78726_c, mc.func_175598_ae().field_78723_d);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
        return getInterpolatedAmount(entity, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return getInterpolatedAmount(entity, (double) partialTicks, (double) partialTicks, (double) partialTicks);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.field_71439_g != null && mc.field_71439_g.func_70644_a(Potion.func_188412_a(1))) {
            int amplifier = mc.field_71439_g.func_70660_b(Potion.func_188412_a(1)).func_76458_c();
            baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
        }

        return baseSpeed;
    }

    public static boolean isPassive(Entity entity) {
        if (entity instanceof EntityWolf && ((EntityWolf) entity).func_70919_bu()) {
            return false;
        } else if (!(entity instanceof EntityAgeable) && !(entity instanceof EntityAmbientCreature) && !(entity instanceof EntitySquid)) {
            return entity instanceof EntityIronGolem && ((EntityIronGolem) entity).func_70643_av() == null;
        } else {
            return true;
        }
    }

    public static boolean isSafe(Entity entity, int height, boolean floor, boolean face) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && mc.field_71439_g != null) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
        }

        return false;
    }

    public static boolean isSafe(Entity entity) {
        return isSafe(entity, 0, false, true);
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u), Math.floor(player.field_70161_v));
    }

    public static List getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie) entity).func_184734_db() || ((EntityPigZombie) entity).func_175457_ck()) {
                return true;
            }
        } else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf) entity).func_70919_bu() && !mc.field_71439_g.equals(((EntityWolf) entity).func_70902_q());
            }

            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman) entity).func_70823_r();
            }
        }

        return isHostileMob(entity);
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static boolean isProjectile(Entity entity) {
        return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isFriendlyMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || isNeutralMob(entity) && !isMobAggressive(entity);
    }

    public static boolean isHostileMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }

    public static List getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList vec3ds = new ArrayList();
        Vec3d[] var4 = getOffsets(height, floor);
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Vec3d vector = var4[var6];
            BlockPos targetPos = (new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
            Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }

        return vec3ds;
    }

    public static boolean isInHole(Entity entity) {
        return isBlockValid(new BlockPos(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] var2 = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            BlockPos pos = var2[var4];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150343_Z) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] var2 = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            BlockPos pos = var2[var4];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] var2 = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            BlockPos pos = var2[var4];
            IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h && touchingState.func_177230_c() != Blocks.field_150343_Z) {
                return false;
            }
        }

        return true;
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return (Vec3d[]) list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List list = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return (Vec3d[]) list.toArray(array);
    }

    public static double getDst(Vec3d vec) {
        return mc.field_71439_g.func_174791_d().func_72438_d(vec);
    }

    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }

    public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
    }

    public static List getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        ArrayList vec3ds = new ArrayList();
        if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
            vec3ds.addAll(getUnsafeBlocks(player, 2, false));
        }

        for (int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
            Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
            BlockPos targetPos = (new BlockPos(player.func_174791_d())).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
            Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }

        return vec3ds;
    }

    public static boolean isInWater(Entity entity) {
        if (entity == null) {
            return false;
        } else {
            double y = entity.field_70163_u + 0.01D;

            for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); ++x) {
                for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ++z) {
                    BlockPos pos = new BlockPos(x, (int) y, z);
                    if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isDrivenByPlayer(Entity entityIn) {
        return mc.field_71439_g != null && entityIn != null && entityIn.equals(mc.field_71439_g.func_184187_bx());
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isAboveWater(Entity entity) {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(Entity entity, boolean packet) {
        if (entity == null) {
            return false;
        } else {
            double y = entity.field_70163_u - (packet ? 0.03D : (isPlayer(entity) ? 0.2D : 0.5D));

            for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); ++x) {
                for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ++z) {
                    BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
                    if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.field_70165_t - px;
        double diry = me.field_70163_u - py;
        double dirz = me.field_70161_v - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0D / 3.141592653589793D;
        yaw = yaw * 180.0D / 3.141592653589793D;
        yaw += 90.0D;
        return new double[]{yaw, pitch};
    }

    public static List getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        ArrayList placeTargets = new ArrayList();
        Iterator var10;
        Vec3d vec3d;
        if (extension == 1) {
            placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
        } else {
            int extend = 1;

            for (var10 = MathUtil.getBlockBlocks(player).iterator(); var10.hasNext(); ++extend) {
                vec3d = (Vec3d) var10.next();
                if (extend > extension) {
                    break;
                }

                placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
            }
        }

        ArrayList removeList = new ArrayList();
        var10 = placeTargets.iterator();

        while (var10.hasNext()) {
            vec3d = (Vec3d) var10.next();
            BlockPos pos = new BlockPos(vec3d);
            if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
                removeList.add(vec3d);
            }
        }

        var10 = removeList.iterator();

        while (var10.hasNext()) {
            vec3d = (Vec3d) var10.next();
            placeTargets.remove(vec3d);
        }

        return placeTargets;
    }

    public static List targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        ArrayList placeTargets = new ArrayList();
        if (antiDrop) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
        }

        if (platform) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
        }

        if (legs) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
        }

        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
        if (antiStep) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
        } else {
            List vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if (vec3ds.size() == 4) {
                Iterator var9 = vec3ds.iterator();

                while (var9.hasNext()) {
                    Vec3d vector = (Vec3d) var9.next();
                    BlockPos position = (new BlockPos(vec3d)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
                    switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
                        case -1:
                        case 1:
                        case 2:
                            break;
                        case 3:
                            placeTargets.add(vec3d.func_178787_e(vector));
                        case 0:
                        default:
                            if (antiScaffold) {
                                Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
                            }

                            return placeTargets;
                    }
                }
            }
        }

        if (antiScaffold) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
        }

        return placeTargets;
    }

    public static List getOffsetList(int y, boolean floor) {
        ArrayList offsets = new ArrayList();
        offsets.add(new Vec3d(-1.0D, (double) y, 0.0D));
        offsets.add(new Vec3d(1.0D, (double) y, 0.0D));
        offsets.add(new Vec3d(0.0D, (double) y, -1.0D));
        offsets.add(new Vec3d(0.0D, (double) y, 1.0D));
        if (floor) {
            offsets.add(new Vec3d(0.0D, (double) (y - 1), 0.0D));
        }

        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[]) offsets.toArray(array);
    }

    public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        List offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[]) offsets.toArray(array);
    }

    public static List getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        ArrayList offsets = new ArrayList(getOffsetList(1, false));
        offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
        if (antiScaffold) {
            offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
        }

        if (antiStep) {
            offsets.addAll(getOffsetList(2, false));
        }

        if (legs) {
            offsets.addAll(getOffsetList(0, false));
        }

        if (platform) {
            offsets.addAll(getOffsetList(-1, false));
            offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
        }

        if (antiDrop) {
            offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
        }

        return offsets;
    }

    public static Vec3d[] getHeightOffsets(int min, int max) {
        ArrayList offsets = new ArrayList();

        for (int i = min; i <= max; ++i) {
            offsets.add(new Vec3d(0.0D, (double) i, 0.0D));
        }

        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[]) offsets.toArray(array);
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.func_174791_d(), 0));
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && !entity.field_70128_L && ((EntityLivingBase) entity).func_110143_aJ() > 0.0F;
    }

    public static boolean isDead(Entity entity) {
        return !isAlive(entity);
    }

    public static float getHealth(Entity entity) {
        if (isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.func_110143_aJ() + livingBase.func_110139_bj();
        } else {
            return 0.0F;
        }
    }

    public static float getHealth(Entity entity, boolean absorption) {
        if (isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.func_110143_aJ() + (absorption ? livingBase.func_110139_bj() : 0.0F);
        } else {
            return 0.0F;
        }
    }

    public static boolean canEntityFeetBeSeen(Entity entityIn) {
        return mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70165_t + (double) mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(entityIn.field_70165_t, entityIn.field_70163_u, entityIn.field_70161_v), false, true, false) == null;
    }

    public static boolean isntValid(Entity entity, double range) {
        return entity == null || isDead(entity) || entity.equals(mc.field_71439_g) || entity instanceof EntityPlayer && Legacy.friendManager.isFriend(entity.func_70005_c_()) || mc.field_71439_g.func_70068_e(entity) > MathUtil.square(range);
    }

    public static boolean isValid(Entity entity, double range) {
        return !isntValid(entity, range);
    }

    public static boolean holdingWeapon(EntityPlayer player) {
        return player.func_184614_ca().func_77973_b() instanceof ItemSword || player.func_184614_ca().func_77973_b() instanceof ItemAxe;
    }

    public static double getMaxSpeed() {
        double maxModifier = 0.2873D;
        if (mc.field_71439_g.func_70644_a((Potion) Objects.requireNonNull(Potion.func_188412_a(1)))) {
            maxModifier *= 1.0D + 0.2D * (double) (((PotionEffect) Objects.requireNonNull(mc.field_71439_g.func_70660_b((Potion) Objects.requireNonNull(Potion.func_188412_a(1))))).func_76458_c() + 1);
        }

        return maxModifier;
    }

    public static void mutliplyEntitySpeed(Entity entity, double multiplier) {
        if (entity != null) {
            entity.field_70159_w *= multiplier;
            entity.field_70179_y *= multiplier;
        }

    }

    public static boolean isEntityMoving(Entity entity) {
        if (entity == null) {
            return false;
        } else if (entity instanceof EntityPlayer) {
            return mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d();
        } else {
            return entity.field_70159_w != 0.0D || entity.field_70181_x != 0.0D || entity.field_70179_y != 0.0D;
        }
    }

    public static double getEntitySpeed(Entity entity) {
        if (entity != null) {
            double distTraveledLastTickX = entity.field_70165_t - entity.field_70169_q;
            double distTraveledLastTickZ = entity.field_70161_v - entity.field_70166_s;
            double speed = (double) MathHelper.func_76133_a(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
            return speed * 20.0D;
        } else {
            return 0.0D;
        }
    }

    public static boolean is32k(ItemStack stack) {
        return EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, stack) >= 1000;
    }

    public static void moveEntityStrafe(double speed, Entity entity) {
        if (entity != null) {
            MovementInput movementInput = mc.field_71439_g.field_71158_b;
            double forward = (double) movementInput.field_192832_b;
            double strafe = (double) movementInput.field_78902_a;
            float yaw = mc.field_71439_g.field_70177_z;
            if (forward == 0.0D && strafe == 0.0D) {
                entity.field_70159_w = 0.0D;
                entity.field_70179_y = 0.0D;
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (float) (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (float) (forward > 0.0D ? 45 : -45);
                    }

                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else if (forward < 0.0D) {
                        forward = -1.0D;
                    }
                }

                entity.field_70159_w = forward * speed * Math.cos(Math.toRadians((double) (yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double) (yaw + 90.0F)));
                entity.field_70179_y = forward * speed * Math.sin(Math.toRadians((double) (yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double) (yaw + 90.0F)));
            }
        }

    }

    public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
        return !shouldCheck || mc.field_71439_g.func_70685_l(entity);
    }

    public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
        Color color = new Color((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, (float) alpha / 255.0F);
        if (entity instanceof EntityPlayer && colorFriends && Legacy.friendManager.isFriend((EntityPlayer) entity)) {
            color = new Color(0.33333334F, 1.0F, 1.0F, (float) alpha / 255.0F);
        }

        return color;
    }

    public static boolean isMoving() {
        return (double) mc.field_71439_g.field_191988_bg != 0.0D || (double) mc.field_71439_g.field_70702_br != 0.0D;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.field_191988_bg != 0.0F || entity.field_70702_br != 0.0F;
    }

    public static EntityPlayer getClosestEnemy(double distance) {
        EntityPlayer closest = null;
        Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

        while (var3.hasNext()) {
            EntityPlayer player = (EntityPlayer) var3.next();
            if (!isntValid(player, distance)) {
                if (closest == null) {
                    closest = player;
                } else if (mc.field_71439_g.func_70068_e(player) < mc.field_71439_g.func_70068_e(closest)) {
                    closest = player;
                }
            }
        }

        return closest;
    }

    public static boolean checkCollide() {
        if (mc.field_71439_g.func_70093_af()) {
            return false;
        } else if (mc.field_71439_g.func_184187_bx() != null && mc.field_71439_g.func_184187_bx().field_70143_R >= 3.0F) {
            return false;
        } else {
            return mc.field_71439_g.field_70143_R < 3.0F;
        }
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos(mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().field_70165_t : mc.field_71439_g.field_70165_t, mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().field_70163_u : mc.field_71439_g.field_70163_u, mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().field_70161_v : mc.field_71439_g.field_70161_v);
    }

    public static boolean isCrystalAtFeet(EntityEnderCrystal crystal, double range) {
        Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

        while (true) {
            EntityPlayer player;
            do {
                do {
                    if (!var3.hasNext()) {
                        return false;
                    }

                    player = (EntityPlayer) var3.next();
                } while (mc.field_71439_g.func_70068_e(player) > range * range);
            } while (Legacy.friendManager.isFriend(player));

            Vec3d[] var5 = doubleLegOffsetList;
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Vec3d vec = var5[var7];
                if ((new BlockPos(player.func_174791_d())).func_177963_a(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c) == crystal.func_180425_c()) {
                    return true;
                }
            }
        }
    }

    public static double[] forward(double speed) {
        float forward = mc.field_71439_g.field_71158_b.field_192832_b;
        float side = mc.field_71439_g.field_71158_b.field_78902_a;
        float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static void swingArmNoPacket(EnumHand hand, EntityLivingBase entity) {
        ItemStack stack = entity.func_184586_b(hand);
        if (stack.func_190926_b() || !stack.func_77973_b().onEntitySwing(entity, stack)) {
            if (!entity.field_82175_bq || entity.field_110158_av >= ((IEntityLivingBase) entity).getArmSwingAnimationEnd() / 2 || entity.field_110158_av < 0) {
                entity.field_110158_av = -1;
                entity.field_82175_bq = true;
                entity.field_184622_au = hand;
            }

        }
    }

    public static Map getTextRadarPlayers() {
        Map output = new HashMap();
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder healthSB = new StringBuilder();
        StringBuilder distanceSB = new StringBuilder();
        Iterator var5 = mc.field_71441_e.field_73010_i.iterator();

        while (var5.hasNext()) {
            EntityPlayer player = (EntityPlayer) var5.next();
            if (!player.func_82150_aj() && !player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_())) {
                int hpRaw = (int) getHealth(player);
                String hp = dfHealth.format((long) hpRaw);
                healthSB.append("Â§");
                if (hpRaw >= 20) {
                    healthSB.append("a");
                } else if (hpRaw >= 10) {
                    healthSB.append("e");
                } else if (hpRaw >= 5) {
                    healthSB.append("6");
                } else {
                    healthSB.append("c");
                }

                healthSB.append(hp);
                int distanceInt = (int) mc.field_71439_g.func_70032_d(player);
                String distance = dfDistance.format((long) distanceInt);
                distanceSB.append("Â§");
                if (distanceInt >= 25) {
                    distanceSB.append("a");
                } else if (distanceInt > 10) {
                    distanceSB.append("6");
                } else {
                    distanceSB.append("c");
                }

                distanceSB.append(distance);
                ((Map) output).put(healthSB.toString() + " " + (Legacy.friendManager.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RED) + player.func_70005_c_() + " " + distanceSB.toString() + " Â§f0", (int) mc.field_71439_g.func_70032_d(player));
                healthSB.setLength(0);
                distanceSB.setLength(0);
            }
        }

        if (!((Map) output).isEmpty()) {
            output = MathUtil.sortByValue((Map) output, false);
        }

        return (Map) output;
    }

    public static void setTimer(float speed) {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0F / speed;
    }

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (mc.field_71439_g != null) {
            AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(posX, posY, posZ) : mc.field_71439_g.func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(posX, posY, posZ);
            int y = (int) bb.field_72338_b;

            for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
                for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
                    block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                }
            }
        }

        return block;
    }

    public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
        return entity.field_70163_u >= (double) blockPos.func_177956_o();
    }
}
