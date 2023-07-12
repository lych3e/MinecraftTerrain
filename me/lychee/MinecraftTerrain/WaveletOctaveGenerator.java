package me.lychee.MinecraftTerrain;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public interface WaveletOctaveGenerator {
	public static double[][] noise(long seed, int octaves, int x, int y, int n, double persistence) {
		Random r = new Random(seed);
		int offsetX = r.nextInt(16384) - 8192; // so that when the noises from each axis are combined they don't produce the same results each
		int offsetY = r.nextInt(16384) - 8192;
		
		long seedX = seed, seedY = seed;
		// Go to the relevant location in the noise
		for (int i = 0; i < Math.abs(x + offsetX); i++) // seed change per coordinate
			for (int Q = 0; Q < octaves; Q++) {// seed change per coordinate per octave
				seedX = getNextSeed(seedX); // change the seed
			}
		for (int i = 0; i < Math.abs(y + offsetY); i++) // seed change per coordinate
			for (int Q = 0; Q < octaves; Q++) // seed change per coordinate per octave
				seedY = getNextSeed(seedY); // change the seed
		
		double[][] out = new double[n][n];
		
		Random ranX = new Random(seedX);
		Random ranY = new Random(seedY);
		
		double dS = ((double) n) / octaves; // change in scale per octave
		double P = 1;
		
		
		for (int i = 1; i <= octaves; i++) {
			int size = (int) Math.round(dS * i);
			BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB); //initialise the image TODO do something faster & smarter than using bufferedimage for 1D upscaling
			P *= persistence;
			
			for (int X = 0; X < dS * i; X++)
				for (int Y = 0; Y < dS * i; Y++) {
					int A = (int) Math.round(255d * (ranX.nextDouble() + ranY.nextDouble()) / (2d));
					
					img.setRGB(X, Y, new Color(A, A, A).getRGB());
				}
			
			BufferedImage img2 = new BufferedImage(n, n, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img2.createGraphics();
			g.drawImage(img.getScaledInstance(n, n, BufferedImage.SCALE_SMOOTH), 
						0, 0, n,    n, 
						0, 0, size, size, null);
			g.dispose();
			
			for (int X = 0; X < n; X++)
				for (int Y = 0; Y < n; Y++) {
					out[X][Y] += new Color(img2.getRGB(X, Y)).getRed() * P / 255d;
				}
		}
		
		return out;
	}
	
	/**
	 * @param seed
	 * @param octaves - Number of maps to use
	 * @param x - Where in the generator to begin
	 * @param n - Number of data points
	 * @param dScale - Equivalent of change in frequency in simplex octave noise.
	 * @param persistence - Change in amplitude per wavelet
	 * @return
	 */
	public static double[] noise(long seed, int octaves, int x, int n, double persistence) {
		//TODO - OFFSET SO DIFFERENT AXIS PRODUCE DIFFERENT RESULTS
		
		// Go to the relevant location in the noise
		for (int i = 0; i < x; i++) // seed change per coordinate
			for (int Q = 0; Q < octaves; Q++) // seed change per coordinate per octave
				seed = getNextSeed(seed); // change the seed
		
		return noise(seed, octaves, n, persistence);
	}
	
	private static double[] noise(long localSeed, int octaves, int n, double persistence) {
		double[] out = new double[n];
		Random ran = new Random(localSeed);
		
		double dS = n / ((double) octaves); // change in scale per octave
		double P = 1;
		
		for (int i = 1; i <= octaves; i++) {
			BufferedImage img = new BufferedImage((int) Math.round(dS * i), 1, BufferedImage.TYPE_INT_RGB); //initialise the image TODO do something faster & smarter than using bufferedimage for 1D upscaling
			P *= persistence;
			
			for (int Q = 0; Q < img.getWidth(); Q++) { // populate the image
				int A = (int) Math.round(P * 255 * ran.nextDouble()); // amplitude
				img.setRGB(Q, 0, new Color(A, A, A).getRGB());
			}
			
			img.getScaledInstance(n, n, BufferedImage.SCALE_SMOOTH);
			
			for (int Q = 0; Q < n; Q++) out[Q] += img.getRGB(Q, 0);
		}
		
		return out;
	}
	
	
	
	public static long getNextSeed(long seed) {
		long multiplier = 0x5DEECE66DL;
	    long addend = 0xBL;
	    long mask = (1L << 48) - 1;
	    
	    return (seed * multiplier + addend) & mask;
	}
}