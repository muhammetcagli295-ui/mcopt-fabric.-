package com.example.mcopt;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class OptConfig {

	public boolean enableTpsAutoTune = true;
	public double lagTickTimeMs = 55.0;
	public double recoverTickTimeMs = 45.0;
	public int laggedRandomTickSpeed = 1;
	public int normalRandomTickSpeed = 3;

	public boolean enableEntityMerging = true;
	public int mergeIntervalTicks = 40;
	public double itemMergeRadius = 1.5;
	public double xpOrbMergeRadius = 1.0;

	private static final String FILE_NAME = "mcopt.properties";

	public static OptConfig loadOrCreate() {
		OptConfig cfg = new OptConfig();
		Path configDir = FabricLoader.getInstance().getConfigDir();
		Path file = configDir.resolve(FILE_NAME);

		Properties props = new Properties();

		if (Files.exists(file)) {
			try (var in = Files.newInputStream(file)) {
				props.load(in);
				cfg.enableTpsAutoTune = Boolean.parseBoolean(props.getProperty("enableTpsAutoTune", String.valueOf(cfg.enableTpsAutoTune)));
				cfg.lagTickTimeMs = Double.parseDouble(props.getProperty("lagTickTimeMs", String.valueOf(cfg.lagTickTimeMs)));
				cfg.recoverTickTimeMs = Double.parseDouble(props.getProperty("recoverTickTimeMs", String.valueOf(cfg.recoverTickTimeMs)));
				cfg.laggedRandomTickSpeed = Integer.parseInt(props.getProperty("laggedRandomTickSpeed", String.valueOf(cfg.laggedRandomTickSpeed)));
				cfg.normalRandomTickSpeed = Integer.parseInt(props.getProperty("normalRandomTickSpeed", String.valueOf(cfg.normalRandomTickSpeed)));
				cfg.enableEntityMerging = Boolean.parseBoolean(props.getProperty("enableEntityMerging", String.valueOf(cfg.enableEntityMerging)));
				cfg.mergeIntervalTicks = Integer.parseInt(props.getProperty("mergeIntervalTicks", String.valueOf(cfg.mergeIntervalTicks)));
				cfg.itemMergeRadius = Double.parseDouble(props.getProperty("itemMergeRadius", String.valueOf(cfg.itemMergeRadius)));
				cfg.xpOrbMergeRadius = Double.parseDouble(props.getProperty("xpOrbMergeRadius", String.valueOf(cfg.xpOrbMergeRadius)));
			} catch (IOException | NumberFormatException e) {
				McOptMod.LOGGER.warn("[McOpt] Config okunamadi, varsayilanlar kullaniliyor: " + e.getMessage());
			}
		} else {
			cfg.save();
		}
		return cfg;
	}

	public void save() {
		Path configDir = FabricLoader.getInstance().getConfigDir();
		Path file = configDir.resolve(FILE_NAME);
		Properties props = new Properties();
		props.setProperty("enableTpsAutoTune", String.valueOf(enableTpsAutoTune));
		props.setProperty("lagTickTimeMs", String.valueOf(lagTickTimeMs));
		props.setProperty("recoverTickTimeMs", String.valueOf(recoverTickTimeMs));
		props.setProperty("laggedRandomTickSpeed", String.valueOf(laggedRandomTickSpeed));
		props.setProperty("normalRandomTickSpeed", String.valueOf(normalRandomTickSpeed));
		props.setProperty("enableEntityMerging", String.valueOf(enableEntityMerging));
		props.setProperty("mergeIntervalTicks", String.valueOf(mergeIntervalTicks));
		props.setProperty("itemMergeRadius", String.valueOf(itemMergeRadius));
		props.setProperty("xpOrbMergeRadius", String.valueOf(xpOrbMergeRadius));

		try {
			Files.createDirectories(configDir);
			try (var out = Files.newOutputStream(file)) {
				props.store(out, "McOpt Configuration");
			}
		} catch (IOException e) {
			McOptMod.LOGGER.warn("[McOpt] Config kaydedilemedi: " + e.getMessage());
		}
	}
}
