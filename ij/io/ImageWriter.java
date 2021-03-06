package ij.io;
import ij.IJ;

import java.io.IOException;
import java.io.OutputStream;

/** Writes a raw image described by a FileInfo object to an OutputStream. */
public class ImageWriter {
	private FileInfo fi;
	private boolean showProgressBar=true;
	
	public ImageWriter (FileInfo fi) {
		this.fi = fi;
	}
	
	private void showProgress(double progress) {
		if (showProgressBar)
			IJ.showProgress(progress);
	}
	
	void write8BitImage(OutputStream out, byte[] pixels)  throws IOException {
		int bytesWritten = 0;
		int size = fi.width*fi.height;
		int count = 8192;
		
		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = size - bytesWritten;
			//System.out.println(bytesWritten + " " + count + " " + size);
			out.write(pixels, bytesWritten, count);
			bytesWritten += count;
			showProgress((double)bytesWritten/size);
		}
	}
	
	void write8BitStack(OutputStream out, Object[] stack)  throws IOException {
		showProgressBar = false;
		for (int i=0; i<fi.nImages; i++) {
			IJ.showStatus("Writing: " + (i+1) + "/" + fi.nImages);
			write8BitImage(out, (byte[])stack[i]);
			IJ.showProgress((double)(i+1)/fi.nImages);
		}
	}

	void write16BitImage(OutputStream out, short[] pixels)  throws IOException {
		int bytesWritten = 0;
		int size = fi.width*fi.height*2;
		int count = 8192;
		byte[] buffer = new byte[count];

		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = size - bytesWritten;
			int j = bytesWritten/2;
			int value;
			if (fi.intelByteOrder)
				for (int i=0; i < count; i+=2) {
					value = pixels[j];
					buffer[i] = (byte)value;
					buffer[i+1] = (byte)(value>>>8);
					j++;
				}
			else
				for (int i=0; i < count; i+=2) {
					value = pixels[j];
					buffer[i] = (byte)(value>>>8);
					buffer[i+1] = (byte)value;
					j++;
				}
			out.write(buffer, 0, count);
			bytesWritten += count;
			showProgress((double)bytesWritten/size);
		}
	}
	
	void write16BitStack(OutputStream out, Object[] stack)  throws IOException {
		showProgressBar = false;
		for (int i=0; i<fi.nImages; i++) {
			IJ.showStatus("Writing: " + (i+1) + "/" + fi.nImages);
			write16BitImage(out, (short[])stack[i]);
			IJ.showProgress((double)(i+1)/fi.nImages);
		}
	}

	void writeFloatImage(OutputStream out, float[] pixels)  throws IOException {
		int bytesWritten = 0;
		int size = fi.width*fi.height*4;
		int count = 8192;
		byte[] buffer = new byte[count];
		int tmp;

		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = size - bytesWritten;
			int j = bytesWritten/4;
			if (fi.intelByteOrder)
				for (int i=0; i < count; i+=4) {
					tmp = Float.floatToIntBits(pixels[j]);
					buffer[i]   = (byte)tmp;
					buffer[i+1] = (byte)(tmp>>8);
					buffer[i+2] = (byte)(tmp>>16);
					buffer[i+3] = (byte)(tmp>>24);
					j++;
				}
			else
				for (int i=0; i < count; i+=4) {
					tmp = Float.floatToIntBits(pixels[j]);
					buffer[i]   = (byte)(tmp>>24);
					buffer[i+1] = (byte)(tmp>>16);
					buffer[i+2] = (byte)(tmp>>8);
					buffer[i+3] = (byte)tmp;
					j++;
				}
			out.write(buffer, 0, count);
			bytesWritten += count;
			showProgress((double)bytesWritten/size);
		}
	}
	
	void writeFloatStack(OutputStream out, Object[] stack)  throws IOException {
		showProgressBar = false;
		for (int i=0; i<fi.nImages; i++) {
			IJ.showStatus("Writing: " + (i+1) + "/" + fi.nImages);
			writeFloatImage(out, (float[])stack[i]);
			IJ.showProgress((double)(i+1)/fi.nImages);
		}
	}

	void writeRGBImage(OutputStream out, int[] pixels)  throws IOException {
		int bytesWritten = 0;
		int size = fi.width*fi.height*3;
		int count = fi.width*24;
		byte[] buffer = new byte[count];

		while (bytesWritten<size) {
			if ((bytesWritten + count)>size)
				count = size - bytesWritten;
			int j = bytesWritten/3;
			for (int i=0; i < count; i+=3) {
				buffer[i]   = (byte)(pixels[j]>>16);	//red
				buffer[i+1] = (byte)(pixels[j]>>8);	//green
				buffer[i+2] = (byte)pixels[j];		//blue
				j++;
			}
			out.write(buffer, 0, count);
			bytesWritten += count;
			showProgress((double)bytesWritten/size);
		}
	}
	
	void writeRGBStack(OutputStream out, Object[] stack)  throws IOException {
		showProgressBar = false;
		for (int i=0; i<fi.nImages; i++) {
			IJ.showStatus("Writing: " + (i+1) + "/" + fi.nImages);
			writeRGBImage(out, (int[])stack[i]);
			IJ.showProgress((double)(i+1)/fi.nImages);
		}
	}

	/** Writes the image to the specified OutputStream.
		The OutputStream is not closed. The fi.pixels field
		must contain the image data. If fi.nImages>1
		then fi.pixels must be a 2D array, for example an
 		array of images returned by ImageStack.getImageArray()).
 		The fi.offset field is ignored. */
	public void write(OutputStream out) throws IOException {
		if (fi.pixels==null)
				throw new IOException("ImageWriter: fi.pixels==null");
		if (fi.nImages>1 && !(fi.pixels instanceof Object[]))
				throw new IOException("ImageWriter: fi.pixels not a stack");
		switch (fi.fileType) {
			case FileInfo.GRAY8:
			case FileInfo.COLOR8:
				if (fi.nImages>1)
					write8BitStack(out, (Object[])fi.pixels);
				else
					write8BitImage(out, (byte[])fi.pixels);
				break;
			case FileInfo.GRAY16_SIGNED:
			case FileInfo.GRAY16_UNSIGNED:
				if (fi.nImages>1)
					write16BitStack(out, (Object[])fi.pixels);
				else
					write16BitImage(out, (short[])fi.pixels);
				break;
			case FileInfo.GRAY32_FLOAT:
				if (fi.nImages>1)
					writeFloatStack(out, (Object[])fi.pixels);
				else
					writeFloatImage(out, (float[])fi.pixels);
				break;
			case FileInfo.RGB:
				if (fi.nImages>1)
					writeRGBStack(out, (Object[])fi.pixels);
				else
					writeRGBImage(out, (int[])fi.pixels);
				break;
			default:
		}
	}
	
}

