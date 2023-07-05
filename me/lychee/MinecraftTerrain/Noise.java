package me.lychee.MinecraftTerrain;

import org.bukkit.util.noise.SimplexOctaveGenerator;

public interface Noise {
	public static final double clusterSize = 3; // smallest biome 
	
	// how far inland a given chunk is
	public static double getContinentalness(long seed, int chunkX, int chunkY) {
		SimplexOctaveGenerator g = new SimplexOctaveGenerator(seed+1, 10);
		g.setScale(0.003D);
	
		//Bukkit.broadcastMessage(""+g.noise(chunkX, chunkY, 1d/16d, 1d));
		
		return g.noise(chunkX, chunkY, 3d, 1/4d)*20;
	}
	
	public static double[][] getPeaksandValleys(long seed, double continentalness, int chunkX, int chunkY) {
		SimplexOctaveGenerator g = new SimplexOctaveGenerator(seed+2, 8);
		g.setScale(0.01D);
		
		double[][] out = new double[16][16];
		
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++) {
				
				out[x][y] = sigmoid(continentalness*g.noise(16*chunkX+x, 16*chunkY+y, 2d, 1/2d))*75d+50d;
			}
		return out;
	}
	
	public static double[][] getHumidity(long seed, int chunkX, int chunkY) {
		return null;
	}
	
	public static double[][] getErosion(long seed, int chunkX, int chunkY) {
		return null;
	}
	
	public static double[][] getTemperature(long seed, int chunkX, int chunkY) {
		return null;
	}
	
	
	public static double sigmoid(double x)
	{
	    return 1 / (1 + Math.exp(-x));
	}
}
