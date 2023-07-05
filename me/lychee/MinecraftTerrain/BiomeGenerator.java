package me.lychee.MinecraftTerrain;

import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

public class BiomeGenerator extends BiomeProvider {
	//TODO this
	@Override
	public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
		// TODO Auto-generated method stub
		return Biome.THE_VOID;
	}

	@Override
	public List<Biome> getBiomes(WorldInfo worldInfo) {
		// TODO Auto-generated method stub
		return Arrays.asList(Biome.THE_VOID);
	}

}
