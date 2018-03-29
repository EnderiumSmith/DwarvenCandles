package dwarvenCandles.entity;

import java.util.List;

import dwarvenCandles.core.Config;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityDynamite extends Entity{

	public EntityLivingBase thrower;
	public int fuse;
	
	public EntityDynamite(World worldIn) {
		super(worldIn);
		this.setSize(0.25F, 0.25F);
		fuse=80;
	}
	
	public EntityDynamite(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public EntityDynamite(World worldIn, EntityLivingBase throwerIn)
    {
        this(worldIn, throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.10000000149011612D, throwerIn.posZ);
        this.thrower = throwerIn;
    }

	@Override
	protected void entityInit() {
		
	}
	
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.hasNoGravity())
        {
            this.motionY -= 0.03999999910593033D;
        }
        
        Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.99D;
        this.motionY *= 0.99D;
        this.motionZ *= 0.99D;

        if (this.onGround)
        {
            this.motionX *= 0;
            this.motionZ *= 0;
            this.motionY *= -0.5D;
        }
        
        if(this.inWater){
        	this.motionX *= 0.9D;
            this.motionY *= 0.9D;
            this.motionZ *= 0.8D;
        }
        
        if(raytraceresult!=null){
	        BlockPos blockpos = raytraceresult.getBlockPos();
	        IBlockState iblockstate = this.world.getBlockState(blockpos);
	
	        if (iblockstate.getMaterial() != Material.AIR)
	        {
	            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);
	
	            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).isVecInside(vec3d))
	            {
	                this.motionX=0;
	                this.motionY=0;
	                this.motionZ=0;
	            }
	        }
        }

        --this.fuse;

        if (this.fuse <= 0)
        {
            this.setDead();

            if (!this.world.isRemote)
            {
                this.explode();
            }
        }
        else
        {
            this.handleWaterMovement();
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
	}
	
	
	
	/**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void setHeadingFromThrower(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.setThrowableHeading((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround)
        {
            this.motionY += entityThrower.motionY;
        }
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }
    
    public void explode(){
		Explosion boom=new Explosion(world, thrower, this.posX, this.posY, this.posZ, 3, false, false);
		world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
		world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, new int[0]);
		if(this.isInWater()){
			//fish
			for(int x=-1;x<2;x++){
				for(int y=-1;y<2;y++){
					for(int z=-1;z<2;z++){
						BlockPos block=new BlockPos(this.posX+x, this.posY+y, this.posZ+z);
						IBlockState state=world.getBlockState(block);
						if(state.getBlock()==Blocks.WATER||state.getBlock()==Blocks.FLOWING_WATER){
							if(this.rand.nextFloat()<Config.fishChance){
								LootTable table=world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING_FISH);
								LootContext ctx=new LootContext(0, (WorldServer) world, world.getLootTableManager(), null, null, DamageSource.causeExplosionDamage(boom));
								List<ItemStack> fishes=table.generateLootForPools(this.rand, ctx);
								for(ItemStack fish:fishes){
									InventoryHelper.spawnItemStack(world, this.posX+x, this.posY+y, this.posZ+z, fish);
								}
							}
						}
								//particle
								double d0 = (double)((float)block.getX() + this.world.rand.nextFloat());
			                    double d1 = (double)((float)block.getY() + this.world.rand.nextFloat());
			                    double d2 = (double)((float)block.getZ() + this.world.rand.nextFloat());
			                    double d3 = d0 - this.posX;
			                    double d4 = d1 - this.posY;
			                    double d5 = d2 - this.posZ;
			                    double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
			                    d3 = d3 / d6;
			                    d4 = d4 / d6;
			                    d5 = d5 / d6;
			                    double d7 = 0.5D / (d6 / (double)3 + 0.1D);
			                    d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
			                    d3 = d3 * d7;
			                    d4 = d4 * d7;
			                    d5 = d5 * d7;
			                    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.posX) / 2.0D, (d1 + this.posY) / 2.0D, (d2 + this.posZ) / 2.0D, d3, d4, d5, new int[0]);
			                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
					}
				}
			}
		}else{
			for(int x=-1;x<2;x++){
				for(int y=-1;y<2;y++){
					for(int z=-1;z<2;z++){
						BlockPos block=new BlockPos(this.posX+x, this.posY+y, this.posZ+z);
						IBlockState state=world.getBlockState(block);
						if(state.getBlock().getExplosionResistance(world, block, null, boom)<=Config.maxBlastStrength){
							if (state.getMaterial() != Material.AIR)
			                {
			                    if (state.getBlock().canDropFromExplosion(boom))
			                    {
			                        state.getBlock().dropBlockAsItemWithChance(this.world, block, this.world.getBlockState(block), 1.0F, 0);
			                    }
	
			                    state.getBlock().onBlockExploded(this.world, block, boom);
			                }
							//particle
							double d0 = (double)((float)block.getX() + this.world.rand.nextFloat());
		                    double d1 = (double)((float)block.getY() + this.world.rand.nextFloat());
		                    double d2 = (double)((float)block.getZ() + this.world.rand.nextFloat());
		                    double d3 = d0 - this.posX;
		                    double d4 = d1 - this.posY;
		                    double d5 = d2 - this.posZ;
		                    double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
		                    d3 = d3 / d6;
		                    d4 = d4 / d6;
		                    d5 = d5 / d6;
		                    double d7 = 0.5D / (d6 / (double)3 + 0.1D);
		                    d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
		                    d3 = d3 * d7;
		                    d4 = d4 * d7;
		                    d5 = d5 * d7;
		                    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.posX) / 2.0D, (d1 + this.posY) / 2.0D, (d2 + this.posZ) / 2.0D, d3, d4, d5, new int[0]);
		                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
						}
					}
				}
			}
		}
		//entity
		float f3 = Config.explosionDamage * 2.0F;
        int k1 = MathHelper.floor(this.posX+0.5F - (double)f3 - 1.0D);
        int l1 = MathHelper.floor(this.posX+0.5F + (double)f3 + 1.0D);
        int i2 = MathHelper.floor(this.posY+0.5F - (double)f3 - 1.0D);
        int i1 = MathHelper.floor(this.posY+0.5F + (double)f3 + 1.0D);
        int j2 = MathHelper.floor(this.posZ+0.5F - (double)f3 - 1.0D);
        int j1 = MathHelper.floor(this.posZ+0.5F + (double)f3 + 1.0D);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        Vec3d vec3d = new Vec3d(this.posX+0.5F, this.posY+0.5F, this.posZ+0.5F);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity)list.get(k2);

            if (!entity.isImmuneToExplosions()&&!(entity instanceof EntityItem))
            {
                double d12 = entity.getDistance(this.posX+0.5F, this.posY+0.5F, this.posZ+0.5F) / (double)f3;

                if (d12 <= 1.0D)
                {
                    double d5 = entity.posX - this.posX+0.5F;
                    double d7 = entity.posY + (double)entity.getEyeHeight() - this.posY+0.5F;
                    double d9 = entity.posZ - this.posZ+0.5F;
                    double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                    if (d13 != 0.0D)
                    {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double)this.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                        double d10 = (1.0D - d12) * d14;
                        entity.attackEntityFrom(DamageSource.causeExplosionDamage(boom), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                        double d11 = d10;

                        if (entity instanceof EntityLivingBase)
                        {
                            d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                        }

                        entity.motionX += d5 * d11;
                        entity.motionY += d7 * d11;
                        entity.motionZ += d9 * d11;
                    }
                }
            }
        }
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		fuse=compound.getInteger("fuse");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("fuse", fuse);
	}

}
