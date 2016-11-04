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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import magictool.VerticalLayout;

/**
 * MetaGridOptionsDialog is a JDialog which allows user to set various grid properties
 * for metagridding a microarray image.
 */
public class MetaGridOptionsDialog extends JDialog {
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JPanel noGridsPanel = new JPanel();
  private JLabel gridNumLabel = new JLabel();
  private JTextField gridNum = new JTextField();
  private JPanel confirmPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel gridSetupPanel = new JPanel();
  private JPanel numberingPanel = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel positionPanel = new JPanel();
  private TitledBorder titledBorder1;
  private JPanel textPanel = new JPanel();
  private VerticalLayout verticalLayout2 = new VerticalLayout();
  //private JLabel warningNote = new JLabel();
  private JLabel numberQuestion = new JLabel();
  private VerticalLayout verticalLayout3 = new VerticalLayout();
  private JPanel horizAndVertNumberingPanel = new JPanel();
  private GridLayout gridLayout1 = new GridLayout();
  private ButtonGroup horizontalButtons = new ButtonGroup();
  private ButtonGroup verticalButtons = new ButtonGroup();
  private JLabel spotDirection = new JLabel();
  private JRadioButton vertDirection = new JRadioButton();
  private JRadioButton horizDirection = new JRadioButton();
  private ButtonGroup relativeButtons = new ButtonGroup();
  private VerticalLayout verticalLayout4 = new VerticalLayout();
  private JPanel relativeButtonsPanel = new JPanel();
  private GridLayout gridLayout2 = new GridLayout();
  private JPanel verticallyPanel = new JPanel();
  private JPanel horizontallyPanel = new JPanel();
  private JRadioButton bottom2Top = new JRadioButton();
  private JRadioButton right2Left = new JRadioButton();
  private JRadioButton top2Bottom = new JRadioButton();
  private JRadioButton left2Right = new JRadioButton();
  private VerticalLayout verticalLayout5 = new VerticalLayout();
  private VerticalLayout verticalLayout6 = new VerticalLayout();
  private TitledBorder titledBorder2;
  private TitledBorder titledBorder3;

  private boolean ok=false;

  /**whether or not the spot placement is left to right*/
  protected boolean horizontal;
  /**whether or not the spot placement is top to bottom*/
  protected boolean vertical;
  /**whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1*/
  protected boolean firsth;
  /**number of grids*/
  protected int grids=0;


  /**
   * Constructs a dialog for users to set grid properties
   * @param owner parent frame
   */
  public MetaGridOptionsDialog(Frame owner) {
    super(owner);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Metagrid Setup");
    titledBorder2 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),"Horizontally");
    titledBorder3 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),"Vertically");
    gridNumLabel.setText("How many metagrids?");
    gridNum.setColumns(20);
    gridNum.getCaret().setVisible(true);
    okButton.setNextFocusableComponent(cancelButton);
    okButton.setText("OK");
    this.getRootPane().setDefaultButton(okButton);
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    cancelButton.setRequestFocusEnabled(false);
    cancelButton.setFocusPainted(false);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    gridSetupPanel.setLayout(borderLayout1);
    positionPanel.setBorder(BorderFactory.createEtchedBorder());
    positionPanel.setLayout(verticalLayout4);
    noGridsPanel.setBorder(BorderFactory.createEtchedBorder());
    gridSetupPanel.setBorder(titledBorder1);
    numberingPanel.setLayout(verticalLayout2);
    //warningNote.setForeground(Color.red);
    //warningNote.setText("Spot numbers must match the gene names in the genelist.");
    numberQuestion.setText("How are the grids numbered within the metagrid?");
    textPanel.setLayout(verticalLayout3);
    horizAndVertNumberingPanel.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    spotDirection.setText("Where is grid 2 relative to grid 1:");
    vertDirection.setText("Vertically");
    horizDirection.setSelected(true);
    horizDirection.setText("Horizontally");
    relativeButtonsPanel.setLayout(gridLayout2);
    gridLayout2.setColumns(2);
    bottom2Top.setText("Bottom to Top");
    right2Left.setText("Right to Left");
    top2Bottom.setSelected(true);
    top2Bottom.setText("Top to Bottom");
    left2Right.setSelected(true);
    left2Right.setText("Left to Right");
    horizontallyPanel.setLayout(verticalLayout5);
    verticallyPanel.setLayout(verticalLayout6);
    horizontallyPanel.setBorder(titledBorder2);
    verticallyPanel.setBorder(titledBorder3);
    this.setTitle("Grid Setup");
    this.getContentPane().setLayout(verticalLayout1);
    this.getContentPane().add(noGridsPanel,  BorderLayout.NORTH);
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screen.width)/2-200,(screen.height)/2-150);
    noGridsPanel.add(gridNumLabel, null);
    noGridsPanel.add(gridNum, null);
    confirmPanel.add(okButton, null);
    confirmPanel.add(cancelButton, null);
    this.getContentPane().add(gridSetupPanel, BorderLayout.CENTER);
    gridSetupPanel.add(numberingPanel,  BorderLayout.CENTER);
    numberingPanel.add(textPanel, null);
    //textPanel.add(warningNote, null);
    textPanel.add(numberQuestion, null);
    numberingPanel.add(horizAndVertNumberingPanel, null);
    horizontallyPanel.add(left2Right, null);
    horizontallyPanel.add(right2Left, null);
    horizAndVertNumberingPanel.add(horizontallyPanel, null);
    horizAndVertNumberingPanel.add(verticallyPanel, null);
    verticallyPanel.add(top2Bottom, null);
    verticallyPanel.add(bottom2Top, null);
    gridSetupPanel.add(positionPanel, BorderLayout.SOUTH);
    positionPanel.add(spotDirection, null);
    relativeButtons.add(vertDirection);
    relativeButtons.add(horizDirection);
    positionPanel.add(relativeButtonsPanel, null);
    relativeButtonsPanel.add(horizDirection, null);
    relativeButtonsPanel.add(vertDirection, null);
    this.getContentPane().add(confirmPanel, BorderLayout.SOUTH);
    horizontalButtons.add(left2Right);
    horizontalButtons.add(right2Left);
    verticalButtons.add(top2Bottom);
    verticalButtons.add(bottom2Top);
  }

  /**
   * gets the number of grids specified by the user
   * @return number of grids specified by the user
   */
  public int getGridNum(){
    return grids;
  }

  /**
   * gets whether or not the spot placement is left to right
   * @return whether or not the spot placement is left to right
   */
  public boolean getHorizontal(){
    return horizontal;
  }

   /**
   * gets whether or not the spot placement is top to bottom
   * @return whether or not the spot placement is top to bottom
   */
  public boolean getVertical(){
    return vertical;
  }

  /**
   * gets whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   * @return whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public boolean getFirstGrid(){
    return firsth;
  }

  /**
   * sets the options displayed in the dialog box
   * @param grids number of grids
   * @param leftRight whether or not the spot placement is left to right
   * @param topBottom whether or not the spot placement is top to bottom
   * @param spotDirection whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public void setOptions(int grids, boolean leftRight, boolean topBottom, boolean spotDirection){
    this.grids = grids;
    horizontal = leftRight;
    vertical = topBottom;
    firsth = spotDirection;
    gridNum.setText(""+grids);
    left2Right.setSelected(horizontal);
    top2Bottom.setSelected(vertical);
    horizDirection.setSelected(firsth);
    right2Left.setSelected(!horizontal);
    bottom2Top.setSelected(!vertical);
    vertDirection.setSelected(!firsth);
  }

  /**
   * returns whether or not the user has hit the ok button
   * @return whether or not the user has hit the ok button
   */
  public boolean getOK() {
    return ok;
  }

  //handles the ok button and ensures all fields entered correctly
  private void okButton_actionPerformed(ActionEvent e) {
    try{
      grids=Integer.parseInt(gridNum.getText().trim());
      horizontal=left2Right.isSelected();
      vertical=top2Bottom.isSelected();
      firsth=horizDirection.isSelected();
      if(grids>0){
        ok=true;
        this.dispose();
      }
      else JOptionPane.showMessageDialog(this, "Error! There must be at least one metagrid.");
    }
    catch(NumberFormatException e1){
      JOptionPane.showMessageDialog(this, "Please enter an integer value for the number of metagrids.");
    }

  }

  //handles the cancel button
  private void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }
}