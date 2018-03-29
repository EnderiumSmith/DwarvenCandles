package dwarvenCandles.tile;

import java.util.List;

import dwarvenCandles.blocks.BlockDynamite;
import dwarvenCandles.core.Config;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class TileDynamite extends TileEntity implements ITickable{
	
	public int fuse;
	
	public TileDynamite() {
		fuse=80;
	}

	@Override
	public void update() {
		fuse--;
		smoke();
		if(fuse<0){
			explode();
		}
		
	}
	
	public void explode(){
		world.setBlockToAir(pos);
		Explosion boom=new Explosion(world, null, pos.getX(), pos.getY(), pos.getZ(), 3, false, false);
		world.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
		world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.getX(), pos.getY(), pos.getZ(), 1.0D, 0.0D, 0.0D, new int[0]);
		for(int x=-1;x<2;x++){
			for(int y=-1;y<2;y++){
				for(int z=-1;z<2;z++){
					BlockPos block=new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
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
	                    double d3 = d0 - pos.getX();
	                    double d4 = d1 - pos.getY();
	                    double d5 = d2 - pos.getZ();
	                    double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
	                    d3 = d3 / d6;
	                    d4 = d4 / d6;
	                    d5 = d5 / d6;
	                    double d7 = 0.5D / (d6 / (double)3 + 0.1D);
	                    d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
	                    d3 = d3 * d7;
	                    d4 = d4 * d7;
	                    d5 = d5 * d7;
	                    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + pos.getX()) / 2.0D, (d1 + pos.getY()) / 2.0D, (d2 + pos.getZ()) / 2.0D, d3, d4, d5, new int[0]);
	                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
					}
				}
			}
		}
		//entity
		float f3 = Config.explosionDamage * 2.0F;
        int k1 = MathHelper.floor(pos.getX()+0.5F - (double)f3 - 1.0D);
        int l1 = MathHelper.floor(pos.getX()+0.5F + (double)f3 + 1.0D);
        int i2 = MathHelper.floor(pos.getY()+0.5F - (double)f3 - 1.0D);
        int i1 = MathHelper.floor(pos.getY()+0.5F + (double)f3 + 1.0D);
        int j2 = MathHelper.floor(pos.getZ()+0.5F - (double)f3 - 1.0D);
        int j1 = MathHelper.floor(pos.getZ()+0.5F + (double)f3 + 1.0D);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        Vec3d vec3d = new Vec3d(pos.getX()+0.5F, pos.getY()+0.5F, pos.getZ()+0.5F);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity)list.get(k2);

            if (!entity.isImmuneToExplosions()&&!(entity instanceof EntityItem))
            {
                double d12 = entity.getDistance(pos.getX()+0.5F, pos.getY()+0.5F, pos.getZ()+0.5F) / (double)f3;

                if (d12 <= 1.0D)
                {
                    double d5 = entity.posX - pos.getX()+0.5F;
                    double d7 = entity.posY + (double)entity.getEyeHeight() - pos.getY()+0.5F;
                    double d9 = entity.posZ - pos.getZ()+0.5F;
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
	
	public void smoke(){
		IBlockState state=world.getBlockState(pos);
		 EnumFacing enumfacing = (EnumFacing)state.getValue(BlockDynamite.FACING);
	        double d0 = (double)pos.getX() + 0.5D;
	        double d1 = (double)pos.getY() + 0.7D;
	        double d2 = (double)pos.getZ() + 0.5D;
	        //double d3 = 0.22D;
	        //double d4 = 0.27D;
	        double d5 = (double)pos.getY() + 0.3D;

	        if (enumfacing.getAxis().isHorizontal())
	        {
	            EnumFacing enumfacing1 = enumfacing.getOpposite();
	            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.27D * (double)enumfacing1.getFrontOffsetX(), d1 + 0.22D, d2 + 0.27D * (double)enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
	        }
	        else if(enumfacing==EnumFacing.UP)
	        {
	            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
	        }else{
	        	world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d5, d2, 0.0D, 0.0D, 0.0D, new int[0]);
	        }
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("fuse", fuse);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		fuse=compound.getInteger("fuse");
	}
	
}
