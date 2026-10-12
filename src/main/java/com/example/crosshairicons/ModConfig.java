package com.example.crosshairicons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("crosshairicons.json");

	public static ModConfig INSTANCE = new ModConfig();

	public boolean enabled = true;
	public boolean showElytra = true;
	public boolean showChestplate = true;
	/** Непрозорість у відсотках (25, 50, 75, 100). */
	public int opacity = 50;
	/** Розмір іконки на екрані в пікселях GUI (8, 12 або 16). */
	public int size = 8;
	/** Відступ іконки вниз від центру екрана. */
	public int offsetY = 12;

	public static void load() {
		try {
			if (Files.exists(PATH)) {
				ModConfig loaded = GSON.fromJson(Files.readString(PATH), ModConfig.class);
				if (loaded != null) INSTANCE = loaded;
			} else {
				save();
			}
		} catch (Exception e) {
			CrosshairIconsClient.LOGGER.error("Не вдалося завантажити конфіг, використовую стандартний", e);
			INSTANCE = new ModConfig();
		}
	}

	public static void save() {
		try {
			Files.writeString(PATH, GSON.toJson(INSTANCE));
		} catch (IOException e) {
			CrosshairIconsClient.LOGGER.error("Не вдалося зберегти конфіг", e);
		}
	}
}
