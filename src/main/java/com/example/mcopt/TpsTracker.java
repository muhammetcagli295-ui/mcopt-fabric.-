package com.example.mcopt;

public class TpsTracker {

	private static final int WINDOW_SIZE = 100;
	private final double[] samples = new double[WINDOW_SIZE];
	private int index = 0;
	private int filled = 0;
	private long lastTickStart = -1;

	public void onTickStart() {
		long now = System.nanoTime();
		if (lastTickStart >= 0) {
			double elapsedMs = (now - lastTickStart) / 1_000_000.0;
			samples[index] = elapsedMs;
			index = (index + 1) % WINDOW_SIZE;
			if (filled < WINDOW_SIZE) filled++;
		}
		lastTickStart = now;
	}

	public double getAverageTickTimeMs() {
		if (filled == 0) return 50.0;
		double sum = 0;
		for (int i = 0; i < filled; i++) sum += samples[i];
		return sum / filled;
	}

	public double getEstimatedTps() {
		double avg = getAverageTickTimeMs();
		if (avg <= 50.0) return 20.0;
		return 1000.0 / avg;
	}
}
