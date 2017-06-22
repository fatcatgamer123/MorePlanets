package stevekung.mods.moreplanets.module.planets.chalos.entity;

import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.IBoss;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import stevekung.mods.moreplanets.module.planets.chalos.blocks.ChalosBlocks;
import stevekung.mods.moreplanets.module.planets.chalos.entity.projectile.EntityCheeseSpore;
import stevekung.mods.moreplanets.module.planets.chalos.items.ChalosItems;
import stevekung.mods.moreplanets.util.IMorePlanetsBossDisplayData;
import stevekung.mods.moreplanets.util.entity.EntityFlyingBossMP;
import stevekung.mods.moreplanets.util.tileentity.TileEntityTreasureChestMP;

public class EntityCheeseCubeEyeBoss extends EntityFlyingBossMP implements IEntityBreathable, IMorePlanetsBossDisplayData, IBoss
{
    private TileEntityDungeonSpawner spawner;
    private Entity targetedEntity;
    public int deathTicks = 0;
    public int attackCounter;
    public int prevAttackCounter;
    public int entitiesWithin;
    public int entitiesWithinLast;
    private int spawnCount = 10;

    public EntityCheeseCubeEyeBoss(World world)
    {
        super(world);
        this.setSize(1.8F, 2.0F);
        this.moveHelper = new GhastMoveHelper(this);
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(5, new AIRandomFly(this));
        this.tasks.addTask(7, new AILookAround(this));
        this.tasks.addTask(7, new AIFireballAttack(this));
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(750.0F * ConfigManagerCore.dungeonBossHealthMod);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(15.0F);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
    }

    @Override
    public void onKillCommand()
    {
        this.setHealth(0.0F);
    }

    @Override
    public void knockBack(Entity entity, float strength, double x, double z) {}

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void onDeathUpdate()
    {
        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200)
        {
            float f = (this.rand.nextFloat() - 0.5F) * 1.5F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 1.5F;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + f, this.posY + 2.0D + f1, this.posZ + f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!this.worldObj.isRemote)
        {
            if (this.deathTicks >= 180 && this.deathTicks % 5 == 0)
            {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_PLAY_SOUND_EXPLODE, GCCoreUtil.getDimensionID(this.worldObj), new Object[] { }), new TargetPoint(GCCoreUtil.getDimensionID(this.worldObj), this.posX, this.posY, this.posZ, 40.0D));
            }
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0)
            {
                i = 150;

                while (i > 0)
                {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }

            if (this.deathTicks == 40)
            {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH, GCCoreUtil.getDimensionID(this.worldObj), new Object[] { this.getSoundPitch() - 0.1F }), new TargetPoint(GCCoreUtil.getDimensionID(this.worldObj), this.posX, this.posY, this.posZ, 40.0D));
            }
        }

        this.moveEntity(0.0D, -0.10000000149011612D, 0.0D);

        if (this.deathTicks == 200 && !this.worldObj.isRemote)
        {
            i = 150;

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            TileEntityTreasureChestMP chest = null;

            if (this.spawner != null && this.spawner.getChestPos() != null)
            {
                TileEntity chestTest = this.worldObj.getTileEntity(this.spawner.getChestPos());

                if (chestTest != null && chestTest instanceof TileEntityTreasureChestMP)
                {
                    chest = (TileEntityTreasureChestMP) chestTest;
                }
            }

            if (chest == null)
            {
                chest = TileEntityTreasureChestMP.findClosest(this, 5);
            }

            if (chest != null)
            {
                double dist = this.getDistanceSq(chest.getPos().getX() + 0.5, chest.getPos().getY() + 0.5, chest.getPos().getZ() + 0.5);

                if (dist < 1000 * 1000)
                {
                    if (!chest.locked)
                    {
                        chest.locked = true;
                    }

                    for (int k = 0; k < chest.getSizeInventory(); k++)
                    {
                        chest.setInventorySlotContents(k, null);
                    }

                    /*ChestGenHooks info = ChestGenHooks.getInfo(ItemLootHelper.COMMON_SPACE_DUNGEON);//TODO Loot Table

                    // Generate twice, since it's an extra special chest
                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));*/

                    ItemStack schematic = this.getGuaranteedLoot(this.rand);
                    int slot = this.rand.nextInt(chest.getSizeInventory());
                    chest.setInventorySlotContents(slot, schematic);
                }
            }

            this.entityDropItem(new ItemStack(ChalosItems.CHALOS_DUNGEON_KEY, 1, 0), 0.5F);
            super.setDead();

            if (this.spawner != null)
            {
                this.spawner.isBossDefeated = true;
                this.spawner.boss = null;
                this.spawner.spawned = false;
            }
        }
    }

    @Override
    public void onLivingUpdate()
    {
        EntityPlayer player = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 256.0, false);

        if (player != null && !player.equals(this.targetedEntity) && !player.capabilities.isCreativeMode)
        {
            if (this.getDistanceSqToEntity(player) < 400.0D)
            {
                this.getNavigator().getPathToEntityLiving(player);
                this.targetedEntity = player;
            }
        }
        else
        {
            this.targetedEntity = null;
        }

        if (this.spawner != null)
        {
            List<EntityPlayer> playersWithin = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.spawner.getRangeBounds());
            this.entitiesWithin = playersWithin.size();

            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0)
            {
                List<EntityPlayer> entitiesWithin2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.spawner.getRangeBoundsPlus11());

                for (EntityPlayer p : entitiesWithin2)
                {
                    p.addChatMessage(new TextComponentString(GCCoreUtil.translate("gui.skeleton_boss.message")));
                }
                this.setDead();
                return;
            }
            this.entitiesWithinLast = this.entitiesWithin;
        }
        super.onLivingUpdate();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.getHealth() <= 0.0F)
        {
            return;
        }

        if (this.spawnCount > 0 && this.getHealth() <= this.getMaxHealth() / 2)
        {
            if (this.spawner != null)
            {
                if (this.ticksExisted % 100 == 0)
                {
                    EntityCheeseFloater tentacle1 = new EntityCheeseFloater(this.worldObj);
                    tentacle1.setLocationAndAngles(this.posX + 2.0F, this.posY, this.posZ + 2.0F, 0.0F, 0.0F);
                    tentacle1.setAbsorptionAmount(25.0F);
                    tentacle1.setMinion(true);

                    if (tentacle1.getCanSpawnHere() && tentacle1.isNotColliding())
                    {
                        this.worldObj.spawnEntityInWorld(tentacle1);
                    }

                    EntityCheeseFloater tentacle2 = new EntityCheeseFloater(this.worldObj);
                    tentacle2.setLocationAndAngles(this.posX - 2.0F, this.posY, this.posZ - 2.0F, 0.0F, 0.0F);
                    tentacle2.setAbsorptionAmount(25.0F);
                    tentacle2.setMinion(true);

                    if (tentacle2.getCanSpawnHere() && tentacle2.isNotColliding())
                    {
                        this.worldObj.spawnEntityInWorld(tentacle2);
                    }

                    EntityCheeseFloater tentacle3 = new EntityCheeseFloater(this.worldObj);
                    tentacle3.setLocationAndAngles(this.posX + 2.0F, this.posY, this.posZ - 2.0F, 0.0F, 0.0F);
                    tentacle3.setAbsorptionAmount(25.0F);
                    tentacle3.setMinion(true);

                    if (tentacle3.getCanSpawnHere() && tentacle3.isNotColliding())
                    {
                        this.worldObj.spawnEntityInWorld(tentacle3);
                    }

                    EntityCheeseFloater tentacle4 = new EntityCheeseFloater(this.worldObj);
                    tentacle4.setLocationAndAngles(this.posX - 2.0F, this.posY, this.posZ + 2.0F, 0.0F, 0.0F);
                    tentacle4.setAbsorptionAmount(25.0F);
                    tentacle4.setMinion(true);

                    if (tentacle4.getCanSpawnHere() && tentacle4.isNotColliding())
                    {
                        this.worldObj.spawnEntityInWorld(tentacle4);
                    }
                    this.spawnCount--;
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (source.getDamageType().contains("arrow"))
        {
            if (!this.worldObj.isRemote)
            {
                if (this.worldObj instanceof WorldServer)
                {
                    for (int i = 0; i < 16; i++)
                    {
                        ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY + 1.0D, this.posZ, 10, this.width / 4.0F, this.height / 4.0F, this.width / 4.0F, 0.05D, new int[] {Block.getStateId(ChalosBlocks.CHEESE_SLIME_BLOCK.getDefaultState())});
                    }
                }
            }

            if (this.isEntityInvulnerable(source))
            {
                return false;
            }
            else if (super.attackEntityFrom(source, damage))
            {
                Entity entity = source.getEntity();

                if (this.getPassengers().contains(entity) && this.getRidingEntity() != entity)
                {
                    if (entity != this)
                    {
                        this.targetedEntity = entity;
                    }
                    return true;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SLIME_DEATH;
    }

    @Override
    protected void dropFewItems(boolean drop, int fortune)
    {
        int j = this.rand.nextInt(3) + this.rand.nextInt(1 + fortune);

        for (int i = 0; i < j; ++i)
        {
            this.entityDropItem(new ItemStack(ChalosItems.CHEESE_SLIMEBALL, 1), 0.0F);
        }
        for (int i = 0; i < this.rand.nextInt(3); ++i)
        {
            this.entityDropItem(new ItemStack(ChalosItems.CHEESE_FOOD, 1), 0.0F);
        }
    }

    @Override
    public boolean canBreath()
    {
        return true;
    }

    @Override
    public void onBossSpawned(TileEntityDungeonSpawner spawner)
    {
        this.spawner = spawner;
    }

    @Override
    public float getBossMaxHealth()
    {
        return this.getMaxHealth();
    }

    @Override
    public float getBossHealth()
    {
        return this.getHealth();
    }

    @Override
    public ITextComponent getBossDisplayName()
    {
        return this.getDisplayName();
    }

    private ItemStack getGuaranteedLoot(Random rand)
    {
        List<ItemStack> stackList = GalacticraftRegistry.getDungeonLoot(5);
        return stackList.get(rand.nextInt(stackList.size()));
    }

    private static class AIFireballAttack extends EntityAIBase
    {
        private EntityCheeseCubeEyeBoss parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityCheeseCubeEyeBoss ghast)
        {
            this.parentEntity = ghast;
        }

        @Override
        public boolean shouldExecute()
        {
            return this.parentEntity.getAttackTarget() != null;
        }

        @Override
        public void startExecuting()
        {
            this.attackTimer = 0;
        }

        @Override
        public void resetTask() {}

        @Override
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            if (entitylivingbase.getDistanceSqToEntity(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(entitylivingbase))
            {
                World world = this.parentEntity.worldObj;
                ++this.attackTimer;

                if (this.attackTimer == 20)
                {
                    Vec3d vec3d = this.parentEntity.getLook(1.0F);
                    double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.xCoord * 4.0D);
                    double d3 = entitylivingbase.getEntityBoundingBox().minY + entitylivingbase.height / 2.0F - (0.5D + this.parentEntity.posY + this.parentEntity.height / 2.0F);
                    double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.zCoord * 4.0D);
                    world.playEvent((EntityPlayer)null, 1016, new BlockPos(this.parentEntity), 0);
                    EntityCheeseSpore cheeseSpore = new EntityCheeseSpore(world, this.parentEntity, d2, d3, d4);
                    cheeseSpore.posX = this.parentEntity.posX + vec3d.xCoord * 4.0D;
                    cheeseSpore.posY = this.parentEntity.posY + this.parentEntity.height / 2.0F + 0.5D;
                    cheeseSpore.posZ = this.parentEntity.posZ + vec3d.zCoord * 4.0D;
                    world.spawnEntityInWorld(cheeseSpore);
                    this.attackTimer = -40;
                }
            }
            else if (this.attackTimer > 0)
            {
                --this.attackTimer;
            }
        }
    }

    private static class AILookAround extends EntityAIBase
    {
        private EntityCheeseCubeEyeBoss entity;

        public AILookAround(EntityCheeseCubeEyeBoss entity)
        {
            this.entity = entity;
            this.setMutexBits(2);
        }

        @Override
        public boolean shouldExecute()
        {
            return true;
        }

        @Override
        public void updateTask()
        {
            if (this.entity.getAttackTarget() == null)
            {
                this.entity.rotationYaw = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
                this.entity.renderYawOffset = this.entity.rotationYaw;
            }
            else
            {
                EntityLivingBase entitylivingbase = this.entity.getAttackTarget();

                if (entitylivingbase.getDistanceSqToEntity(this.entity) < 4096.0D)
                {
                    double d1 = entitylivingbase.posX - this.entity.posX;
                    double d2 = entitylivingbase.posZ - this.entity.posZ;
                    this.entity.rotationYaw = -((float)MathHelper.atan2(d1, d2)) * (180F / (float)Math.PI);
                    this.entity.renderYawOffset = this.entity.rotationYaw;
                }
            }
        }
    }

    private static class AIRandomFly extends EntityAIBase
    {
        private EntityCheeseCubeEyeBoss entity;

        public AIRandomFly(EntityCheeseCubeEyeBoss entity)
        {
            this.entity = entity;
            this.setMutexBits(1);
        }

        @Override
        public boolean shouldExecute()
        {
            EntityMoveHelper entitymovehelper = this.entity.getMoveHelper();

            if (!entitymovehelper.isUpdating())
            {
                return true;
            }
            else
            {
                double d0 = entitymovehelper.getX() - this.entity.posX;
                double d1 = entitymovehelper.getY() - this.entity.posY;
                double d2 = entitymovehelper.getZ() - this.entity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean continueExecuting()
        {
            return false;
        }

        @Override
        public void startExecuting()
        {
            Random random = this.entity.getRNG();
            double d0 = this.entity.posX + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double d1 = this.entity.posY + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double d2 = this.entity.posZ + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.entity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
        }
    }

    private static class GhastMoveHelper extends EntityMoveHelper
    {
        private EntityCheeseCubeEyeBoss entity;
        private int courseChangeCooldown;

        public GhastMoveHelper(EntityCheeseCubeEyeBoss entity)
        {
            super(entity);
            this.entity = entity;
        }

        @Override
        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - this.entity.posX;
                double d1 = this.posY - this.entity.posY;
                double d2 = this.posZ - this.entity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.courseChangeCooldown-- <= 0)
                {
                    this.courseChangeCooldown += this.entity.getRNG().nextInt(5) + 2;
                    d3 = MathHelper.sqrt_double(d3);

                    if (this.isNotColliding(this.posX, this.posY, this.posZ, d3))
                    {
                        this.entity.motionX += d0 / d3 * 0.1D;
                        this.entity.motionY += d1 / d3 * 0.1D;
                        this.entity.motionZ += d2 / d3 * 0.1D;
                    }
                    else
                    {
                        this.action = EntityMoveHelper.Action.WAIT;
                    }
                }
            }
        }

        private boolean isNotColliding(double x, double y, double z, double distance)
        {
            double d0 = (x - this.entity.posX) / distance;
            double d1 = (y - this.entity.posY) / distance;
            double d2 = (z - this.entity.posZ) / distance;
            AxisAlignedBB axisalignedbb = this.entity.getEntityBoundingBox();

            for (int i = 1; i < distance; ++i)
            {
                axisalignedbb = axisalignedbb.offset(d0, d1, d2);

                if (!this.entity.worldObj.getCollisionBoxes(this.entity, axisalignedbb).isEmpty())
                {
                    return false;
                }
            }
            return true;
        }
    }
}