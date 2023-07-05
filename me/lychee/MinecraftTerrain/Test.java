package me.lychee.MinecraftTerrain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JPanel {
	private static final long serialVersionUID = 1938396399503105639L;
	static final int X = 1000, Y = 1000;
	public Test() {setSize(X, Y);}
	
	long seed = new Random().nextLong();
	BufferedImage i = new BufferedImage(X, Y, BufferedImage.TYPE_INT_RGB);
	boolean rendered = false;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!rendered) {
			for (int x = 0; x < X; x++)
				for (int y = 0; y < Y; y++) {
					double n = Noise.getContinentalness(seed, x, y)+128;
					//System.out.println(n);
					Color c = new Color((int) n, (int) n, (int) n);
					//System.out.println(c.toString());
					
					i.setRGB(x, y, c.getRGB());
				}
			//System.out.println("AA"+new Color(i.getRGB(500, 500)).toString());
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
