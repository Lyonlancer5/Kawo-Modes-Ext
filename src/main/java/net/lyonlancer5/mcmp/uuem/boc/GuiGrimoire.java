package net.lyonlancer5.mcmp.uuem.boc;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.lyonlancer5.mcmp.uuem.LL5_UUEntityMode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiGrimoire extends GuiContainer {

	private static final ResourceLocation guiImg = new ResourceLocation(LL5_UUEntityMode.MODID,
			"textures/gui/bookofcaptive.png");
	private int selection;
	private Entity drawEntity;

	public GuiGrimoire(InventoryPlayer pInventoryPlayer, ItemStack pItemStackBook) {
		super(new ContainerGrimoire(pInventoryPlayer, pItemStackBook));
		ySize = 221;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(guiImg);
		int lx = (width - xSize) / 2;
		int ly = (height - ySize) / 2;
		this.drawTexturedModalRect(lx, ly, 0, 0, xSize, ySize);

		ItemStack litemstack = inventorySlots.getSlot(selection).getStack();
		if (drawEntity != null && litemstack != null) {
			drawSelectEntity(width / 2F - 45F, height / 2F - 35F, 30F, 30F, 0F);
			drawString(fontRendererObj, String.format("HP: %.1f", LL5_UUEntityMode.getSplendorHealth(litemstack)),
					width / 2 - 75, height / 2 - 30, 0xffffff);
			String ls = String.format("/ %.1f", LL5_UUEntityMode.getSplendorMaxHealth(litemstack));
			drawString(fontRendererObj, ls, width / 2 - 10 - fontRendererObj.getStringWidth(ls), height / 2 - 20,
					0xffffff);
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		for (int li = 0; li < 9; li++) {
			SlotGrimoire lslot = (SlotGrimoire) inventorySlots.inventorySlots.get(li);
			if (func_146978_c(lslot.xDisplayPosition, lslot.yDisplayPosition, 16, 16, par1, par2)) {
				if (selection != lslot.slotNumber) {
					selection = lslot.slotNumber;
					drawEntity = LL5_UUEntityMode.convertCardToEntity(lslot.getStack(), mc.theWorld);
					if (drawEntity == null)
						System.out.println("warn-drawnull!");
				}
			}
		}
	}

	protected void drawSelectEntity(float pX, float pY, float pZoom, float pYaw, float pPitch) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(pX, pY, 50.0F);
		GL11.glScalef(-pZoom, pZoom, pZoom);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float) Math.atan((double) (pPitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		drawEntity.rotationYaw = (float) Math.atan((double) (pYaw / 40.0F)) * 40.0F;
		drawEntity.rotationPitch = -((float) Math.atan((double) (pPitch / 40.0F))) * 20.0F;
		if (drawEntity instanceof EntityLivingBase) {
			EntityLivingBase lentity = (EntityLivingBase) drawEntity;
			lentity.renderYawOffset = (float) Math.atan((double) (pYaw / 40.0F)) * 20.0F;
			lentity.rotationYawHead = drawEntity.rotationYaw;
			lentity.prevRotationYawHead = drawEntity.rotationYaw;
		}
		GL11.glTranslatef(0.0F, drawEntity.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(drawEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

	}

}
