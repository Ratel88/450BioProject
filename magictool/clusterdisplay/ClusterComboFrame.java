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

package magictool.clusterdisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import magictool.ExpFile;
import magictool.GrpFile;
import magictool.ListableCluster;
import magictool.PlotFrame;
import magictool.Project;
import magictool.VerticalLayout;
import magictool.cluster.KMeansClust;
import magictool.cluster.NodeInfo;
import magictool.cluster.QTClust;
import magictool.cluster.SupervisedQTClust;

/**
 *ClusterComboFrame displays a QTCluster, a Supervised Clust or a KMeans Clust. It is composed of a list
 *of genes at the head of each cluster and another list which displays the other genes
 *in the cluster. The frame provides the option to graph the clusters. Other clusters may be displayed on this
 *frame as it simply requires that a cluster implement the ListableCluster interface.
 */

public class ClusterComboFrame extends JInternalFrame implements KeyListener{

    /**name of the cluster files for the frame*/
    protected File clustFile;
    /**name of the expression file used to build the cluster file*/
    protected ExpFile exp;
    /**vector of cluster data*/
    protected Vector clusterData;
    private TitledBorder titledBorder1;
    private JPanel buttonPanel = new JPanel();
    private GridLayout gridLayout1 = new GridLayout();
    private JPanel mainPanel = new JPanel();
    private JPanel listPanel = new JPanel(gridLayout1);
    private JPanel labelPanel = new JPanel(gridLayout1);
    /**list model for building JList*/
    protected DefaultListModel elementListModel, clusterListModel;
    /**JList displaying genes in cluster*/
    protected JList clusterList,elementList;
    /**cluster method*/
    protected int type;

    private JButton plotButton = new JButton();
    private JButton closeButton = new JButton();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JLabel elementLabel, clusterLabel;
    private JScrollPane cl, el;
    private VerticalLayout verticalLayout1 = new VerticalLayout();
    /**parent frame*/
    protected Frame parentFrame;
    private Project project;

  /**
   * Constructs the frame and builds the lists based on the cluster file
   * @param clustFile name of the clusterfile to be displayed
   * @param exp name of the expression file of data used to create the cluster
   * @param parentFrame parent frame
   * @param project open project
   */
  public ClusterComboFrame(File clustFile, ExpFile exp, Frame parentFrame, Project project){
    this(clustFile,exp,0,parentFrame, project);
  }

  /**
   * Constructs the frame and builds the lists based on the cluster file
   * @param clustFile name of the clusterfile to be displayed
   * @param type cluster method
   * @param exp name of the expression file of data used to create the cluster
   * @param parentFrame parent frame
   * @param project open project
   */
  public ClusterComboFrame(File clustFile, ExpFile exp, int type, Frame parentFrame, Project project) {
        this.clustFile = clustFile;
        this.exp=exp;
        this.type=type;
        this.parentFrame=parentFrame;
        this.project = project;
        try {
            jbInit();
            this.addKeyListenerRecursively(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  private void jbInit() throws Exception {
    //gets the cluster data
    ListableCluster lc=null;
    if(type==TreeFrame.KMEANS) lc = new KMeansClust();
    if(type==TreeFrame.SUPERVISED) lc = new SupervisedQTClust();
    else lc = new QTClust();
    clusterData = lc.getDataInVector(clustFile);
    clusterListModel = new DefaultListModel();
    int length = clusterData.size();
    for(int i=0; i<length; i++){
      try{
        Vector elements = (Vector)clusterData.elementAt(i);
        clusterListModel.addElement(((NodeInfo)elements.firstElement()).toString());
      }catch(NoSuchElementException e1){}
    }

    //builds the lists
    elementListModel = new DefaultListModel();
    elementList = new JList(elementListModel);
    clusterList = new JList(clusterListModel);

    clusterList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e){
        clusterList_ValueChanged(e);
      }
    });

    //sets up the frame
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),1),"Choose A Cluster");
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(5);
    elementLabel = new JLabel();
    clusterLabel = new JLabel();
    mainPanel.setLayout(verticalLayout1);
    mainPanel.setBackground(new Color(204, 204, 204));
    mainPanel.setBorder(titledBorder1);
    clusterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    buttonPanel.setBackground(new Color(204, 204, 204));
    this.setClosable(true);
    this.setTitle("Displaying "+clustFile.getName());
    this.getContentPane().setBackground(new Color(204, 204, 204));
    this.getContentPane().setLayout(verticalLayout1);
    plotButton.setText("Plot Cluster As A Group");
    plotButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotButton_actionPerformed(e);
      }
    });
    closeButton.setText("Close");
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });
    cl = new JScrollPane(clusterList);
    el = new JScrollPane(elementList);

    clusterLabel.setBackground(new Color(204, 204, 204));
    clusterLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    clusterLabel.setForeground(Color.blue);
    clusterLabel.setText("Cluster");
    elementLabel.setBackground(new Color(204, 204, 204));
    elementLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    elementLabel.setForeground(Color.blue);
    elementLabel.setText("Element   -  Dissimilarity");
    labelPanel.setBackground(new Color(204, 204, 204));
    labelPanel.setForeground(Color.magenta);
    labelPanel.add(clusterLabel, null);
    labelPanel.add(elementLabel, null);

    listPanel.add(cl, null);
    listPanel.add(el, null);

    mainPanel.add(labelPanel, null);
    mainPanel.add(listPanel, null);

    this.getContentPane().add(mainPanel, BorderLayout.NORTH);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(plotButton, null);

    buttonPanel.add(closeButton, null);

  }

  /**
   * sets the cluster method
   * @param type cluster method
   */
  public void setType(int type){
    this.type=type;
  }

  //changes the element list based on the selection in the cluster list
  private void clusterList_ValueChanged(ListSelectionEvent e){
      if(e.getValueIsAdjusting()) return;
      JList l = (JList)e.getSource();
      if(l.isSelectionEmpty()){
        elementListModel.removeAllElements();
      }
      else{
        int index=l.getSelectedIndex();
        Vector elements = (Vector)clusterData.get(index);
        int s = elements.size();
        elementListModel.removeAllElements();
        for(int i=1; i<s; i++){
          NodeInfo ni = (NodeInfo)elements.elementAt(i);
          elementListModel.addElement(ni.toString()+" - "+ni.getDistance());
        }
      }
  }

//plots the selected genes
  private void plotButton_actionPerformed(ActionEvent e) {
        if(clusterList.isSelectionEmpty()) return;
        int c = clusterList.getSelectedIndex();
        Vector clust = (Vector)clusterData.elementAt(c);
        GrpFile group = new GrpFile(clustFile.getName()+"_"+((NodeInfo)clust.firstElement()).toString());
        int max = clust.size();
        if(!elementList.isSelectionEmpty()) max = elementList.getSelectedIndex()+1;
        for(int i=0; i<max; i++){
          group.addOne(((NodeInfo)clust.elementAt(i)).toString());
        }

        PlotFrame plotFrame = new PlotFrame(group, exp,parentFrame, project);
        this.getDesktopPane().add(plotFrame);
        plotFrame.show();
  }

  //closes the window
  private void closeButton_actionPerformed(ActionEvent e) {
      dispose();
  }


  private void addKeyListenerRecursively(Component c){
      c.removeKeyListener(this);
      c.addKeyListener(this);
      if(c instanceof Container){
        Container cont = (Container)c;
        Component[] children = cont.getComponents();
        for(int i = 0; i < children.length; i++){
          addKeyListenerRecursively(children[i]);
        }
      }
  }

  /**
     * Closes the frame when user press control + 'w'
     * @param e key event
     */
   public void keyPressed(KeyEvent e){
        if(e.getKeyCode()== KeyStroke.getKeyStroke(KeyEvent.VK_W,KeyEvent.CTRL_MASK).getKeyCode()&&e.isControlDown()){
          this.dispose();
        }
   }

   /**
     * Not implemented in this frame
     * @param e key event
     */
   public void keyReleased(KeyEvent e){}

   /**
     * Not implemented in this frame
     * @param e key event
     */
   public void keyTyped(KeyEvent e){}



}