package me.lychee.MinecraftTerrain;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public interface Noise {
	//TODO make a function for finding how far away from water something is. This differs from continentalness due to rivers. This is useful because water caves are less likely to be above sea level, the closer you are to sea level water, such as (kinda) rivers & the sea
	//Aquifers frfr. Deep air-filled caves are impossible IRL, except for pressurised chambers. Some concessions should be made here for gameplay purposes.
	
	// how far inland a given chunk is
	// also generates 1 block outside each chunk as hydraulic erosion needs that to be seamless
	// but doesn't have to start at -1 as long as I'm consistent
	public static double[][] getContinentalness(long seed, int chunkX, int chunkY) {
		SimplexOctaveGenerator g = new SimplexOctaveGenerator(seed+1, 10);
		g.setScale(0.002D);
		
		double[][] out = new double[18][18];
		
		for (int x = 0; x < 18; x++)
			for (int y = 0; y < 18; y++)
				out[x][y] = g.noise(chunkX*16+x, chunkY*16+y, 3d, 1/16d)*30;
		
		return out;
	}
	
	// also generates 1 block outside each chunk as hydraulic erosion needs that to be seamless
	// but doesn't have to start at -1 as long as I'm consistent
	public static double[][] getPeaksandValleys(long seed, double[][] continentalness, int chunkX, int chunkY) {
		SimplexOctaveGenerator g = new SimplexOctaveGenerator(seed+2, 8);
		g.setScale(0.004D);
		
		double[][] out = new double[18][18];
		
		for (int x = 0; x < 18; x++)
			for (int y = 0; y < 18; y++) {
				
				out[x][y] = continentalness[x][y]*sigmoid(g.noise(16*chunkX+x, 16*chunkY+y, 3d, 1/13d, true)*10)*3+64d;
			}
		return out;
	}
	
	
	public static double[][] getHumidity(long seed, int chunkX, int chunkY) {
		SimplexOctaveGenerator g = new SimplexOctaveGenerator(seed+3, 8);
		g.setScale(0.003D);
		
		double[][] out = new double[18][18];
		
		for (int x = 0; x < 18; x++)
			for (int y = 0; y < 18; y++) {
				
				out[x][y] = g.noise(16*chunkX+x-1, 16*chunkY+y-1, 32d, 1/128d, true)*2;
			}
		
		return out;
	}
	
	public static Material underwaterGenerator(SimplexOctaveGenerator g, int x, int y) {
		double i = ((g.noise(x, y, 5, 1/2, true)+1)/2); //TODO wavelet noise might be better here
		if (i > 2d/3d) return Material.CLAY;
		else if (i > 1d/3d) return Material.GRAVEL;
		else return Material.SAND;
	}
	
	// Attempt at hydraulic erosion. Doesn't work properly, don't use
	//TODO include humidity once it's implemented
	// this isn't noise but it has a similar vibe so it makes more sense to implement it here IMO
	public static double[][] getHydraulicErosion(long seed, double[][] PV) {
		double[][] out = new double[18][18];
		
		System.arraycopy(PV, 0, out, 0, 18); // copy initial terrain height into output
		
		Random ran = new Random();
		
		for (int i = 0; i < 64; i++) {// TODO better sampling method (other than true random)
			int x = ran.nextInt(16) + 1;
			int y = ran.nextInt(16) + 1;
			
			erosion(ran, out, 10, x, y);
		}
			
		
		return out;
	}
	
	private static void erosion(Random ran, double[][] out, int i, int x, int y) {
		erosion(ran, out, i, x, y, new double[] {0, 0}, 0);
	}
	// velocity doesn't actually determine how fast a water droplet travels, except if flowing uphill
	private static void erosion(Random ran, double[][] out, int i, int X, int Y, double[] sqrVelocity, double mass) {
		double lowest = Integer.MAX_VALUE;
		int lowX = 0, lowY = 0;
		
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				if (lowest > out[X+x-1][Y+y-1]) {
					lowest = out[X+x-1][Y+y-1];
					lowX = x; lowY = y;
				}
		
		
		// if flowing uphill
		if (lowX == 1 && lowY == 1) {
			//TODO: erosion continuation if local minima is not chunk minima
			smooth(out, mass, X, Y);
			return;
		}
		
		
		double dH = out[X][Y] - lowest; // absolute change in height
		
		double critical_mass = 1 / (20-i) + 1;
		
		// if mass > critical_mass, rH will be negative, hence this handles the case where mass > critical_mass
		double rH = lerp(critical_mass - mass, dH, 0.2); // removed height
		
		sqrVelocity[0] += 2 * lowX * dH;
		sqrVelocity[1] += 2 * lowY * dH;
		
		out[X+lowX-1][Y+lowY-1] -= rH;
		
		mass += rH; // Mass = volume * density; volume = 1*1*dH, hence mass is directly proportional to height in this instance.
		
		// if it should flow onto the next chunk (which it can't do, or else there'll be an infinite loop of chunk generation)
		if (X+lowX-1 == 17 || X+lowX-1 == 0 || Y+lowY-1 == 17 || Y+lowY-1 == 0) return;
		
		if (i == 0) {
			smooth(out, mass, X, Y);
			return;
		}
		
		erosion(ran, out, i-1, X+lowX-1, Y+lowY-1, sqrVelocity, mass);
	}
	
	private static void smooth(double[][] out, double mass, int X, int Y) {
		// add mass in small packets to the local minima, check if the local minima has changed, repeat until dH has been expended
		do {
			int lX = 0, lY = 0;
			for (int x = 0; x < 3; x++)
				for (int y = 0; y < 3; y++)
					if (out[x][y] < out[lX][lY])
					{lX = x; lY = y;} // finds local minima
			
			out[lX][lY] += 1;
			mass -= 1;
		} while(mass > 0.2);
		out[X][Y] += mass;
	}
	
	public static double[][] getErosion(long seed, int chunkX, int chunkY) {
		return null;
	}
	
	public static double[][] getTemperature(long seed, int chunkX, int chunkY) {
		return null;
	}
	
	public static void normalise(double[] sqrVec2) {
		double mag = Math.sqrt(sqrVec2[0] + sqrVec2[1]);
		
		sqrVec2[0] /= mag;
		sqrVec2[1] /= mag;
	}
	
	public static double lerp(double A, double B, double d) {
		double delta = B-A;
		return A+delta*d;
	}
	
	public static double sigmoid(double x)
	{
	    return 1 / (1 + Math.exp(-x));
	}
}
