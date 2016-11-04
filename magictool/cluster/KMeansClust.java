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
import java.util.Random;
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
  * KMeansClust is a class which creates a KMeans cluster based on an expression
  * file, a desired number of clusters and maximum number of cycles.
  * KMeansClust is capable of outputting the clusters to files and can be used to
  * read KMeans cluster files
  */
public class KMeansClust extends AbstractCluster implements TreeableCluster, ListableCluster {

  /**name of the expression file*/
  protected String filename;
  /**name of the new cluster file*/
  protected String writeOutFile;
  private JDesktopPane desktop;
  /**number of clusters*/
  protected int numCluster;
  /**maximum number of iterations for KMeans cluster*/
  protected int maxCycle;

  /**average data for all genes in each cluster*/
  protected double[][] allClusters;
  private ExpFile exp; //expression file
  private int clusterAssign[]; //cluster assignment for each gene
  private int oldAssign[]; //old cluster assignment for each gene
  private int numAssigned[]; //number of genes assigned to each cluster
  private int oldNumAssigned[]; //old number of genes assigned to each cluster
  /**progress frame to show the progress of the cluster*/
  protected ProgressFrame progress;
  private Random rand;
  /**whether or not to recalculate clusters after moving each individual gene*/
  protected boolean alwaysCalculate=false;

  /**
   * Null constructor - useful for reading KMeans cluster files
   */
  public KMeansClust() {
  }

  /**
   * Constructs a KMeans cluster with given expression file and cluster filename. Sets the default
   * number of clusters to 10 and the maximum iterations to 20. Constructor does not begin the
   * clustering process.
   * @param filename expression filename for clustering
   * @param writeOutFile name of the new cluster file
   * @param desktop desktop to place the progress frame on
   */
  public KMeansClust(String filename, String writeOutFile, JDesktopPane desktop){

      this.filename = filename;
      this.writeOutFile = writeOutFile;
      this.desktop = desktop;
      this.numCluster = 10;
      this.maxCycle = 20;

  }


  /**
     * Starts the building of a hierarchical cluster and outputs the clusters to the specified file.
     * Required by the AbstractCluster class.
     */
  public void writeClusterFile() {

    //initializes the clusters
    rand = new Random();
    exp = new ExpFile(new File(filename));
    clusterAssign = new int[exp.numGenes()];
    oldAssign = new int[exp.numGenes()];
    numAssigned = new int[numCluster];
    oldNumAssigned = new int[numCluster];
    allClusters = new double[numCluster][exp.getLabelArray().length];


    initCluster();



    setSeeds();

    //creates the progress bar frame
    progress = new ProgressFrame("Creating Cluster...", true, this);
    desktop.add(progress);
    progress.show();
    progress.setMaximum(maxCycle);
    progress.setValue(0);


    //iterates through clustering process up to maxCycles times
    for(int i=0; i<maxCycle&&!isCancelled(); i++){


      for(int j=0; j<clusterAssign.length; j++){
        oldAssign[j]=clusterAssign[j];
        int cl=getClosestCluster(j);
        clusterAssign[j]=cl;

        numAssigned[cl]++;
        numAssigned[oldAssign[j]]--;

        if(this.alwaysCalculate) setSeeds(j,cl,oldAssign[j]); //recalculates seeds if constant calculation is in place
      }

      if(!this.alwaysCalculate) setSeeds(); //recalculates seeds if constant calculation is not in place

      boolean cont=false;
      for(int j=0; j<clusterAssign.length; j++){
        if(clusterAssign[j]!=oldAssign[j]){
          cont=true;
          break;
        }
      }

      if(!cont) break;
      progress.addValue(1);
    }

    //writes the clusters to the specified file
    if(!isCancelled()){
    try{
          super.writeHeaders(writeOutFile,exp.numGenes(), filename, -1, "", "n/a", "KMeans", "k=" + numCluster + ", Max Cycles=" + maxCycle, "");
          FileWriter stream = new FileWriter(writeOutFile, true);
          progress.setTitle("Writing File...");
          progress.setMaximum(numCluster);
          progress.setValue(0);
          for(int i=0; i<numCluster; i++){
            NodeInfo ni[] = new NodeInfo[numAssigned[i]];
            int p=0;
            for(int j=0; j<clusterAssign.length; j++){
              if(clusterAssign[j]==i) {
                ni[p] = new NodeInfo(exp.getGeneName(j), exp.correlation(j, allClusters[i]));
                p++;
              }
            }
            sortNodes(ni);

            writeOutCluster(stream, i+1, ni);
            progress.addValue(1);
          }

          stream.close();
          String name = exp.getName();
          if(name.endsWith(".exp")) name=name.substring(0, name.lastIndexOf("."));
          if(project!=null&&!isCancelled()) project.addFile((name + File.separator + writeOutFile.substring(writeOutFile.lastIndexOf(File.separator)+1)).trim());
          completed=true;
        }

      catch(Exception e){
        JOptionPane.showMessageDialog(null, "Error Writing KMeans Cluster");
      }
    }

    progress.dispose();
    over=true;

  }

  /**
   * sets whether or not to recalculate seeds of the clusters after each gene movement
   * @param constant whether or not to recalculate seeds of the clusters after each gene movement
   */
  public void setConstantCalculation(boolean constant){
    this.alwaysCalculate=constant;
  }

  /**
   * returns whether or not to there is recalculation of the seeds of the clusters after each gene movement
   * @return whether or not to there is recalculation of the seeds of the clusters after each gene movement
   */
  public boolean getConstantCalculation(){
    return this.alwaysCalculate;
  }

  //initializes the clusters
  private void initCluster(){
    int genesAssigned[] = new int[numCluster];

    for(int i = 0; i < exp.numGenes(); i++){
      clusterAssign[i] = -1;
    }

    for(int i = 0; i < numCluster; i++){
      boolean finding = true;
      while(finding){
        int n = rand.nextInt(exp.numGenes());
        boolean assign = true;
        for(int j = 0; j < i; j++){
          if(genesAssigned[j]==n){
            assign = false;
            break;
          }
        }
        if(assign){
          clusterAssign[n] = i;
          numAssigned[i]=1;
          finding=false;
        }
      }
    }

    for(int i = 0; i < exp.numGenes(); i++){
      if(clusterAssign[i] == -1) {
        int n = rand.nextInt(numCluster);
        clusterAssign[i] = n;
        numAssigned[n]++;
      }
    }


  }

  //sorts the nodes based on the dissimilarities
  private void sortNodes(NodeInfo ni[]){
     NodeInfo kn;
     int i, j;
     for(i = 1; i < ni.length; i++ ){
        kn = ni[i];
        j = i - 1;
        while (j >= 0 && ni[j].getDistance() > kn.getDistance()){
           ni[ j +1] = ni[ j ];
           j-=1;
        }
         ni[ j+1 ] = kn;

      }

  }

  /**
     * returns the cluster filename
     * @return cluster filename
     */
  public String getOutFile(){
      return writeOutFile;
  }



  /**
   * recalculates the seeds for the two clusters which are affected by a gene's switch
   * @param gene gene which moved
   * @param newassign new gene cluster location
   * @param oldassign old gene cluster location
   */
   public void setSeeds(int gene,int newassign, int oldassign){
    double tempData[] = exp.getData(gene);

    for(int i=0; i<tempData.length; i++){
      allClusters[newassign][i]*=oldNumAssigned[newassign];
      allClusters[oldassign][i]*=oldNumAssigned[oldassign];
      allClusters[newassign][i]+=tempData[i];
      allClusters[oldassign][i]-=tempData[i];
      allClusters[newassign][i]/=numAssigned[newassign];
      allClusters[oldassign][i]/=numAssigned[oldassign];
    }

    oldNumAssigned[newassign]++;
    oldNumAssigned[oldassign]--;
  }

  /**
   * recalculates the seeds for each cluster
   */
  public void setSeeds(){
    for(int i = 0; i < numCluster; i++){
      double tempData[][] = new double[numAssigned[i]][exp.getLabelArray().length];
      int p = 0;
      for(int l = 0; l < clusterAssign.length; l++){
        if(clusterAssign[l] == i){
          double t2[] = exp.getData(l);
          for(int k=0; k<t2.length; k++){
            tempData[p][k] = t2[k];
          }
          p++;
        }
      }

      double[] t = getAverageData(tempData);
      for(int j=0; j<t.length; j++){
        allClusters[i][j]=t[j];
      }


    }

    for(int j=0; j<oldNumAssigned.length; j++){
        oldNumAssigned[j]=numAssigned[j];
      }

  }

  //returns the average data - used to get cluster data averages
  private double[] getAverageData(double data[][]){
    double[] returndata = new double[exp.getLabelArray().length];
    for(int i=0; i<returndata.length; i++){
      double total=0;
      for(int j = 0; j<data.length; j++){
        total+=data[j][i];

      }
      returndata[i] = total/data.length;
    }

    return returndata;
  }


  /**
   * sets the number of cluster
   * @param numCluster number of clusters
   */
  public void setNumberOfClusters(int numCluster) {
    this.numCluster = numCluster;
  }

  /**
   * returns the number of cluster
   * @return number of clusters
   */
  public int getNumberOfClusters() {
    return numCluster;
  }

  /**
   * sets the maximum number of iterations
   * @param maxCycle maximum number of iterations
   */
  public void setMaxCycle(int maxCycle) {
    this.maxCycle = maxCycle;
  }

  /**
   * returns the maximum number of iterations
   * @return maximum number of iterations
   */
  public int getMaxCycle() {
    return maxCycle;
  }

  /**
   * returns the closest cluster number for a particular gene
   * @param geneNumber gene to find closest cluster for
   * @return closest cluster number for a particular gene
   */
  public int getClosestCluster(int geneNumber){
    int j=0;
    while(oldNumAssigned[j]==0){j++;}
    int closest = j;
    float cdistance = exp.correlation(geneNumber, allClusters[j]);
    for(int i=j+1; i<numCluster; i++){
      if(oldNumAssigned[i]!=0){
        float distance = exp.correlation(geneNumber, allClusters[i]);
        if(distance<cdistance){
          cdistance=distance;
          closest=i;
        }
      }
    }

    return closest;
  }

  //writes a cluster to filestream
  private void writeOutCluster(FileWriter stream, int number, NodeInfo ni[]) throws Exception{
    stream.write("Cluster " + number + "\n");
    for(int i=0; i<ni.length; i++){
      stream.write(ni[i].toString() + "\t" + ni[i].getDistance() + "\n");
    }
    stream.write("\n");

  }


  /**
   * reads a KMeans cluster file and puts it into a Default Mutable Tree Node. This is required as
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

    }catch(Exception e){System.out.println("Error in qtNode()"+e);}
      return firstNode;
    }


  /**
   * reads a KMeans cluster file and puts it into a Vector of clusters. This is required as
   * part of the Listable Cluster interface
   * @param clust entire filename of the cluster file
   * @return vector containing the clusters read from the file
   */
    public Vector getDataInVector(File clust){
      Vector all = new Vector();
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
                if(clusterGroup.size()>0)all.add(clusterGroup);


            }

        }

      }
      catch(Exception e){System.out.println("Error in KMeans()"+e);}

      return all;
    }

}