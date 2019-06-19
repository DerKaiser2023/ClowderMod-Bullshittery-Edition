package com.hfr.render.hud;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.hfr.lib.RefStrings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderRadarScreen {

	private static final ResourceLocation base = new ResourceLocation(RefStrings.MODID + ":textures/hud/radarscreen.png");
	private static final ResourceLocation error = new ResourceLocation(RefStrings.MODID + ":textures/hud/radarscreen_altitude.png");
	private static final ResourceLocation combat = new ResourceLocation(RefStrings.MODID + ":textures/hud/radarscreen_zoom.png");
	private static final ResourceLocation blipGreen = new ResourceLocation(RefStrings.MODID + ":textures/hud/blip.png");
	private static final ResourceLocation blipCircle = new ResourceLocation(RefStrings.MODID + ":textures/hud/blipLarge.png");
	private static final ResourceLocation blipX = new ResourceLocation(RefStrings.MODID + ":textures/hud/blipX.png");
	private static final ResourceLocation blipRed = new ResourceLocation(RefStrings.MODID + ":textures/hud/blipRed.png");
	private static final ResourceLocation blipDanger = new ResourceLocation(RefStrings.MODID + ":textures/hud/blipDanger.png");
	private static final ResourceLocation north = new ResourceLocation(RefStrings.MODID + ":textures/hud/north.png");
	private static final ResourceLocation south = new ResourceLocation(RefStrings.MODID + ":textures/hud/south.png");
	private static final ResourceLocation east = new ResourceLocation(RefStrings.MODID + ":textures/hud/east.png");
	private static final ResourceLocation west = new ResourceLocation(RefStrings.MODID + ":textures/hud/west.png");
	
	public static List<Blip> blips = new ArrayList();
	public static boolean sufficient;
	public static float scale = 1.0F;
	
	public static void renderRadar(int offset, int range, boolean zoom) {
		
		Minecraft minecraft = Minecraft.getMinecraft();
		
		int width = minecraft.displayWidth;
		int height = minecraft.displayHeight;
		int size = (int) (height * 0.075 * scale);
		int marginX = 10;
		int marginY = 10 + offset;
		double zLevel = 0;
		float blipSize = size * 0.025F;
		int compassSize = 3;
		float clamp = size * 0.7F;
		float clampScaled = clamp * 0.005F;

		minecraft.getTextureManager().bindTexture(base);
		renderBase(marginX, marginY, size, zLevel);
		renderComapss(marginX, marginY, size, zLevel, compassSize, clampScaled);
		
		if(sufficient) {
			renderBlips(marginX, marginY, size, zLevel, blipSize, clampScaled, range);
		} else {
			minecraft.getTextureManager().bindTexture(error);
			renderBase(marginX, marginY, size, zLevel);
		}
		
		if(zoom) {
			minecraft.getTextureManager().bindTexture(combat);
			renderBase(marginX, marginY, size, zLevel);
		}
        
        minecraft.fontRenderer.drawString("" + blips.size(), marginX, marginY, 0xBBFFBB);
        
        GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	public static void renderBase(int marginX, int marginY, int size, double zLevel) {

		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(marginX + 0, 	marginY + size, zLevel, 0, 1);
        tessellator.addVertexWithUV(marginX + size, marginY + size, zLevel, 1, 1);
        tessellator.addVertexWithUV(marginX + size, marginY + 0, 	zLevel, 1, 0);
        tessellator.addVertexWithUV(marginX + 0, 	marginY + 0, 	zLevel, 0, 0);
        tessellator.draw();
	}
	
	public static void renderBlips(int marginX, int marginY, int size, double zLevel, float blipSize, float clamp, int range) {

		int cX = marginX + size / 2;
		int cY = marginY + size / 2;
		
		clamp = clamp * 75 / range;

		Tessellator tessellator = Tessellator.instance;
		Minecraft minecraft = Minecraft.getMinecraft();
        tessellator.startDrawingQuads();
        
		for(Blip blip : blips) {

			float x = (blip.x * clamp * blip.x * clamp);
			float z = (blip.z * clamp * blip.z * clamp);
			if(Math.sqrt(x + z) > size * 0.35)
				continue;

			switch(blip.type) {
			case 0: minecraft.getTextureManager().bindTexture(blipGreen); break;
			case 1: minecraft.getTextureManager().bindTexture(blipCircle); break;
			case 2: minecraft.getTextureManager().bindTexture(blipX); break;
			case 3: minecraft.getTextureManager().bindTexture(blipRed); break;
			case 4: minecraft.getTextureManager().bindTexture(blipDanger); break;
			}

	        tessellator.addVertexWithUV(cX + (blip.x * clamp) - blipSize, cY + (blip.z * clamp) + blipSize, zLevel, 0, 1);
	        tessellator.addVertexWithUV(cX + (blip.x * clamp) + blipSize, cY + (blip.z * clamp) + blipSize, zLevel, 1, 1);
	        tessellator.addVertexWithUV(cX + (blip.x * clamp) + blipSize, cY + (blip.z * clamp) - blipSize, zLevel, 1, 0);
	        tessellator.addVertexWithUV(cX + (blip.x * clamp) - blipSize, cY + (blip.z * clamp) - blipSize, zLevel, 0, 0);
		}
		
        tessellator.draw();

        float fontScale = 2F / scale;
        
		GL11.glScalef(1 / fontScale, 1 / fontScale, 1 / fontScale);
		for(Blip blip : blips) {

			float x = (blip.x * clamp * blip.x * clamp);
			float z = (blip.z * clamp * blip.z * clamp);
			if(Math.sqrt(x + z) > size * 0.35)
				continue;

	        minecraft.fontRenderer.drawString("" + Math.round(blip.y), (int) (cX * fontScale + (int)(blip.x * clamp * fontScale) - 6), (int) (cY * fontScale + (int)(blip.z * clamp * fontScale) + 4), 0xBBFFBB);
		}
		GL11.glScalef(fontScale, fontScale, fontScale);
		
        GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	public static void renderComapss(int marginX, int marginY, int size, double zLevel, float blipSize, float clamp) {

		int cX = marginX + size / 2;
		int cY = marginY + size / 2;
		
		blipSize *= scale;

		Tessellator tessellator = Tessellator.instance;
		Minecraft minecraft = Minecraft.getMinecraft();
        
		float rotation = (float) ((minecraft.thePlayer.rotationYaw - 90) * Math.PI / 180F);
		Vec3 vec = Vec3.createVectorHelper(clamp * 125, 0, 0);
		vec.rotateAroundZ(rotation);
		
		for(int i = 0; i < 4; i++) {

			if(i == 0)
				minecraft.getTextureManager().bindTexture(north);
			if(i == 1)
				minecraft.getTextureManager().bindTexture(west);
			if(i == 2)
				minecraft.getTextureManager().bindTexture(south);
			if(i == 3)
				minecraft.getTextureManager().bindTexture(east);
			
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(cX + vec.xCoord - blipSize, cY + vec.yCoord + blipSize, zLevel, 0, 1);
	        tessellator.addVertexWithUV(cX + vec.xCoord + blipSize, cY + vec.yCoord + blipSize, zLevel, 1, 1);
	        tessellator.addVertexWithUV(cX + vec.xCoord + blipSize, cY + vec.yCoord - blipSize, zLevel, 1, 0);
	        tessellator.addVertexWithUV(cX + vec.xCoord - blipSize, cY + vec.yCoord - blipSize, zLevel, 0, 0);
	        tessellator.draw();
	
			vec.rotateAroundZ((float) (90 * Math.PI / 180F));
			}
		
        GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	public static class Blip {
		
		public float x;
		public float y;
		public float z;
		public int type;
		
		public Blip(float x, float y, float z, int type) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = type;
		}
	}
	
}
