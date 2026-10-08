package com.github.elenterius.magianaturalis.easteregg;

import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

/**
 * 彩蛋实体：白瞳凝视者。
 *
 * 行为：
 * - 出场：雷劈 + 末影人尖叫 + 假局域网加入消息（黄色）
 * - 站位：不动，死死盯住 32 格内最近的玩家
 * - 玩家靠近 10 格 → 立刻消失，留话
 * - 玩家不靠近，20 秒后 → 自动消失，留话
 * - 无敌、不被推、不被撞、不能被攻击
 */
public class EntityHerobrineWatcher extends EntityMob {

    private static final int LIFETIME = 20 * 20;
    private static final double NEAR_RADIUS = 10.0D;
    private static final double WHISPER_RADIUS = 32.0D;
    private static final double LOOK_RADIUS = 32.0D;

    private static final String LANG_MSG = "chat.magianaturalis.easteregg.herobrine";
    private static final String LANG_JOIN = "chat.magianaturalis.herobrine.join";

    private boolean said = false;
    private boolean introPlayed = false;

    public EntityHerobrineWatcher(World world) {
        super(world);
        setSize(0.6F, 1.8F);
        this.experienceValue = 0;

        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();
        this.getNavigator()
            .setCanSwim(false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
            .setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
            .setBaseValue(0.0D);
    }

    @Override
    protected boolean isAIEnabled() {
        return false;
    }

    // ==================================================
    // 【关键】禁止服务端同步朝向回来
    // 只同步位置，不同步 yaw / pitch。
    // 否则客户端渲染器刚锁完的朝向会被服务端每 tick 打回去。
    // ==================================================
    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        this.setPosition(x, y, z);
    }

    // ==================================================
    // 彻底禁用 AI 的“动作状态”，防止它自己转来转去
    // ==================================================
    @Override
    protected void updateEntityActionState() {
        // 什么都不做
    }

    // ==================================================
    // 禁止任何移动输入，重力保留
    // ==================================================
    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        super.moveEntityWithHeading(0.0F, 0.0F);
    }

    // ==================================================
    // 关闭“视线朝向”缓动，防止 LookHelper 乱拉头
    // ==================================================
    @Override
    public void setRotationYawHead(float angle) {
        // 忽略
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // 完全静止
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        // 服务端也锁一次朝向，双保险
        // 渲染器每帧还会再锁一次，所以只要有一层生效，头就稳
        EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, LOOK_RADIUS);
        if (target != null) {
            double dx = target.posX - this.posX;
            double dz = target.posZ - this.posZ;
            float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;

            this.rotationYaw = yaw;
            this.prevRotationYaw = yaw;
            this.rotationYawHead = yaw;
            this.prevRotationYawHead = yaw;
            this.renderYawOffset = yaw;
            this.prevRenderYawOffset = yaw;
            this.rotationPitch = 0.0F;
            this.prevRotationPitch = 0.0F;
        }

        if (worldObj.isRemote) return;

        if (!introPlayed && ticksExisted == 1) {
            introPlayed = true;
            playIntro();
        }

        if (said) return;

        if (hasPlayerNearby()) {
            disappear();
            return;
        }

        if (ticksExisted >= LIFETIME) {
            disappear();
        }
    }

    /** 出场效果 */
    private void playIntro() {
        MagiaNaturalis.lightning(worldObj, posX, posY + 25, posZ, posX, posY, posZ, 12, 0.3F, 20, 0);

        // 末影人尖叫，5.0 音量
        worldObj.playSoundEffect(posX, posY, posZ, "mob.endermen.scream", 5.0F, 1.0F);

        // 假局域网加入消息
        if (MinecraftServer.getServer() != null) {
            ChatComponentTranslation msg = new ChatComponentTranslation(LANG_JOIN, new Object[] { "Herobrine" });
            msg.getChatStyle()
                .setColor(EnumChatFormatting.YELLOW);

            MinecraftServer.getServer()
                .getConfigurationManager()
                .sendChatMsg(msg);
        }
    }

    /** 消失效果 */
    private void disappear() {
        said = true;

        whisperToNearbyPlayers();

        worldObj.playSoundEffect(posX, posY, posZ, "mob.endermen.hit", 5.0F, 0.6F);
        worldObj.playSoundEffect(posX, posY, posZ, "mob.endermen.portal", 3.0F, 0.8F);

        setDead();
    }

    @SuppressWarnings("unchecked")
    private boolean hasPlayerNearby() {
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            posX - NEAR_RADIUS,
            posY - NEAR_RADIUS,
            posZ - NEAR_RADIUS,
            posX + NEAR_RADIUS,
            posY + NEAR_RADIUS,
            posZ + NEAR_RADIUS);

        List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        return !players.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private void whisperToNearbyPlayers() {
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            posX - WHISPER_RADIUS,
            posY - WHISPER_RADIUS,
            posZ - WHISPER_RADIUS,
            posX + WHISPER_RADIUS,
            posY + WHISPER_RADIUS,
            posZ + WHISPER_RADIUS);

        List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        for (EntityPlayer p : players) {
            p.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.DARK_RED + ""
                        + EnumChatFormatting.ITALIC
                        + StatCollector.translateToLocal(LANG_MSG)));
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canAttackClass(Class clazz) {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("Said", said);
        tag.setBoolean("IntroPlayed", introPlayed);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        said = tag.getBoolean("Said");
        introPlayed = tag.getBoolean("IntroPlayed");
    }
}
