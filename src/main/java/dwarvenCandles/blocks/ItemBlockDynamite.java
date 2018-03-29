package dwarvenCandles.blocks;

import dwarvenCandles.core.StuffRegistry;
import dwarvenCandles.entity.EntityDynamite;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockDynamite extends ItemBlock{

	public ItemBlockDynamite(Block block) {
		super(block);
		setRegistryName("dynamite");
		setCreativeTab(CreativeTabs.REDSTONE);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity)null))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
                int slot=player.inventory.currentItem;
                int z=Math.max(0, slot-1),k=Math.min(8, slot+1);
                for(int s=z;s<=k;s++){
                	ItemStack stack=player.inventory.getStackInSlot(s);
                	if(stack.getItem()==StuffRegistry.dynamite_remote){
                		StuffRegistry.dynamite_remote.toggleTarget(stack, pos, null);
                	}
                }
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
	}
	
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack held=player.getHeldItem(hand);
		int slot=player.inventory.currentItem;
		int z=Math.max(0, slot-1),k=Math.min(8, slot+1);
		for(int i=z;i<=k;i++){
			ItemStack stack=player.inventory.getStackInSlot(i);
			if(stack.getItem()==Items.FLINT_AND_STEEL){
				if(!world.isRemote){
					if(!player.isCreative()){
						stack.damageItem(1, player);
						held.shrink(1);
					}
					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					EntityDynamite dynamite=new EntityDynamite(world, player);
					dynamite.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.0F, 1.0F);
					world.spawnEntity(dynamite);
				}
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
	}

}
