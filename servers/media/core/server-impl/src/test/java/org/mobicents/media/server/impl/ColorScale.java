package org.mobicents.media.server.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ColorScale {

	
	
	private static Logger logger=Logger.getLogger(ColorScale.class);
	private static int scaleSize = 500;
	static Color[] colors=new Color[scaleSize];
	static Color[] spectrum = null;

	static {
		// Generate the colors and store them in the array.
		float maxHue = 0f;
		float minHue = 0.7f;
		float currentHue = minHue;
		float _step = (maxHue - minHue) / scaleSize;
		for (int i = 0; i < scaleSize; ++i) {
			// Here we specify colors by Hue, Saturation, and Brightness,
			// each of which is a number in the range [0,1], and use
			// a utility routine to convert it to an RGB value before
			// passing it to the Color() constructor.
			// spectrum[i] = new Color( Color.HSBtoRGB(i/(float)scaleSize,1,1)
			// );
			colors[i] = new Color(Color.HSBtoRGB(currentHue, 1, 1));
			currentHue += _step;

		}
		
		//Here read image
		try {
			//System.out.println(new File(ColorScale.class.getResource("spectrum2.png").toString()));
			//BufferedImage bi=ImageIO.read(new File(ColorScale.class.getResource("spectrum2.png").toString()));
                        
			BufferedImage bi=ImageIO.read(new File("src/test/java/org/mobicents/media/server/impl/spectrum2.png"));
//			BufferedImage bi=ImageIO.read(ColorScale.class.getResourceAsStream("/test/org/mobicents/media/server/impl/spectrum2.png"));
			Graphics2D g=(Graphics2D) bi.getGraphics();
			int width=bi.getWidth();
			spectrum=new Color[width];
			for(int i=0;i<width;i++)
			{
				spectrum[i]=new Color(bi.getRGB(i, 0));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Image createScale() {
		BufferedImage bi = new BufferedImage(scaleSize * 2, 100,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		for (int i = 0; i < spectrum.length * 2; i += 2) {
			g.setColor(spectrum[i / 2]);
			g.fillRect(i, 0, 2, 100);

		}

		return bi;
	}

	/**
	 * Returns color for passed value. f must conform to: 0<=f<=1
	 * This is generated color with HSB color set.
	 * @param f
	 */
	public static Color getScaleColor(double f) {
		if (f < 0 || f > 1) {
			throw new IllegalArgumentException("");
		}
		
		
		double matchStep=(float)1/scaleSize;
		int index=0;
		double copy=f;
		while(copy>0 && copy >matchStep)
		{
			index++;
			copy-=matchStep;
		}	
		
		if(copy>matchStep/2)
		{
			index++;
		}
		logger.info("INDEX["+index+"] FOR["+f+"]");
		if(index==colors.length)
			index--;
		return colors[index];
	}

	/**
	 * Returns color coresponding to f magnitude - f must be 0<=f<=1.
	 * This is spectrum color, as read from image.
	 * @param f
	 * @return
	 */
	public static Color getSpectrumColor(double f)
	{
		if (f < 0 || f > 1) {
			throw new IllegalArgumentException("");
		}
		
		
		double matchStep=(float)1/spectrum.length;
		int index=0;
		double copy=f;
		while(copy>0 && copy >matchStep)
		{
			index++;
			copy-=matchStep;
		}	
		
		if(copy>matchStep/2)
		{
			index++;
		}
		
		if(index==spectrum.length)
			index--;
		return spectrum[index];
	}
	public static void main(String[] args) {

		//JFrame frame = new JFrame("Image Maker");
		//frame.addWindowListener(new WindowAdapter() {
		//	public void windowClosing(WindowEvent event) {
		//		System.exit(0);
		//	}
		//});

		//frame.setBounds(0, 0, 200, 200);
		//JImagePanel panel = new JImagePanel(createScale());
		//JScrollPane sp = new JScrollPane(panel);
		//frame.add(sp);
		//frame.setVisible(true);
		
	}

}
