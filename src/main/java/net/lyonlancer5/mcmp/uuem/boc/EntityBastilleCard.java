package net.lyonlancer5.mcmp.uuem.boc;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import io.netty.buffer.ByteBuf;
import net.lyonlancer5.mcmp.uuem.LL5_UUEntityMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityBastilleCard extends EntityThrowable implements IThrowableEntity, IEntityAdditionalSpawnData {

	ItemStack cardItem = null;
	boolean isSplendor = false;


	public EntityBastilleCard(World par1World) {
		super(par1World);
	}

	public EntityBastilleCard(World par1World, EntityLivingBase par2EntityLivingBase) {
		super(par1World, par2EntityLivingBase);
	}

	public EntityBastilleCard(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}


	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (!this.worldObj.isRemote) {
			if (LL5_UUEntityMode.isSplendor(cardItem)) {
				// 中身が詰まっていれば開放する
				Entity lentity = LL5_UUEntityMode.convertCardToEntity(cardItem, worldObj);
				if (lentity != null) {
					lentity.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
					
					// TODO: Not preserved across login-logout
					//if(getThrower() != null && lentity instanceof EntityLiving){
					//	for(Object o : ((EntityLiving) lentity).targetTasks.taskEntries){
					//		EntityAITaskEntry e = (EntityAITaskEntry)o;
					//		if(e.action instanceof EntityAINearestAttackableTarget){
					//			e.action = EntityAINearestAttackableTargetExcept.from((EntityAINearestAttackableTarget) e.action, getThrower().getCommandSenderName());
					//		}
					//	}
					//}
					
					worldObj.spawnEntityInWorld(lentity);
					LL5_UUEntityMode.showEffect(this);
				}
			} else {
				// 空カードもドロップするので別記述
				ItemStack litemstack = new ItemStack(LL5_UUEntityMode.CARD_ITEM);
				if (movingobjectposition.typeOfHit == MovingObjectType.ENTITY) {
					if(movingobjectposition.entityHit instanceof EntityPlayer){
						entityDropItem(litemstack, 0F);
						setDead();
						return;
					}
					litemstack = LL5_UUEntityMode.convertEntityToCard(movingobjectposition.entityHit);
					LL5_UUEntityMode.showEffect(movingobjectposition.entityHit);
					movingobjectposition.entityHit.setDead();
				}
				entityDropItem(litemstack, 0F);
			}
			setDead();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readEntityFromNBT(par1nbtTagCompound);
		if (par1nbtTagCompound.hasKey("Splendor")) {
			// カードの中身を読出
			NBTTagCompound ltag = par1nbtTagCompound.getCompoundTag("Splendor");
			cardItem = ItemStack.loadItemStackFromNBT(ltag);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeEntityToNBT(par1nbtTagCompound);
		if (LL5_UUEntityMode.isSplendor(cardItem)) {
			// カードの中身を記録
			NBTTagCompound ltag = new NBTTagCompound();
			par1nbtTagCompound.setTag("Splendor", ltag);
			cardItem.writeToNBT(ltag);
		}
	}

	@Override
	public void setThrower(Entity entity) {
		// 特に指定はない
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeBoolean(LL5_UUEntityMode.isSplendor(cardItem));
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		isSplendor = data.readBoolean();
	}


	public void setCardItem(ItemStack pItem) {
		cardItem = pItem;
		isSplendor = LL5_UUEntityMode.isSplendor(cardItem);
	}

}
