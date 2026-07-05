package com.example.mcopt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McOptMod implements ModInitializer {

	public static final String MOD_ID = "mcopt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private OptConfig config;
	private final TpsTracker tpsTracker = new TpsTracker();

	private boolean currentlyLagging = false;
	private long tickCounter = 0;

	@Override
	public void onInitialize() {
		LOGGER.info("[McOpt] Yukleniyor...");
		config = OptConfig.loadOrCreate();

		ServerTickEvents.START_SERVER_TICK.register(this::onStartTick);
		ServerTickEvents.END_SERVER_TICK.register(this::onEndTick);

		registerCommands();

		LOGGER.info("[McOpt] Hazir. TPS otomatik ayar: {}, Entity birlestirme: {}",
				config.enableTpsAutoTune, config.enableEntityMerging);
	}

	private void onStartTick(MinecraftServer server) {
		tpsTracker.onTickStart();
	}

	private void onEndTick(MinecraftServer server) {
		tickCounter++;

		if (config.enableTpsAutoTune) {
			handleAutoTune(server);
		}

		if (config.enableEntityMerging && tickCounter % config.mergeIntervalTicks == 0) {
			for (ServerWorld world : server.getWorlds()) {
				EntityMerger.mergeItemsAndOrbs(world, config);
			}
		}
	}

	private void handleAutoTune(MinecraftServer server) {
		double avgMs = tpsTracker.getAverageTickTimeMs();

		if (!currentlyLagging && av
