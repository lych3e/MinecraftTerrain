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
		
		
		double[][] h =
				Noise.getPeaksandValleys(worldInfo.getSeed(), 
				Noise.getContinentalness(worldInfo.getSeed(), chunkX, chunkZ), chunkX, chunkZ);
		//System.out.println("{ \n	"+h[0][0]+"\n	"+h[15][15]+"\n}");
		
		
		
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				chunkData.setRegion(x, worldInfo.getMinHeight(), z, 
						x+1, (int) Math.round(Math.min(h[x+1][z+1], worldInfo.getMaxHeight())), z+1, 
						Material.STONE);
		
		generateCaves(worldInfo, ran, chunkX, chunkZ, chunkData);
	}
	
	
	
	
	static final int sea_level = 60;
	@Override
	public void generateSurface(@Nonnull WorldInfo worldInfo, 
								@Nonnull Random random, 
								int chunkX, int chunkZ, 
								@Nonnull ChunkData chunkData) {
		
		//SimplexOctaveGenerator g = new SimplexOctaveGenerator(worldInfo.getSeed(), 8);
		//g.setScale(0.05);
		
		double[][] elevation = Noise.getPeaksandValleys(worldInfo.getSeed(), 
								Noise.getContinentalness(worldInfo.getSeed(), chunkX, chunkZ), chunkX, chunkZ);
		
		Material[][] underwaterMats = Noise.underwaterGenerator(worldInfo.getSeed(), chunkX*16, chunkZ*16, elevation, sea_level);
		
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				int Y = (int) Math.round(elevation[x+1][z+1]);
				
				if (Y < sea_level) {
						chunkData.setRegion(x, Y+1, z, x+1, sea_level, z+1, Material.WATER); //TODO non-water caves potential conflict
						//TODO does sand vs clay depend on something? water depth maybe?
						int lowest = Y;
						for (int x2 = -1; x2 <= 1; x2++)
							for (int z2 = -1; z2 <= 1; z2++)
								if (elevation[x+x2+1][z+z2+1] < lowest) // get lowest from neibouring blocks so that it populates EXPOSED blocks, rather than blocks directly on the surface
									lowest = (int) Math.round(elevation[x+x2+1][z+z2+1]);
						
						chunkData.setRegion(x, lowest, z, x+1, Y+1, z+1, underwaterMats[x][z]);
				}
				
				else chunkData.setBlock(x, Y, z, Material.GRASS_BLOCK);
			}
	}
	
	
	@Override
	public void generateCaves(@Nonnull WorldInfo worldInfo, 
								@Nonnull Random random, 
								int chunkX, int chunkZ, 
								@Nonnull ChunkData chunkData) {
		
		int[][] h = new int[16][16]; // find min heights
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				h[x][z] = getSurfaceHeight(x, z, chunkData);
		// get a map of cave holes. True for cave. false for no cave
		boolean[][][] caves = Noise.getCaves(worldInfo.getSeed(), worldInfo.getMinHeight(), worldInfo.getMaxHeight(), h, chunkX, chunkZ);
		
		for (int x = 0; x < 16; x++) // apply the caves to the chunkData
			for (int z = 0; z < 16; z++)
				for (int y = 0; y < chunkData.getMaxHeight(); y++)
					if (caves[x][y][z]) chunkData.setBlock(x, y+worldInfo.getMinHeight(), z, Material.AIR);
	}
	
	@Override
	public void generateBedrock(
			@Nonnull WorldInfo worldInfo, 
			@Nonnull Random ran, 
			int chunkX, int chunkZ, 
			@Nonnull ChunkData chunkData) {
		
		
		chunkData.setRegion(0, worldInfo.getMinHeight(), 0, 16, worldInfo.getMinHeight()+1, 16, Material.BEDROCK);
	}
	
	
	
	public int getSurfaceHeight(int x, int z, ChunkData data) {
		for (int y = data.getMaxHeight(); y > data.getMinHeight(); y--)
			if (!data.getType(x, y, z).isAir()) return y;
		return data.getMinHeight()-1;
	}

}
