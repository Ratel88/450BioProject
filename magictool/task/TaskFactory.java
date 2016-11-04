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

package magictool.task;

import java.io.File;
import java.util.Vector;

import javax.swing.JDesktopPane;

import magictool.Executable;
import magictool.Project;
import magictool.cluster.AbstractCluster;
import magictool.cluster.HiClust;
import magictool.cluster.KMeansClust;
import magictool.cluster.QTClust;
import magictool.cluster.SupervisedQTClust;
import magictool.dissim.Dissimilarity;

/**
 * Factory class which produces executable tasks. Currently this class can produce cluster files
 * and dissimilarity files, however more types can be added later.
 */
public class TaskFactory {

  /**hierarchical clustering method*/
  public static final int HIERARCHICAL=0;
  /**qt clustering method*/
  public static final int QTCLUST=1;
  /**kmeans clustering method*/
  public static final int KMEANS=2;
  /**supervised qt clustering method*/
  public static final int SUPERVISED=3;

  /**
   * Default constructor which is not really ever needed
   */
  public TaskFactory() {
  }

  /**
   * constructs an executable task which can be added to a task manager
   * @param filetype type of file to be created - currently dissimilarity or cluster file
   * @param method method of creating file
   * @param inFile file to be used for creation
   * @param outFile file to be created
   * @param project porject to place created file in
   * @param modifiers other parameters for creating the file
   * @param desktop desktop
   * @return executable task to be added to a task manager
   */
  public static Task createTask(int filetype, int method, String inFile, String outFile, Project project,Object[] modifiers, JDesktopPane desktop){
    Executable exe=null;
    if(filetype==Project.DIS){
      exe = new Dissimilarity(inFile,outFile,method,(String)modifiers[0], desktop);
      ((Dissimilarity)exe).setProject(project);
    }
    else if(filetype==Project.CLUST){
      if(method==HIERARCHICAL){
        exe = new HiClust(inFile,outFile,desktop);
        ((HiClust)exe).setStyle(((Integer)modifiers[0]).intValue());
      }
      else if(method==QTCLUST){
        exe = new QTClust(inFile, outFile,((Float)modifiers[0]).floatValue(),((Integer)modifiers[1]).intValue(),((Integer)modifiers[2]).intValue(),desktop);
      }
      else if(method==SUPERVISED){
        if(((Boolean)modifiers[0]).booleanValue()){
          exe = new SupervisedQTClust(inFile,outFile,((Float)modifiers[1]).floatValue(),(String)modifiers[2],desktop);
        }
        else{
          double[] values = new double[modifiers.length-2];
          for(int i=2; i<modifiers.length; i++){
            values[i-2]=((Double)modifiers[i]).doubleValue();
          }
          exe = new SupervisedQTClust(inFile,outFile,((Float)modifiers[1]).floatValue(),values,desktop);
        }
      }
      else if(method==KMEANS){
        exe = new KMeansClust(inFile, outFile,desktop);
        ((KMeansClust)exe).setMaxCycle(((Integer)modifiers[0]).intValue());
        ((KMeansClust)exe).setNumberOfClusters(((Integer)modifiers[1]).intValue());
        ((KMeansClust)exe).setConstantCalculation(((Boolean)modifiers[2]).booleanValue());
      }
      else return null;
      ((AbstractCluster)exe).setProject(project);
    }
    else return null;

    String out, in;
    Vector required= new Vector();
    required.addElement(in=shortenFileName(inFile));
    if(in.endsWith(".dis")){
        required.addElement(getExpFile(inFile));
    }
    Task t = new Task(exe,out=shortenFileName(outFile),out,required);
    return t;
  }

  /**
   * returns the file path within the project directory for a given file
   * @param file file to return shortened path
   * @return file path within the project directory for a given file
   */
  public static String shortenFileName(String file){
    File f = new File(file);
    return f.getParentFile().getName()+File.separator+f.getName();
  }

  /**
   * returns the expression file associated with a given file
   * @param file filename which its associated expression file is desired
   * @return expression file associated with a given file
   */
  public static String getExpFile(String file){
    File f = new File(file);
    return f.getParentFile().getName()+File.separator+f.getParentFile().getName() + ".exp";
  }
}