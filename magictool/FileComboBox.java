/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003  Laurie Heyer
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
 *   Laurie Heyer
 *   Dept. of Mathematics
 *   PO Box 6959
 */

package magictool;

import java.io.File;
import java.util.Vector;

import javax.swing.JComboBox;


/**
 *
 *FileComboBox is a JComboBox which displays a list of all the files of a given type
 *available in a given project. It displays a short version of the filename but will
 *output the absolute filepath when desired.
 */
public class FileComboBox extends JComboBox {

  /**file path of the project*/
  protected String path;
  /**project whose files are displayed*/
  protected Project p;
  /**type of files to display*/
  protected int filetype;
  /**expression filename that can be used to limit group files displayed*/
  protected String expFile;
  /**whether or not to scan for existance of files*/
  protected boolean scan=true;
  /**the name of the type of file we're looking for*/
  protected String typeName;
  /**this contains the text "Select Existing typeName File"*/
  protected String selectExisting;
  
  /**
   * Constructs the ComboBox for a given project and type of file.
   * Scans files for existance before displaying them.
   * @param p project whose files are to be displayed
   * @param filetype type of files to display
   */
  public FileComboBox(Project p, int filetype){
    this(p, filetype, "all");
  }

  /**
   * Constructs the ComboBox for a given project and type of file
   * @param p project whose files are to be displayed
   * @param filetype type of files to display
   * @param scan whether or not to scan files for existance before displaying them
   */
  public FileComboBox(Project p, int filetype,boolean scan){
    this(p, filetype, "all",scan);
  }

  /**
   * Constructs the ComboBox for a given project and type of file and limits group files displayed
   * to a specific expression file. Scans files for existance before displaying them.
   * @param p project whose files are to be displayed
   * @param filetype type of files to display
   * @param expFile expression filename used to limit group files displayed
   */
  public FileComboBox(Project p, int filetype, String expFile) {
    this(p,filetype,expFile,true);
  }

  /**
   * Constructs the ComboBox for a given project and type of file and limits group files displayed
   * to a specific expression file.
   * @param p project whose files are to be displayed
   * @param filetype type of files to display
   * @param expFile expression filename used to limit group files displayed
   * @param scan whether or not to scan files for existance before displaying them
   */
  public FileComboBox(Project p, int filetype, String expFile, boolean scan) {
    /*this.p=p;
    this.filetype=filetype;
    this.expFile =expFile;
    this.scan=scan;
    reload();*/
	  this(p,filetype,expFile,scan,"Group");
  }
  
  public FileComboBox(Project p, int filetype, String expFile, boolean scan, String typeName) {
	  this.p=p;
	  this.filetype = filetype;
	  this.expFile = expFile;
	  this.scan = scan;
	  this.typeName = typeName;
	  selectExisting = "Select Existing " + typeName + " File";
	  reload();
  }

   /**
    * returns the file type being displayed
    * @return file type being displayed
    */
   public int getFileType(){
    return filetype;
   }

   /**
    * sets the file type being displayed
    * @param filetype file type being displayed
    */
   public void setFileType(int filetype){
    this.filetype=filetype;
   }

   /**
    * returns the absolute filepath of the selected item
    * @return absolute filepath of the selected item
    */
   public String getFilePath(){
    if(this.getSelectedIndex()==0) return null;
    return path + super.getSelectedItem().toString();
   }



   /**
    * returns the filename selected item
    * @return filename of the selected item
    */
   public String getFileName(){
    if(this.getSelectedIndex()==0||this.getItemCount()==0) return null;
    String name = getSelectedItem().toString();
    return name.substring(name.lastIndexOf(File.separator)+1);
   }

   /**
    * returns the filename selected item without the file extension
    * @return filename of the selected item without the file extension
    */
   public String getSimpleName(){
    String n = getFileName();
    if(n==null) return null;
    return n.substring(0,n.lastIndexOf("."));
   }

  /**
   * reloads the list of files displayed in the ComboBox
   */
  public void reload(){
        this.removeAllItems();
        if(scan)p.scanForExistance();
        if(expFile.endsWith(".exp")) expFile = expFile.substring(0, expFile.lastIndexOf("."));
        String addFiles[] = p.getFiles(filetype);
        Vector truefiles = new Vector();
        for(int i=0; i<addFiles.length; i++){
          int loc=addFiles[i].lastIndexOf(File.separator);
          if(expFile.equals("all")) truefiles.addElement(addFiles[i]);
          else if(loc!=-1&&addFiles[i].substring(0, loc).equalsIgnoreCase(expFile)) truefiles.addElement(addFiles[i]);
        }
        if (truefiles.size()>0) this.addItem(selectExisting);
        else this.addItem("No Group Files Exist" + (expFile.equals("all")?"":" For " + expFile));
        for(int i=0; i<truefiles.size(); i++){
          this.addItem(truefiles.elementAt(i));
        }
        path = p.getPath();
        this.revalidate();
        this.repaint();

   }




}