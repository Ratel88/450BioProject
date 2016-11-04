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

package magictool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;


/**
 * ProjectPropertiesFrame is a frame which allows a user to change the project properties.
 * Currently the only project property is the method to handle missing data and the missing
 * data threshold.
 */
public class ProjectPropertiesFrame extends JInternalFrame implements KeyListener{

  private JPanel mainPanel = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel buttonPanel = new JPanel();
  private TitledBorder titledBorder1;
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel ignorePanel = new JPanel();
  private JComboBox missingBox = new JComboBox();
  private JTabbedPane propertyTabs = new JTabbedPane();
  private JPanel mPanel = new JPanel();
  private JPanel missingPanel = new JPanel();
  private JSlider requiredSlider = new JSlider();
  private JPanel missingpropPanel = new JPanel();
  private BorderLayout borderLayout3 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel1 = new JLabel();
  private JPanel imagePanel = new JPanel();
  private VerticalLayout verticalLayout2 = new VerticalLayout();
  private JLabel imageLabel = new JLabel();
  private JSlider imageSlider = new JSlider();
  private TitledBorder titledBorder2;
  private JPanel groupPanel = new JPanel();
  private TitledBorder titledBorder3;
  private VerticalLayout verticalLayout3 = new VerticalLayout();
  private JPanel import1 = new JPanel();
  private JPanel import2 = new JPanel();
  private BorderLayout borderLayout4 = new BorderLayout();
  private BorderLayout borderLayout5 = new BorderLayout();
  private JLabel importLabel2 = new JLabel();
  private JComboBox importComboBox1 = new JComboBox();
  private JLabel importLabel1 = new JLabel();
  private JComboBox importComboBox2 = new JComboBox();

  /**project whose properties are going to be updated*/
  protected Project project;
  /**parent frame*/
  protected MainFrame parent;




  /**
   * Constructs the frame from given project whose properties are going to be updated
   * @param parent parent frame
   * @param project project whose properties are going to be updated
   */
  public ProjectPropertiesFrame(MainFrame parent, Project project) {
    this.project=project;
    this.parent=parent;

    try {
      jbInit();
      addKeyListenerRecursively(this);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //initializes the frame
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Missing Data");
    titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Image Size");
    titledBorder3 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Importing Group Files");
    mainPanel.setLayout(borderLayout1);
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    this.setResizable(true);
    this.setTitle("Project Properties");

    ignorePanel.setLayout(borderLayout3);
    mPanel.setLayout(borderLayout2);
    requiredSlider.setMajorTickSpacing(20);
    requiredSlider.setMinimum(0);
    requiredSlider.setMinorTickSpacing(5);
    requiredSlider.setPaintLabels(true);
    requiredSlider.setPaintTicks(true);

    missingpropPanel.setLayout(verticalLayout1);
    jLabel3.setText("Percent Existing Data Required:");
    jLabel1.setText("Missing Data:  ");
    imagePanel.setLayout(verticalLayout2);
    imageLabel.setText("Maximum Image Size (Megapixels) ");

    Hashtable imageDictionary = new Hashtable();
    imageDictionary.put(new Integer(20), new JLabel("2"));
    imageDictionary.put(new Integer(40), new JLabel("4"));
    imageDictionary.put(new Integer(60), new JLabel("6"));
    imageDictionary.put(new Integer(80), new JLabel("8"));
    imageDictionary.put(new Integer(100), new JLabel("10"));

    imageSlider.setMajorTickSpacing(20);
    imageSlider.setMinimum(20);
    imageSlider.setMinorTickSpacing(5);
    imageSlider.setLabelTable(imageDictionary);
    imageSlider.setPaintLabels(true);
    imageSlider.setPaintTicks(true);

    imagePanel.setBorder(titledBorder2);
    missingpropPanel.setBorder(titledBorder1);
    groupPanel.setBorder(titledBorder3);
    groupPanel.setLayout(verticalLayout3);
    import1.setLayout(borderLayout4);
    import2.setLayout(borderLayout5);
    importLabel2.setText("When Averaging Replicates, Keep Genes When:");
    importLabel1.setText("New Expression Files Carry Group Files When:");
    importComboBox1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        importComboBox1_actionPerformed(e);
      }
    });
    importComboBox1.addItem("Always");
    importComboBox1.addItem("Expression Data Is Not Altered");
    importComboBox1.addItem("Never");
    importComboBox2.addItem("Any Replicate is Present");
    importComboBox2.addItem("Half of the Replicates are Present");
    importComboBox2.addItem("All of the Replicates are Present");
    importComboBox2.addItem("Never Add Replicate Genes");
    this.getContentPane().add(mainPanel,  BorderLayout.CENTER);
    mainPanel.add(buttonPanel,  BorderLayout.SOUTH);
    buttonPanel.add(okButton, null);
    buttonPanel.add(cancelButton, null);
    mainPanel.add(propertyTabs,  BorderLayout.CENTER);
    propertyTabs.add(missingpropPanel,  "Data Handling");
    ignorePanel.add(jLabel3, BorderLayout.WEST);
    ignorePanel.add(requiredSlider, BorderLayout.CENTER);
    propertyTabs.add(imagePanel,   "Image Saving");
    missingpropPanel.add(missingPanel, null);
    missingPanel.add(mPanel, null);
    missingpropPanel.add(ignorePanel, null);
    mPanel.add(jLabel1, BorderLayout.WEST);
    mPanel.add(missingBox, BorderLayout.CENTER);
    imagePanel.add(imageLabel, null);
    imagePanel.add(imageSlider, null);
    propertyTabs.add(groupPanel,   "Group Files");
    groupPanel.add(import1, null);
    import1.add(importComboBox1,  BorderLayout.CENTER);
    import1.add(importLabel1, BorderLayout.NORTH);
    groupPanel.add(import2, null);
    import2.add(importLabel2,  BorderLayout.NORTH);
    import2.add(importComboBox2,  BorderLayout.CENTER);
    missingBox.addItem("Remove All Genes Missing Data");
    missingBox.addItem("Ignore Missing Data");
    missingBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        missingBox_itemStateChanged(e);
      }
    });

    //sets the project properties
    missingBox.setSelectedIndex(project.getMissingDataStyle());
    if(missingBox.getSelectedIndex()!=1) ignorePanel.setVisible(false);

    requiredSlider.setValue(100-project.getMissingThreshold());
    imageSlider.setValue((int)Math.round(project.getImageSize()*10));
    importComboBox1.setSelectedIndex(project.getGroupMethod());
    importComboBox2.setSelectedIndex(project.getAverageReplicateMethod());
    import2.setVisible(!importComboBox1.getSelectedItem().toString().equalsIgnoreCase("Never"));
  }

  //updates the project properties when ok is pressed
  private void okButton_actionPerformed(ActionEvent e) {
    project.setMissingDataStyle(missingBox.getSelectedIndex());
    project.setMissingThreshold(100-requiredSlider.getValue());
    project.setImageSize(imageSlider.getValue()/10.0);
    project.setGroupMethod(importComboBox1.getSelectedIndex());
    project.setAverageReplicateMethod(importComboBox2.getSelectedIndex());
    parent.expMain = new ExpFile(parent.expMain.getExpFile());
    project.writeProject();
    this.dispose();
  }

  //closes the frame
  private void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }


  private void missingBox_itemStateChanged(ItemEvent e) {
    if(missingBox.getSelectedIndex()!=1) ignorePanel.setVisible(false);
    else ignorePanel.setVisible(true);
  }

  //adds the key listeners
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

  private void importComboBox1_actionPerformed(ActionEvent e) {
    if(importComboBox1.getSelectedItem().toString().equalsIgnoreCase("Never")){
      import2.setVisible(false);
    }
    else import2.setVisible(true);
  }
}