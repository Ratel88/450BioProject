package magictool.filefilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * FlagFilter is a file filter used to select flag files in a file chooser
 * @author Laurie Heyer
 *
 */
public class FlagFilter extends FileFilter {
	/**
	 * Default Constructor for FlagFilter
	 *
	 */
	public FlagFilter() {}
	
	/**
	 * returns whether or not file is has flag extension
	 * @param file to check
	 * @return whether or not fileobj has flag extension 
	 */
	public boolean accept(File fileobj) {
		String extension = "";
		
		if(fileobj.getPath().lastIndexOf('.') > 0)
			extension = fileobj.getPath().substring(fileobj.getPath().lastIndexOf('.') + 1).toLowerCase();
		if(extension != "")
			return extension.equals("flag");
		else
			return fileobj.isDirectory();
	}
	
	public String getDescription() {
		return "Flag Files (*.flag)";
	}
}
