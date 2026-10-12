package com.example.crosshairicons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrosshairIconsClient implements ClientModInitializer {
	public static final String MOD_ID = "crosshairicons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Identifier ELYTRA_ICON = Identifier.of(MOD_ID, "textures/gui/elytra.png");
	private static final Identifier CHESTPLATE_ICON = Identifier.of(MOD_ID, "textures/gui/chestplate.png");

	@Override
	public void onInitializeClient() {
		ModConfig.load();

		// Малюємо одразу після прицілу
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.CROSSHAIR,
				Identifier.of(MOD_ID, "chest_icon"),
				CrosshairIconsClient::renderIcon
		);
	}

	private static void renderIcon(DrawContext context, RenderTickCounter tickCounter) {
		ModConfig cfg = ModConfig.INSTANCE;
		if (!cfg.enabled) return;

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden) return;
		if (!client.options.getPerspective().isFirstPerson()) return;

		ItemStack chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
		if (chest.isEmpty()) return;

		Identifier texture;
		if (chest.isOf(Items.ELYTRA)) {
			// Елітри теж мають слот CHEST, тому перевіряємо їх першими
			if (!cfg.showElytra) return;
			texture = ELYTRA_ICON;
		} else if (isChestplate(chest)) {
			if (!cfg.showChestplate) return;
			texture = CHESTPLATE_ICON;
		} else {
			return;
		}

		int size = cfg.size;
		int x = context.getScaledWindowWidth() / 2 - size / 2;
		int y = context.getScaledWindowHeight() / 2 + cfg.offsetY;

		int alpha = Math.round(255f * cfg.opacity / 100f);
		int color = ColorHelper.withAlpha(alpha, 0xFFFFFFFF);

		// Текстура 16x16 масштабується до size x size
		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0f, 0f, size, size, 16, 16, 16, 16, color);
	}

	private static boolean isChestplate(ItemStack stack) {
		EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
		return equippable != null && equippable.slot() == EquipmentSlot.CHEST;
	}
}
