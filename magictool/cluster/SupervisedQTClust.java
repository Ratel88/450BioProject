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

package magictool.cluster;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import magictool.ExpFile;
import magictool.ListableCluster;
import magictool.ProgressFrame;
import magictool.TreeableCluster;
/**
  * SupervisedQTClust is a class which creates a supervised qt cluster based on a dissimilairty
  * file. SupervisedQTClust is capable of outputting the clusters to files and can be used to
  * read hierarchical cluster files. The class is very similar to QTCluster with the exception that
  * the number of clusters are limited to one which is based around either a created gene or
  * one selected from the expression file.
  */
public class SupervisedQTClust extends AbstractCluster implements TreeableCluster, ListableCluster {

  //User-Entered Variables
    private float thresh;                       // user-entered threshold value
    private String selectedgene;                // user-selected gene
    private String filename;                    // path of file containing dissimilarity data
    private String writeOutFile;                // path to write output

    //Internal supervisedQTClust variables
    private int numCands;       // number of candidates in candList at a given time
    private int clust[];
    private ExpFile exp;

    private float diamstore[];  // list of the genes as they are added to a cluster
    private int candList[];     // list of all the candidates
    private int iter;

    /* stores a diameter list for the current cluster
     * to keep track of the diameter after each gene is added*/
    private float diam[];

    private boolean taken[]; //taken[i] is true if i is already in the candidate cluster that is being constructed.

    private int clustSize=0;


    private FileWriter stream;
    private ProgressFrame progress;
    private JDesktopPane desktop;

    /**whether or not an existing gene is being used as first gene in the cluster*/
    protected boolean existingGene;

    private double values[]; //values for a created gene
    private double createdDis[]; //dissimilarities between a created gene and the rest of the genes


  /**
     * Default constructor which is useful for reading supervised qt clusters from files
     */
  public SupervisedQTClust() {
  }

    /**
      * Constructor which sets up the parameters specified by the user to create the cluster file which
      * contains a single cluster based around a created gene with user specified values.
      * The constructor does not begin the actual clustering process.
      * @param filename dissimilarity file to be used for clustering
      * @param writeOutFile new cluster file name
      * @param thresh qt cluster threshold
      * @param values array of values for created gene
      * @param desktop desktop
      */
  public SupervisedQTClust(String filename, String writeOutFile, float thresh, double values[], JDesktopPane desktop) {

      this.filename = filename;
      this.thresh = thresh;
      this.writeOutFile = writeOutFile;
      this.desktop = desktop;
      this.selectedgene = selectedgene;
      existingGene=false;
      this.values=values;


  }


  /**
      * Constructor which sets up the parameters specified by the user to create the cluster file which
      * contains a single cluster based around an existing gene specified by the user.
      * The constructor does not begin the actual clustering process.
      * @param filename dissimilarity file to be used for clustering
      * @param writeOutFile new cluster file name
      * @param thresh qt cluster threshold
      * @param selectedgene gene to base clustering around
      * @param desktop desktop
      */
  public SupervisedQTClust(String filename, String writeOutFile, float thresh, String selectedgene, JDesktopPane desktop) {

      this.filename = filename;
      this.thresh = thresh;
      this.writeOutFile = writeOutFile;
      this.desktop = desktop;
      this.selectedgene = selectedgene;
      existingGene=true;

  }


 /**
     * calculates and writes the clusters to file. This method is required by the AbstractCluster
     * class and provides the implementation specific to the supervised qt cluster.
     */
  public void writeClusterFile() {

       try{

            progress = new ProgressFrame("Supervised QTClust Running", true, this);
            desktop.add(progress);
            progress.show();

            if(!isCancelled())readSerializedDissimilarityFile(filename,progress);



              progress.setTitle("Creating Cluster...");

              progress.setMaximum(numGenes);
              progress.setValue(0);
              clust = new int[numGenes];
              taken = new boolean[numGenes];
              candList = new int[numGenes];
              diam = new float[numGenes];

              String exp1 = expFilePath.substring(expFilePath.lastIndexOf(File.separator)+1);
              if(exp1.endsWith(".exp")) exp1=exp1.substring(0, exp1.lastIndexOf("."));


            if(!isCancelled()){
                  String vals="";
                  if(!this.existingGene){
                    vals="{";
                    for(int i=0; i<values.length; i++){
                      vals+=values[i];
                      if(i!=values.length-1) vals+=", ";
                    }
                    vals+="}";
                    System.out.println(vals);
                  }

                  super.writeHeaders(writeOutFile, numGenes, exp1 + ".exp", dissimMethod, dissimParams, filename.substring(filename.lastIndexOf(File.separator)+1), "Supervised QTCluster", "threshold=" + thresh + ", Gene = " + (existingGene?this.selectedgene: vals), null);
                  stream = new FileWriter(writeOutFile,true);




                  boolean flag;
                  initialize();


                  exp = new ExpFile(new File(expFilePath=(project.getPath()+exp1+File.separator+exp1+".exp")));


                  for(int i = 0; i < numGenes; i++){
                    taken[i] = false;
                    clust[i] = -1;
                    diam[i] = 0;
                  }

                  if(existingGene){
                    int num= exp.findGeneName(this.selectedgene);
                    makeList(num);
                    clustSize = makeClust(num);

                  }

                  else{
                    createdDis = new double[numGenes];

                    if(getDisMethod()==1){
                      int p = 2;
                        try{
                          p=Integer.parseInt(getDisParams());
                        }catch(Exception e){
                          p=2;
                        }
                      for(int i=0; i<createdDis.length; i++){
                        createdDis[i]=exp.weightedlp(i,values, p);
                      }
                    }
                    else if(getDisMethod()==2){
                      for(int i=0; i<createdDis.length; i++){
                        createdDis[i]=exp.jackknife(i,values);
                      }
                    }
                    else{
                      for(int i=0; i<createdDis.length; i++){
                        createdDis[i]=exp.correlation(i,values);
                      }


                     }

                  makeList();

                  clustSize = makeClust();
                }


                  writeOutCluster(stream, 1);
                  progress.dispose();


              }

              stream.close();
              if(project!=null&&!isCancelled()) project.addFile((exp1 + File.separator + writeOutFile.substring(writeOutFile.lastIndexOf(File.separator)+1)).trim());
              completed=true;

             }
             catch(Exception e){
                if(!isCancelled())JOptionPane.showMessageDialog(null, "Error Writing Supervised QT Cluster");
             }

            progress.dispose();
            over=true;
    }

    //initializes variable
    private void initialize () {
        for (int count = 0; count < candList.length; count++)
            candList[count] = 0;
    }


    //lists candidate genes for clustering around base gene at index m
    private void makeList (int m) {
        numCands = 0;
        for (int place1 = 0; place1 < numGenes; place1++) {
            if (place1 != m) {
                if (meet(m, place1) <= thresh)  {
                candList[numCands] = place1;
                numCands++;
                }
            }
        }
    }

    //lists candidate genes for clustering around created gene
    private void makeList () {
        numCands = 0;
        for (int place1 = 0; place1 < numGenes; place1++) {
            if (createdDis[place1] <= thresh) {
              candList[numCands] = place1;
              numCands++;
            }
        }
    }

  //makes the single cluster based around the created gene
  private int makeClust () {
      boolean stop = false;
      int best = 0;
      int size = 0;
      float dissim[] = new float[numGenes];
      float value=0f;
      float min;
      while (!stop) {
          min = (thresh+1);
          for (int i = 0; i < numCands; i++) {
              int candidate = candList[i];
              if (taken[candidate] == false) {
                  int last = size - 1;
                  if (last == -1) dissim[candidate] = (float)createdDis[candidate];
                  else{
                    value = meet(candidate, clust[last]);
                    if (value > dissim[candidate]) dissim[candidate] = value;
                  }
                  if ((dissim[candidate] < min) || (i==0)) {
                      min = dissim[candidate];
                      best = candidate;

              }
          }
      }
      if (min <= thresh) {
          clust[size] = best;
          diam[size] = min;
          taken[best] = true;
          size++;
      }
      else
        stop = true;
    }
    return size;
  }

  //returns the dissimilarity of two genes at indices l and j
  private float meet (int l, int j) {

      int high = Math.max(l, j);
      int low = Math.min(l, j);

      return dys[high - 1][low];
  }

 //makes the single cluster based around the selected gene at index k
  private int makeClust (int k) {
    boolean stop = false;
      int best = 0;
      int size = 1;
      float dissim[] = new float[numGenes];
      float value = 0f;
      float min;
      diam[0] = 0;
      clust[0] = k;
      taken[k] = true;
      while (!stop) {
          min = (thresh+1);
          for (int i = 0; i < numCands; i++) {
              int candidate = candList[i];
              if (taken[candidate] == false) {
                  int last = size - 1;
                  value = meet(candidate, clust[last]);
                  if (last == 0) dissim[candidate] = value;
                  else if (value > dissim[candidate]) dissim[candidate] = value;
                  if ((dissim[candidate] < min) || (i==0)) {
                      min = dissim[candidate];
                      best = candidate;

              }
          }
      }
      if (min <= thresh) {
          clust[size] = best;
          diam[size] = min;
          taken[best] = true;
          size++;
      }
      else
        stop = true;
    }
    return size;


  }

  //writes the cluster to file
  private void writeOutCluster (FileWriter stream, int number) throws Exception{
        stream.write("Cluster " + number+"\n");
        for (int l = 0; l<clustSize; l++)
            stream.write(geneLabels[clust[l]] + "\t" + diam[l]+"\n");
        stream.write("\n");
  }


  /**
     * returns the name of the new cluster file
     * @return name of the new cluster file
     */
  public String getOutFile(){
      return writeOutFile;
    }


 /**
   * reads a Supervised QT cluster file and puts it into a Default Mutable Tree Node. This is required as
   * part of the Treeable Cluster interface
   * @param clustFile entire filename of the cluster file
   * @return node containing the clusters read from the file
   */
 public DefaultMutableTreeNode getDataInTree(File clustFile){
    DefaultMutableTreeNode firstNode = new DefaultMutableTreeNode(clustFile.getName());
    try{
        RandomAccessFile clustfile = new RandomAccessFile(clustFile, "r");
        while(!clustfile.readLine().endsWith("******/")){}
        while(clustfile.getFilePointer()!=clustfile.length()){
            String line = clustfile.readLine();
            if(line.startsWith("Cluster")){
            StringTokenizer tokenizer = new StringTokenizer(line);
            tokenizer.nextToken();
            DefaultMutableTreeNode cluster = new DefaultMutableTreeNode(new NodeInfo(Integer.parseInt(tokenizer.nextToken()),(float)0));//made the base node for this cluster
            firstNode.add(cluster);

            for(String line2 = clustfile.readLine(); (line2.length()!=0)&&(clustfile.getFilePointer()!=clustfile.length()); line2 = clustfile.readLine()){//for each of the leaves
              StringTokenizer tokenizer2 = new StringTokenizer(line2);
              DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(new NodeInfo(tokenizer2.nextToken(),Float.parseFloat(tokenizer2.nextToken())));
              cluster.add(leaf);
            }

            }
      }

    }catch(Exception e){}

    return firstNode;
    }

    /**
   * reads a Supervised QT cluster file and puts it into a vector of clusters which are each composed of a vector
   * of genes listed in order. This is required as part of the Listable Cluster interface
   * @param clust entire filename of the cluster file
   * @return vector of clusters which are each composed of a vector of genes listed in order
   */
    public Vector getDataInVector(File clust){
      Vector allClusters = new Vector();
      try{
          RandomAccessFile clustfile = new RandomAccessFile(clust, "r");
          while(!clustfile.readLine().endsWith("******/")){}


          while(clustfile.getFilePointer()!=clustfile.length()){
              String line = clustfile.readLine();
              if(line.startsWith("Cluster")){
                Vector clusterGroup = new Vector();
                String line2;
                while((line2=clustfile.readLine())!=null&&!line2.trim().equals("")&&(clustfile.getFilePointer()!=clustfile.length())){
                  StringTokenizer tokenizer = new StringTokenizer(line2);
                  clusterGroup.add(new NodeInfo(tokenizer.nextToken(), Float.parseFloat(tokenizer.nextToken())));

                }
                allClusters.add(clusterGroup);


            }

        }

      }
      catch(Exception e){}

      return allClusters;
    }
}




