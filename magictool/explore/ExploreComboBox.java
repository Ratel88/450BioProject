/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007  Laurie Heyer
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *   Contact Information:
 *   Dr. Laurie Heyer
 *   Dept. of Mathematics
 *   Davidson College
 *   PO Box 6959
 *   Davidson, NC 28035-6959
 *   UNITED STATES
 */

package magictool.explore;

import java.io.File;
import java.util.*;
import javax.swing.JComboBox;
import magictool.*;

public class ExploreComboBox extends JComboBox {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3614963950595095717L;
	/**file path of the project*/
	protected String path;
	/**active project*/
	protected Project p;
	/**expression filename*/
	protected String expFile;
	/**whether or not to scan for existence of files*/
	protected boolean scan;
	/**whether or not there is a "temporary group" included in our listing*/
	protected boolean tempGrp = false;
	
	/**
	 * Constructs the ExploreComboBox for a given project and limits the group files displayed to a specific expression file
	 * @param prj project whose files are to be displayed
	 * @param ef expression filename
	 * @param scan whether or not to scan files for existence before displaying them
	 */
	public ExploreComboBox(Project prj, String ef, boolean s) {
		this.p = prj;
		this.expFile = ef;
		this.scan = s;
		reload();
	}
	
	/**
	 * Constructs the ExploreComboBox for a given project and limits the group files displayed to a expression file
	 * @param prj project whose files are to be displayed
	 * @param ef expression filename
	 */
	public ExploreComboBox(Project prj, String ef) {
		this(prj,ef,true);
	}
	
	/**
	 * Constructs the ExploreComboBox for a given project; does not limit the group files to be displayed
	 * @param prj project whose files are to be displayed
	 * @param s whether or not to scan files for existence before displaying them
	 */
	public ExploreComboBox(Project prj, boolean s) {
		this(prj,"all",true);
	}
	
	/**
	 * Constructs the ExploreComboBox for a given project; does not limit the group files to be displayed
	 * @param prj project whose files are to be displayed
	 */
	public ExploreComboBox(Project prj) {
		this(prj,true);
	}
	
	/**
	 * Returns the absolute filepath of the selected iteem
	 * @return absolute filepath of the selected item
	 */
	public String getFilePath() {
		if (this.getSelectedIndex()==0) return null;
		return path + super.getSelectedItem().toString();
	}
	
	/**
	 * Returns the filename of the selected item
	 * @return the filename of the selected item
	 */
	public String getFileName() {
		if(this.getSelectedIndex()==0||this.getItemCount()==0) return null;
		String name = getSelectedItem().toString();
		return name.substring(name.lastIndexOf(File.separator)+1);
	}
	
	/**
	 * Returns the filename of the selected item without the extension
	 * @return the filename of the selected item without the extension
	 */
	public String getSimpleName() {
		String n = this.getFileName();
		if (n==null) return null;
		return n.substring(0,n.lastIndexOf("."));
	}
	
	/**
	 * reloads the list of files displayed in the ExploreComboBox
	 *
	 */
	public void reload() {
		this.removeAllItems();
		if(scan) p.scanForExistance();
		if (expFile.endsWith(".exp")) expFile = expFile.substring(0,expFile.lastIndexOf("."));
		String addFiles[] = p.getFiles(Project.GRP);
		Vector<String> trueFiles = new Vector<String>();
		trueFiles.addElement("Entire Expression File");
		for (int i = 0; i < addFiles.length; i++) {
			int loc=addFiles[i].lastIndexOf(File.separator);
			if(expFile.equals("all")) trueFiles.addElement(addFiles[i]);
			else if (loc!=1 && addFiles[i].substring(0, loc).equalsIgnoreCase(expFile)) trueFiles.addElement(addFiles[i]);
		}
		if (trueFiles.size() > 1){
			//this.addItem("Select Existing Group File");
			//iterate and add
			Iterator it = trueFiles.iterator();
			while (it.hasNext()) this.addItem(it.next());
		}
		//else this.addItem("No Group Files Exist" + (expFile.equals("all")?"":" For " + expFile));
		else this.addItem("Entire Expression File");		
		path = p.getPath();
		this.revalidate();
		this.repaint();
	}
	
	public void goTemporaryGroup() {
		Vector<String> allItems = new Vector<String>();
		for (int i = 0; i < this.getItemCount(); i++) allItems.addElement(this.getItemAt(i).toString());	//put each element into a vector
		allItems.remove(0);
		allItems.add(0, "Temporary Group");
		this.removeAllItems();
		String[] allItSort = new String[allItems.size()];
		allItems.toArray(allItSort);
		Arrays.sort(allItSort, String.CASE_INSENSITIVE_ORDER);
		int loc = Arrays.binarySearch(allItSort, "Entire Expression File", String.CASE_INSENSITIVE_ORDER);
		if (loc < 0) {
			allItems.add(1,"Entire Expression File");
		}
		Iterator it = allItems.iterator();
		while (it.hasNext()) this.addItem(it.next());
		this.setSelectedIndex(0);
	}
	
	public void removeTemporaryGroup() {
		Vector<String> allItems = new Vector<String>();
		for (int i = 0; i < this.getItemCount(); i++) allItems.addElement(this.getItemAt(i).toString());	//put each element into a vector
		for (int i = 0; i < allItems.size(); i++) {
			if (allItems.get(i).equals("Temporary Group")) {
				this.removeItem("Temporary Group");
				break;
			}
		}
	}
}
