package me.lychee.MinecraftTerrain;

import java.util.Random;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class TerrainGenerator extends ChunkGenerator {
	
	
	@Override
	public void generateNoise(
			@Nonnull WorldInfo worldInfo, 
			@Nonnull Random ran, 
			int chunkX, int chunkZ, 
			@Nonnull ChunkData chunkData) {
		
		
		double[][] h = Noise.getPeaksandValleys(worldInfo.getSeed(), 
				Noise.getContinentalness(worldInfo.getSeed(), chunkX, chunkZ),
				chunkX, chunkZ);
		//System.out.println("{ \n	"+h[0][0]+"\n	"+h[15][15]+"\n}");
		
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				for (int y = worldInfo.getMinHeight(); y < Math.min(h[x][z], worldInfo.getMaxHeight()); y++)
					chunkData.setBlock(x, y, z, Material.STONE);
		
	}

}
