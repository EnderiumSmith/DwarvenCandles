package dwarvenCandles.blocks;

import java.util.Random;

import dwarvenCandles.entity.EntityDynamite;
import dwarvenCandles.tile.TileDynamite;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDynamite extends Block implements ITileEntityProvider{

	public static final PropertyDirection FACING=PropertyDirection.create("facing");
	public static final PropertyBool PRIMED=PropertyBool.create("primed");
	
	protected static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D);
	protected static final AxisAlignedBB HANGING_AABB=new AxisAlignedBB(0.4000000059604645D, 1D-0.6000000238418579D, 0.4000000059604645D, 0.6000000238418579D, 1D, 0.6000000238418579D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3499999940395355D, 0.20000000298023224D, 0.699999988079071D, 0.6499999761581421D, 0.800000011920929D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3499999940395355D, 0.20000000298023224D, 0.0D, 0.6499999761581421D, 0.800000011920929D, 0.30000001192092896D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.699999988079071D, 0.20000000298023224D, 0.3499999940395355D, 1.0D, 0.800000011920929D, 0.6499999761581421D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.20000000298023224D, 0.3499999940395355D, 0.30000001192092896D, 0.800000011920929D, 0.6499999761581421D);
	
	public BlockDynamite() {
		super(Material.CIRCUITS);
		setRegistryName("dynamite");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabs.REDSTONE);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(PRIMED, false));
		setHardness(0);
		setResistance(0);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta%6)).withProperty(PRIMED, meta>5);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex()+(state.getValue(PRIMED)?6:0);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING,PRIMED});
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch(state.getValue(FACING)){
		case DOWN:
			return HANGING_AABB;
		case EAST:
			return EAST_AABB;
		case NORTH:
			return NORTH_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case UP:
			return STANDING_AABB;
		case WEST:
			return WEST_AABB;
		default:
			return STANDING_AABB;
		
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
	    return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		EnumFacing side=facing;
		if(world.isSideSolid(pos.offset(facing.getOpposite()), facing)){
			side=facing;
		}else{
			for(EnumFacing face:EnumFacing.VALUES){
				BlockPos blockpos = pos.offset(face.getOpposite());
				if(world.isSideSolid(blockpos, face, false)){
					side=face;
				}
			}
		}
		if(world.isBlockPowered(pos)){
			world.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return getDefaultState().withProperty(FACING, side).withProperty(PRIMED, true);
		}else
			return getDefaultState().withProperty(FACING, side);
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		for(EnumFacing facing:EnumFacing.VALUES){
			BlockPos blockpos = pos.offset(facing);
			if(world.isSideSolid(blockpos, facing.getOpposite(), false)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!(worldIn.isSideSolid(pos.offset(state.getValue(FACING).getOpposite()), state.getValue(FACING), false))){
			worldIn.scheduleUpdate(pos, this, 1);
		}else{
			if(state.getValue(PRIMED)==false&&worldIn.isBlockPowered(pos)){
				worldIn.setBlockState(pos, state.withProperty(PRIMED, true));
				worldIn.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		state.getBlock().dropBlockAsItemWithChance(worldIn, pos, state, 1.0F, 0);
		worldIn.setBlockToAir(pos);
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {
		return false;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
		worldIn.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		EntityDynamite dynamite=new EntityDynamite(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
		dynamite.fuse=0;
		worldIn.spawnEntity(dynamite);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.getHeldItem(hand).getItem()==Items.FLINT_AND_STEEL){
			if(state.getValue(PRIMED)==false){
				if(!worldIn.isRemote){
					worldIn.setBlockState(pos, state.withProperty(PRIMED, true));
					worldIn.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					playerIn.getHeldItem(hand).damageItem(1, playerIn);
					return true;
				}else
					return true;
			}
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(meta>5)
			return new TileDynamite();
		else
			return null;
	}

}
