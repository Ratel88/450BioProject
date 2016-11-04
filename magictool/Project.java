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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Project is a class which contains all the files in a particular project as well as the name
 * and file path for a project and a few project properties. Project is capable of writing and
 * reading project files (.gprj) which stores these items.
 */
public class Project {

  /**list of expression files in the project*/
  private Vector expression = new Vector();
  /**list of dissimilarity files in the project*/
  private Vector dissimilarity = new Vector();
  /**list of cluster files in the project*/
  private Vector cluster = new Vector();
  /**list of group files in the project*/
  private Vector group = new Vector();
  /**name of the project*/
  private String name = new String();
  /**file path of the project*/
  private String path = new String();
  /**method for handling calculations with missing data*/
  private int missingDataStyle;
  /**threshold of data required when missing data method is to ignore particular missing values*/
  private int missingThresh;
  /**maximum megapixel in a single saved image*/
  private double imageSize=6;
  /**holds when to create group files when creating new expression files*/
  private int groupMethod=0;
  /**handles when to include replicate genes when averaging*/
  private int averageRepMethod=0;

  /**always create group files when creating expression files from already existing expression files*/
  public static final int ALWAYS_CREATE = 0;
  /**only create group files when data is not altered when creating expression files from already existing expression files*/
  public static final int SAME_DATA_CREATE = 1;
  /**never create group files when creating expression files from already existing expression files*/
  public static final int NEVER_CREATE = 2;

  /**keep gene in group file when any replicate is present*/
  public static final int ANY_ADD_REPLICATES = 0;
  /**keep gene in group file when half of the replicates are present*/
  public static final int HALF_ADD_REPLICATES = 1;
  /**keep gene in group file when all replicates are present*/
  public static final int ALL_ADD_REPLICATES = 2;
  /**never keep replicate genes in new group file*/
  public static final int NEVER_ADD_REPLICATES = 3;

  /**expression file*/
  public static final int EXP=0;
  /**dissimilarity file*/
  public static final int DIS=1;
  /**cluster file*/
  public static final int CLUST=2;
  /**group file*/
  public static final int GRP=3;
  /**image file (.tiff format)*/
  public static final int IMG=4;
  /**grid file (.grid format)*/
  public static final int GRD=5;


  /**
   * Constructs a project with a specified file path and vectors containing lists of files in the project
   * @param filename full file path of the project file
   * @param expression list of expression files
   * @param dissimilarity list of dissimilarity files
   * @param cluster list of cluster files
   * @param group list of group files
   */
  public Project(String filename, Vector expression, Vector dissimilarity, Vector cluster, Vector group) {
    this.path=filename;
    int pos = filename.lastIndexOf(File.separator);
    this.name=filename.substring(pos+1);
    this.expression=expression;
    this.dissimilarity = dissimilarity;
    this.cluster=cluster;
  }


  /**
   * Constructs an empty project with a specified file path but no associated files
   * @param filename full file path of the project file
   */
  public Project(String filename){
    this.path=filename;
    int pos = filename.lastIndexOf(File.separator);
    this.name=filename.substring(pos+1);
    path+=File.separator;
  }

  /**
   * Default constructor which may be useful to read a project file
   */
  public Project(){}

  /**
   * opens a project file and gathers the information about the project from the file
   * @param filename full file path of a project file to open
   * @throws Exception when an exception occurs reading the data about the project from the file
   * @return project containing all the information from the file
   */
  public static Project openProject(String filename) throws Exception{
    Project p = new Project();
    try{
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String line;
      String prj = in.readLine();
      if(prj.toLowerCase().indexOf("project file")==-1) throw new Exception();
      else{
        Vector prjprops = new Vector();
        while((line=in.readLine())!=null&&line.indexOf("****/")==-1){
            if(!line.trim().equals("")) prjprops.addElement(line);
        }
        if(prjprops.size()>=1) p.path=prjprops.elementAt(0).toString();
        if(prjprops.size()>=2) p.name=prjprops.elementAt(1).toString();
        if(prjprops.size()>=3) p.missingDataStyle=Integer.parseInt(prjprops.elementAt(2).toString());
        else p.missingDataStyle=ExpFile.REMOVE;
        if(prjprops.size()>=4) p.missingThresh=Integer.parseInt(prjprops.elementAt(3).toString());
        else p.missingThresh=0;
        if(prjprops.size()>=5) p.imageSize=Double.parseDouble(prjprops.elementAt(4).toString());
        else p.imageSize=6;
        if(prjprops.size()>=6) p.groupMethod=Integer.parseInt(prjprops.elementAt(5).toString());
        else p.groupMethod=0;
        if(prjprops.size()>=7) p.averageRepMethod=Integer.parseInt(prjprops.elementAt(6).toString());
        else p.averageRepMethod=0;
        ExpFile.setMissingDataStyle(p.missingDataStyle);
        ExpFile.setMissingThreshold(p.missingThresh);
        File f = new File(filename);
        p.path = f.getParentFile().getPath()+File.separator;
        p.name = f.getName().substring(0,f.getName().lastIndexOf("."));

        for(int i=0; i<4; i++){
          while((line=in.readLine())!=null&&!line.startsWith("**--")){}
          Vector v = new Vector();
          int ft=-1;
          if(line.toLowerCase().indexOf("expression")!=-1) ft=0;
          else if(line.toLowerCase().indexOf("dissimilarity")!=-1) ft=1;
          else if(line.toLowerCase().indexOf("cluster")!=-1) ft=2;
          else if(line.toLowerCase().indexOf("group")!=-1) ft=3;

          while((line=in.readLine())!=null&&line.indexOf("****/")==-1){
            if(!line.trim().equals("")) v.addElement(line);
          }
          switch(ft){
            case 0: p.expression=v; break;
            case 1: p.dissimilarity=v; break;
            case 2: p.cluster=v; break;
            case 3: p.group=v; break;
            default: break;
          }

        }
      }
      in.close();
      } catch(Exception e){ throw new Exception();}

      return p;
  }

  /**
   * sets the method to handle missing data
   * @param style method to handle missing data
   */
  public void setMissingDataStyle(int style){
    this.missingDataStyle=style;
    ExpFile.setMissingDataStyle(style);
    writeProject();
  }

  /**
   * sets the threshold for allowable missing data in a gene when the method to handle
   * missing data is ignore
   * @param thresh threshold for allowable missing data in a gene
   */
  public void setMissingThreshold(int thresh){
    this.missingThresh=thresh;
    ExpFile.setMissingThreshold(thresh);
    writeProject();
  }

 /**
   * sets the number of megapixels at which to split saved images
   * @param megapixels maxmimum megapixels in an image
   */
  public void setImageSize(double megapixels){
    this.imageSize=megapixels;
  }

  /**
   * returns the method for when to create group files along with expression files that
   * are made from already existing expression files
   * @return group file creation method
   */
  public int getGroupMethod(){
    return this.groupMethod;
  }

    /**
   * sets the method for when to create group files along with expression files that
   * are made from already existing expression files
   * @param groupMethod group file creation method
   */
  public void setGroupMethod(int groupMethod){
    this.groupMethod=groupMethod;
  }

  /**
   * sets the method for when to keep genes in new group files that are replicates
   * @param averageRepMethod method for when to keep genes in new group files that are replicates
   */
  public void setAverageReplicateMethod(int averageRepMethod){
    this.averageRepMethod=averageRepMethod;
  }

  /**
   * returns the method for when to keep genes in new group files that are replicates
   * @return method for when to keep genes in new group files that are replicates
   */
  public int getAverageReplicateMethod(){
    return averageRepMethod;
  }



  /**
   * returns the number of megapixels at which to split saved images
   * @return the number of megapixels at which to split saved images
   */
  public double getImageSize(){
    return this.imageSize;
  }

  /**
   * returns the method to handle missing data
   * @return method to handle missing data
   */
  public int getMissingDataStyle(){
    return this.missingDataStyle;
  }

  /**
   * returns the threshold for allowable missing data in a gene when the method to handle
   * missing data is ignore
   * @return threshold for allowable missing data in a gene
   */
  public int getMissingThreshold(){
    return this.missingThresh;
  }

  /**
   * writes the information about the project to the proper project file
   */
  public void writeProject(){
    try{
	File f = new File(path);
	if(!f.exists()) f.mkdir();
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path + name + ".gprj")));
	out.println("/****Project File");
        out.println(path);
        out.println(name);
        out.println(missingDataStyle);
        out.println(missingThresh);
        out.println(imageSize);
        out.println(groupMethod);
        out.println(averageRepMethod);
        out.println("********/");


        String filetypes[] = {"Expression Files","Dissimilarity Files","Cluster Files","Group Files"};
        for(int i=0; i<4; i++){
          out.println("**--"+filetypes[i]);
          String[] fnames = getFiles(i);
          for(int j=0; j<fnames.length; j++){
            out.println(fnames[j]);
          }
          out.println("********/");

        }

        out.close();
        } catch(IOException e){}
  }


  /**
   * adds a file to the project
   * @param name name of the file to add
   */
  public void addFile(String name){
    File f = new File(path+name);

    boolean added=true;
    if(name.toLowerCase().endsWith(".exp")){ if(!expression.contains(name)) expression.addElement(name);}
    else if(name.toLowerCase().endsWith(".dis")){ if(!dissimilarity.contains(name)) dissimilarity.addElement(name); }
    else if(name.toLowerCase().endsWith(".clust")){ if(!cluster.contains(name)) cluster.addElement(name);}
    else if(name.toLowerCase().endsWith(".grp")){ if(!group.contains(name)) group.addElement(name);}
    else added=false;


    if(added) writeProject();

  }

  /**
   * returns whether or not a file already exists within the project
   * @param name name of the file to check for existance within the project
   * @return whether or not a file already exists within the project
   */
  public boolean fileExists(String name){
    String[] files = getAllFiles();
    for(int i=0; i<files.length; i++){
      if(files[i].equals(name)) return true;
    }
    return false;
  }


  /**
   * removes a file from the project
   * @param name name of the file to remove from the project
   */
  public void removeFile(String name){
    boolean deleted=false;
    if(name.endsWith(".exp")){
      for(int i=0; i<expression.size(); i++){
        if(((String)expression.elementAt(i)).equals(name)){
          expression.removeElementAt(i);
          deleted=true;
        }
      }
    }
    else if(name.endsWith(".grp")){
      for(int i=0; i<group.size(); i++){
        if(((String)group.elementAt(i)).equals(name)){
          group.removeElementAt(i);
          deleted=true;
        }
      }
    }
    else if(name.endsWith(".dis")){
      for(int i=0; i<dissimilarity.size(); i++){
        if(((String)dissimilarity.elementAt(i)).equals(name)){
          dissimilarity.removeElementAt(i);
          deleted=true;
        }
      }
    }
    else if(name.endsWith(".clust")){
      for(int i=0; i<cluster.size(); i++){
        if(((String)cluster.elementAt(i)).equals(name)){
          cluster.removeElementAt(i);
          deleted=true;
        }
      }
    }
  }

  /**
   * returns the file path of the project
   * @return file path of the project
   */
  public String getPath(){
    return path;
  }

  /**
   * adds all the files in the given directory to the project
   * @param directory directory to add files from
   */
  public void addFiles(String directory){
    File f = new File(path+directory);
    if (f.isDirectory()) {
      String filenames[] = f.list();
      if (!directory.endsWith(File.separator)) directory+=File.separator;
      for(int i=0; i<filenames.length; i++){
        addFile(directory+filenames[i]);
      }
    }

  }

  /**
   * scans the directories of all the expression files for any existing file adding any
   * that are not part of the project and removing those file which no longer exist
   */
  public void scanDirectory(){
    scanForExistance();
    for(int i=0; i<expression.size(); i++){
      String s=(String)expression.elementAt(i);
      addFiles(s.substring(0,s.lastIndexOf(File.separator)));
    }
    scanForExistance();
  }

  /**
   * scans all the files listed in the project to ensure they exist and removes those that do not
   */
  public void scanForExistance(){

    String files[] = getAllFiles();
    for(int i=0; i<files.length; i++){
      File f = new File(this.getPath()+files[i]);

      if(!f.exists()){
        removeFile(files[i]);
        if(files[i].toLowerCase().endsWith(".exp")){
          String n = files[i].substring(0, files[i].lastIndexOf(File.separator)+1);
          for(int j=0; j<files.length; j++){
            if(j!=i&&files[j].startsWith(n)) removeFile(files[j]);
          }
        }
      }
    }

  }

  /**
   * returns an array of expression files which exist in the project
   * @return array of expression files which exist in the project
   */
  public String[] getExpressionFiles(){
    return getFiles(0);
  }

  /**
   * returns an array of dissimilarity files which exist in the project
   * @return array of dissimilarity files which exist in the project
   */
  public String[] getDissimilarityFiles(){
    return getFiles(1);
  }

  /**
   * returns an array of cluster files which exist in the project
   * @return array of cluster files which exist in the project
   */
  public String[] getClusterFiles(){
    return getFiles(2);
  }

  /**
   * returns an array of group files which exist in the project
   * @return array of group files which exist in the project
   */
  public String[] getGroupFiles(){
    return getFiles(3);
  }

  /**
   * returns an array of group files which exist in the project for the given expression file
   * @param expression expression file to return group files for
   * @return array of group files which exist in the project for the given expression file
   */
  public String[] getGroupFiles(String expression){
    String express = expression.toLowerCase();
    if(express.endsWith(".exp")) express = express.substring(0,express.lastIndexOf(".exp"));
    if(express.lastIndexOf(File.separator)!=-1) express = express.substring(express.lastIndexOf(File.separator+1));
    String[] possibleFiles = getFiles(3);
    Vector gr = new Vector(possibleFiles.length);
    for(int i=0; i<possibleFiles.length; i++){
      int pos=possibleFiles[i].lastIndexOf(File.separator);
      if(possibleFiles[i].substring(0, (pos!=-1?pos:possibleFiles[i].length())).equalsIgnoreCase(express)) gr.add(possibleFiles[i]);
    }
    String[] groupFiles = new String[gr.size()];
    for(int i=0; i<gr.size(); i++){
      groupFiles[i] = gr.get(i).toString();
    }
    return groupFiles;
  }


  /**
   * returns an array of all files which exist in the project
   * @return array of all files which exist in the project
   */
  public String[] getAllFiles(){
    String files[] = new String[expression.size()+dissimilarity.size()+cluster.size()+group.size()];
    int i=0;
      for(int j=0; j<4; j++){
        String[] typefiles = getFiles(j);
        for(int p=0; p<typefiles.length; p++){
          files[i]=typefiles[p];
          i++;
        }
      }
    return files;
  }

  /**
   * returns an array of files of given type in the project
   * @param type type of files to return
   * @return array of files of given type in the project
   */
  public String[] getFiles(int type){
    Vector v;
    switch(type){
      case 0: v=expression; break;
      case 1: v=dissimilarity; break;
      case 2: v=cluster; break;
      case 3: v=group; break;
      default: v = expression; break;
    }
    String files[] = new String[v.size()];
    for(int i=0; i<v.size(); i++){
      files[i] = (String)v.elementAt(i);
    }
    return files;
  }

  /**
   * sets the path of the project
   * @param path path of the project
   */
  public void setPath(String path){
    this.path = path;
  }

  /**
   * sets the name of the project
   * @param name name of the project
   */
  public void setName(String name){
    this.name = name;
  }

  /**
   * gets the name of the project
   * @return name of the project
   */
  public String getName(){
    return name;
  }


}