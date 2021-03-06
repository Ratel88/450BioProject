package ij.process;
import java.awt.Image;
import java.awt.image.ColorModel;

/** This class converts an ImageProcessor to another data type. */
public class TypeConverter {

	private static final int BYTE=0, SHORT=1, FLOAT=2, RGB=3;
	private ImageProcessor ip;
	private int type;
	boolean doScaling = true;
	int width, height;

	public TypeConverter(ImageProcessor ip, boolean doScaling) {
		this.ip = ip;
		this.doScaling = doScaling;
		if (ip instanceof ByteProcessor)
			type = BYTE;
		else if (ip instanceof ShortProcessor)
			type = SHORT;
		else if (ip instanceof FloatProcessor)
			type = FLOAT;
		else
			type = RGB;
		width = ip.getWidth();
		height = ip.getHeight();
	}

	/** Converts processor to a ByteProcessor. */
	public ImageProcessor convertToByte() {
		switch (type) {
			case BYTE:
				return ip;
			case SHORT:
				return convertShortToByte();
			case FLOAT:
				return convertFloatToByte();
			case RGB:
				return convertRGBToByte();
			default:
				return null;
		}
	}

	/** Converts a ShortProcessor to a ByteProcessor. */
	ByteProcessor convertShortToByte() {
		if (doScaling) {
			Image img = ip.createImage();
			return new ByteProcessor(img);
		} else {
			short[] pixels16 = (short[])ip.getPixels();
			byte[] pixels8 = new byte[width*height];
			int value;
			for (int i=0; i<width*height; i++) {
				value = pixels16[i]&0xffff;
				if (value>255) value = 255;
				pixels8[i] = (byte)value;
			}
			return new ByteProcessor(width, height, pixels8, ip.getColorModel());
		}
	}

	/** Converts a FloatProcessor to a ByteProcessor. */
	ByteProcessor convertFloatToByte() {
		if (doScaling) {
			Image img = ip.createImage();
			return new ByteProcessor(img);
		} else {
			float[] pixels32 = (float[])ip.getPixels();
			byte[] pixels8 = new byte[width*height];
			float value;
			for (int i=0; i<width*height; i++) {
				value = pixels32[i];
				if (value<0f) value = 0f;
				if (value>255f) value = 255f;
				pixels8[i] = (byte)Math.round(value);
			}
			return new ByteProcessor(width, height, pixels8, ip.getColorModel());
		}
	}

	/** Converts a ColorProcessor to a ByteProcessor. */
	ByteProcessor convertRGBToByte() {
		int c, r, g, b;
		int[] pixels32;
		byte[] pixels8;
		Image img8;
		
		//get RGB pixels
		pixels32 = (int[])ip.getPixels();
		
		//convert to grayscale
		pixels8 = new byte[width * height];
		for (int i=0; i < width*height; i++) {
			c = pixels32[i];
			r = (c&0xff0000)>>16;
			g = (c&0xff00)>>8;
			b = c&0xff;
			pixels8[i] = (byte)((int)(r*0.299 + g*0.587 + b*0.114) & 0xff);
		}
		
		return new ByteProcessor(width, height, pixels8, null);
	}
	
	/** Converts processor to a ShortProcessor. */
	public ImageProcessor convertToShort() {
		switch (type) {
			case BYTE:
				return convertByteToShort();
			case SHORT:
				return ip;
			case FLOAT:
				return convertFloatToShort();
			case RGB:
				//return convertRGBToShort(ctable);
				return null;
			default:
				return null;
		}
	}

	/** Converts a ByteProcessor to a ShortProcessor. */
	ShortProcessor convertByteToShort() {
		byte[] pixels8 = (byte[])ip.getPixels();
		short[] pixels16 = new short[width * height];
		for (int i=0,j=0; i<width*height; i++) {
			pixels16[i] = (short)(pixels8[i]&0xff);
		}
	    return new ShortProcessor(width, height, pixels16, ip.getColorModel());
	}

	/** Converts a FloatProcessor to a ShortProcessor. */
	ShortProcessor convertFloatToShort() {
		float[] pixels32 = (float[])ip.getPixels();
		short[] pixels16 = new short[width*height];
		double min = ip.getMin();
		double max = ip.getMax();
		double scale;
		if ((max-min)==0.0)
			scale = 1.0;
		else
			scale = 65535.0/(max-min);
		double value;
		for (int i=0,j=0; i<width*height; i++) {
			if (doScaling)
				value = (pixels32[i]-min)*scale;
			else
				value = pixels32[i];
			if (value<0.0) value = 0.0;
			if (value>65535.0) value = 65535.0;
			pixels16[i] = (short)value;
		}
	    return new ShortProcessor(width, height, pixels16, ip.getColorModel());
	}

	/** Converts processor to a FloatProcessor. */
	public ImageProcessor convertToFloat(float[] ctable) {
		switch (type) {
			case BYTE:
				return convertByteToFloat(ctable);
			case SHORT:
				return convertShortToFloat(ctable);
			case FLOAT:
				return ip;
			case RGB:
				//return convertRGBToFloat(ctable);
				return null;
			default:
				return null;
		}
	}

	/** Converts a ByteProcessor to a FloatProcessor. Applies a
		calibration function if the calibration table is not null.
		@see ImageProcessor.setCalibrationTable
	 */
	FloatProcessor convertByteToFloat(float[] cTable) {
		byte[] pixels8 = (byte[])ip.getPixels();
		boolean invertedLut = ip.isInvertedLut();
		float[] pixels32 = new float[width*height];
		int value;
		if (cTable!=null && cTable.length==256)
			for (int i=0; i<width*height; i++)
				pixels32[i] = cTable[pixels8[i]&255];
		else
			for (int i=0; i<width*height; i++)
				pixels32[i] = pixels8[i]&255;
	    ColorModel cm = ip.getColorModel();
	    return new FloatProcessor(width, height, pixels32, cm);
	}

	/** Converts a ShortProcessor to a FloatProcessor. Applies a
		calibration function if the calibration table is not null.
		@see ImageProcessor.setCalibrationTable
	 */
	FloatProcessor convertShortToFloat(float[] cTable) {
		short[] pixels16 = (short[])ip.getPixels();
		boolean invertedLut = false; //imp.isInvertedLut();
		float[] pixels32 = new float[width*height];
		int value;
		if (cTable!=null && cTable.length==65536)
			for (int i=0; i<width*height; i++)
				pixels32[i] = cTable[pixels16[i]&0xffff];
		else
			for (int i=0; i<width*height; i++)
				pixels32[i] = pixels16[i]&0xffff;
	    ColorModel cm = ip.getColorModel();
	    return new FloatProcessor(width, height, pixels32, cm);
	}
	
	/** Converts processor to a ColorProcessor. */
	public ImageProcessor convertToRGB() {
		if (type==RGB)
			return ip;
		else {
			ImageProcessor ip2 = ip.convertToByte(doScaling);
			return new ColorProcessor(ip2.createImage());
		}
	}

}
