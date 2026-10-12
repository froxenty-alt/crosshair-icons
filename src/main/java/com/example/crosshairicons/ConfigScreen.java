package com.example.crosshairicons;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigScreen extends Screen {
	private final Screen parent;

	public ConfigScreen(Screen parent) {
		super(Text.translatable("crosshairicons.config.title"));
		this.parent = parent;
	}

	private static Text onOff(String key, boolean value) {
		return Text.translatable("crosshairicons.option." + key)
				.append(": ")
				.append(value
						? Text.translatable("options.on").formatted(Formatting.GREEN)
						: Text.translatable("options.off").formatted(Formatting.RED));
	}

	private static Text opacityText() {
		return Text.translatable("crosshairicons.option.opacity")
				.append(": " + ModConfig.INSTANCE.opacity + "%");
	}

	private static Text sizeText() {
		String key = switch (ModConfig.INSTANCE.size) {
			case 12 -> "medium";
			case 16 -> "large";
			default -> "small";
		};
		return Text.translatable("crosshairicons.option.size")
				.append(": ")
				.append(Text.translatable("crosshairicons.size." + key));
	}

	private static Text offsetText() {
		return Text.translatable("crosshairicons.option.offset")
				.append(": " + ModConfig.INSTANCE.offsetY);
	}

	@Override
	protected void init() {
		ModConfig cfg = ModConfig.INSTANCE;
		int w = 220;
		int x = this.width / 2 - w / 2;
		int y = this.height / 4 - 10;
		int step = 24;

		addDrawableChild(ButtonWidget.builder(onOff("enabled", cfg.enabled), b -> {
			cfg.enabled = !cfg.enabled;
			b.setMessage(onOff("enabled", cfg.enabled));
		}).dimensions(x, y, w, 20).build());

		addDrawableChild(ButtonWidget.builder(onOff("elytra", cfg.showElytra), b -> {
			cfg.showElytra = !cfg.showElytra;
			b.setMessage(onOff("elytra", cfg.showElytra));
		}).dimensions(x, y + step, w, 20).build());

		addDrawableChild(ButtonWidget.builder(onOff("chestplate", cfg.showChestplate), b -> {
			cfg.showChestplate = !cfg.showChestplate;
			b.setMessage(onOff("chestplate", cfg.showChestplate));
		}).dimensions(x, y + step * 2, w, 20).build());

		addDrawableChild(ButtonWidget.builder(opacityText(), b -> {
			cfg.opacity = cfg.opacity >= 100 ? 25 : cfg.opacity + 25;
			b.setMessage(opacityText());
		}).dimensions(x, y + step * 3, w, 20).build());

		addDrawableChild(ButtonWidget.builder(sizeText(), b -> {
			cfg.size = switch (cfg.size) {
				case 8 -> 12;
				case 12 -> 16;
				default -> 8;
			};
			b.setMessage(sizeText());
		}).dimensions(x, y + step * 4, w, 20).build());

		addDrawableChild(ButtonWidget.builder(offsetText(), b -> {
			cfg.offsetY = cfg.offsetY >= 24 ? 8 : cfg.offsetY + 4;
			b.setMessage(offsetText());
		}).dimensions(x, y + step * 5, w, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), b -> close())
				.dimensions(x, y + step * 6 + 6, w, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFFFF);
	}

	@Override
	public void close() {
		ModConfig.save();
		if (this.client != null) {
			this.client.setScreen(parent);
		}
	}
}
