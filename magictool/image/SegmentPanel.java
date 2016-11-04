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

package magictool.image;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import magictool.VerticalLayout;

/**
 * SegmentPanel is a panel which displays the graphical user interface for users to
 * select the desired segmentation method and parameters for creating or appending an
 * expression file.
 */
public class SegmentPanel extends JPanel {
   
   private VerticalLayout verticalLayout3 = new VerticalLayout();
   private JLabel chooseGridLabel = new JLabel();
   private JLabel chooseSpotLabel = new JLabel();
   private JPanel gridSpotPanel = new JPanel();
   private VerticalLayout verticalLayout6 = new VerticalLayout();
   private ButtonGroup segmentMethods = new ButtonGroup();
   private JTextField gridNumText = new JTextField();
   private JPanel gridNumPanel = new JPanel();
   private BorderLayout borderLayout1 = new BorderLayout();
   private JButton prevGridButton = new JButton();
   private JButton nextGridButton = new JButton();
   private JButton nextSpotButton = new JButton();
   private JButton prevSpotButton = new JButton();
   private JTextField spotNumText = new JTextField();
   private JPanel spotNumPanel = new JPanel();
   private BorderLayout borderLayout2 = new BorderLayout();
   private VerticalLayout verticalLayout7 = new VerticalLayout();
   private JPanel segmentMethodOuterPanel = new JPanel();
   private JPanel segmentMethodPanel = new JPanel();
   private VerticalLayout verticalLayout2 = new VerticalLayout();
   private VerticalLayout verticalLayout1 = new VerticalLayout();
   private JLabel chooseMethodLabel = new JLabel();
   private JComboBox methodCombo = new JComboBox();
   private JPanel paramPanel = new JPanel();
   private CardLayout paramCard = new CardLayout();
   private JPanel fixradiusPanel = new JPanel();
   private JLabel fixradiusUnits = new JLabel();
   private VerticalLayout verticalLayout4 = new VerticalLayout();
   private JLabel fixradiusLabel = new JLabel();
   private BorderLayout borderLayout3 = new BorderLayout();
   private JTextField fixradiusField = new JTextField();
   private JPanel fixedCircleSubPanel = new JPanel();
   private JPanel seededRegionSubPanel = new JPanel();
   private JSlider seedthresholdSlider = new JSlider();
   private VerticalLayout verticalLayout10 = new VerticalLayout();
   private JLabel seedthreshLabel = new JLabel();
   private JPanel seedthresholdPanel = new JPanel();
   private BorderLayout borderLayout4 = new BorderLayout();
   private JPanel adaptSubPanel = new JPanel();
   private VerticalLayout verticalLayout13 = new VerticalLayout();
   private JPanel maxradiusPanel = new JPanel();
   private JLabel maxradiusUnits = new JLabel();
   private JLabel maxradiusLabel = new JLabel();
   private JTextField maxradiusField = new JTextField();
   private BorderLayout borderLayout5 = new BorderLayout();
   private JPanel minradiusPanel = new JPanel();
   private JLabel minradiusUnits = new JLabel();
   private JLabel minradiusLabel = new JLabel();
   private BorderLayout borderLayout6 = new BorderLayout();
   private JTextField minradiusField = new JTextField();
   private JLabel adaptthreshLabel = new JLabel();
   private BorderLayout borderLayout7 = new BorderLayout();
   private JPanel adaptthresholdPanel = new JPanel();
   private JSlider adaptthresholdSlider = new JSlider();
   private Border border1;
   private TitledBorder titledBorder2;
   private JButton changeGeneButton = new JButton();
   private JPanel geneDataOuterPanel = new JPanel();
   private JLabel geneNameLabel = new JLabel();
   private VerticalLayout verticalLayout11 = new VerticalLayout();
   private VerticalLayout verticalLayout12 = new VerticalLayout();
   private JPanel geneDataPanel = new JPanel();
   private JLabel ratioLabel = new JLabel();
   private JLabel greenbgLabel = new JLabel();
   private JLabel greenfgLabel = new JLabel();
   private JLabel redbgLabel = new JLabel();
   private JLabel redfgLabel = new JLabel();
   private JLabel flaggedLabel = new JLabel(); //added 6-11-2007 by Michael Gordon
   private JButton updateButton = new JButton();
   private JButton changeButton = new JButton();
   private JPanel ratioMethodOuterPanel = new JPanel();
   private JLabel ratioMethodLabel = new JLabel();
   private VerticalLayout verticalLayout8 = new VerticalLayout();
   private VerticalLayout verticalLayout9 = new VerticalLayout();
   private JPanel ratioMethodPanel = new JPanel();
   private JComboBox ratioComboBox = new JComboBox();
   private JPanel savePanel = new JPanel();
   private VerticalLayout verticalLayout5 = new VerticalLayout();
   private JButton saveButton = new JButton();
   private TitledBorder titledBorder1;
   private JPanel autoFlaggingButtonPanel = new JPanel(); //added 6-11-2007 by Michael Gordon
   private JButton autoFlaggingButton = new JButton(); //added 6-11-2007 by Michael Gordon
   private JLabel autoFlaggingButtonLabel1 = new JLabel("Automatic Flagging", JLabel.CENTER);
   private JLabel autoFlaggingButtonLabel2 = new JLabel("Options", JLabel.CENTER);
   private SummaryStatsPanel summaryStatsPanel;

   
   private DecimalFormat df = new DecimalFormat("###.####");
   private GeneData gd;
   private int spotNum=0,gridNum=0;  //grid and spot number
   protected int ratioMethod = SingleGeneImage.TOTAL_SIGNAL;
   
   /**red image display*/
   protected SegmentDisplay rImage;
   /**green image display*/
   protected SegmentDisplay gImage;
   /**parent segment frame*/
   protected SegmentFrame segframe;
   /**grid manager for the microarray images*/
   protected GridManager manager;
   /**Automatic Flagging Options Dialog associated with the SegmentFrame associated with this SegmentPanel*/
   protected AutoFlaggingOptionsDialog afod;
   /**flag manager associated with these grids*/
   protected FlagManager flagman;
   
   
   /**
    * Constructs a SegmentPanel with the given grid manager in the specified segment frame
    * @param m grid manager that manages the gridding of the microarray images
    * @param idR red image display
    * @param idG green image display
    * @param sg parent segment frame
    */
   public SegmentPanel(GridManager m, FlagManager fm, SegmentDisplay idR, SegmentDisplay idG, SegmentFrame sg, AutoFlaggingOptionsDialog a) {	//modified 6/11/2007 by Michael Gordon
      this.setLayout(new BorderLayout());
      this.setBorder(BorderFactory.createRaisedBevelBorder());
      this.manager = m;
      this.flagman = fm;
      this.rImage = idR;
      this.gImage = idG;
      this.segframe=sg;
      this.afod = a;	//added 6/11/2007 by Michael Gordon
      
      try {
         jbInit();
      } catch(Exception e) {
         e.printStackTrace();
      }
      
      this.validate();
      this.setPreferredSize(new Dimension(200,getPreferredSize().height));
      
      
   }
   
   private void jbInit() throws Exception {
	  summaryStatsPanel = new SummaryStatsPanel(this, segframe);
      titledBorder1 = new TitledBorder("");
      border1 = BorderFactory.createEtchedBorder(Color.white,new Color(165, 163, 151));
      titledBorder2 = new TitledBorder(border1,"Parameters");
      this.setLayout(verticalLayout7);
      methodCombo.addItem(new String("Fixed Circle"));
      methodCombo.addItem(new String("Seeded Region Growing"));
      methodCombo.addItem(new String("Adaptive Circle"));
      chooseGridLabel.setPreferredSize(new Dimension(186, 17));
      chooseGridLabel.setToolTipText("");
      chooseGridLabel.setText("Grid Number:");
      chooseSpotLabel.setText("Spot Number:");
      gridSpotPanel.setLayout(verticalLayout6);
      gridSpotPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      gridNumText.setPreferredSize(new Dimension(48, 21));
      gridNumText.setText("1");
      gridNumText.setHorizontalAlignment(SwingConstants.CENTER);
      
      gridNumPanel.setLayout(borderLayout1);
      prevGridButton.setText("Prev.");
      prevGridButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            prevGridButton_actionPerformed(e);
         }
      });
      nextGridButton.setText("Next");
      nextGridButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            nextGridButton_actionPerformed(e);
         }
      });
      nextSpotButton.setText("Next");
      nextSpotButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            nextSpotButton_actionPerformed(e);
         }
      });
      prevSpotButton.setText("Prev.");
      prevSpotButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            prevSpotButton_actionPerformed(e);
         }
      });
      spotNumText.setHorizontalAlignment(SwingConstants.CENTER);
      spotNumText.setText("1");
      manager.getCurrentGrid().setCurrentSpot(0);
      spotNumPanel.setLayout(borderLayout2);
      segmentMethodOuterPanel.setLayout(verticalLayout2);
      segmentMethodOuterPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      segmentMethodPanel.setBorder(BorderFactory.createEtchedBorder());
      segmentMethodPanel.setLayout(verticalLayout1);
      chooseMethodLabel.setText("Choose Segmentation Method:");
      geneDataOuterPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      geneDataOuterPanel.setLayout(verticalLayout11);
      geneNameLabel.setText("Expression Data For:");
      geneDataPanel.setBorder(BorderFactory.createEtchedBorder());
      geneDataPanel.setLayout(verticalLayout12);
      ratioLabel.setText("Ratio:");
      greenbgLabel.setText("Green Background:");
      greenfgLabel.setText("Green Foreground:");
      redbgLabel.setText("Red Background:");
      redfgLabel.setText("Red Foreground:");
      updateButton.setText("Update Data");
      updateButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            updateButton_actionPerformed(e);
         }
      });
      changeButton.setText("Jump To Spot");
      changeButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            changeButton_actionPerformed(e);
         }
      });
      ratioMethodOuterPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      ratioMethodOuterPanel.setLayout(verticalLayout8);
      ratioMethodLabel.setText("Choose Ratio Method:");
      ratioMethodPanel.setBorder(BorderFactory.createEtchedBorder());
      ratioMethodPanel.setLayout(verticalLayout9);
      ratioComboBox.addItem("Total Signal");
      ratioComboBox.addItem("Average Signal");
      ratioComboBox.addItem("Total Signal BG Subtraction");
      ratioComboBox.addItem("Average Signal BG Subtraction");
      ratioComboBox.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            ratioComboBox_itemStateChanged(e);
         }
      });
      savePanel.setLayout(verticalLayout5);
      savePanel.setBorder(BorderFactory.createLineBorder(Color.black));
      /*begin added 6/11/2007 by Michael Gordon*/
      autoFlaggingButtonPanel.setLayout(verticalLayout5);
      autoFlaggingButtonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      /*end added 6/11/2007 by Michael Gordon*/
      saveButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            saveButton_actionPerformed(e);
         }
      });
      saveButton.setText("Create Expression File");
      paramPanel.setLayout(paramCard);
      fixradiusPanel.setLayout(borderLayout3);
      fixradiusUnits.setText("pixels");
      verticalLayout4.setAlignment(VerticalLayout.MIDDLE);
      verticalLayout4.setHgap(3);
      fixradiusLabel.setText("(Fixed) Radius = ");
      fixradiusField.setText("6");
      fixradiusField.setHorizontalAlignment(SwingConstants.CENTER);
      fixedCircleSubPanel.setLayout(verticalLayout4);
      seededRegionSubPanel.setLayout(verticalLayout10);
      seedthresholdSlider.setMajorTickSpacing(15);
      seedthresholdSlider.setMaximum(50);
      seedthresholdSlider.setMinimum(5);
      seedthresholdSlider.setMinorTickSpacing(5);
      seedthresholdSlider.setPaintLabels(true);
      seedthresholdSlider.setPaintTicks(true);
      seedthresholdSlider.setValue(10);
      seedthreshLabel.setText("Threshold");
      seedthresholdPanel.setLayout(borderLayout4);
      adaptSubPanel.setLayout(verticalLayout13);
      maxradiusPanel.setLayout(borderLayout5);
      maxradiusUnits.setText("pixels");
      maxradiusLabel.setText("Max Radius = ");
      maxradiusField.setText("8");
      maxradiusField.setHorizontalAlignment(SwingConstants.CENTER);
      paramPanel.setBorder(titledBorder2);
      minradiusPanel.setLayout(borderLayout6);
      minradiusUnits.setText("pixels");
      minradiusLabel.setText("Min Radius = ");
      minradiusField.setText("3");
      minradiusField.setHorizontalAlignment(SwingConstants.CENTER);
      adaptthreshLabel.setText("Threshold");
      adaptthresholdPanel.setLayout(borderLayout7);
      adaptthresholdSlider.setMajorTickSpacing(25);
      adaptthresholdSlider.setMaximum(75);
      adaptthresholdSlider.setMinimum(25);
      adaptthresholdSlider.setMinorTickSpacing(10);
      adaptthresholdSlider.setPaintLabels(true);
      adaptthresholdSlider.setPaintTicks(true);
      methodCombo.addItemListener(new java.awt.event.ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            methodCombo_itemStateChanged(e);
         }
      });
      verticalLayout10.setAlignment(VerticalLayout.MIDDLE);
      verticalLayout13.setAlignment(VerticalLayout.MIDDLE);
      changeGeneButton.setText("Jump To Gene Name");
      changeGeneButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            changeGeneButton_actionPerformed(e);
         }
      });
      adaptthresholdPanel.add(adaptthreshLabel, BorderLayout.WEST);
      adaptthresholdPanel.add(adaptthresholdSlider, BorderLayout.CENTER);
      adaptSubPanel.add(minradiusPanel, null);
      adaptSubPanel.add(maxradiusPanel, null);
      adaptSubPanel.add(adaptthresholdPanel, null);
      minradiusPanel.add(minradiusLabel, BorderLayout.WEST);
      minradiusPanel.add(minradiusField, BorderLayout.CENTER);
      minradiusPanel.add(minradiusUnits, BorderLayout.EAST);
      ratioMethodPanel.add(ratioComboBox, null);
      
      geneDataPanel.add(redfgLabel, null);
      geneDataPanel.add(redbgLabel, null);
      geneDataPanel.add(greenfgLabel, null);
      geneDataPanel.add(greenbgLabel, null);
      geneDataPanel.add(ratioLabel, null);
      geneDataPanel.add(flaggedLabel, null);	//added 6-11-2007 by Michael Gordon
      flaggedLabel.setText("<html>Flagging Status: </html>");
      
      spotNumPanel.add(spotNumText, BorderLayout.CENTER);
      spotNumPanel.add(prevSpotButton, BorderLayout.WEST);
      spotNumPanel.add(nextSpotButton, BorderLayout.EAST);
      gridSpotPanel.add(changeGeneButton, null);
      gridSpotPanel.add(chooseGridLabel, null);
      gridSpotPanel.add(gridNumPanel, null);
      gridNumPanel.add(gridNumText, BorderLayout.CENTER);
      gridNumPanel.add(prevGridButton, BorderLayout.WEST);
      gridNumPanel.add(nextGridButton, BorderLayout.EAST);
      gridSpotPanel.add(chooseSpotLabel, null);
      gridSpotPanel.add(spotNumPanel, null);
      gridSpotPanel.add(changeButton, null);
      
      
      
      segmentMethodOuterPanel.add(chooseMethodLabel, null);
      segmentMethodOuterPanel.add(segmentMethodPanel, null);
      segmentMethodPanel.add(methodCombo, null);
      segmentMethodOuterPanel.add(updateButton, null);
      
      prevGridButton.setEnabled(false);
      prevSpotButton.setEnabled(false);
      geneDataOuterPanel.add(geneNameLabel, null);
      geneDataOuterPanel.add(geneDataPanel, null);
      ratioMethodOuterPanel.add(ratioMethodLabel, null);
      ratioMethodOuterPanel.add(ratioMethodPanel, null);
      savePanel.add(saveButton, null);
      
      //begin added 6-11-2007 by Michael Gordon
      //autoFlaggingButton.setText("<html><center>Automatic Flagging</center><P><center>Options</center></html>");
      autoFlaggingButton.setLayout(new BorderLayout());
      autoFlaggingButton.add(BorderLayout.NORTH, autoFlaggingButtonLabel1);
      autoFlaggingButton.add(BorderLayout.SOUTH, autoFlaggingButtonLabel2);
      autoFlaggingButton.addActionListener(new java.awt.event.ActionListener() {
    	  public void actionPerformed(ActionEvent e) {
    		  autoFlaggingButton_actionPerformed(e);
    	  }
      });
      autoFlaggingButtonPanel.add(autoFlaggingButton, null);
      //end added 6-11-2007 by Michael Gordon
      
      
      
      this.add(segmentMethodOuterPanel, null);
      this.add(ratioMethodOuterPanel, null);
      this.add(gridSpotPanel, null);
      this.add(geneDataOuterPanel, null);
      this.add(summaryStatsPanel, null);	//added 6/18/2008 by Michael Gordon
      this.add(autoFlaggingButtonPanel,null);	//added 6-11-2007 by Michael Gordon
      this.add(savePanel, null);
      segmentMethodPanel.add(paramPanel, null);
      fixradiusPanel.add(fixradiusLabel, BorderLayout.WEST);
      fixradiusPanel.add(fixradiusField, BorderLayout.CENTER);
      fixradiusPanel.add(fixradiusUnits, BorderLayout.EAST);
      paramPanel.add(seededRegionSubPanel,  "seeded");
      seededRegionSubPanel.add(seedthresholdPanel, null);
      seedthresholdPanel.add(seedthreshLabel, BorderLayout.WEST);
      seedthresholdPanel.add(seedthresholdSlider, BorderLayout.CENTER);
      paramPanel.add(fixedCircleSubPanel, "fixed");
      fixedCircleSubPanel.add(fixradiusPanel, null);
      paramPanel.add(adaptSubPanel, "adapt");
      maxradiusPanel.add(maxradiusLabel, BorderLayout.WEST);
      maxradiusPanel.add(maxradiusField, BorderLayout.CENTER);
      maxradiusPanel.add(maxradiusUnits, BorderLayout.EAST);
      
      paramCard.show(paramPanel,"fixed");
   }
   
   /**
    * sets the current spot to the specified grid and (transformed) spot number
    * @param grid grid number
    * @param spot transformed spot number
    */
   public void setSpot(int grid, int spot){
      if(grid >= 0 && grid < manager.getNumGrids() && spot>=0 && spot<manager.getGrid(grid).getNumOfSpots()){
         gridNum=grid;
         spotNum=spot;
         manager.setCurrentGrid(grid);
         manager.getCurrentGrid().setCurrentSpot(manager.getActualSpotNum(grid,spot));
         this.segframe.showCurrentCell();
         
         updateGeneInfo();
         updateButtons();
         
         rImage.repaint();
         gImage.repaint();
         
      }
   }
   
   //updates the buttons
   private void updateButtons(){
      if(manager.getNumGrids() <= (manager.currentGridNum + 1)) nextGridButton.setEnabled(false);
      else nextGridButton.setEnabled(true);
      if (manager.currentGridNum<=0) prevGridButton.setEnabled(false);
      else prevGridButton.setEnabled(true);
      Grid temp = manager.getCurrentGrid();
      if(temp.getNumOfSpots() <= spotNum+1) nextSpotButton.setEnabled(false);
      else nextSpotButton.setEnabled(true);
      if (spotNum<1) prevSpotButton.setEnabled(false);
      else prevSpotButton.setEnabled(true);
   }
   
   private void nextGridButton_actionPerformed(ActionEvent e) {
      setSpot(gridNum+1, spotNum);
      gridNumText.setText(""+(gridNum+1));
      spotNumText.setText(""+(spotNum+1));
      
   }
   
   private void prevGridButton_actionPerformed(ActionEvent e) {
      setSpot(gridNum-1, spotNum);
      gridNumText.setText(""+(gridNum+1));
      spotNumText.setText(""+(spotNum+1));
   }
   
   private void nextSpotButton_actionPerformed(ActionEvent e) {
      setSpot(gridNum, spotNum+1);
      gridNumText.setText(""+(gridNum+1));
      spotNumText.setText(""+(spotNum+1));
   }
   
   private void prevSpotButton_actionPerformed(ActionEvent e) {
      setSpot(gridNum, spotNum-1);
      gridNumText.setText(""+(gridNum+1));
      spotNumText.setText(""+(spotNum+1));
   }
   
   /**
    * gets the ratio method
    * @return ratio method
    */
   public int getRatioMethod(){
      return ratioMethod;
   }
   
   /**
    * updates the data for the spot with the user specified parameters from the
    * graphical user interface
    */
   protected void updateGeneInfo(){
      geneNameLabel.setText("Data For: " + manager.getCurrentGeneName());
      SingleGeneImage currentGene = new SingleGeneImage(rImage.getCellPixels(),gImage.getCellPixels(),rImage.getCellHeight(), rImage.getCellWidth());
      
      gd = null;
      boolean flagStatus = flagman.checkFlag(gridNum, spotNum);
      int[] autoThresh;
      if (afod.getOK()) autoThresh = afod.getThresholds();
      else{
   	   autoThresh = new int[4];
   	   autoThresh[0] = -1;
   	   autoThresh[1] = Integer.MAX_VALUE;
   	   autoThresh[2] = -1;
   	   autoThresh[3] = Integer.MAX_VALUE;
      }
      
      if(methodCombo.getSelectedItem().toString().equals("Fixed Circle")){
         Object[] params = new Object[1];
         int rad;
         try{
            rad = Integer.parseInt(fixradiusField.getText().trim());
            if (rad<1){
               JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Fixed Circle Radius Must Have A Positive Integer Value.\nThe Radius Has Been Reset To The Default Value Of 6.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
               rad=6;
               fixradiusField.setText("6");
            }
         }catch(Exception e){
            JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Fixed Circle Radius Must Have A Positive Integer Value.\nThe Radius Has Been Reset To The Default Value Of 6.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
            rad=6;
            fixradiusField.setText("6");
         }
         params[0] = new Integer(rad);
         rImage.setFixedCircle(rad);
         gImage.setFixedCircle(rad);
         gd = currentGene.getData(SingleGeneImage.FIXED_CIRCLE,params);
      }
      
      else if(methodCombo.getSelectedItem().toString().equals("Seeded Region Growing")){
         Object[] params = new Object[1];
         params[0] = new Integer(seedthresholdSlider.getValue());
         gd = currentGene.getData(SingleGeneImage.SEEDED_REGION, params);
         rImage.setSeededRegion(currentGene.getCenterSpots(true));
         gImage.setSeededRegion(currentGene.getCenterSpots(false));
      }
      
      else if(methodCombo.getSelectedItem().toString().equals("Adaptive Circle")){
         int minr, maxr;
         try{
            minr = Integer.parseInt(minradiusField.getText().trim());
            maxr = Integer.parseInt(maxradiusField.getText().trim());
            if(minr<1||maxr<1||minr>maxr){
               JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Radii Must Be Positive Integer Values.\nThe Maximum Radius Must Also Be Greater Than Or Equal To The Minimum Radius.\nThe Radii Have Been Reset To The Default Values Of 3 and 8.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
               minr=3;
               maxr=8;
               minradiusField.setText(""+3);
               maxradiusField.setText(""+8);
            }
         }catch(Exception e){
            minr=3;
            maxr=8;
            minradiusField.setText(""+3);
            maxradiusField.setText(""+8);
            JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Radii Must Be Positive Integer Values.\nThe Radii Have Been Reset To The Default Values Of 3 and 8.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
         }
         Object[] params = new Object[3];
         params[0] = new Integer(minr);
         params[1] = new Integer(maxr);
         params[2] = new Integer(adaptthresholdSlider.getValue());
         gd = currentGene.getData(SingleGeneImage.ADAPTIVE_CIRCLE, params);
         rImage.setAdaptiveCircle(currentGene.getCenterAndRadius());
         gImage.setAdaptiveCircle(currentGene.getCenterAndRadius());
         
      }
      
      
      if(gd!=null){
         ratioLabel.setText("Ratio: " + df.format(gd.getRatio(ratioMethod)));
         if(flagStatus) flaggedLabel.setText("<html>Flagging Status: <font color=\"#0000FF\">MANUALLY FLAGGED</font></html>");
         else if ((gd.getRedForegroundTotal() < autoThresh[0]) || (gd.getRedBackgroundTotal() > autoThresh[1]) || (gd.getGreenForegroundTotal() < autoThresh[2]) || (gd.getGreenBackgroundTotal() > autoThresh[3])) flaggedLabel.setText("<html>Flagging Status: <font color=\"FF9900\">AUTOMATICALLY FLAGGED</font></html>");
         else flaggedLabel.setText("Flagging Status: Not Flagged");
         if(ratioMethod==SingleGeneImage.TOTAL_SIGNAL||ratioMethod==SingleGeneImage.TOTAL_SUBTRACT_BG){
            greenbgLabel.setText("Green BG Total: " + df.format(gd.getGreenBackgroundTotal()));
            greenfgLabel.setText("Green FG Total: " + df.format(gd.getGreenForegroundTotal()));
            redbgLabel.setText("Red BG Total: " + df.format(gd.getRedBackgroundTotal()));
            redfgLabel.setText("Red FG Total: " + df.format(gd.getRedForegroundTotal()));
         } else{
            greenbgLabel.setText("Green BG Avg: " + df.format(gd.getGreenBackgroundAvg()));
            greenfgLabel.setText("Green FG Avg: " + df.format(gd.getGreenForegroundAvg()));
            redbgLabel.setText("Red BG Avg: " + df.format(gd.getRedBackgroundAvg()));
            redfgLabel.setText("Red FG Avg: " + df.format(gd.getRedForegroundAvg()));
         }
      } else{
         ratioLabel.setText("Ratio: N/A");
         greenbgLabel.setText("Green Background: N/A");
         greenfgLabel.setText("Green Foreground: N/A");
         redbgLabel.setText("Red Background: N/A");
         redfgLabel.setText("Red Foreground: N/A");
         flaggedLabel.setText("Flagging Status: N/A");
      }
   }
   
   private void updateButton_actionPerformed(ActionEvent e) {
      updateGeneInfo();
      summaryStatsPanel.clearStats();
      if(segframe.allGeneData != null) segframe.allGeneData.invalidate();
      rImage.repaint();
      gImage.repaint();
   }
   
   private void changeButton_actionPerformed(ActionEvent e) {
      int g=-1, sp=-1;
      try{
         g = Integer.parseInt(gridNumText.getText().trim());
         sp = Integer.parseInt(spotNumText.getText().trim());
      } catch(Exception e1){}
      if(g > 0 && g <= manager.getNumGrids() && sp>0 && sp<=manager.getGrid(g-1).getNumOfSpots()){
         setSpot(g-1,sp-1);
      } else{
         JOptionPane.showMessageDialog(segframe.getDesktopPane(), "You Have Entered Incorrect Values For The Grid And/Or Spot Number.\nThe Values Have Been Reset To The Current Spot.", "Error!", JOptionPane.ERROR_MESSAGE);
         gridNumText.setText("" + (gridNum+1));
         spotNumText.setText("" + (spotNum+1));
      }
   }
     
   /**
    * a protected function causing SegmentPanel to jump to a particular spot on the grid
    * the publically accessible equivalent is in SegmentFrame
    * @param grid indexed grid number (0 &lt;= grid &lt; manager.getNumGrids()) 
    * @param spot indexed spot number (0 &lt;= spot &lt; manager.getGrid(grid).getNumOfSpots())
    */
   protected void jumpToSpot(int grid, int spot) {
	   if(grid >= 0 && grid <= manager.getNumGrids() && spot >= 0 && spot < manager.getGrid(grid).getNumOfSpots()) {
		   setSpot(grid,spot);
		   gridNumText.setText("" + (grid+1));
		   spotNumText.setText("" + (spot+1));
	   }
	   else segframe.dispose();
   }
   
   protected void doClickOnChangeButton() {
	   changeButton.doClick();
   }
   
   private void ratioComboBox_itemStateChanged(ItemEvent e) {
      if(ratioComboBox.getSelectedIndex()==0) ratioMethod = SingleGeneImage.TOTAL_SIGNAL;
      else if (ratioComboBox.getSelectedIndex()==1) ratioMethod = SingleGeneImage.AVG_SIGNAL;
      else if(ratioComboBox.getSelectedIndex()==2) ratioMethod = SingleGeneImage.TOTAL_SUBTRACT_BG;
      else if (ratioComboBox.getSelectedIndex()==3) ratioMethod = SingleGeneImage.AVG_SUBTRACT_BG;
      if(gd!=null){
         ratioLabel.setText("Ratio: " + df.format(gd.getRatio(ratioMethod)));
         if(ratioMethod==SingleGeneImage.TOTAL_SIGNAL||ratioMethod==SingleGeneImage.TOTAL_SUBTRACT_BG){
            greenbgLabel.setText("Green BG Total: " + df.format(gd.getGreenBackgroundTotal()));
            greenfgLabel.setText("Green FG Total: " + df.format(gd.getGreenForegroundTotal()));
            redbgLabel.setText("Red BG Total: " + df.format(gd.getRedBackgroundTotal()));
            redfgLabel.setText("Red FG Total: " + df.format(gd.getRedForegroundTotal()));
         } else{
            greenbgLabel.setText("Green BG Avg: " + df.format(gd.getGreenBackgroundAvg()));
            greenfgLabel.setText("Green FG Avg: " + df.format(gd.getGreenForegroundAvg()));
            redbgLabel.setText("Red BG Avg: " + df.format(gd.getRedBackgroundAvg()));
            redfgLabel.setText("Red FG Avg: " + df.format(gd.getRedForegroundAvg()));
         }
      }
      summaryStatsPanel.setStats();
      if (segframe.allGeneData != null) segframe.allGeneData.invalidate();
   }
   
   
   private void saveButton_actionPerformed(ActionEvent e) {
      Object params[]=null;
      int method=SingleGeneImage.FIXED_CIRCLE;
      boolean error=false;
      
      if(methodCombo.getSelectedItem().toString().equals("Fixed Circle")){
         params = new Object[1];
         int rad;
         method = SingleGeneImage.FIXED_CIRCLE;
         try{
            rad = Integer.parseInt(fixradiusField.getText().trim());
            if (rad<0){
               JOptionPane.showMessageDialog(segframe, "Fixed Circle Radius Must Have A Positive Integer Value.\nThe Radius Has Been Reset To The Default Value Of 6.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
               rad=6;
               fixradiusField.setText("6");
               error=true;
            }
         }catch(Exception e1){
            JOptionPane.showMessageDialog(segframe, "Fixed Circle Radius Must Have A Positive Integer Value.\nThe Radius Has Been Reset To The Default Value Of 6.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
            rad=6;
            fixradiusField.setText("6");
            error=true;
         }
         params[0] = new Integer(rad);
      }
      
      else if(methodCombo.getSelectedItem().toString().equals("Seeded Region Growing")){
         params = new Object[1];
         params[0] = new Integer(seedthresholdSlider.getValue());
         method = SingleGeneImage.SEEDED_REGION;
      }
      
      else if(methodCombo.getSelectedItem().toString().equals("Adaptive Circle")){
         int minr, maxr;
         try{
            minr = Integer.parseInt(minradiusField.getText().trim());
            maxr = Integer.parseInt(maxradiusField.getText().trim());
            if(minr<1||maxr<1||minr>maxr){
               JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Radii Must Be Positive Integer Values.\nThe Maximum Radius Must Also Be Greater Than Or Equal To The Minimum Radius.\nThe Radii Have Been Reset To The Default Values Of 3 and 8.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
               minr=3;
               maxr=8;
               minradiusField.setText(""+3);
               maxradiusField.setText(""+8);
               error=true;
            }
         }catch(Exception e2){
            minr=3;
            maxr=8;
            minradiusField.setText(""+3);
            maxradiusField.setText(""+8);
            error=true;
            JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Radii Must Be Positive Integer Values.\nThe Radii Have Been Reset To The Default Values Of 3 and 8.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
         }
         params = new Object[3];
         params[0] = new Integer(minr);
         params[1] = new Integer(maxr);
         params[2] = new Integer(adaptthresholdSlider.getValue());
         method = SingleGeneImage.ADAPTIVE_CIRCLE;
         
      }
      
      if(!error){
         final ExpressionOptionsDialog eod = new ExpressionOptionsDialog(segframe.main,segframe.project, manager);
         eod.setModal(true);
         eod.pack();
         System.out.println("setting ExpressionOptionsDialog visible");
         eod.setVisible(true);
         
         System.out.println("getting ExpressionOptionsDialog OK-state");
         
         //if(eod.getOK()){
         //   segframe.createNewExpressionFile(eod.getFileName(), eod.getColumnName(), eod.getNewFile(), eod.getAppendName(), eod.getByName(), method, ratioMethod, params);
         //  }
         
         final int finalMethod = method;
         final Object[] finalParams = params;
         if(eod.getOK()){
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  if(eod.getExpChoice() || eod.getRawChoice()){
                     segframe.createNewExpRawFiles(eod.getExpChoice(), eod.getRawChoice(), eod.getExpFileName(), eod.getRawFileName(), eod.getColumnName(), eod.getNewExpFile(), eod.getAppendName(), eod.getByName(), finalMethod, ratioMethod, finalParams);
                  }
                  //if(eod.getExpChoice()){
                  //	  segframe.createNewExpressionFile(eod.getExpFileName(), eod.getColumnName(), eod.getNewExpFile(), eod.getAppendName(), eod.getByName(), finalMethod, ratioMethod, finalParams);
                  //}
                  //if(eod.getRawChoice()){
                  //	  segframe.createNewRawDataFile(eod.getRawFileName(), eod.getExpFileName(), eod.getColumnName(), finalMethod, ratioMethod, finalParams);
                  //}
                  
               }
            });
            System.out.println("end of SegmentPanel OK action");
         }
      }
   }
   
   private void methodCombo_itemStateChanged(ItemEvent e) {
      if(methodCombo.getSelectedItem().toString().equals("Fixed Circle")){
         paramCard.show(paramPanel,"fixed");
      } else if(methodCombo.getSelectedItem().toString().equals("Seeded Region Growing")){
         paramCard.show(paramPanel,"seeded");
      } else if(methodCombo.getSelectedItem().toString().equals("Adaptive Circle")){
         paramCard.show(paramPanel,"adapt");
      }
      if(segframe.allGeneData != null) segframe.allGeneData.invalidate();
   }
   
   private void changeGeneButton_actionPerformed(ActionEvent e) {
      String gene = JOptionPane.showInputDialog(segframe,"Please Enter Gene Name:");
      if(gene!=null && !gene.trim().equals("")){
         Point loc = manager.findGeneLocation(gene);
         if(loc!=null){
            gridNumText.setText(""+(loc.x+1));
            spotNumText.setText(""+(loc.y+1));
            setSpot(loc.x,loc.y);
         } else JOptionPane.showMessageDialog(segframe, "Error! Gene Does Not Exist. Please Try Again.", "Error!", JOptionPane.ERROR_MESSAGE);
      }
   }
   
   private int getMethod() {
	   int method;
	   if (methodCombo.getSelectedItem().toString().equals("Fixed Circle")) method = SingleGeneImage.FIXED_CIRCLE;
	   else if (methodCombo.getSelectedItem().toString().equals("Seeded Region Growing")) method = SingleGeneImage.SEEDED_REGION;
	   else if (methodCombo.getSelectedItem().toString().equals("Adaptive Circle")) method = SingleGeneImage.ADAPTIVE_CIRCLE;
	   else method = -1;
	   return method;
   }
      
   private Object[] makeParams(Boolean error) {
	      Object params[]=null;
	      int method=SingleGeneImage.FIXED_CIRCLE;
	      error=Boolean.FALSE;
	      
	      if(methodCombo.getSelectedItem().toString().equals("Fixed Circle")){
	         params = new Object[1];
	         int rad;
	         method = SingleGeneImage.FIXED_CIRCLE;
	         try{
	            rad = Integer.parseInt(fixradiusField.getText().trim());
	            if (rad<0){
	               JOptionPane.showMessageDialog(segframe, "Fixed Circle Radius Must Have A Positive Integer Value.\nThe Radius Has Been Reset To The Default Value Of 6.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
	               rad=6;
	               fixradiusField.setText("6");
	               error=Boolean.TRUE;
	            }
	         }catch(Exception e1){
	            JOptionPane.showMessageDialog(segframe, "Fixed Circle Radius Must Have A Positive Integer Value.\nThe Radius Has Been Reset To The Default Value Of 6.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
	            rad=6;
	            fixradiusField.setText("6");
	            error=Boolean.TRUE;
	         }
	         params[0] = new Integer(rad);
	      }
	      
	      else if(methodCombo.getSelectedItem().toString().equals("Seeded Region Growing")){
	         params = new Object[1];
	         params[0] = new Integer(seedthresholdSlider.getValue());
	         method = SingleGeneImage.SEEDED_REGION;
	      }
	      
	      else if(methodCombo.getSelectedItem().toString().equals("Adaptive Circle")){
	         int minr, maxr;
	         try{
	            minr = Integer.parseInt(minradiusField.getText().trim());
	            maxr = Integer.parseInt(maxradiusField.getText().trim());
	            if(minr<1||maxr<1||minr>maxr){
	               JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Radii Must Be Positive Integer Values.\nThe Maximum Radius Must Also Be Greater Than Or Equal To The Minimum Radius.\nThe Radii Have Been Reset To The Default Values Of 3 and 8.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
	               minr=3;
	               maxr=8;
	               minradiusField.setText(""+3);
	               maxradiusField.setText(""+8);
	               error=Boolean.TRUE;
	            }
	         }catch(Exception e2){
	            minr=3;
	            maxr=8;
	            minradiusField.setText(""+3);
	            maxradiusField.setText(""+8);
	            error=Boolean.TRUE;
	            JOptionPane.showMessageDialog(segframe.getDesktopPane(), "Radii Must Be Positive Integer Values.\nThe Radii Have Been Reset To The Default Values Of 3 and 8.", "Incorrect Entry!", JOptionPane.ERROR_MESSAGE);
	         }
	         params = new Object[3];
	         params[0] = new Integer(minr);
	         params[1] = new Integer(maxr);
	         params[2] = new Integer(adaptthresholdSlider.getValue());
	         method = SingleGeneImage.ADAPTIVE_CIRCLE;
	         
	      }
	      return params;
   }
   
   private void autoFlaggingButton_actionPerformed(ActionEvent e) {
	   Point segframelocation = segframe.getLocation();
	   Point afodLocation = new Point();
	   afodLocation.setLocation(segframelocation.getX()+segframe.getWidth()/3.0, segframelocation.getY()+segframe.getHeight()/3.0);
	   afod.setLocation(afodLocation);
	   afod.setModal(true);
	   afod.setVisible(true);
	   int choice = JOptionPane.showConfirmDialog(segframe,"Would you like to analyze spots to determine their flagging status?\n<html><b>WARNING!</b> This may take a long time if you've chosen to use a method other than fixed circle.</html>", "MAGIC Tool Question", JOptionPane.YES_NO_OPTION );
	   segframe.setTitle("SEGMENTATION (working...)");
	   this.repaint();
	   segframe.repaint();
	   segframe.main.repaint();
	   segframe.main.getJDesktopPane().repaint();
	   if (choice == JOptionPane.YES_OPTION) {
		   //stuff to do the calculations
		   Boolean paramsError = new Boolean(false);
		   Object[] params = makeParams(paramsError);
		   int method = getMethod();
		   if ((!(paramsError.booleanValue())) && (method >= 0)) { 
			   segframe.allGeneData = new AllGeneData(segframe.manager, segframe.flagman, segframe.autoflagman, segframe.idRed, segframe.idGreen, method, ratioMethod, params, segframe.afod, segframe );
			   segframe.allGeneData.calculate();
			   doSummaryPopup();
			   summaryStatsPanel.setStats();
			   if (segframe.main.getFlagFrame() != null) segframe.main.getFlagFrame().refreshImage();
		   }
		   else {
			   JOptionPane.showMessageDialog(segframe, "Error: Parameters are not valid.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		   }
	   }
	   segframe.setTitle("SEGMENTATION");
   }
  private void doSummaryPopup() {
	  if (segframe.allGeneData != null) {
		  double percentage = (double)segframe.allGeneData.nSpotsFlagged / (double)segframe.allGeneData.nSpots;
		  percentage *= 100;
		  DecimalFormat df = new DecimalFormat("0.0#");
		  String pct = df.format(percentage);
		  JOptionPane.showMessageDialog(segframe, "Calculations completed.\nYou have auto-flagged " + segframe.allGeneData.nSpotsFlagged + " spots, which is " + pct + "% of your spots.", "MAGIC Tool Message", JOptionPane.INFORMATION_MESSAGE);
	  }
  }
   
}