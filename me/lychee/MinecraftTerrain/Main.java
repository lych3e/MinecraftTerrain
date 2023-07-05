package me.lychee.MinecraftTerrain;

import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	// @Override public void onEnable() {}
	
	ChunkGenerator generator = new TerrainGenerator();
	BiomeProvider biomes = new BiomeGenerator();
	
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		switch(worldName) {
		case "world":
			return generator;
		default: return null;
		}
	}
	
	@Override
	public BiomeProvider getDefaultBiomeProvider(String worldName, String id) {
		switch(worldName) {
		case "world":
			return biomes;
		default: return null;
		}
	}
}
