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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import magictool.ProcessTimer;
import magictool.ProgressFrame;
import magictool.TreeableCluster;


/**
 * HiClust is a class which creates a hierarchical cluster based on a dissimilairty
 * file. HiClust is capable of outputting the clusters to files and can be used to
 * read hierarchical cluster files
 */
public class HiClust extends AbstractCluster implements TreeableCluster {
   
   /**dissimilarity file to cluster*/
   protected String filename;
   /**output cluster filename*/
   protected String writeOutFile;
   private JDesktopPane desktop;
   /**linkage method*/
   protected int style;
   
   private LinkedList clustLabels; //label names
   private LinkedList dis; //disimilairty data in linked list
   
   private Vector openNodes = new Vector(); //vector containing open clusters
   private GroupPosition positionArray[]; //array holding the positions of data for sorting purposes
   private LinkedList array2;
   private short usedInCluster[]; //location of each gene in terms of open nodes
   
   //Internal hiClust variables
   private int SPOTA;
   private int SPOTB;
   
   private boolean displayCluster;
   private ProgressFrame progress;
   
   /**complete linkage method*/
   public static final int COMPLETE=1;
   /**single linkage method*/
   public static final int SINGLE=0;
   /**average linkage method*/
   public static final int AVERAGE=2;
   
   /**
    * Null contructor - useful for reading hierarchical cluster files
    */
   public HiClust(){
   }
   
   /**
    * Constructor sets up the hierarchical cluster but does not begin the calculations. Initializes the method
    * of cluster as complete linkage (MC:7/19/2005 - actually, I think it initializes to single linkage).
    * @param  filename name of the dissimilarity file
    * @param writeOutFile names of the output cluster file
    * @param desktop the desktop
    */
   public HiClust(String filename, String writeOutFile, JDesktopPane desktop) {
      this.filename = filename;
      this.writeOutFile = writeOutFile;
      this.displayCluster = true;
      this.desktop=desktop;
      style=0;
   }
   
   /**
    * returns the style of linkage
    * @param s integer representation of the linkage style
    * @return string representation of the linkage style
    */
   public String styleToString(int s){
      if(s==1) return "Complete";
      else if(s==2) return "Average";
      else return "Single";
   }
   
   /**
    * Starts the building of a hierarchical cluster and outputs the clusters to the specified file.
    * Required by the AbstractCluster class.
    */
   public void writeClusterFile() {
      try{
         ProcessTimer writeClusterTimer = new ProcessTimer( "HiClust.writeClusterFile()" );
         
         //creates the progress bar frame
         progress = new ProgressFrame("Building a matrix holding the addresses of all the dissimiliarity scores (there are:" + numPairs + ")...", true, this);
         desktop.add(progress);
         progress.show();
         
         //reads the dissimilarity file
         //if(!isCancelled()) readDissimilarityFile(filename, progress);
         if(!isCancelled()) {
            readSerializedDissimilarityFile(filename, progress);
         }
         
         //single linkage method
         if(style==SINGLE){
            progress.setValue(0);
            progress.setMaximum(numPairs);
            //sorts the data
            int p=0;
            positionArray = new GroupPosition[numPairs];		//remember there are numPairs number of locations in dys[][];
            for(int row=0; row<numGenes-1; row++){
               for(int column=0; column<=row; column++){
                  positionArray[p] = new GroupPosition((short)row, (short)column);
                  p++;
                  progress.addValue(1);
               }
            }
            progress.setTitle("Sorting the matrix of addresses from least dissimilarity to most...");
            progress.setValue(0);
            quickSort(positionArray, dys, progress);
            
            int nodecounter = numGenes-1;
            
            progress.setTitle("Creating Cluster...");
            progress.setValue(0);
            progress.setMaximum(nodecounter);
            
            usedInCluster = new short[numGenes];
            for(int i=0; i<numGenes; i++){
               usedInCluster[i]=-1;
            }
            
            //clusters the sorted data
            openNodes = new Vector();
            int pos=0;
            short newpos=0;
            int genesAdded=0;
            while(nodecounter>0){
               
               // the +1 is because the dissimilarity scores are calculated between genes numbered n+1 and n, n+1 coming first.
               // for example, if we calculated the dissimilarities for 4 genes, A, B, C & D, the actual sequence of comparisons
               // would look like:
               // 				 	BA, CA, CB, DA, DB, DC.  Or by gene number: 1-0, 2-0, 2-1, 3-0, 3-1, 3-2.
               // In other words, the first dissimilarity score in the array dys, relating the first two genes in the godlist
               // (given by labels[1], labels[0]), is accessed by dys[0][0].  Because of the order in which dissimilarity scores
               // are initially calculated, the first index of any score stored in dys
               //String spot1 = labels.get(positionArray[pos].pos1+1).toString();
               //String spot2 = labels.get(positionArray[pos].pos2).toString();
               String spot1 = geneLabels[positionArray[pos].pos1+1];
               String spot2 = geneLabels[positionArray[pos].pos2];
               
               
               short firstPos=usedInCluster[positionArray[pos].pos1+1];
               short secondPos=usedInCluster[positionArray[pos].pos2];
               
               if(firstPos==secondPos&&firstPos!=-1){
                  pos++;
               }
               
               else {
                  DefaultMutableTreeNode node = new DefaultMutableTreeNode(new NodeInfo(nodecounter,dys[positionArray[pos].pos1][positionArray[pos].pos2]));
                  DefaultMutableTreeNode dmtn1=null, dmtn2=null;
                  if(firstPos==-1){
                     dmtn1 = new DefaultMutableTreeNode(spot1);
                     node.add(dmtn1);
                     genesAdded++;
                  } else{
                     node.add((DefaultMutableTreeNode)openNodes.elementAt(firstPos));
                  }
                  
                  if(secondPos==-1){
                     dmtn2 = new DefaultMutableTreeNode(spot2);
                     node.add(dmtn2);
                     genesAdded++;
                  } else{
                     node.add((DefaultMutableTreeNode)openNodes.elementAt(secondPos));
                  }
                  
                  if(firstPos!=-1&&secondPos!=-1){
                     newpos = (short)Math.min(firstPos,secondPos);
                     short hightemp = (short)Math.max(firstPos,secondPos);
                     
                     openNodes.setElementAt(node,newpos);
                     openNodes.removeElementAt(hightemp);
                     for(int q=0; q<usedInCluster.length; q++){
                        if(usedInCluster[q]==hightemp) usedInCluster[q]=newpos;
                        else if(usedInCluster[q]>hightemp) usedInCluster[q]--;
                     }
                  }
                  
                  else if(firstPos!=-1){
                     openNodes.setElementAt(node,(newpos=firstPos));
                     usedInCluster[positionArray[pos].pos2] = firstPos;
                  }
                  
                  else if(secondPos!=-1){
                     openNodes.setElementAt(node,(newpos=secondPos));
                     usedInCluster[positionArray[pos].pos1+1] = secondPos;
                  }
                  
                  else{
                     openNodes.addElement(node);
                     usedInCluster[positionArray[pos].pos1+1] = (newpos=(short)(openNodes.size()-1));
                     usedInCluster[positionArray[pos].pos2] = newpos;
                  }
                  pos++;
                  nodecounter--;
                  progress.addValue(1);
               }
            }
         }
         
         //complete linkage - very slow right now
         else{
            int nodecounter = geneLabels.length;
            
            progress.setTitle("Creating Cluster...");
            progress.setValue(0);
            progress.setMaximum(nodecounter);
            
            dis = getDisDataInList();
            
            clustLabels = new LinkedList();
            
            //for(int i=0; i<labels.size(); i++){
            //   clustLabels.add(new DefaultMutableTreeNode(labels.get(i)));
            //}
            for (int i=0; i<geneLabels.length; i++)
               clustLabels.add(new DefaultMutableTreeNode(geneLabels[i]));
            while (clustLabels.size() > 2 && !isCancelled()) {
               
               nodecounter--;
               //Find the closest distance in dis
               float distance = findclosest();
               //Make a nodeInfo to store name and distance
               NodeInfo nodeinfo = new NodeInfo(nodecounter, distance);
               //Create the node itself - contains both a name and distance
               DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeinfo);
               
               //Add children nodes - there is a one space difference between
               node.add(((DefaultMutableTreeNode)clustLabels.get(SPOTA - 1)));
               node.add(((DefaultMutableTreeNode)clustLabels.get(SPOTB - 1)));
               //Recalculate the distances before any information is erased
               recalc(SPOTA, SPOTB);
               //Add the node to clustLabels so it can be clustered itself
               
               clustLabels.add(node);
               //Clean up the matrix - clear values no longer necessary
               cleanup(SPOTA, SPOTB);
               progress.addValue(1);
            }
         }
         
         //outputs the clusters to the file specified
         if(!isCancelled()){
            //make the final node
            ProcessTimer timer = new ProcessTimer("making .clust file");
            DefaultMutableTreeNode finalNode;
            if(style==SINGLE)  finalNode = (DefaultMutableTreeNode)openNodes.elementAt(0);
            else finalNode = finalnode();
            //write out to a file
            try {
               String exp = expFilePath.substring(expFilePath.lastIndexOf(File.separator)+1);
               if(exp.endsWith(".exp")) exp=exp.substring(0, exp.lastIndexOf("."));
               
               super.writeHeaders(writeOutFile, numGenes, exp + ".exp", dissimMethod, dissimParams, filename.substring(filename.lastIndexOf(File.separator)+1), "Hierarchical", "Index Style=" + styleToString(style), null);
               
               RandomAccessFile stream = new RandomAccessFile(writeOutFile, "rw");
               stream.seek(stream.length());
               writeClusToFile(finalNode,stream);
               stream.close();
               
               
               if(project!=null&&!isCancelled()) project.addFile((exp + File.separator + writeOutFile.substring(writeOutFile.lastIndexOf(File.separator)+1)).trim());
               completed=true;
            } catch (Exception e) {
               
               JOptionPane.showMessageDialog(null, "Error writing file - " + writeOutFile);
            }
            timer.finish();
         }
         //progress.dispose();		//causes annoying chain of swing ArrayIndexOutOfBoundsExceptions.  MC:7/11/2005 fixed, see SwingUtilities.invokeLater(...)
         SwingUtilities.invokeLater( new Runnable() {
            public void run(){
               progress.dispose();
            }
         });
         writeClusterTimer.finish();
         
      }catch(Exception e){
         System.out.println("HiClust: ");
         e.printStackTrace();
         if(!isCancelled())JOptionPane.showMessageDialog(null, "Error Reading Dissimilarity File");
         progress.dispose();
      }
      over=true;
   }
   
   /**
    * returns the cluster filename
    * @return cluster filename
    */
   public String getOutFile(){
      return writeOutFile;
   }
   
   /**
    * sets the linkage method
    * @param style linkage method
    */
   public void setStyle(int style){
      this.style=style;
   }
   
   /**
    * sorts the dissimilarities
    * @param A positions of the dissimilarities
    * @param values array of dissimilarity values
    * @param p start position
    * @param r end position
    * @param prog reference to ProgressFrame
    */
   public static void quickSort(GroupPosition[] A, float values[][],int p, int r, ProgressFrame prog){
      while (r > p){
         prog.addValue(1);
         int q = partition(A, values, p, r);
         if ((r - q) > (q - p)){
            quickSort(A,values, p, q, prog);
            p = q + 1;
         } else{
            quickSort(A,values, q + 1, r, prog);
            r = q;
         }
      }
   }
/*    public static void quickSort(GroupPosition[] A, float values[][],int p, int r){
        while (r > p){
          int q = partition(A, values, p, r);
          if ((r - q) > (q - p)){
            quickSort(A,values, p, q);
            p = q + 1;
          }
          else{
            quickSort(A,values, q + 1, r);
            r = q;
          }
        }
      }*/
   
   /**
    * used by quicksort for swapping position
    * @param A positions of the dissimilarities
    * @param values array of dissimilarity values
    * @param p start position
    * @param r end position
    * @return j position
    */
   private static int partition(GroupPosition[] A, float values[][], int p, int r){
      int i = p - 1;
      int j = r + 1;
      float pivot = values[A[p].pos1][A[p].pos2];
      for(;;){
         do j--; while (values[A[j].pos1][A[j].pos2] > pivot);
         do i++; while (values[A[i].pos1][A[i].pos2] < pivot);
         if (i < j) swap(A, i, j);
         else break;
      }
      
      return j;
   }
   
   /**
    * sorts the dissimilarities from start to finish
    * @param A positions of the dissimilarities
    * @param values array of dissimilarity values
    */
   public static void quickSort(GroupPosition[] A, float[][] values, ProgressFrame prog){
      prog.addValue(1);
      quickSort(A, values, 0, A.length-1, prog);
   }
/*
 * 	public static void quickSort (GroupPosition[] A, float[][] values){
        quickSort(A,values,0,A.length-1);
        progress.addValue(1);
      }
 */
   
   /**
    * swaps two positions of dissimilarities
    * @param A positions of the dissimilarities
    * @param a first position
    * @param b second position
    */
   public static void swap(GroupPosition[] A, int a, int b) {
      short temp1 = A[a].pos1;
      short temp2 = A[a].pos2;
      A[a].pos1 = A[b].pos1;
      A[a].pos2 = A[b].pos2;
      A[b].pos1 = temp1;
      A[b].pos2 = temp2;
   }
   
   
   /**
    * reads a Hierarchical cluster file and puts it into a Default Mutable Tree Node. This is required as
    * part of the Treeable Cluster interface
    * @param clustFile entire filename of the cluster file
    * @return node containing the clusters read from the file
    */
   public DefaultMutableTreeNode getDataInTree(File clustFile){
      DefaultMutableTreeNode firstNode = null;
      try{
         RandomAccessFile clustfile = new RandomAccessFile(clustFile, "r");
         while(!clustfile.readLine().endsWith("******/")){}
         boolean nodebool= clustfile.readBoolean();
         
         TreeMap nodetreemap = new TreeMap();
         firstNode = new DefaultMutableTreeNode(new NodeInfo(Integer.parseInt(clustfile.readUTF()),clustfile.readFloat()));
         nodetreemap.put(firstNode.toString(), firstNode);
         
         while(clustfile.getFilePointer()<clustfile.length()){
            nodebool = clustfile.readBoolean();
            
            if(nodebool == true){
               
               DefaultMutableTreeNode node = new DefaultMutableTreeNode(new NodeInfo(Integer.parseInt(clustfile.readUTF()),clustfile.readFloat()));
               String parent = clustfile.readUTF();
               
               ((DefaultMutableTreeNode)nodetreemap.get(parent)).add(node);
               nodetreemap.put(node.toString(), node);
            }
            
            else if(nodebool==false){
               
               DefaultMutableTreeNode node = new DefaultMutableTreeNode(clustfile.readUTF());
               String parent = clustfile.readUTF();
               ((DefaultMutableTreeNode)nodetreemap.get(parent)).add(node);
            }
         }
         clustfile.close();
      } catch(Exception e){}
      return firstNode;
   }
   
   private float meet(int l, int j) {
      int high = Math.max(l, j);
      int low = Math.min(l, j);
      return  ((Float)((LinkedList)dis.get(high - 2)).get(low - 1)).floatValue();
   }
   
   
   private void disremove(int l, int j) {
      int high = Math.max(l, j);
      int low = Math.min(l, j);
      ((LinkedList)dis.get(high - 2)).remove(low - 1);
   }
   
   /**
    * Finds and returns the smallest value in dis and changes the points of the two genes,
    * which are instance variables, so they can be used to develop a node.
    * Assumes the first gene is #1 or else meet() will not work correctly.
    *
    * @return returns the smallest dissimilarity value
    */
   private float findclosest() {
      float closest = 0;
      for (int place1 = 1; place1 <= clustLabels.size(); place1++) {
         for (int place2 = place1 + 1; place2 <= clustLabels.size(); place2++) {
            if (place1 == 1 && place2 == 2) {
               SPOTA = place1;
               SPOTB = place2;
               closest = meet(place1, place2);
            }
            if (meet(place1, place2) < closest) {
               closest = meet(place1, place2);
               SPOTA = place1;
               SPOTB = place2;
            }
         }
      }
      return  closest;
   }
   
   /**
    * Cleans up the matrix, deleting the end values first so the indices of the
    * values to the front of the ArrayList are not affected.
    * @param nodeA - one node being clustered/erased
    * @param nodeB - the other node
    */
   private void cleanup(int nodeA, int nodeB) {
      int highnode = 0;
      int lownode = 0;
      highnode = Math.max(nodeA, nodeB);
      lownode = Math.min(nodeA, nodeB);
      //Remove everything above highnode
      for (int countdown = clustLabels.size() - 1; countdown > highnode; countdown--) {
         disremove(countdown, highnode);
         disremove(countdown, lownode);
      }
      //Remove hignode column
      if (highnode != 1) {
         dis.remove(highnode - 2);
      }
      //Remove everything between highnode and lownode
      for (int countdown = highnode - 1; countdown > lownode; countdown--) {
         disremove(lownode, countdown);
      }
      //Remove the lownode column
      if (lownode != 1) {
         dis.remove(lownode - 2);
      }
      if (((LinkedList)dis.get(0)).size() == 0)
         dis.remove(0);
      //Remove the clustLabels themselves, starting from the back
      clustLabels.remove(highnode - 1);
      clustLabels.remove(lownode - 1);
   }
   
   /**
    * Recalculation of similarities
    * @param nodeA
    * @param nodeB
    */
   private void recalc(int nodeA, int nodeB) {
      //COMPLETE LINKAGE
      LinkedList array = new LinkedList();
      dis.add(array);
      for (int count = 1; count <= clustLabels.size(); count++) {
         //If count doesn't equal one of the nodes being erased, calculate the new value
         if ((count != nodeA) && (count != nodeB)) {
            //In this case, the greatest distance is kept
            float value1 = meet(count, nodeA);
            float value2 = meet(count, nodeB);
            if(style==1) array.add(new Float(Math.min(value1, value2)));
            else if(style==2) array.add(new Float((value1+value2)/2));
            else array.add(new Float(Math.max(value1, value2)));
         }
      }
   }
   
    /*
     * Create the final node
     * The only thing special about this node is that it has no parents and
     * should be used as a base node for a tree or writing to a file.
     */
   private DefaultMutableTreeNode finalnode() {
      DefaultMutableTreeNode finalNode = new DefaultMutableTreeNode(new NodeInfo(1,
              (Float)((LinkedList)dis.get(0)).get(0)));
      //Set the final two children
      finalNode.add(((DefaultMutableTreeNode)clustLabels.get(0)));
      finalNode.add(((DefaultMutableTreeNode)clustLabels.get(1)));
      return  finalNode;
   }
   
   
   
   // MC:4/7/2005  ****************************************************************************************
   // Replaced this recursive implementation with a non-recursive one (or at least not so blatently recursive).
   // the method that provides the enumeration of the nodes of the JTree might itself be implemented recursively,
   // but it probably manages it's own private stack and does some other magical stuff to keep stack overflows
   // from happening, which was the case with the original writeClusToFile recursive implementation.  We were
   // having problems with a certain set of genes > 5022
   // *****************************************************************************************************
   //writes the cluster data to the specified file stream
/*    private void writeClusToFile (DefaultMutableTreeNode node, RandomAccessFile stream) throws Exception {
                counster++;
                System.out.println(counster + ": " + node);
                System.out.flush();
        if (node.getParent() == null) {
            stream.writeBoolean(true);
            stream.writeUTF(node.toString());
            stream.writeFloat(((NodeInfo)node.getUserObject()).getDistance());
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
                writeClusToFile(child, stream);
            }
        }
        else if (node.isLeaf()) {
            stream.writeBoolean(false);
            stream.writeUTF(node.toString());
            stream.writeUTF(node.getParent().toString());
        }
        else {
            stream.writeBoolean(true);
            stream.writeUTF(node.toString());
            stream.writeFloat(((NodeInfo)node.getUserObject()).getDistance());
            stream.writeUTF(node.getParent().toString());
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
                writeClusToFile(child, stream);
            }
        }
    }
 */
   
   private void writeClusToFile(DefaultMutableTreeNode rootNode, RandomAccessFile stream) throws Exception {
      
      Enumeration preorder = rootNode.preorderEnumeration();
      while (preorder.hasMoreElements()) {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) preorder.nextElement();
         if (node.getParent() == null) {
            //System.out.println("rootnode");
            stream.writeBoolean(true);
            stream.writeUTF(node.toString());
            stream.writeFloat(((NodeInfo)node.getUserObject()).getDistance());
         } else if (node.isLeaf()) {
            //System.out.println("leafnode");
            stream.writeBoolean(false);
            stream.writeUTF(node.toString());
            stream.writeUTF(node.getParent().toString());
         } else {
            //System.out.println("childnode");
            stream.writeBoolean(true);
            stream.writeUTF(node.toString());
            stream.writeFloat(((NodeInfo)node.getUserObject()).getDistance());
            stream.writeUTF(node.getParent().toString());
         }
      }
   }
   
   //holds the position of the dissimilarity for sorting purposes
   private class GroupPosition{
      
      protected short pos1, pos2;
      
      public GroupPosition(short pos1, short pos2){
         this.pos1=pos1;
         this.pos2=pos2;
      }
      
      public void set(short pos1, short pos2){
         this.pos1=pos1;
         this.pos2=pos2;
      }
      
      public short getPos1(){
         return pos1;
      }
      
      public short getPos2(){
         return pos2;
      }
   }
}






