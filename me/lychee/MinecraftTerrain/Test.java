package me.lychee.MinecraftTerrain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JPanel {
	private static final long serialVersionUID = 1938396399503105639L;
	static final int X = 1024, Y = 1024;
	public Test() {setSize(X, Y);}
	
	long seed = new Random().nextLong();
	BufferedImage i = new BufferedImage(X, Y, BufferedImage.TYPE_INT_RGB);
	boolean rendered = false;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!rendered) {
			/*
			for (int x = 0; x < X / 16; x++)
				for (int y = 0; y < Y / 16; y++) {
					double[][] d = Noise.getPeaksandValleysV2(seed, Noise.getContinentalness(seed, x, y),x, y);
					
					for (int n1 = 0; n1< 16; n1++)
						for (int n2 = 0; n2< 16; n2++) {
							int a = (int) (d[n1][n2]* 255d / 75d);
							//System.out.println(a);
							i.setRGB(x*16+n1, y*16+n2, 
									new Color(a, a, a).getRGB()
								);
						}
				}
				*/
			double[][] d = WaveletOctaveGenerator.noise(seed, 8, 0, 0, X, 1/2d);
			for (int x = 0; x < X; x++)
				for (int y = 0; y < Y; y++) {
					
					int A = (int) (d[x][y]*255);
					i.setRGB(x, y,
							new Color(A, A, A).getRGB());
				}
			
			
			rendered = true;
			
			System.out.println("Done!");
		}
		g.drawImage(i,
					0, 0, 
					getWidth(), getHeight(), 
					0, 0, 
					i.getWidth(), i.getHeight(), 
					null);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new Test());
		frame.setSize(X, Y);
		
		frame.setVisible(true);
	}

}
