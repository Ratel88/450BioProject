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

package  magictool.cluster;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import magictool.ListableCluster;
import magictool.ProgressFrame;
import magictool.TreeableCluster;

/**
 * QTClust is a class which creates a qt cluster based on a dissimilairty
 * file. QTClust is capable of outputting the clusters to files and can be used to
 * read hierarchical cluster files.
 */
public class QTClust extends AbstractCluster implements TreeableCluster, ListableCluster {
   //User-Entered Variables
   private int maxNumClust;                    // max number of clusters
   private int clustSizeMax;                   // max cluster size
   private int clustSizeMin;                   // min cluster size
   private float thresh;       // user-entered threshold value
   private String filename;                    // path of file containing dissimilarity data
   private String writeOutFile;                // path to write output
   
   //Internal qtClust variables
   private int numCands;       // number of candidates in candList at a given time
   private int clust[];        // list of the genes as they are added to a cluster
   private int biggest[];      // copy of the biggest cluster
   private float diamstore[];    // copy of diams for biggest cluster
   private int candList[];                     // list of all the candidates
   private int iter;
   
    /* stores a diameter list for the current cluster
     * to keep track of the diameter after each gene is added*/
   private float diam[];
   
    /* gone[i] is true if the object i has been removed from consideration
     * because it is in a finished cluster.  taken[i] is true if i is already
     * in the candidate cluster that is being constructed.
     */
   private boolean gone[], taken[];
   
   
   
   private FileWriter stream;
   private ProgressFrame progress;
   
   private JDesktopPane desktop;
   
   /**
    * Default constructor which is useful for reading qt clusters from files
    */
   public QTClust(){}
   
   /**
    * Constructor which sets up the parameters specified by the user to create the cluster file.
    * However it does not begin the actual clustering process.
    * @param filename dissimilarity file to be used for clustering
    * @param writeOutFile new cluster file name
    * @param thresh qt cluster threshold
    * @param maxNumClust maximum number of clusters
    * @param clustSizeMin minimum number of genes in a cluster
    * @param desktop desktop
    */
   public QTClust(String filename, String writeOutFile, float thresh, int maxNumClust,
           int clustSizeMin, JDesktopPane desktop) {
      
      this.filename = filename;
      this.maxNumClust = maxNumClust;
      this.clustSizeMin = clustSizeMin;
      this.thresh = thresh;
      this.writeOutFile = writeOutFile;
      this.desktop=desktop;
      
   }
   
   
   
   /**
    * calculates and writes the clusters to file. This method is required by the AbstractCluster
    * class and provides the implementation specific to the qt cluster.
    */
   public void writeClusterFile() {
      
      try{
         
         //creates the progress frame
         progress = new ProgressFrame("QTClust Running", true, this);
         desktop.add(progress);
         progress.show();
         
         //reads the dissimilarity file
         if(!isCancelled())readSerializedDissimilarityFile(filename,progress);
         
         
         
         progress.setTitle("Creating Cluster...");
         
         progress.setMaximum(numGenes);
         progress.setValue(0);
         clust = new int[numGenes];
         biggest = new int[numGenes];
         diamstore = new float[numGenes];
         gone = new boolean[numGenes];
         taken = new boolean[numGenes];
         candList = new int[numGenes];
         diam = new float[numGenes];
         
         String exp = expFilePath.substring(expFilePath.lastIndexOf(File.separator)+1);
         if(exp.endsWith(".exp")) exp=exp.substring(0, exp.lastIndexOf("."));
         
         
         //begins the clustering process
         if(!isCancelled()){
            
            //writes out the header information
            super.writeHeaders(writeOutFile, numGenes, exp + ".exp", dissimMethod, dissimParams, filename.substring(filename.lastIndexOf(File.separator)+1), "QTCluster", "threshold=" + thresh + ", maxNumClust=" + maxNumClust + ", clustSizeMin="+clustSizeMin, null);
            stream = new FileWriter(writeOutFile,true);
            
            
            int number, gene;
            int max = 0;
            boolean flag, done;
            initialize();
            number = 0;             //stores the number of clusters created
            done = false;
            
            
            while (!done&&!isCancelled()) {
               max = 0;
               for (int count = 0; count < numGenes; count++)biggest[count] = -1;            //reset biggest[]
               for (int l = 0; l < numGenes; l++) diamstore[l] = 0;
               for (int k = 0; k < numGenes; k++) {//for each object
                  for (int l = 0; l < numGenes; l++) clust[l] = -1;              //reset clust[]
                  if (gone[k] == false) {         //if gene is still in contention, calculate a cluster around it
                     for (int i = 0; i < numGenes; i++) taken[i] = false;       //reset taken
                     makeList(k);                //find all objects that are within thresh of k
                     int size = makeClust(k);                    //make the candidate cluster from elements in the list
                     //check the size of the new cluster
                     if (size > max) {
                        max = size;             //max will store the size of the largest cluster
                        gene = k;               // gene stores the index of the base of the largest cluster
                        for (int count = 0; count < size; count++) biggest[count] = clust[count];      //biggest stores a copy of the largest cluster
                        for(int count=0; count<size; count++) diamstore[count] = diam[count];
                     }
                  }
               }
               
               if (max >= clustSizeMin) {
                  number++;           //update number of clusters formed
                  writeOutCluster(stream, number); //writes the new cluster to file
               }
               
               //update which objects are gone from consideration
               for (int l = 0; biggest[l] != -1; l++){
                  gone[biggest[l]] = true;
                  progress.addValue(1);
                  
               }
               
               
               
               
               if (max < clustSizeMin||number>=maxNumClust){
                  done = true;
                  progress.dispose();
               }
               
            }
            
            stream.close();
            
            //adds the file to the project
            if(project!=null&&!isCancelled()) project.addFile((exp + File.separator + writeOutFile.substring(writeOutFile.lastIndexOf(File.separator)+1)).trim());
            completed=true;
         }
      } catch(Exception e){
         if(!isCancelled())JOptionPane.showMessageDialog(null, "Error Writing QT Cluster");
      }
      
      progress.dispose();
      over=true;
   }
   
   /**
    * returns the name of the new cluster file
    * @return name of the new cluster file
    */
   public String getOutFile(){
      return writeOutFile;
   }
   
   //initializes variables for cluster
   private void initialize() {
      for (int count = 0; count < candList.length; count++)
         candList[count] = 0;
      for (int count = 0; count < gone.length; count++)
         gone[count] = false;
   }
   
   //makes a list of genes with dissimilarities less than the threshold compared to gene at place m
   private void makeList(int m) {
      numCands = 0;
      // Compare all other genes to gene m
      for (int place1 = 0; place1 < numGenes; place1++) {
         if (place1 != m) {
            if ((meet(m, place1) <= thresh) && (gone[place1]==false)) {
               candList[numCands] = place1;
               numCands++;
            }
         }
      }
   }
   
   //makes the new cluster
   private int makeClust(int k) {
      boolean stop = false;
      int best = 0;           //number of the best candidate
      int size = 1;           //
      float dissim[] = new float[numGenes];
      float value;
      float min;
      diam[0] = 0;
      clust[0] = k;
      taken[k] = true;
      while (!stop) {
         min = (thresh+1);
         //find the object from the candidate list that has the smallest effect on diameter increase
         for (int i = 0; i < numCands; i++) {
            int candidate = candList[i];
            if (taken[candidate] == false) {
               int last = size - 1;  //this is the index of the last object added to the cluster
               value = meet(candidate, clust[last]);//only have to compute the distance to the newest one
               if (last == 0)            //if this is the first time through, calculate all the dissims
                  dissim[candidate] = value;
               
               
               if (value > dissim[candidate])//if the object is further from the new gene than it is from any other gene in the cluster
                  dissim[candidate] = value;
               if ((dissim[candidate] < min)||(i==0)) {
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
         } else
            stop = true;
      }
      
      return  size;
   }
   
   //finds the dissimilarity for two genes at indices l and j
   private float meet(int l, int j) {
      
      
      int high = Math.max(l, j);
      int low = Math.min(l, j);
      
      
      
      return  dys[high - 1][low];
   }
   
   //writes a cluster to the file stream
   private void writeOutCluster(FileWriter stream, int number) throws Exception{
      
      stream.write("Cluster " + number+"\n");
      for (int l = 0; biggest[l] != -1; l++)
         stream.write(geneLabels[biggest[l]] + "\t" + diamstore[l]+"\n");
      stream.write("\n");
      
   }
   
   /**
    * reads a QT cluster file and puts it into a Default Mutable Tree Node. This is required as
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
    * reads a QT cluster file and puts it into a vector of clusters which are each composed of a vector
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
         
      } catch(Exception e){}
      
      return allClusters;
   }
}



