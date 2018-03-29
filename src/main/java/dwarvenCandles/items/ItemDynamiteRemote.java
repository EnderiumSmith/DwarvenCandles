package dwarvenCandles.items;

import dwarvenCandles.blocks.BlockDynamite;
import dwarvenCandles.core.StuffRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemDynamiteRemote extends Item{
	
	public ItemDynamiteRemote() {
		super();
		setRegistryName("dynamite_remote");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabs.REDSTONE);
		setMaxStackSize(1);
	}
	
	public void toggleTarget(ItemStack stack, BlockPos pos, EntityPlayer player){
		if(stack.getTagCompound()==null)
			stack.setTagCompound(new NBTTagCompound());
		if(!stack.getTagCompound().hasKey("targets")){
			stack.getTagCompound().setIntArray("targets", new int[0]);
		}
		int[] targets=stack.getTagCompound().getIntArray("targets");
		for(int i=0;i<targets.length;i+=3){
			if(pos.getX()==targets[i]&&pos.getY()==targets[i+1]&&pos.getZ()==targets[i+2]){
				int[] newTargets=new int[targets.length-3];
				int z=0;
				for(int y=0;y<targets.length;y++){
					if(y-i<0||y-i>2){
						newTargets[z]=targets[y];
						z++;
					}
				}
				stack.getTagCompound().setIntArray("targets", newTargets);
				if(player!=null){
					player.sendMessage(new TextComponentString("Removed Candle at "+pos.toString()));
				}
				return;
			}
		}
		int[] newTargets=new int[targets.length+3];
		for(int i=0;i<targets.length;i++){
			newTargets[i]=targets[i];
		}
		newTargets[targets.length]=pos.getX();
		newTargets[targets.length+1]=pos.getY();
		newTargets[targets.length+2]=pos.getZ();
		if(player!=null){
			player.sendMessage(new TextComponentString("Added Candle at "+pos.toString()));
		}
		stack.getTagCompound().setIntArray("targets", newTargets);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state=worldIn.getBlockState(pos);
		if(state.getBlock()==StuffRegistry.dynamite_block){
			if(!worldIn.isRemote)
				toggleTarget(player.getHeldItem(hand), pos, player);
			return EnumActionResult.SUCCESS;
		}else{
			return EnumActionResult.PASS;
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack=playerIn.getHeldItem(handIn);
		if(stack.getTagCompound()!=null){
			if(stack.getTagCompound().hasKey("targets")){
				worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
				int[] targets=stack.getTagCompound().getIntArray("targets");
				for(int i=0;i<targets.length;i+=3){
					IBlockState state=worldIn.getBlockState(new BlockPos(targets[i], targets[i+1], targets[i+2]));
					if(state.getBlock()==StuffRegistry.dynamite_block&&state.getValue(BlockDynamite.PRIMED)==false){
						worldIn.setBlockState(new BlockPos(targets[i], targets[i+1], targets[i+2]), state.withProperty(BlockDynamite.PRIMED, true));
						worldIn.playSound(playerIn, targets[i], targets[i+1], targets[i+2], SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
				}
				stack.getTagCompound().setIntArray("targets", new int[0]);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}
	
}
