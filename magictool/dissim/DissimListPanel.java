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

package magictool.dissim;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.File;
import java.util.Vector;

import javax.swing.JPanel;

import magictool.ListableCluster;
import magictool.cluster.AbstractCluster;
import magictool.cluster.KMeansClust;
import magictool.cluster.NodeInfo;
import magictool.cluster.QTClust;
import magictool.cluster.SupervisedQTClust;


/**
 * DissimListPanel displays a list of dissimilarities in the order genes were clustered.
 * The panel draws a black line in between two sets of clusters if they exist.
 * This panel is intended to be used in conjunction with a colored table of data.
 * The panel currently works with QTCluster, SupervisedQTCluster, and KMeans Clustering
 * but can work with any cluster that implements the ListableCluster interface.
 */
public class DissimListPanel extends JPanel {

  /**cluster file*/
  protected File clustFile;
  /**height between two genes*/
  protected int lineheight;
  /**cluster from which to display the information*/
  protected ListableCluster cluster=null;
  /**vector of data from the cluster file that includes the name and dissimilarity of genes*/
  protected Vector clusterData;

  /**
   * contructs the panel
   * @param clustFile cluster file
   * @param lineheight height between two genes
   */
  public DissimListPanel(File clustFile, int lineheight) {
    this.clustFile=clustFile;
    this.lineheight=lineheight;

    //gets the cluster information and selects the correct method
    try{
      String[] info = AbstractCluster.readHeaders(clustFile.getPath());
      if(info[5].equals("QTCluster")) cluster = new QTClust();
      else if(info[5].equals("Supervised QTCluster")) cluster = new SupervisedQTClust();
      else if(info[5].equals("KMeans")) cluster = new KMeansClust();
    }
    catch(Exception e){}

    //gets the cluster data
    clusterData = new Vector();
    if(cluster!=null) clusterData = cluster.getDataInVector(clustFile);
  }


  /**
   * sets the height between two genes
   * @param lineheight height between two genes
   */
  public void setLineHeight(int lineheight){
    this.lineheight=lineheight;
    this.repaint();
  }

  /**
   * paints the panel
   * @param g graphics to paint the panel on
   */
  public void paintComponent(Graphics g){
      g.setColor(Color.black);

      //sets the font
      Font f = new Font("Times New Roman", Font.PLAIN, 10);
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics(f);

      //draws the dissimilarites if the height is large enough to show the numbers in full
      int height=0;
      for(int i=0; i<clusterData.size(); i++){
        Vector v = (Vector)clusterData.elementAt(i);
        for(int j=0; j<v.size(); j++){
          height+=lineheight;
          if(lineheight>=10){
            String s = String.valueOf(((NodeInfo)v.elementAt(j)).getDistance());
            while(fm.stringWidth(s)>this.getWidth()){
              s = s.substring(0,s.length()-1);
            }
            g.drawString(s,this.getWidth()-fm.stringWidth(s), height-fm.getDescent());
          }

        }
        g.fillRect(0,height-1,this.getWidth(),2); //draws a line if it is at the end of a cluster
      }


  }

  /**
   * returns a vector of gene names in the order they are displayed
   * @return a vector of gene names in the order they are displayed
   */
  public Vector getGeneVector(){
    Vector v = new Vector();
    for(int i=0; i<clusterData.size(); i++){
      Vector tempData = (Vector)clusterData.elementAt(i);
      for(int j=0; j<tempData.size(); j++){
        v.addElement(((NodeInfo)tempData.elementAt(j)).toString());
      }
    }
    return v;
  }

}