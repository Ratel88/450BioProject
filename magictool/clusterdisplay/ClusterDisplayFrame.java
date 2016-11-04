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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import magictool.ExpFile;
import magictool.FileComboBox;
import magictool.Project;
import magictool.VerticalLayout;
import magictool.cluster.AbstractCluster;
import magictool.cluster.ClusterExportFrame;



/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
  * ClusterDisplayFrame is an internal frame that allows the user to select and display
  * a cluster file. The frame also displays information about the cluster file selected.
 */

public class ClusterDisplayFrame extends JInternalFrame implements KeyListener{

  private JPanel contentPane = new JPanel();
  private JPanel cardPanel = new JPanel();
  private CardLayout cardLayout1 = new CardLayout();
  private JPanel hiPane = new JPanel();
  private JPanel qtPane = new JPanel();
  private JPanel kmeansPane = new JPanel();
  private VerticalLayout verticalLayout5 = new VerticalLayout();
  private JPanel titlePanel = new JPanel();
  private TitledBorder titledBorder1;
  private VerticalLayout verticalLayout3 = new VerticalLayout();
  private JPanel hiTitlePanel = new JPanel();
  private JPanel noPane = new JPanel();
  private JPanel qtTitlePanel = new JPanel();
  private VerticalLayout verticalLayout4 = new VerticalLayout();
  private JRadioButton powerButton = new JRadioButton();
  private JLabel powerLabel = new JLabel();
  private JPanel selectPanel1 = new JPanel();
  private JLabel disFileInfo = new JLabel();
  private JLabel disMethodLabel = new JLabel();
  private JPanel bClustPanel = new JPanel();
  private JLabel bMeth = new JLabel();
  private JPanel extendInfoPanel = new JPanel();
  private JPanel basicInfoPanel = new JPanel();
  private JLabel clustMethod = new JLabel();
  private JLabel clustMethodLabel = new JLabel();
  private JPanel bParamPanel = new JPanel();
  private JPanel numPanel = new JPanel();
  private JPanel expFileInfoPanel = new JPanel();
  private JPanel disFileInfoPanel = new JPanel();
  private JPanel disParamPanel = new JPanel();
  private JLabel expFileInfo = new JLabel();
  private JLabel disParam = new JLabel();
  private JPanel expInfoPanel = new JPanel();
  private JPanel clustParamPanel = new JPanel();
  private JPanel bNumPanel = new JPanel();
  private JLabel disMethod = new JLabel();
  private JLabel bParam = new JLabel();
  private JPanel clustInfoPanel = new JPanel();
  private JLabel bMethLabel = new JLabel();
  private JPanel disMethodPanel = new JPanel();
  private VerticalLayout verticalLayout2 = new VerticalLayout();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private FlowLayout extFlowLayout = new FlowLayout();
  private JLabel bParamLabel = new JLabel();
  private JLabel numLabel = new JLabel();
  private JLabel expFileInfoLabel = new JLabel();
  private JPanel clustMethodPanel = new JPanel();
  private JLabel disFileInfoLabel = new JLabel();
  private JTabbedPane TabbedClust = new JTabbedPane();
  private JLabel clustParam = new JLabel();
  private JLabel disParamLabel = new JLabel();
  private JLabel bNum = new JLabel();
  private JLabel clustParamLabel = new JLabel();
  private JLabel num = new JLabel();
  private JPanel disInfoPanel = new JPanel();
  private JLabel bNumLabel = new JLabel();
  private VerticalLayout extVerticalLayout = new VerticalLayout();
  private VerticalLayout verticalLayout6 = new VerticalLayout();
  private TitledBorder titledBorder2;
  private JButton hiDispButton = new JButton();
  private TitledBorder titledBorder3;
  private JButton qtDispButton = new JButton();
  private JButton qtDispButton2 = new JButton();
  private JButton hiDisp2Button = new JButton();
  private JButton hiDisp3Button = new JButton();
  private JPanel kmeanTitlePanel = new JPanel();
  private VerticalLayout verticalLayout7 = new VerticalLayout();
  private JButton kListButton = new JButton();
  private JButton kTreeButton = new JButton();
  private TitledBorder titledBorder4;
  private FileComboBox clustBox;
  private JLabel jLabel1 = new JLabel();
  private Border border1;
  /**project*/
  protected Project project;
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton qtDisp3Button = new JButton();
  private JButton kTTButton = new JButton();
  /**parent frame*/
  protected Frame parentFrame;

  /**
   * Constructs the frame allowing user to choose cluster files from given project
   * @param p project to select files from
   * @param parentFrame parent frame
   */

  public ClusterDisplayFrame(Project p, Frame parentFrame) {
    project=p;
    this.parentFrame=parentFrame;
    clustBox = new FileComboBox(project, Project.CLUST,"all",true,"Cluster");
    try {
      jbInit();
      addKeyListenerRecursively(this);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {

    //constructs the frame
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Display Options");
    titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Hierarchical Display");
    titledBorder3 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"QTClust Display");
    titledBorder4 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"KMeansClust Display");
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(148, 145, 140),new Color(103, 101, 98)),BorderFactory.createEmptyBorder(3,3,3,3));
    this.setClosable(true);
    contentPane.setLayout(verticalLayout5);
    cardPanel.setLayout(cardLayout1);
    hiPane.setLayout(verticalLayout3);
    qtPane.setLayout(verticalLayout4);
    titlePanel.setBorder(titledBorder1);
    titlePanel.setLayout(verticalLayout6);
    selectPanel1.setLayout(borderLayout1);
    selectPanel1.setBorder(BorderFactory.createEtchedBorder());
    disMethodLabel.setText("Dissimilarity Method:");
    disMethodLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    bClustPanel.setLayout(extFlowLayout);
    extendInfoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    extendInfoPanel.setLayout(verticalLayout1);
    basicInfoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    basicInfoPanel.setLayout(verticalLayout2);
    clustMethodLabel.setText("Cluster Method:");
    clustMethodLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    bParamPanel.setLayout(extFlowLayout);
    numPanel.setLayout(extFlowLayout);
    expFileInfoPanel.setLayout(extFlowLayout);
    disFileInfoPanel.setLayout(extFlowLayout);
    disParamPanel.setLayout(extFlowLayout);
    expInfoPanel.setLayout(extVerticalLayout);
    expInfoPanel.setBorder(BorderFactory.createEtchedBorder());
    clustParamPanel.setLayout(extFlowLayout);
    bNumPanel.setLayout(extFlowLayout);
    clustInfoPanel.setLayout(extVerticalLayout);
    clustInfoPanel.setBorder(BorderFactory.createEtchedBorder());
    bMethLabel.setText("Cluster Method:");
    disMethodPanel.setLayout(extFlowLayout);
    verticalLayout2.setVgap(15);
    extFlowLayout.setAlignment(FlowLayout.LEFT);
    extFlowLayout.setVgap(0);
    bParamLabel.setText("Parameters:");
    numLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    numLabel.setText("Number of genes:");
    expFileInfoLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    expFileInfoLabel.setText("Original .exp File:");
    clustMethodPanel.setLayout(extFlowLayout);
    disFileInfoLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    disFileInfoLabel.setText("Original .dis File:");
    disParamLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    disParamLabel.setText("Parameters:");
    clustParamLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    clustParamLabel.setText("Parameters:");
    disInfoPanel.setLayout(extVerticalLayout);
    disInfoPanel.setBorder(BorderFactory.createEtchedBorder());
    bNumLabel.setText("Number of Genes:");
    extVerticalLayout.setVgap(0);
    hiTitlePanel.setBorder(titledBorder2);
    hiDispButton.setText("Exploding Tree");
    hiDispButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hiDispButton_actionPerformed(e);
      }
    });
    qtTitlePanel.setBorder(titledBorder3);
    qtDispButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        qtDispButton_actionPerformed(e,1);
      }
    });
    qtDispButton.setText("Exploding Tree");
    qtDispButton2.setText("List");
    qtDispButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        qtDispButton_actionPerformed(e,0);
      }
    });
    hiDisp2Button.setText("Metric Tree");
    hiDisp2Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hiDisp2Button_actionPerformed(e);
      }
    });
    hiDisp3Button.setText("Tree/Table");
    hiDisp3Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hiDisp3Button_actionPerformed(e);
      }
    });

    kmeansPane.setLayout(verticalLayout7);
    kListButton.setText("List");
    kListButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        kDispButton_actionPerformed(e,0);
      }
    });
    kTreeButton.setText("Exploding Tree");
    kTreeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        kDispButton_actionPerformed(e,1);
      }
    });
    kmeanTitlePanel.setBorder(titledBorder4);
    jLabel1.setBorder(border1);
    jLabel1.setText("Clust File");
    clustBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        clustBox_itemStateChanged(e);
      }
    });
    qtDisp3Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        qtDisp3Button_actionPerformed(e);
      }
    });
    qtDisp3Button.setText("Tree/Table");
    kTTButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        kTTButton_actionPerformed(e);
      }
    });
    kTTButton.setText("Tree/Table");
    hiTitlePanel.add(hiDisp2Button, null);
    hiPane.add(hiTitlePanel, null);
    hiTitlePanel.add(hiDispButton, null);
    hiTitlePanel.add(hiDisp3Button, null);
    contentPane.add(titlePanel, null);
    selectPanel1.add(jLabel1, BorderLayout.WEST);
    selectPanel1.add(clustBox, BorderLayout.CENTER);
    clustParamPanel.add(clustParamLabel, null);
    clustParamPanel.add(clustParam, null);
    clustInfoPanel.add(clustMethodPanel, null);
    clustInfoPanel.add(clustParamPanel, null);
    clustMethodPanel.add(clustMethodLabel, null);
    clustMethodPanel.add(clustMethod, null);
    extendInfoPanel.add(disInfoPanel, null);
    extendInfoPanel.add(clustInfoPanel, null);
    disMethodPanel.add(disMethodLabel, null);
    disMethodPanel.add(disMethod, null);
    disInfoPanel.add(disFileInfoPanel, null);
    disInfoPanel.add(disMethodPanel, null);
    disFileInfoPanel.add(disFileInfoLabel, null);
    disFileInfoPanel.add(disFileInfo, null);
    disInfoPanel.add(disParamPanel, null);
    disParamPanel.add(disParamLabel, null);
    disParamPanel.add(disParam, null);
    extendInfoPanel.add(expInfoPanel, null);
    numPanel.add(numLabel, null);
    numPanel.add(num, null);
    expInfoPanel.add(expFileInfoPanel, null);
    expInfoPanel.add(numPanel, null);
    expFileInfoPanel.add(expFileInfoLabel, null);
    expFileInfoPanel.add(expFileInfo, null);
    TabbedClust.add(basicInfoPanel, "Basic Info");
    TabbedClust.add(extendInfoPanel, "Extended Info");
    bClustPanel.add(bMethLabel, null);
    bClustPanel.add(bMeth, null);
    basicInfoPanel.add(bNumPanel, null);
    basicInfoPanel.add(bClustPanel, null);
    bNumPanel.add(bNumLabel, null);
    bNumPanel.add(bNum, null);
    basicInfoPanel.add(bParamPanel, null);
    bParamPanel.add(bParamLabel, null);
    bParamPanel.add(bParam, null);
    titlePanel.add(selectPanel1, null);
    titlePanel.add(TabbedClust, null);
    this.getContentPane().add(contentPane,  BorderLayout.CENTER);
    contentPane.add(cardPanel, null);
    cardPanel.add("noPane", noPane);
    cardPanel.add("hiPane", hiPane);
    cardPanel.add("qtPane",qtPane);
    qtPane.add(qtTitlePanel, null);

    qtTitlePanel.add(qtDispButton2, null);
    qtTitlePanel.add(qtDispButton, null);
    qtTitlePanel.add(qtDisp3Button, null);
    cardPanel.add("kmeansPane", kmeansPane);
    kmeansPane.add(kmeanTitlePanel, null);

    kmeanTitlePanel.add(kTreeButton, null);
    kmeanTitlePanel.add(kListButton, null);
    kmeanTitlePanel.add(kTTButton, null);
    bParamPanel.add(bParamLabel, null);
    bParamPanel.add(bParam, null);
    bNumPanel.add(bNumLabel, null);
    bNumPanel.add(bNum, null);

  }


  //creates tree frame of hierarchical cluster
  private void hiDispButton_actionPerformed(ActionEvent e) {
      Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          TreeFrame viewwindow = new TreeFrame(new File(clustBox.getFilePath()),0,new ExpFile(new File(getExpFile())),parentFrame, project);
          getDesktopPane().add(viewwindow);
          viewwindow.setVisible(true);
          viewwindow.setBounds(100, 100, 450, 300);
          viewwindow.show();
          viewwindow.toFront();
          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
  }


  //returns the full expression file path
  private String getExpFile(){
    return project.getPath() + expFileInfo.getText().substring(0,expFileInfo.getText().lastIndexOf("."))+ File.separator + expFileInfo.getText();
  }

  /**
   * reads the cluster file to gather information about it from the file header
   */
  protected void setInfo(){

    try{

        String[] info = AbstractCluster.readHeaders(clustBox.getFilePath());

        bNum.setText(info[0]);//number of genes
        num.setText(bNum.getText());

        expFileInfo.setText(info[1].substring(info[1].lastIndexOf(File.separator)+1));

        // dissimilarity method
        disMethod.setText(info[2]);
        disParam.setText(info[3]);

        disFileInfo.setText(info[4]);

        bMeth.setText(info[5]);
        bParam.setText(info[6]);
        clustMethod.setText(info[5]);

        clustParam.setText(info[6]);
        if(info[5].equals(new String("Hierarchical")))
          cardLayout1.show(cardPanel,"hiPane");

        else if(info[5].equals(new String("QTCluster")))
          cardLayout1.show(cardPanel,"qtPane");

        else if(info[5].equals(new String("KMeans")))
          cardLayout1.show(cardPanel,"kmeansPane");

        else if(info[5].equals(new String("Supervised QTCluster")))
          cardLayout1.show(cardPanel,"qtPane");

        else cardLayout1.show(cardPanel, "noPane");

    }
    catch(Exception e){}

  }

  //displays for qt cluster
  private void qtDispButton_actionPerformed(ActionEvent e, int style) {
      final int st = style;
      Thread thread = new Thread(){
        public void run(){
            if(st==0){
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              ClusterComboFrame viewwindow = new ClusterComboFrame(new File(clustBox.getFilePath()),new ExpFile(new File(getExpFile())), parentFrame, project);
              getDesktopPane().add(viewwindow);
              viewwindow.setVisible(true);
              viewwindow.setBounds(100, 100, 480, 320);
              viewwindow.show();
              setCursor(Cursor.getDefaultCursor());
            }
            else{
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              TreeFrame viewwindow = new TreeFrame(new File(clustBox.getFilePath()),1,new ExpFile(new File(getExpFile())),parentFrame, project);
              getDesktopPane().add(viewwindow);
              viewwindow.setVisible(true);
              viewwindow.setBounds(100, 100, 450, 280);
              viewwindow.show();
              setCursor(Cursor.getDefaultCursor());
            }
        }
      };
      thread.start();

  }

  //displays metric tree for hierarchical
  private void hiDisp2Button_actionPerformed(ActionEvent e) {
      Thread thread = new Thread(){
        public void run(){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            MetricTreeFrame viewwindow = new MetricTreeFrame(new File(clustBox.getFilePath()),new ExpFile(new File(getExpFile())), parentFrame, project);
            getDesktopPane().add(viewwindow);
            viewwindow.setVisible(true);
            viewwindow.setBounds(100, 100, 450, 300);
            viewwindow.show();
            setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();

  }

  //displays metric tree and colored table for hierarchical
  private void hiDisp3Button_actionPerformed(ActionEvent e) {
      Thread thread = new Thread(){
        public void run() {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          MetricTreeTableFrame viewwindow = new MetricTreeTableFrame(new File(clustBox.getFilePath()), new ExpFile(new File(getExpFile())), parentFrame, project);
          getDesktopPane().add(viewwindow);
          viewwindow.setVisible(true);
          viewwindow.setBounds(100, 100, 450, 300);
          viewwindow.show();
          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
  }

  //kmeans cluster
  private void kDispButton_actionPerformed(ActionEvent e, int style) {
      final int st = style;
      Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              if(st==0){
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  ClusterComboFrame viewwindow = new ClusterComboFrame(new File(clustBox.getFilePath()),new ExpFile(new File(getExpFile())), TreeFrame.KMEANS,parentFrame, project);
                  getDesktopPane().add(viewwindow);
                  viewwindow.setVisible(true);
                  viewwindow.setBounds(100, 100, 480, 320);
                  viewwindow.show();
                  setCursor(Cursor.getDefaultCursor());
              }
              else{
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                TreeFrame viewwindow = new TreeFrame(new File(clustBox.getFilePath()),TreeFrame.KMEANS,new ExpFile(new File(getExpFile())),parentFrame, project);
                getDesktopPane().add(viewwindow);
                viewwindow.setVisible(true);
                viewwindow.setBounds(100, 100, 450, 280);
                viewwindow.show();
                setCursor(Cursor.getDefaultCursor());
              }
        }
      };
      thread.start();
  }

  //changes info about cluster file
  private void clustBox_itemStateChanged(ItemEvent e) {
    setInfo();
  }

  //colored table and dissimilarity list
  private void qtDisp3Button_actionPerformed(ActionEvent e) {
      Thread thread = new Thread(){
        public void run(){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            MetricTreeTableFrame viewwindow = new MetricTreeTableFrame(new File(clustBox.getFilePath()), new ExpFile(new File(getExpFile())),parentFrame, project);
            getDesktopPane().add(viewwindow);
            viewwindow.setVisible(true);
            viewwindow.setBounds(100, 100, 450, 300);
            viewwindow.show();
            setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
  }

  //colored table and dissimilarity list
  private void kTTButton_actionPerformed(ActionEvent e) {
      Thread thread = new Thread(){
        public void run(){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            MetricTreeTableFrame viewwindow = new MetricTreeTableFrame(new File(clustBox.getFilePath()), new ExpFile(new File(getExpFile())),parentFrame, project);
            getDesktopPane().add(viewwindow);
            viewwindow.setVisible(true);
            viewwindow.setBounds(100, 100, 450, 300);
            viewwindow.show();
            setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
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