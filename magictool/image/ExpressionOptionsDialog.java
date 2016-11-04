/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007  Laurie Heyer
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
 *   Davidson College
 *   PO Box 6959
 *   Davidson, NC 28035-6959
 *   UNITED STATES
 */


package magictool.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import magictool.ExpFile;
import magictool.FileComboBox;
import magictool.Project;
import magictool.VerticalLayout;
import magictool.image.GridManager;

/**
 * ExpressionOptionsDialog is a JDialog which allows users to
 * enter desired options for creating new expression files or appending
 * existing expression files with new data created from microarray images.
 * Note: this is the updated (sept 2005) ExpressionOptionsClass.
 *
 * changing version of project from MT13_a to HEAD (1:34am 9/26/05)
 */

public class ExpressionOptionsDialog extends JDialog{
   
   //main Buttons & textfields
   private JCheckBox expCheckBox;
   private JCheckBox rawCheckBox;
   private JButton okButton;
   private JButton cancelButton;
   
   //.exp file buttons, textboxes, and labels
   private JLabel expFileNameLabel;
   private JLabel columnLabel;
   private JTextField expFileNameField;
   private JTextField columnField;
   private ButtonGroup dataOptionsGroup;
   private JRadioButton newFileButton;
   private JRadioButton appendButton;
   private JLabel appendBylabel;
   private FileComboBox expressionComboBox;
   private ButtonGroup appendButtonGroup;
   private JRadioButton byNameButton;
   private JRadioButton byOrderButton;
   
   //.raw textfield & labels
   private JTextField rawFileNameField;
   private JLabel rawFileNameLabel;
   
   // this dimension object should really be more universal!
   private static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
   private boolean ok = false;
   private boolean expChoice = true;		//it is selected by default
   private boolean rawChoice = false;
   
   /**project associated with the expression files*/
   protected Project project;
   /**name of the new expression file*/
   protected String expFileName=null;
   /**name of the expression file to append*/
   protected String appendname=null;
   /**name of the raw data file*/
   protected String rawFileName = null;
   /**whether or not user selected ok button*/
   protected boolean byName=true;
   /**whether or not a new file should be created*/
   protected boolean newExpFile = true;
   /**name of the new data column*/
   protected String colname=null;
   /**active grid manager*/
   protected GridManager manager;
   
   
   
   /**
    * Constructs a new options dialog for user
    * @param owner parent frame
    * @param p project to place new expression file in
    * @param manager active grid manager
    */
   public ExpressionOptionsDialog(Frame owner, Project p, GridManager manager) {
      super(owner);
      project=p;
      this.manager=manager;
      expressionComboBox = new FileComboBox(project,Project.EXP,"all",true,"Expression");
      expressionComboBox.setEnabled(false);
      
      try {
         jbInit();
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   private void jbInit() throws Exception {
      //file & column name input
      JPanel namePanel = new JPanel();
      namePanel.setBorder(BorderFactory.createEtchedBorder());
      namePanel.setLayout(new BorderLayout());
      JPanel fileNamePanel = new JPanel();
      fileNamePanel.setLayout(new BorderLayout());	//so textfield scales with window
      expFileNameLabel = new JLabel("Enter Expression Filename:");
      expFileNameField = new JTextField();
      fileNamePanel.add(expFileNameLabel, BorderLayout.LINE_START);
      fileNamePanel.add(expFileNameField, BorderLayout.CENTER);
      JLabel columnLabel = new JLabel("Enter Column Name:");
      columnField = new JTextField();
      namePanel.add(columnLabel, BorderLayout.LINE_START);
      namePanel.add(columnField, BorderLayout.CENTER);
      namePanel.add(fileNamePanel, BorderLayout.PAGE_START);
      
      JPanel filePanel = new JPanel();
      filePanel.setBorder(BorderFactory.createEtchedBorder());
      filePanel.setLayout(new VerticalLayout());
      newFileButton = new JRadioButton("Create New File",true);
      newFileButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            newFileButton_actionPerformed(e);
         }
      });
      dataOptionsGroup = new ButtonGroup();
      dataOptionsGroup.add(newFileButton);
      filePanel.add(newFileButton);
      
      JPanel appendFilePanel = new JPanel();
      appendFilePanel.setLayout(new BorderLayout());
      appendButton = new JRadioButton("Append To File:", false);
      appendButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            appendButton_actionPerformed(e);
         }
      });
      dataOptionsGroup.add(appendButton);
      expressionComboBox = new FileComboBox(project,project.EXP,true);
      //appendButton.setEnabled(false);
      expressionComboBox.setEnabled(false);
      appendFilePanel.add(appendButton, BorderLayout.LINE_START);
      appendFilePanel.add(expressionComboBox, BorderLayout.CENTER);
      
      JPanel appendSubPanel = new JPanel();
      appendSubPanel.setLayout(new BorderLayout());
      appendBylabel = new JLabel("Append By:");
      appendSubPanel.add(appendBylabel, BorderLayout.LINE_START);
      appendBylabel.setEnabled(false);
      
      JPanel appendSubPanel2 = new JPanel();
      appendSubPanel2.setLayout(new VerticalLayout());
      byNameButton = new JRadioButton("Name (Gene Names Must Match Exactly)", true);
      byOrderButton = new JRadioButton("Name (List Orders Must Match Exactly)", true);
      appendButtonGroup = new ButtonGroup();
      appendButtonGroup.add(byNameButton);
      appendButtonGroup.add(byOrderButton);
      byNameButton.setEnabled(false);
      byOrderButton.setEnabled(false);
      appendSubPanel2.add(byNameButton);
      appendSubPanel2.add(byOrderButton);
      
      appendSubPanel.add(appendSubPanel2, BorderLayout.CENTER);
      filePanel.add(appendFilePanel);
      filePanel.add(appendSubPanel);
      
      //rawPanel sub-panels
      /**JPanel rawNamePanel = new JPanel();
       * rawNamePanel.setLayout(new BorderLayout());
       * rawFileNameLabel = new JLabel("Enter Filename:");
       * rawFileNameField = new JTextField(25);
       * rawFileNameLabel.setEnabled(false);
       * rawFileNameField.setEnabled(false);
       * rawNamePanel.add(rawFileNameLabel, BorderLayout.LINE_START);
       * rawNamePanel.add(rawFileNameField, BorderLayout.CENTER);*/
      
      // penultimate (top) panels & buttons
      JPanel expPanel = new JPanel();
      TitledBorder expBorder = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),".exp  and .raw file options");
      expPanel.setBorder(expBorder);
      expPanel.setLayout(new VerticalLayout());
      expCheckBox = new JCheckBox("Create Expression File?", true);
      expCheckBox.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            checkBox_ItemEvent(e);
         }
      });
      rawCheckBox = new JCheckBox("Create Raw Data File (Named after Column Name)?");
      expPanel.add(expCheckBox);
      expPanel.add(namePanel);
      expPanel.add(filePanel);
      expPanel.add(rawCheckBox);
      
      /**JPanel rawPanel = new JPanel();
       * TitledBorder rawBorder = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),".raw file options");
       * rawPanel.setBorder(rawBorder);
       * rawPanel.setLayout(new VerticalLayout());
       * rawCheckBox = new JCheckBox("Create Raw Data File?");
       * rawCheckBox.addItemListener(new ItemListener() {
       * public void itemStateChanged(ItemEvent e) {
       * checkBox_ItemEvent(e);
       * }
       * });
       * rawPanel.add(rawCheckBox);
       * rawPanel.add(rawNamePanel);*/
      
      // top panels (all descend from this)
      JPanel expressionSetupPanel = new JPanel();
      TitledBorder protoBorder = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Expression Data Options");
      expressionSetupPanel.setBorder(protoBorder);
      expressionSetupPanel.setLayout(new VerticalLayout());
      expressionSetupPanel.add(expPanel);
      //expressionSetupPanel.add(rawPanel);
      
      JPanel confirmPanel = new JPanel();
      okButton = new JButton();
      cancelButton = new JButton();
      okButton.setText("OK");
      cancelButton.setText("Cancel");
      this.getRootPane().setDefaultButton(okButton);
      okButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            okButton_actionPerformed(e);
         }
      });
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            cancelButton_actionPerformed(e);
         }
      });
      confirmPanel.add(okButton, null);
      confirmPanel.add(cancelButton, null);
      
      this.setTitle("Expression File Parameters");
      this.getContentPane().add(expressionSetupPanel, BorderLayout.CENTER);
      this.getContentPane().add(confirmPanel, BorderLayout.PAGE_END);
      this.pack();
      this.setLocation((screen.width)/2-this.getWidth()/2,(screen.height)/2-this.getHeight()/2);
      //this.setVisible(true);
   }
   
   private void okButton_actionPerformed(ActionEvent e) {
      System.out.println("ExpressionOptionsDialog OK button clicked!");
      expFileName = expFileNameField.getText().trim();
      //rawFileName = rawFileNameField.getText().trim();
      colname = columnField.getText().trim();
      rawFileName=colname;
      if(expChoice){
    	  System.out.println("expChoice is true");
         if(newFileButton.isSelected()){
            newExpFile=true;
         } else{
            newExpFile=false;
            appendname=expressionComboBox.getFileName();
            byName = byNameButton.isSelected();
         }
         if(expFileName!=null&&!expFileName.trim().equals("")){
            if(!expFileName.toLowerCase().endsWith(".exp")) expFileName+=".exp";
            
            if(colname!=null&&!colname.trim().equals("")){
               if(newExpFile||(appendname!=null&&!appendname.trim().equals(""))){
                  if(!newExpFile&&!appendname.toLowerCase().endsWith(".exp")) appendname+=".exp";
                  if(newExpFile||!appendname.equalsIgnoreCase(expFileName)){
                     int cont = JOptionPane.YES_OPTION;
                     if(!newExpFile&&!byName){
                        ExpFile temp = new ExpFile(new File(expressionComboBox.getFilePath()));
                        if(temp.numGenes()!=manager.getGeneList().getNumWriteableGenes()) cont = JOptionPane.showConfirmDialog(this, "Warning! You Have Chosen To Append By Order, However The Number Genes Specified\nDoes Not Match The Number of Genes In The Expression File.\nDo You Want To Continue Anyway?","Warning!",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                     }
                     if(cont==JOptionPane.YES_OPTION){
                        ok=true;
                        //this.dispose();
                     }
                  } else JOptionPane.showMessageDialog(this, "Error! In Order To Prevent Accidental Overwriting,\nThe New Filename Cannot Be The Same As The Appended File.\nPlease Enter Another Filename.", "Error", JOptionPane.ERROR_MESSAGE);
               } else JOptionPane.showMessageDialog(this, "Error! Please Select A File To Append.", "Error", JOptionPane.ERROR_MESSAGE);
            } else JOptionPane.showMessageDialog(this, "Error! Please Enter A Data Column Name.", "Error", JOptionPane.ERROR_MESSAGE);
         } else JOptionPane.showMessageDialog(this, "Error! Please Enter A Filename for the .exp file.", "Error", JOptionPane.ERROR_MESSAGE);
      } 
      
      if(rawChoice){
         if(expFileName==null || expFileName.trim().equals("")) expFileName = "[filename not provided]";
    	  if(colname==null || colname.trim().equals("")) colname = "[column name not provided]";
    	  if(rawFileName!=null && !rawFileName.trim().equals("")){
    		  if(!rawFileName.toLowerCase().endsWith(".raw")) rawFileName+=".raw";
        	  ok=true;
            
         } else JOptionPane.showMessageDialog(this, "Error! Please Enter A Filename for the .raw file.", "Error", JOptionPane.ERROR_MESSAGE);
      }
      System.out.println("After Raw");
      if(ok==true) this.dispose();
   }
   
   private void cancelButton_actionPerformed(ActionEvent e) {
      this.dispose();
   }
   
   /**
    * returns whether or not the user has pushed ok button
    * @return whether or not the user has pushed ok button
    */
   public boolean getOK() {
      return ok;
   }
   
   /**
    * @return the selection state of the .exp file output checkbox
    */
   public boolean getExpChoice() {
      return expChoice;
   }
   
   /**
    * @return the selection state of the .raw file output checkbox
    */
   public boolean getRawChoice() {
      //return rawChoice;
      return rawCheckBox.isSelected();
   }
   
   
   /**
    * returns whether to create a completely new expression file or appending to an existing one
    * @return whether to create a completely new expression file
    */
   public boolean getNewExpFile(){
      return newExpFile;
   }
   
   /**
    * returns whether to append by name (true) or by order (false)
    * @return whether to append by name (true) or by order (false)
    */
   public boolean getByName(){
      return byName;
   }
   
   /**
    * gets the new data column name
    * @return new data column name
    */
   public String getColumnName(){
      return colname;
   }
   
   /**
    * gets the new expression file name
    * @return new expression file name
    */
   public String getExpFileName(){
      return expFileName;
   }
   
   /**
    * gets the new .raw file name
    * @return new .raw file name
    */
   public String getRawFileName(){
      return rawFileName;
   }
   
   /**
    * returns the name of the expression file to append
    * @return name of the expression file to append
    */
   public String getAppendName(){
      return appendname;
   }
   
   private void checkBox_ItemEvent(ItemEvent e) {
      //set exp & raw panels to enabled or disabled based on selection of the CheckBoxes.
      expChoice = expCheckBox.isSelected();
      rawChoice = rawCheckBox.isSelected();
      
      expFileNameField.setEnabled(expChoice);
      columnField.setEnabled(expChoice);
      newFileButton.setEnabled(expChoice);
      appendButton.setEnabled(expChoice);
      if (!expChoice && appendButton.isSelected()) {
         newFileButton.setEnabled(true);
         newFileButton.doClick();
         newFileButton.setEnabled(false);
      }
      
      //rawFileNameField.setEnabled(rawChoice);
      //rawFileNameLabel.setEnabled(rawChoice);
   }
   
   private void newFileButton_actionPerformed(ActionEvent e) {
      if(newFileButton.isSelected()){
         expressionComboBox.setEnabled(false);
         appendBylabel.setEnabled(false);
         byNameButton.setEnabled(false);
         byOrderButton.setEnabled(false);
      } else{
         expressionComboBox.setEnabled(true);
         appendBylabel.setEnabled(true);
         byNameButton.setEnabled(true);
         byOrderButton.setEnabled(true);
      }
   }
   
   private void appendButton_actionPerformed(ActionEvent e) {
      if(newFileButton.isSelected()){
         expressionComboBox.setEnabled(false);
         appendBylabel.setEnabled(false);
         byNameButton.setEnabled(false);
         byOrderButton.setEnabled(false);
      } else{
         expressionComboBox.setEnabled(true);
         appendBylabel.setEnabled(true);
         byNameButton.setEnabled(true);
         byOrderButton.setEnabled(true);
      }
   }
   
}